package com.spring.mvcframework.servlet.v3.di.beans.config;


import lombok.Data;

@Data
public class MyBeanDefinition {

    public boolean isLazyInit() {
        return false;
    }

    public boolean isSingleton(){return true;}

    //beanName
    private String factoryBeanName;

    //原生类的全类名
    private String beanClassName;

}
