package com.spring.mvcframework.servlet.v5.aop;

import com.spring.mvcframework.servlet.v5.aop.intercept.MyMethodInvocation;
import com.spring.mvcframework.servlet.v5.aop.support.MyAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class MyJdkDynamicAopProxy implements MyAopProxy, InvocationHandler {

    private MyAdvisedSupport advised;

    public MyJdkDynamicAopProxy(MyAdvisedSupport advised) {
        this.advised = advised;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> chain = this.advised.getInterceptorAndDynamicInterceptionAdvice(method, this.advised.getTargetClass());
        MyMethodInvocation mi = new MyMethodInvocation(proxy, this.advised.getTarget(), args, method, this.advised.getTargetClass(), chain);
        return mi.proceed();
    }

    @Override
    public Object getProxy() {
        return getProxy(this.getClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, this.advised.getTargetClass().getInterfaces(), this);
    }
}
