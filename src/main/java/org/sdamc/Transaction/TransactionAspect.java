package org.sdamc.Transaction;

import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;

public class TransactionAspect {

    public static Object executeTransactional(Method method, Object[] args, Object target) throws Throwable {
        Transactional annotation = method.getAnnotation(Transactional.class);
        if (annotation == null) {
            return method.invoke(target, args); // 没有 @Transactional 注解，直接执行方法
        }

        // 开始事务，设置隔离级别
        TransactionManager.beginTransaction(annotation.isolationLevel());

        try {
            if (annotation.lockingStrategy() == LockingStrategy.PESSIMISTIC) {
                // 使用悲观锁
                Lock lock = TransactionManager.getPessimisticLock(method.getName());
                lock.lock();
                try {
                    return method.invoke(target, args); // 执行方法
                }
                finally {
                    lock.unlock();
                }
            }
            else {
                // 使用乐观锁
                return method.invoke(target, args); // 执行方法
            }
        }
        catch (Exception e) {
            // 发生异常回滚事务
            TransactionManager.rollbackTransaction();
            throw e;
        }
        finally {
            // 正常结束提交事务
            TransactionManager.commitTransaction();
        }
    }

}
