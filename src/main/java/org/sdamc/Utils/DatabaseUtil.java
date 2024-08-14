package org.sdamc.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/*
* This is a utility class that provides an ORM interface for interacting with the database,
* Aimed for abstract some of the boilerplate code.
* */
public class DatabaseUtil {
    DatabaseUtil() throws ClassNotFoundException {
        //empty constructor
        Class.forName(JDBC_DRIVER);
    }
    private static final String JDBC_DRIVER = "org.postgresql.Driver";

    // 新的 JDBC URL
    private static final String JDBC_URL = "jdbc:postgresql://ep-icy-sea-a7vt9oiq.ap-southeast-2.aws.neon.tech/postgres1?user=postgres1_owner&password=nt4ug9SwXZUr&sslmode=require";

    // 新的数据库用户名
    private static final String JDBC_USER = "postgres1_owner";

    // 新的数据库密码
    private static final String JDBC_PASSWORD = "nt4ug9SwXZUr";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

}