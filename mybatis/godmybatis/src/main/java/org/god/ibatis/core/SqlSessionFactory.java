package org.god.ibatis.core;

import java.util.Map;

/**
 * salSessionFactory对象：
 *         一个数据库一般对应一个SqlSessionFactory对象
 *         通过它可以获取多个SqlSession对象
 * @author 李睿炜
 * @version 1.0
 * @since 1.0
 */
public class SqlSessionFactory {

    /**
     * 事务管理器属性
     * 事务管理器是可以灵活切换的
     * SqlSessionFactory类中的事务管理器应该是面向接口编程的
     * SqlSessionFactory类中应该有一个事务管理器接口
     */
    private Transaction transaction;


    public Map<String, MappedStatement> getMappedStatements() {
        return mappedStatements;
    }

    public void setMappedStatements(Map<String, MappedStatement> mappedStatements) {
        this.mappedStatements = mappedStatements;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public SqlSessionFactory() {
    }

    /**
     * 获取sql会话对象
     * @return
     */
    public SqlSession openSession(){
        // 开启会话的前提是开启连接（连接打开了）
        transaction.openConnection();
        // 创建SqlSession对象
        SqlSession sqlSession = new SqlSession(this);
        return sqlSession;
    }

    /**
     * 存放sql语句的Map集合
     * key的值是sqlid
     * value值是对应的SQL标签信息对象
     */
    private Map<String,MappedStatement> mappedStatements;

    public SqlSessionFactory(Map<String, MappedStatement> mappedStatements, Transaction transaction) {
        this.mappedStatements = mappedStatements;
        this.transaction = transaction;
    }

    @Override
    public String toString() {
        return "SqlSessionFactory{" +
                "mappedStatements=" + mappedStatements +
                ", transaction=" + transaction +
                '}';
    }
}
