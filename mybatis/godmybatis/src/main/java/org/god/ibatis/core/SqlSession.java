package org.god.ibatis.core;

import java.lang.reflect.Method;
import java.sql.*;

/**
 * 专门用于执行sql语句的会话对象
 * @author 李睿炜
 * @version 1.0
 * @since 1.0
 */
public class SqlSession {
    private SqlSessionFactory factory;
    public SqlSession(SqlSessionFactory sqlSessionFactory) {
        this.factory = sqlSessionFactory;
    }

    /**
     * 执行insert方法，向数据库表中插入数据
     * @param sqlId sql语句的id
     * @param pojo 插入的数据
     * @return
     */
    public int insert(String sqlId,Object pojo){
        int count = 0;
        try {
            // JDBC代码，执行insert语句，完成插入操作
            Connection connection = factory.getTransaction().getConnection();
            String godbatisSql = factory.getMappedStatements().get(sqlId).getSql();
            String sql = godbatisSql.replaceAll("#\\{[a-zA-Z0-9_$]*}","?");
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            // 给？传值
            // 获取问号的数量和对应的属性名
            int index = 1;
            int fromIndex = 0;
            while(true) {
                int jingIndex = godbatisSql.indexOf("#",fromIndex);
                if (jingIndex < 0) {
                    break;
                }
                int youkouhaoIndex = godbatisSql.indexOf("}",fromIndex);
                String propertyName = godbatisSql.substring(jingIndex + 2, youkouhaoIndex).trim();
                fromIndex = youkouhaoIndex + 1;
                // 我们拥有属性id，可以拼接他的get方法名来获取属性值
                String getMethodName = "get" + propertyName.toUpperCase().charAt(0) + propertyName.substring(1);
                Method getMethod = pojo.getClass().getDeclaredMethod(getMethodName);
                Object propertyValue = getMethod.invoke(pojo);
                preparedStatement.setString(index,propertyValue.toString());
                index++;
            }
            // 执行sql语句
            count = preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 执行查询语句，返回一个对象，该方法只适合返回一条sql语句
     * @param sqlId
     * @param param
     * @return
     */
    public Object selectOne(String sqlId, Object param){
        Object obj = null;
        try {
            Connection connection = factory.getTransaction().getConnection();
            MappedStatement mappedStatement = factory.getMappedStatements().get(sqlId);
            // 这是一条DQL查询语句
            String godbatisSql = mappedStatement.getSql();
            String sql = godbatisSql.replaceAll("#\\{[a-zA-Z0-9_$]*}", "?");
            PreparedStatement ps = connection.prepareStatement(sql);
            // 给占位符传值
            ps.setString(1,param.toString());
            // 查询结果集
            ResultSet rs = ps.executeQuery();
            // 封装的结果类型
            String resultType = mappedStatement.getResultType();
            // 从结果集中取数据封装对象
            if (rs.next()) {
                //获取resultType的class
                Class<?> resultTypeClass = Class.forName(resultType);
                // 调用无参构造创建对象
                obj = resultTypeClass.newInstance();
                // 给User类的id，name，age属性赋值
                // 获取原数据结果集
                ResultSetMetaData rsmd = rs.getMetaData();
                // 获取列数
                int columnCount = rsmd.getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    String propertyName = rsmd.getColumnName(i + 1);
                    // 拼接方法
                    String setMethodName = "set" + propertyName.toUpperCase().charAt(0) + propertyName.substring(1);
                    // 获取set方法
                    Method setMethod = resultTypeClass.getDeclaredMethod(setMethodName, String.class);
                    // 调用set方法给对象obj传值
                    setMethod.invoke(obj,rs.getString(propertyName));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    /**
     * 提交事务
     */
    public void commit(){
        factory.getTransaction().commit();
    }

    /**
     * 回滚事务
     */
    public void rollback(){
        factory.getTransaction().rollback();
    }

    /**
     * 关闭事务
     */
    public void close(){
        factory.getTransaction().close();
    }
}
