package com.spring.mvcframework.servlet.v5.aop.aspect;

import java.lang.reflect.Method;

public abstract class MyAbstractAspectJAdvice implements MyAdvice {

    private Object aspect;

    private Method adviceMethod;

    public MyAbstractAspectJAdvice(Object aspect, Method adviceMethod) {
        this.aspect = aspect;
        this.adviceMethod = adviceMethod;
    }

    protected Object invokeAdviceMethod(MyJoinPoint joinPoint, Object returnValue, Throwable ex) throws Throwable {

        Class<?>[] parameterTypes = this.adviceMethod.getParameterTypes();
        if (null == parameterTypes || parameterTypes.length == 0) {
            return this.adviceMethod.invoke(aspect);
        } else {
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == MyJoinPoint.class) {
                    args[i] = joinPoint;
                } else if (parameterTypes[i] == Throwable.class) {
                    args[i] = ex;
                } else if (parameterTypes[i] == Object.class) {
                    args[i] = returnValue;
                }
            }
            return this.adviceMethod.invoke(aspect, args);
        }
    }
}
