package com.spring.mvcframework.servlet.v5.aop.intercept;

import com.spring.mvcframework.servlet.v5.aop.aspect.MyJoinPoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyMethodInvocation implements MyJoinPoint {

    protected final Object proxy;

    protected final Object target;

    protected final Method method;

    protected Object[] arguments = new Object[0];

    private final Class<?> targetClass;

    private Map<String, Object> userAttributes = new HashMap<String, Object>();

    protected final List<?> interceptorsAndDynamicMethodMatchers;

    private int currentInterceptorIndex = -1;

    public MyMethodInvocation(Object proxy, Object target, Object[] arguments, Method method, Class<?> targetClass, List<?> interceptorsAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.target = target;
        this.method = method;
        this.targetClass = targetClass;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    public Object proceed() throws Throwable {
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            return this.method.invoke(target, this.arguments);
        }
        Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);

        if (interceptorOrInterceptionAdvice instanceof MyMethodInterceptor) {
            MyMethodInterceptor mi = (MyMethodInterceptor) interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        } else {
            return proceed();
        }
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        this.userAttributes.put(key, value);
    }

    @Override
    public Object getUserAttribute(String key) {
        return this.userAttributes.get(key);
    }
}
