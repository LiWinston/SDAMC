package org.sdamc.Utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class LockManager {

    private static LockManager instance;

    // Key: lockable, Value: Lock object
    private final ConcurrentMap<String, Lock> lockMap;

    // Singleton instance creation
    public static synchronized LockManager getInstance() {
        if (instance == null) {
            instance = new LockManager();
        }
        return instance;
    }

    private LockManager() {
        lockMap = new ConcurrentHashMap<>();
    }

    /**
     * 指数退避锁获取 Attempts to acquire a lock for a specific lockable resource. If the lock is
     * already held, this method will wait for a specific timeout.
     * @param lockable the resource to lock
     * @param owner the owner of the lock (for tracking purposes)
     * @param totalTimeout the maximum time to wait for the lock (in milliseconds)
     * @return true if the lock was acquired, false if the timeout occurred
     */
    public boolean acquireLock(String lockable, String owner, long totalTimeout) {
        Lock lock = lockMap.computeIfAbsent(lockable, k -> new ReentrantLock());

        long waitTime = 100; // 初始等待时间 (100ms)
        long timeSpent = 0; // 已花费的时间

        while (timeSpent < totalTimeout) {
            try {
                // 计算剩余可用的等待时间
                long remainingTime = totalTimeout - timeSpent;
                long currentTimeout = Math.min(waitTime, remainingTime);

                // 尝试在 currentTimeout 时间内获取锁
                if (lock.tryLock(currentTimeout, TimeUnit.MILLISECONDS)) {
                    // 锁获取成功
                    System.out.println("Lock acquired by " + owner + " for " + lockable + " after " + timeSpent
                            + "ms, as a result of retryTimes: "
                            + (int) (Math.log((double) timeSpent / 100) / Math.log(2)));
                    return true;
                }

                // 更新已花费的时间
                timeSpent += currentTimeout;

                // 倍增等待时间
                waitTime *= 2;

            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 恢复中断状态
                return false;
            }
        }

        // 超时，未能获取锁
        System.out.println("Failed to acquire lock for " + lockable + " within total timeout");
        return false;
    }

    /**
     * Releases the lock held by the owner on the specified lockable resource. If no lock
     * exists, this method does nothing.
     * @param lockable the resource to unlock
     * @param owner the owner of the lock (for tracking purposes)
     */
    public void releaseLock(String lockable, String owner) {
        Lock lock = lockMap.get(lockable);
        if (lock != null && lock.tryLock()) {
            try {
                // Ensure lock is held by current thread before releasing
                lock.unlock();
                System.out.println("Lock released by " + owner + " for " + lockable);
                lockMap.remove(lockable);
            }
            finally {
                // Always ensure that the lock is released properly
                lock.unlock();
            }
        }
    }

}
