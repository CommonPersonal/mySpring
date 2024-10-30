package org.god.ibatis.core;

import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Logger;

/**
 * 数据源的实现类UNPOOLED（重点实现）
 * 不使用连接池，每次都新建Connection对象
 *
 * @author 李睿炜
 * @version 1.0
 * @since 1.0
 */
public class UnPooledDataSource implements javax.sql.DataSource {
    private String url;
    private String username;
    private String password;

    /**
     * 创建一个数据源对象
     *
     * @param driver
     * @param password
     * @param url
     * @param username
     */
    public UnPooledDataSource(String driver, String url, String password, String username) {
        try {
            // 直接进行驱动注册
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.url = url;
        this.password = password;
        this.username = username;
    }


    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url,password,username);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }


    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
