package com.socialmediaapp.Util;

import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.Getter;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class DBConnection {
    private static final String HOST_URL = System.getProperty("app.db.hostUrl", "jdbc:mysql://localhost:3306/");
    @Getter
    private static final String DATABASE_NAME = "socialmedia_db";

    private static final String USER = System.getProperty("app.db.user", "root");
    private static final String PASSWORD = System.getProperty("app.db.password", "root");

    private static DataSource buildDataSource(String url){
        if(url.startsWith("jdbc:mysql:")){
            MysqlDataSource mysqlDataSource = new MysqlDataSource();
            mysqlDataSource.setUrl(url);
            mysqlDataSource.setUser(USER);
            mysqlDataSource.setPassword(PASSWORD);
            return mysqlDataSource;
        }
        return new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                return DriverManager.getConnection(url, USER, PASSWORD);
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                return DriverManager.getConnection(url, username, password);
            }

            @Override
            public <T> T unwrap(Class<T> iface) throws SQLException { throw new SQLException("Not a wrapper"); }
            @Override
            public boolean isWrapperFor(Class<?> iface) { return false; }
            @Override
            public PrintWriter getLogWriter() { return null; }
            @Override
            public void setLogWriter(PrintWriter out) { }
            @Override
            public void setLoginTimeout(int seconds) { }
            @Override
            public int getLoginTimeout() { return 0; }
            @Override
            public Logger getParentLogger() throws SQLFeatureNotSupportedException { throw new SQLFeatureNotSupportedException(); }
        };
    }

    public static DataSource getBootstrapDataSource(){
        String bootstrapUrl = System.getProperty("app.db.bootstrapUrl", HOST_URL);
        return buildDataSource(bootstrapUrl);
    }
    public static DataSource getAppDataSource(){
        String appUrl = System.getProperty("app.db.url", HOST_URL + DATABASE_NAME);
        return buildDataSource(appUrl);
    }
}
