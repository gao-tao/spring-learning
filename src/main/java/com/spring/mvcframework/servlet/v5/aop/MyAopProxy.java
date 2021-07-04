package com.spring.mvcframework.servlet.v5.aop;

public interface MyAopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
