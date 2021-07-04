package com.spring.mvcframework.servlet.v5.aop.beans;

import lombok.Getter;

public class MyBeanWrapper {

    @Getter
    private Object wapperedInstance;

    @Getter
    private Class<?> wrappedClass;

    public MyBeanWrapper(Object instance) {
        this.wapperedInstance = instance;
        this.wrappedClass = instance.getClass();
    }
}
