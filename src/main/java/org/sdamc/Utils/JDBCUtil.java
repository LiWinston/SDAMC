package org.sdamc.Utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * JDBC工具类： 1、维护一个连接池对象、维护了一个线程绑定变量的ThreadLocal对象 2、对外提供在ThreadLocal中获取连接的方法
 * 3、对外提供回收连接的方法，回收过程中，将要回收的连接从ThreadLocal中移除
 */
public class JDBCUtil {

    // 创建连接池引用
    private static DataSource dataSource;

    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    // 在项目启动时，即创建连接池对象，赋值给dataSource
    static {
        try {
            Properties properties = new Properties();
            InputStream inputStream = JDBCUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");
            properties.load(inputStream);

            dataSource = DruidDataSourceFactory.createDataSource(properties);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 对外提供在连接池中获取连接的方法
    public static Connection getConnection() throws SQLException {
        return DatabaseUtil.getConnection();
    }

    // 对外提供回收连接的方法
    public static void release() {
        try {
            Connection connection = threadLocal.get();
            if (connection != null) {
                // 从threadLocal中移除当前已经存储的Connection对象
                threadLocal.remove();
                // 如果开启了事务的手动提交，操作完毕后，归还给连接池之前，要将事务的自动提交改为true
                connection.setAutoCommit(true);
                // 将Connection对象归还给连接池
                connection.close();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                // 使用连接池时，close操作会将连接返回池中而不是真正关闭
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
