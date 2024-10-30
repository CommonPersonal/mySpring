package org.god.ibatis.core;

import java.sql.Connection;

/**
 *Managed事务管理器,对这个类不实现
 * @author 李睿炜
 * @version 1.0
 * @since 1.0
 */
public class ManagedTransaction implements Transaction{
    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public void close() {

    }

    @Override
    public void openConnection() {

    }

    @Override
    public Connection getConnection() {
        return null;
    }
}
