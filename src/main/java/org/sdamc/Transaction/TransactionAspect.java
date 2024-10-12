package org.sdamc.Transaction;

import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;

public class TransactionAspect {

    public static Object executeTransactional(Method method, Object[] args, Object target) throws Throwable {
        Transactional annotation = method.getAnnotation(Transactional.class);
        if (annotation == null) {
            return method.invoke(target, args); // 没有 @Transactional 注解，直接执行方法
        }

        System.out.println("Starting transactional method: " + method.getName());
        System.out.println("Isolation Level: " + annotation.isolationLevel());
        System.out.println("Locking Strategy: " + annotation.lockingStrategy());

        // 开始事务
        TransactionManager.beginTransaction(annotation.isolationLevel());

        try {
            if (annotation.lockingStrategy() == LockingStrategy.PESSIMISTIC) {
                // 使用悲观锁
                Lock lock = TransactionManager.getPessimisticLock(method.getName());
                lock.lock();
                System.out.println("Pessimistic lock acquired for method: " + method.getName());
                try {
                    return method.invoke(target, args); // 执行方法
                }
                finally {
                    lock.unlock();
                    System.out.println("Pessimistic lock released for method: " + method.getName());
                }
            }
            else {
                // 使用乐观锁
                System.out.println("Executing method with optimistic locking: " + method.getName());
                return method.invoke(target, args); // 执行方法
            }
        }
        catch (Exception e) {
            // 发生异常回滚事务
            TransactionManager.rollbackTransaction();
            System.err.println("Transaction rolled back due to exception in method: " + method.getName());
            e.printStackTrace();
            throw e;
        }
        finally {
            // 正常结束提交事务
            TransactionManager.commitTransaction();
            System.out.println("Transaction committed for method: " + method.getName());
        }
    }

}