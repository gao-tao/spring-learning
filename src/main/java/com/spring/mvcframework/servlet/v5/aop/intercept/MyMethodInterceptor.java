package com.spring.mvcframework.servlet.v5.aop.intercept;

public interface MyMethodInterceptor {

    Object invoke(MyMethodInvocation invocation) throws Throwable;
}
