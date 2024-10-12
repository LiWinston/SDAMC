package org.sdamc.Transaction;

import jakarta.servlet.http.HttpServletResponse;
import org.sdamc.DTO.Result;
import org.sdamc.Utils.IOWrapper;

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

        boolean success = false; // 标志事务是否成功
        // 开始事务
        TransactionManager.beginTransaction(annotation.isolationLevel());

        try {
            if (annotation.lockingStrategy() == LockingStrategy.PESSIMISTIC) {
                // 使用悲观锁
                Lock lock = TransactionManager.getPessimisticLock(method.getName());
                lock.lock();
                System.out.println("Pessimistic lock acquired for method: " + Thread.currentThread().getName() + " "
                        + method.getName());
                try {
                    Object result = method.invoke(target, args); // 执行方法
                    success = true; // 只有在这里事务才被标记为成功
                    return result;
                }
                finally {
                    lock.unlock();
                    System.out.println("Pessimistic lock released for method: " + Thread.currentThread().getName() + " "
                            + method.getName());
                }
            }
            else {
                // 使用乐观锁
                System.out.println("Executing method with optimistic locking: " + method.getName());
                Object result = method.invoke(target, args); // 执行方法
                success = true; // 标记事务成功
                return result;
            }
        }
        catch (Exception e) {
            // 发生异常回滚事务
            TransactionManager.rollbackTransaction();
            System.err.println("Transaction rolled back due to exception in method: " + Thread.currentThread().getName()
                    + " " + method.getName());
            e.printStackTrace();
            for (Object arg : args) {
                if (arg instanceof HttpServletResponse) {
                    IOWrapper.writeValue((HttpServletResponse) arg, Result.error(e.getMessage()),
                            HttpServletResponse.SC_CONFLICT);
                    break;
                }
            }
            throw e;
        }
        finally {
            if (success) {
                TransactionManager.commitTransaction(); // 只有事务成功时才提交
                System.out.println("Transaction committed for method: " + Thread.currentThread().getName() + " "
                        + method.getName());
            }
        }
    }

}