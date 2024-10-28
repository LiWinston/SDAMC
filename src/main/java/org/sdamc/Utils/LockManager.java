package org.sdamc.Utils;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

public class LockManager {

    private static class LockContext {

        final StampedLock lock;

        final Map<String, Long> threadStamps; // 记录每个线程的stamp

        final Map<String, Integer> reentrantCounts; // 记录重入次数

        LockContext() {
            this.lock = new StampedLock();
            this.threadStamps = new ConcurrentHashMap<>();
            this.reentrantCounts = new ConcurrentHashMap<>();
        }

    }

    private static volatile LockManager instance;

    private final ConcurrentMap<String, WeakReference<LockContext>> lockMap;

    private static final int INITIAL_POOL_SIZE = 128;

    private static final int MAX_POOL_SIZE = 1024;

    // 使用 volatile 数组确保可见性
    private volatile StampedLock[] lockPool;

    // 当前池大小
    private volatile int currentPoolSize;

    // 用于扩容的锁
    private final Object resizeLock = new Object();

    /**
     * 优化的自适应等待时间调整，使用指数退避策略 - 基础等待时间采用指数增长，但有上限 - 引入抖动因子避免惊群效应 - 考虑系统负载动态调整等待时间
     */
    // 等待时间相关常量
    private static final long MIN_WAIT_TIME = 1L; // 最小等待时间，单位毫秒

    private static final long MAX_WAIT_TIME = 1000L; // 最大等待时间，单位毫秒

    private static final double BACKOFF_MULTIPLIER = 1.5; // 指数退避乘数

    private static final double JITTER_FACTOR = 0.1; // 随机抖动因子

    // 用于负载统计
    private final AtomicInteger currentContention = new AtomicInteger(0);

    private volatile long lastContentionReset = System.nanoTime();

    private static final long CONTENTION_RESET_INTERVAL = TimeUnit.SECONDS.toNanos(1);

    private LockManager() {
        lockMap = new ConcurrentHashMap<>();
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

        LockContext context = lockMap.compute(lockable, (k, v) -> {
            if (v != null && v.get() != null) {
                return v;
            }
            return new WeakReference<>(new LockContext());
        }).get();

        if (context == null) {
            context = new LockContext();
            lockMap.put(lockable, new WeakReference<>(context));
        }

        // 检查重入
        Long existingStamp = context.threadStamps.get(currentThread);
        if (existingStamp != null) {
            // 验证stamp是否仍然有效
            if (context.lock.validate(existingStamp)) {
                context.reentrantCounts.compute(currentThread, (k, v) -> v == null ? 1 : v + 1);
                return true;
            }
            else {
                // 如果stamp无效，清除旧的记录
                context.threadStamps.remove(currentThread);
                context.reentrantCounts.remove(currentThread);
            }
        }

        long timeSpent = 0;
        long waitTime = 2;

        while (timeSpent < totalTimeout) {
            try {
                long currentTimeout = Math.min(waitTime, totalTimeout - timeSpent);
                long startAttempt = System.nanoTime();

                long stamp = context.lock.tryWriteLock(currentTimeout, TimeUnit.MILLISECONDS);
                timeSpent += TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startAttempt);

                if (stamp != 0L) {
                    // 成功获取锁，记录stamp和重入计数
                    context.threadStamps.put(currentThread, stamp);
                    context.reentrantCounts.put(currentThread, 1);

                    System.out.println(String.format("Write lock acquired for %s by %s (attempt: %dms/%dms)", lockable,
                            currentThread, timeSpent, totalTimeout));
                    return true;
                }

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
                currentThread, timeSpent));
        return false;
    }

     /**
     * 获取读锁，支持重入和所有者追踪
     */
    public boolean acquireReadLock(String lockable, long totalTimeout) {
        String currentThread = Thread.currentThread().getName();

        LockContext context = lockMap.compute(lockable, (k, v) -> {
            if (v != null && v.get() != null) {
                return v;
            }
            return new WeakReference<>(new LockContext());
        }).get();

        if (context == null) {
            context = new LockContext();
            lockMap.put(lockable, new WeakReference<>(context));
        }

        // 检查重入
        Long existingStamp = context.threadStamps.get(currentThread);
        if (existingStamp != null) {
            // 验证stamp是否仍然有效
            if (context.lock.validate(existingStamp)) {
                context.reentrantCounts.compute(currentThread, (k, v) -> v == null ? 1 : v + 1);
                return true;
            }
            else {
                // 如果stamp无效，清除旧的记录
                context.threadStamps.remove(currentThread);
                context.reentrantCounts.remove(currentThread);
            }
        }

        long timeSpent = 0;
        long waitTime = 2;

        while (timeSpent < totalTimeout) {
            try {
                long startAttempt = System.nanoTime();

                // 首先尝试乐观读
                long stamp = context.lock.tryOptimisticRead();
                if (stamp != 0L && context.lock.validate(stamp)) {
                    timeSpent += TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startAttempt);

                    // 记录stamp和重入计数
                    context.threadStamps.put(currentThread, stamp);
                    context.reentrantCounts.put(currentThread, 1);

                    System.out.println(String.format("Optimistic read lock acquired for %s by %s (attempt: %dms/%dms)",
                            lockable, currentThread, timeSpent, totalTimeout));
                    return true;
                }

                // 乐观读失败，尝试悲观读
                long currentTimeout = Math.min(waitTime, totalTimeout - timeSpent);
                stamp = context.lock.tryReadLock(currentTimeout, TimeUnit.MILLISECONDS);

                timeSpent += TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startAttempt);

                if (stamp != 0L) {
                    // 成功获取锁，记录stamp和重入计数
                    context.threadStamps.put(currentThread, stamp);
                    context.reentrantCounts.put(currentThread, 1);

                    System.out.println(String.format("Read lock acquired for %s by %s (attempt: %dms/%dms)", lockable,
                            currentThread, timeSpent, totalTimeout));
                    return true;
                }

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
                timeSpent));
        return false;
    }

    /**
     * 自适应等待时间调整
     */
    private final Random random = new Random(); // 随机数生成器

    private long adjustWaitTime(long currentWait) {
        // 1. 更新和获取当前系统争用程度
        updateContention();
        int contention = currentContention.get();

        // 2. 计算基础等待时间（指数退避）
        long baseWait = calculateBaseWait(currentWait, contention);

        // 3. 应用随机抖动
        long finalWait = applyJitter(baseWait);

        // 4. 确保在合理范围内
        return Math.min(Math.max(finalWait, MIN_WAIT_TIME), MAX_WAIT_TIME);
    }

    /**
     * 计算基础等待时间，考虑当前争用程度
     */
    private long calculateBaseWait(long currentWait, int contention) {
        // 基础指数退避
        double multiplier = Math.pow(BACKOFF_MULTIPLIER, Math.min(contention, 5));
        long baseWait = (long) (currentWait * multiplier);

        // 根据争用程度调整
        if (contention > 10) {
            // 高争用情况下，增加随机性以避免惊群
            baseWait = (long) (baseWait * (1.0 + random.nextDouble() * 0.5));
        }
        else if (contention < 3) {
            // 低争用情况下，适当减少等待时间
            baseWait = (long) (baseWait * 0.8);
        }

        return baseWait;
    }

    /**
     * 应用随机抖动避免惊群效应
     */
    private long applyJitter(long baseWait) {
        double jitter = 1.0 + (random.nextDouble() * 2 - 1) * JITTER_FACTOR;
        return (long) (baseWait * jitter);
    }

    /**
     * 更新系统争用统计
     */
    private void updateContention() {
        long now = System.nanoTime();
        // 定期重置争用计数
        if (now - lastContentionReset > CONTENTION_RESET_INTERVAL) {
            synchronized (this) {
                if (now - lastContentionReset > CONTENTION_RESET_INTERVAL) {
                    currentContention.set(0);
                    lastContentionReset = now;
                }
            }
        }
        currentContention.incrementAndGet();
    }

    /**
     * 释放锁，支持重入计数
     */
    public void releaseLock(String lockable, boolean isWrite) {
        String currentThread = Thread.currentThread().getName();

        WeakReference<LockContext> contextRef = lockMap.get(lockable);
        if (contextRef == null || contextRef.get() == null) {
            return;
        }

        LockContext context = contextRef.get();
        Long stamp = context.threadStamps.get(currentThread);

        if (stamp == null) {
            System.out.println(String.format("Warning: %s attempting to release an unowned lock", currentThread));
            return;
        }

        // 处理重入
        int count = context.reentrantCounts.compute(currentThread, (k, v) -> v == null ? 0 : v - 1);
        if (count > 0) {
            // 还有重入的锁，不实际释放
            return;
        }

        // 完全释放锁
        try {
            if (isWrite) {
                context.lock.unlockWrite(stamp);
            }
            else {
                context.lock.unlock(stamp);
            }

            // 清理线程相关的记录
            context.threadStamps.remove(currentThread);
            context.reentrantCounts.remove(currentThread);

            System.out.println(String.format("Lock released for %s by %s", lockable, currentThread));

        }
        catch (Exception e) {
            System.out.println(String.format("Error releasing lock for %s: %s", lockable, e.getMessage()));
        }

        // 如果没有任何线程持有锁了，可以清理context
        if (context.threadStamps.isEmpty()) {
            lockMap.remove(lockable, contextRef);
        }
    }

}