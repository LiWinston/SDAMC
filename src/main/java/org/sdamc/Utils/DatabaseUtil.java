package org.sdamc.Utils;

import com.alibaba.druid.pool.DruidDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/*
* This is a utility class that provides an ORM interface for interacting with the database,
* Aimed for abstract some of the boilerplate code.
* */
public class DatabaseUtil {

    private static final DruidDataSource dataSource;
    
    static {
        dataSource = new DruidDataSource();
        // 设置数据库连接参数
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:5432/postgres_sdamc");
        dataSource.setUsername("postgres.auurjplocrivtrknmvuj");
        dataSource.setPassword("JnWA9#Nzse-mQ3@");
        
        // 配置初始化大小、最小、最大
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(5);
        dataSource.setMaxActive(20);
        
        // 配置获取连接等待超时的时间
        dataSource.setMaxWait(60000);
        
        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        
        // 配置一个连接在池中最小生存的时间，单位是毫秒
        dataSource.setMinEvictableIdleTimeMillis(300000);
        
        // 配置检测连接是否有效
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        
        // 打开PSCache，并且指定每个连接上PSCache的大小
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
    }
    
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    // 关闭连接池
    public static void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}