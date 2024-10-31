package org.myspringframework.core;
/**
 * MySpring框架应用上下文接口
 * @author lrw
 * @version 1.0
 * @className ApplicationContext
 * @since 1.0
 */

public interface ApplicationContext {

    /**
     * 根据bean的名称来获取bean对象
     * @param beanName mySpring配置文件中bean标签的id
     * @return 对应单例bean对象
     */
    Object getBean(String beanName);
}
