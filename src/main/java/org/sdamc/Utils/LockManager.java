package org.sdamc.Utils;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

public class LockManager {

    private static volatile LockManager instance;

    private final ConcurrentMap<String, WeakReference<StampedLock>> lockMap;

    private static final int INITIAL_POOL_SIZE = 128;
    private static final int MAX_POOL_SIZE = 1024;

    // 使用 volatile 数组确保可见性
    private volatile StampedLock[] lockPool;

    // 当前池大小
    private volatile int currentPoolSize;
    // 用于扩容的锁
    private final Object resizeLock = new Object();

    // 用于追踪当前锁持有者
    private final ConcurrentMap<String, String> lockOwners;

    // 用于追踪锁重入次数
    private final ConcurrentMap<String, AtomicInteger> lockCounts;

    private LockManager() {
        lockMap = new ConcurrentHashMap<>();
        lockOwners = new ConcurrentHashMap<>();
        lockCounts = new ConcurrentHashMap<>();
        currentPoolSize = INITIAL_POOL_SIZE;
        initializeLockPool(INITIAL_POOL_SIZE);
    }

    private void initializeLockPool(int size) {
        StampedLock[] newPool = new StampedLock[size];
        for (int i = 0; i < size; i++) {
            newPool[i] = new StampedLock();
        }
        lockPool = newPool;
    }

    public static LockManager getInstance() {
        if (instance == null) {
            synchronized (LockManager.class) {
                if (instance == null) {
                    instance = new LockManager();
                }
            }
        }
        return instance;
    }

    private StampedLock getLockFromPool(String lockable) {
        while (true) {
            int size = currentPoolSize;
            int poolIndex = Math.abs(lockable.hashCode() % size);

            // 检查是否需要扩容
            if (poolIndex >= size * 4 / 5) { // 当使用率超过80%时扩容
                tryResizePool();
                continue; // 重新计算索引
            }

            return lockPool[poolIndex];
        }
    }

    private void tryResizePool() {
        // 使用 synchronized 块确保扩容的原子性
        synchronized (resizeLock) {
            int currentSize = currentPoolSize;
            if (currentSize >= MAX_POOL_SIZE) {
                return; // 已达到最大大小，不再扩容
            }

            // 检查是否其他线程已完成扩容
            if (currentSize != lockPool.length) {
                return;
            }

            int newSize = Math.min(currentSize * 2, MAX_POOL_SIZE);
            if (newSize == currentSize) {
                return;
            }

            // 创建新池并复制现有锁
            StampedLock[] newPool = new StampedLock[newSize];
            System.arraycopy(lockPool, 0, newPool, 0, currentSize);

            // 初始化新增的锁
            for (int i = currentSize; i < newSize; i++) {
                newPool[i] = new StampedLock();
            }

            // 更新引用和大小
            lockPool = newPool;
            currentPoolSize = newSize;

            System.out.println(String.format("Lock pool resized from %d to %d", currentSize, newSize));
        }
    }

    /**
     * 获取写锁，支持重入和所有者追踪
     */
    public boolean acquireWriteLock(String lockable, long totalTimeout) {
        String currentThread = Thread.currentThread().getName();
        String owner = lockOwners.get(lockable);

        // 检查重入
        if (currentThread.equals(owner)) {
            lockCounts.get(lockable).incrementAndGet();
            return true;
        }

        StampedLock lock = lockMap.compute(lockable, (k, v) -> {
            if (v != null && v.get() != null) {
                return v;
            }
            return new WeakReference<>(new StampedLock());
        }).get();

        if (lock == null) {
            lock = new StampedLock();
            lockMap.put(lockable, new WeakReference<>(lock));
        }

        long timeSpent = 0;
        long waitTime = 25; // 初始等待时间

        while (timeSpent < totalTimeout) {
            try {
                long currentTimeout = Math.min(waitTime, totalTimeout - timeSpent);
                long stamp = lock.tryWriteLock(currentTimeout, TimeUnit.MILLISECONDS);

                if (stamp != 0L) {
                    // 记录锁的所有者和重入计数
                    lockOwners.put(lockable, currentThread);
                    lockCounts.putIfAbsent(lockable, new AtomicInteger(1));

                    System.out.println(String.format("Write lock acquired for %s by %s (attempt: %dms/%dms)", lockable,
                            currentThread, timeSpent, totalTimeout));
                    return true;
                }

                timeSpent += currentTimeout;
                waitTime = adjustWaitTime(waitTime);

                System.out.println(String.format("Write lock attempt failed for %s by %s, retrying... (%dms/%dms)",
                        lockable, currentThread, timeSpent, totalTimeout));

            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(String.format("Write lock interrupted for %s by %s", lockable, currentThread));
                return false;
            }
        }

        System.out.println(String.format("Failed to acquire write lock for %s by %s after %dms", lockable,
                currentThread, totalTimeout));
        return false;
    }

    /**
     * 获取读锁，支持重入和所有者追踪
     */
    public boolean acquireReadLock(String lockable, long totalTimeout) {
        String currentThread = Thread.currentThread().getName();
        String owner = lockOwners.get(lockable);

        // 检查重入
        if (currentThread.equals(owner)) {
            lockCounts.get(lockable).incrementAndGet();
            return true;
        }

        StampedLock lock = lockMap.compute(lockable, (k, v) -> {
            if (v != null && v.get() != null) {
                return v;
            }
            return new WeakReference<>(new StampedLock());
        }).get();

        if (lock == null) {
            lock = new StampedLock();
            lockMap.put(lockable, new WeakReference<>(lock));
        }

        long timeSpent = 0;
        long waitTime = 25;

        while (timeSpent < totalTimeout) {
            try {
                // 首先尝试乐观读
                long stamp = lock.tryOptimisticRead();
                if (stamp != 0L && lock.validate(stamp)) {
                    lockOwners.put(lockable, currentThread);
                    lockCounts.putIfAbsent(lockable, new AtomicInteger(1));

                    System.out
                        .println(String.format("Optimistic read lock acquired for %s by %s", lockable, currentThread));
                    return true;
                }

                // 乐观读失败，尝试悲观读
                long currentTimeout = Math.min(waitTime, totalTimeout - timeSpent);
                stamp = lock.tryReadLock(currentTimeout, TimeUnit.MILLISECONDS);

                if (stamp != 0L) {
                    lockOwners.put(lockable, currentThread);
                    lockCounts.putIfAbsent(lockable, new AtomicInteger(1));

                    System.out.println(String.format("Read lock acquired for %s by %s (attempt: %dms/%dms)", lockable,
                            currentThread, timeSpent, totalTimeout));
                    return true;
                }

                timeSpent += currentTimeout;
                waitTime = adjustWaitTime(waitTime);

                System.out.println(String.format("Read lock attempt failed for %s by %s, retrying... (%dms/%dms)",
                        lockable, currentThread, timeSpent, totalTimeout));

            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(String.format("Read lock interrupted for %s by %s", lockable, currentThread));
                return false;
            }
        }

        System.out.println(String.format("Failed to acquire read lock for %s by %s after %dms", lockable, currentThread,
                totalTimeout));
        return false;
    }

    /**
     * 自适应等待时间调整
     */
    private long adjustWaitTime(long currentWait) {
        // 最大等待时间1秒，最小等待时间25ms
        if (currentWait < 1000) {
            return Math.min(currentWait * 2, 1000);
        }
        else {
            return Math.max(currentWait / 2, 25);
        }
    }

    /**
     * 释放锁，支持重入计数
     */
    public void releaseLock(String lockable, boolean isWrite) {
        String currentThread = Thread.currentThread().getName();
        String owner = lockOwners.get(lockable);

        // 检查是否是锁的拥有者
        if (!currentThread.equals(owner)) {
            System.out
                .println(String.format("Warning: %s attempting to release lock owned by %s", currentThread, owner));
            return;
        }

        AtomicInteger count = lockCounts.get(lockable);
        if (count != null && count.decrementAndGet() > 0) {
            // 还有重入的锁，不实际释放
            return;
        }

        WeakReference<StampedLock> lockRef = lockMap.get(lockable);
        if (lockRef != null) {
            StampedLock lock = lockRef.get();
            if (lock != null) {
                try {
                    if (isWrite) {
                        if (lock.isWriteLocked()) {
                            long stamp = lock.tryWriteLock();
                            if (stamp != 0L) {
                                lock.unlockWrite(stamp);
                            }
                        }
                    }
                    else {
                        long stamp = lock.tryOptimisticRead();
                        if (stamp != 0L) {
                            lock.unlock(stamp);
                        }
                    }

                    // 清理所有者信息
                    lockOwners.remove(lockable);
                    lockCounts.remove(lockable);

                    System.out.println(String.format("Lock released for %s by %s", lockable, currentThread));

                }
                catch (Exception e) {
                    System.out.println(String.format("Error releasing lock for %s: %s", lockable, e.getMessage()));
                }
            }
            lockMap.remove(lockable, lockRef);
        }
    }

}