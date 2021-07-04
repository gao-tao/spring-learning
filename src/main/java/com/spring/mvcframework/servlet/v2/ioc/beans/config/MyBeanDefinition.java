package com.spring.mvcframework.servlet.v2.ioc.beans.config;

import lombok.Getter;
import lombok.Setter;

public class MyBeanDefinition {

    public boolean isLazyInit() {
        return false;
    }

    //beanName
    @Getter
    @Setter
    private String factoryBeanName;

    //原生类的全类名
    @Getter
    @Setter
    private String beanClassName;

}
