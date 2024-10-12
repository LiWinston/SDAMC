package org.sdamc.Transaction;

import org.sdamc.UnitofWork;
import org.sdamc.Utils.DatabaseUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {

    IsolationLevel isolationLevel() default IsolationLevel.READ_COMMITTED;

    LockingStrategy lockingStrategy() default LockingStrategy.OPTIMISTIC;

}

class TransactionManager {

    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    private static final ConcurrentHashMap<String, Lock> pessimisticLocks = new ConcurrentHashMap<>();

    public static void beginTransaction(IsolationLevel level) throws SQLException {
        Connection conn = DatabaseUtil.getConnection();
//        conn.setAutoCommit(false); //在DBUtil中统一设置了
        setIsolationLevel(conn, level);
        connectionHolder.set(conn);
        UnitofWork.newCurrent(); // 开启新的UnitofWork
    }

    public static void commitTransaction() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            UnitofWork.getCurrent().commit(); // 提交UnitofWork
            conn.commit();
            conn.setAutoCommit(true);
            connectionHolder.remove();
        }
    }

    public static void rollbackTransaction() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            conn.rollback();
            conn.setAutoCommit(true);
            connectionHolder.remove();
        }
    }

    private static void setIsolationLevel(Connection conn, IsolationLevel level) throws SQLException {
        switch (level) {
            case READ_UNCOMMITTED:
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                break;
            case READ_COMMITTED:
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                break;
            case REPEATABLE_READ:
                conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                break;
            case SERIALIZABLE:
                conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                break;
        }
    }

    public static Lock getPessimisticLock(String key) {
        return pessimisticLocks.computeIfAbsent(key, k -> new ReentrantLock());
    }

}
