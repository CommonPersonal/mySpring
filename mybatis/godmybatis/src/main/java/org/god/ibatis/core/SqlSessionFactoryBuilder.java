package org.god.ibatis.core;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.god.ibatis.utils.Resources;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SqlSessionFactory构造器对象
 * 通过SqlSessionFactoryBuilder的build方法来解析
 * godbatis-config.xml文件，然后创造salSessionFactory对象
 * @author 李睿炜
 * @since 1.0
 * @version 1.0
 */
public class SqlSessionFactoryBuilder {
    /**
     * 无参构造方法
     */
    public SqlSessionFactoryBuilder(){}

    /**
     * 解析godbatis-config.xml文件，来构造SqlSessionFactory对象
     * @param in 指向godbatis-config.xml文件的一个输入流
     * @return SqlSessionFactory对象
     */
    public SqlSessionFactory build(InputStream in) {
        SqlSessionFactory factory = null;
        try {
            // 解析godbatis-config.xml文件
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(in);
            Element environments = (Element) document.selectSingleNode("/configuration/environments");
            String defaultId = environments.attributeValue("default");
            Element environment = (Element) document.selectSingleNode("/configuration/environments/environment[@id='" + defaultId + "']");
            Element transactionElt = environment.element("transactionManager");
            Element dataSourceElt = environment.element("dataSource");
            List<String> sqlMappedXmlPathList = new ArrayList<>();
            List<Node> nodes = document.selectNodes("//mapper");
            nodes.forEach(node -> {
                Element mapper = (Element) node;
                String resource = mapper.attributeValue("resource");
                sqlMappedXmlPathList.add(resource);
            });

            // 获取数据库对象
            DataSource dataSource = getDataSource(dataSourceElt);
            // 获取事务管理器
            Transaction transaction = getTransaction(transactionElt,dataSource);
            // 获取mappedStatements
            Map<String,MappedStatement> mappedStatements = getMappedStatements(sqlMappedXmlPathList);
            // 构建SqlSessionFactory对象
            factory = new SqlSessionFactory(mappedStatements,transaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return factory;
    }

    /**
     * 解析所有的SqlMapped.xml文件，然后构建mapper集合
     * @return
     */
    private Map<String, MappedStatement> getMappedStatements(List<String> sqlMapperXmlPathList) {
        HashMap<String, MappedStatement> mapperStatements = new HashMap<>();
        sqlMapperXmlPathList.forEach(sqlMapperXmlPath -> {
            try {
                SAXReader reader = new SAXReader();
                Document document = reader.read(Resources.getResourceAsStream(sqlMapperXmlPath));
                Element mapper = (Element) document.selectSingleNode("mapper");
                String namespace = mapper.attributeValue("namespace");
                List<Element> elements = mapper.elements();
                elements.forEach(element -> {
                    String id = element.attributeValue("id");
                    // 这里进行了id和namespace的拼接最后合成splId
                    String sqlId = namespace + "."+ id;

                    String resultType = element.attributeValue("resultType");
                    String sql = element.getTextTrim();
                    MappedStatement mappedStatement = new MappedStatement(resultType, sql);

                    mapperStatements.put(sqlId,mappedStatement);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        return mapperStatements;
    }

    /**
     * 获取事务管理器
     * @param transactionElt 事务管理器标签对象
     * @param dataSource 数据源对象
     * @return
     */
    private Transaction getTransaction(Element transactionElt, DataSource dataSource){
        Transaction transaction = null;
        String type = transactionElt.attributeValue("type").trim().toUpperCase();
        if (Const.JDBC_TRANSACTION.equals(type)) {
            // 默认开启了事务，将来需要自己关闭
            transaction = new JDBCTransaction(false,dataSource);
        }
        if (Const.MANAGED_TRANSACTION.equals(type)) {
            transaction = new ManagedTransaction();
        }
        return transaction;
    }

    /**
     * 获取数据库对象
     * @param dataSourceElt 数据源标签对象
     * @return
     */
    private DataSource getDataSource(Element dataSourceElt) {
        Map<String,String> map = new HashMap<>();
        List<Element> propertyElts = dataSourceElt.elements("property");
        propertyElts.forEach(propertyElt ->{
            String name = propertyElt.attributeValue("name");
            String value = propertyElt.attributeValue("value");
            map.put(name,value);
        });
        DataSource dataSource = null;
        String type = dataSourceElt.attributeValue("type").trim().toUpperCase();
        if (Const.UN_POOLED_DATASOURCE.equals(type)) {
            dataSource = new UnPooledDataSource(map.get("driver"),map.get("url"),map.get("username"),map.get("password"));
        }
        if (Const.JNDI_DATASOURCE.equals(type)) {
            dataSource = new JNDIDataSource();
        }
        if (Const.POOLED_DATASOURCE.equals(type)) {
            dataSource = new PoolDataSource();
        }
        return dataSource;
    }
}
