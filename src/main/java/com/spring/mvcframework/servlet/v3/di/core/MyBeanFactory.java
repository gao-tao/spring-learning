package com.spring.mvcframework.servlet.v3.di.core;

/**
 * 创建对象工厂的最顶层的接口
 */
public interface MyBeanFactory {

    //根据bean的名字，获取在IOC容器中得到bean实例
    Object getBean(String name);

    Object getBean(Class beanClass);
}
