package org.godbatis.test;

import org.god.ibatis.core.SqlSession;
import org.god.ibatis.core.SqlSessionFactory;
import org.god.ibatis.core.SqlSessionFactoryBuilder;
import org.god.ibatis.utils.Resources;
import org.god.ibatis.pojo.User;
import org.junit.Test;

public class GodBatisTest {

    @Test
    public void testSelectOne(){
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("godbatis-config.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 执行sql语句
        Object obj = sqlSession.selectOne("user.selectById", "1111");
        System.out.println(obj.toString());
        sqlSession.close();
    }

    @Test
    public void testSqlSessionFactory(){
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("godbatis-config.xml"));
        System.out.println(sqlSessionFactory);
    }

    @Test
    public void testInsertUser(){
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("godbatis-config.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 执行insert语句
        User user = new User("1111", "zhangsan", "20");
        int count = sqlSession.insert("user.insertUser", user);
        System.out.println(count);
        sqlSession.commit();
        sqlSession.close();

    }
}
