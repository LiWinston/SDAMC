package org.sdamc.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
* This is a utility class that provides an ORM interface for interacting with the database,
* Aimed for abstract some of the boilerplate code.
* */
public class DatabaseUtil {

    private static final String JDBC_DRIVER = "org.postgresql.Driver";

    // 新的 JDBC URL
    private static final String JDBC_URL = "jdbc:postgresql://ep-icy-sea-a7vt9oiq.ap-southeast-2.aws.neon.tech/postgres1?user=postgres1_owner&password=nt4ug9SwXZUr&sslmode=require";

    // 新的数据库用户名
    private static final String JDBC_USER = "postgres1_owner";

    // 新的数据库密码
    private static final String JDBC_PASSWORD = "nt4ug9SwXZUr";

    private static ThreadLocal<Connection> connection = ThreadLocal.withInitial(() -> null);

    ;

    DatabaseUtil() throws ClassNotFoundException {
        // empty constructor
        Class.forName(JDBC_DRIVER);
    }

    // For test
    public static void connectDatabase(String url, String user, String password) {
        try {
            Class.forName(JDBC_DRIVER);
            connection.set(DriverManager.getConnection(url, user, password));
        }
        catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to create database connection", e);
        }
    }

    public static void connectDatabase() {
        try {
            Class.forName(JDBC_DRIVER);
            connection.set(DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD));
        }
        catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to create database connection", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return connection.get();
    }

}