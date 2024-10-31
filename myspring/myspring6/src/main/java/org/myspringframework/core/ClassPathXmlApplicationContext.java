package org.myspringframework.core;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lrw
 * @version 1.0
 * @className ApplicationContext
 * @since 1.0
 */
public class ClassPathXmlApplicationContext implements ApplicationContext{

    private Map<String,Object> singletonObjects = new HashMap<>();

    /**
     * 解析mySpring的配置文件，然后初始化所有的bean对象
     * @param configLocation
     */
    public ClassPathXmlApplicationContext(String configLocation){

        try {
            // 解析myspring配置文件。然后实例化Bean，将Bean存放到singletonObjects集合中
            // 这是dom4j解析文件的操作对象
            SAXReader saxReader = new SAXReader();
            // 获取输入流用于读文件
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(configLocation);
            Document document = saxReader.read(is);
            // 获取所有的bean标签
            List<Node> nodes = document.selectNodes("//bean");
            nodes.forEach(node -> {
                try {
                    // 由于Element中方法更加丰富，所以进行转型
                    Element element = (Element) node;
                    String id = element.attributeValue("id");
                    String className = element.attributeValue("class");
                    // 通过反射机制获取对象，将其放到Map集合中，进行曝光处理
                    Class<?> aClass = Class.forName(className);
                    // 获取无参数构造方法
                    Constructor<?> declaredConstructor = aClass.getDeclaredConstructor();
                    // 调用无参数构造方法来实例化bean
                    Object bean = declaredConstructor.newInstance();
                    // 将bean曝光加入map中
                    singletonObjects.put(id,bean);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // 再次将所有的node标签遍历一次，这是是为了给对象赋值
            nodes.forEach(node ->{
                try{
                    Element beanElt = (Element) node;
                    // 获取id
                    String id = beanElt.attributeValue("id");
                    // 获取className
                    String className = beanElt.attributeValue("class");
                    // 获取类
                    Class<?> aClass = Class.forName(className);
                    // 获取该bean下的所有property标签
                    List<Element> propertys = beanElt.elements("property");
                    // 遍历所有的属性
                    propertys.forEach(property -> {
                        try{
                            // 获取属性名
                            String propertyName = property.attributeValue("name");
                            // 获取属性类型的对象
                            Field field = aClass.getDeclaredField(propertyName);
                            // 通过属性名获取set方法名
                            String setMethodName = "set" + propertyName.toUpperCase().charAt(0) + propertyName.substring(1);
                            // 获取set方法
                            Method setMethod = aClass.getDeclaredMethod(setMethodName,field.getType());
                            // 调用set方法
                            // 获取具体的值
                            String value = property.attributeValue("value");
                            Object actualValue = null; // 真值
                            String ref = property.attributeValue("ref");
                            if (value != null) {
                                // 调那个对象传什么值
                                // 获取属性名类名
                                String simpleName = field.getType().getSimpleName();
                                switch (simpleName) {
                                    case "byte","Byte"-> actualValue = Byte.parseByte(value);
                                    case "short", "Short" -> actualValue = Short.parseShort(value);
                                    case "int" , "Integer"-> actualValue = Integer.parseInt(value);
                                    case "long" ,"Long"-> actualValue = Long.parseLong(value);
                                    case "float" ,"Float"-> actualValue = Float.parseFloat(value);
                                    case "double" ,"Double"-> actualValue = Double.parseDouble(value);
                                    case "boolean" ,"Boolean"-> actualValue = Boolean.parseBoolean(value);
                                    case "char" , "Character"-> actualValue = value.charAt(0);
                                    case "String" -> actualValue = value;
                                }
                                // set方法传值
                                setMethod.invoke(singletonObjects.get(id),actualValue);

                            }
                            if (ref != null) {
                                // 此时说明这个值是非简单类型
                                // set方法传值
                                setMethod.invoke(singletonObjects.get(id),singletonObjects.get(ref));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }

            });
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getBean(String beanName) {
        return singletonObjects.get(beanName);
    }
}
