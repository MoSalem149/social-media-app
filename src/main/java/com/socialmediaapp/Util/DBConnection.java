package com.socialmediaapp.Util;

import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.Getter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    //Database Host Url to use it for creating database automatically without run queries in Mysql
    // I changed Port Number From 3303 to 3306 to work on my device so don't forget to apply yours!
    private static final String HOST_URL = "jdbc:mysql://localhost:3306/";
    @Getter
    private static final String DATABASE_NAME = "socialmedia_db";

    // Don't Forget To Change Database Username & Password to your Username & Database
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static DataSource getBootstrapDataSource(){
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUrl(HOST_URL);
        mysqlDataSource.setUser(USER);
        mysqlDataSource.setPassword(PASSWORD);
        return mysqlDataSource;
    }
    public static DataSource getAppDataSource(){
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUrl(HOST_URL+DATABASE_NAME);
        mysqlDataSource.setUser(USER);
        mysqlDataSource.setPassword(PASSWORD);
        return mysqlDataSource;
    }
}
