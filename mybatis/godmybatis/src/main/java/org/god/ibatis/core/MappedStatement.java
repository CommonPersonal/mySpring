package org.god.ibatis.core;

/**
 * 一个普通的POJO类，封装了一个标签。
 * 一个MappedStatement对象对应一个SQL标签
 * 一个SQL中的所以信息封装到MappedStatement对象中
 * 面向对象的思想
 * @author 李睿炜
 * @since 1.0
 * @version 1.0
 */
public class MappedStatement {
    /**
     * sql语句
     */
    private String sql;
    /**
     * 要封装的结果集类型，有时候resultType会为null
     * 比如：insert，delete update 语句的时候
     */
    private String resultType;

    @Override
    public String toString() {
        return "MappedStatement{" +
                "resultType='" + resultType + '\'' +
                ", sql='" + sql + '\'' +
                '}';
    }

    public MappedStatement(String resultType, String sql) {
        this.resultType = resultType;
        this.sql = sql;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public MappedStatement(){

    }
}
