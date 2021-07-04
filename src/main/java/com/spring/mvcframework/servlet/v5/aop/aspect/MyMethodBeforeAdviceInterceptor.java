package com.spring.mvcframework.servlet.v5.aop.aspect;

import com.spring.mvcframework.servlet.v5.aop.intercept.MyMethodInterceptor;
import com.spring.mvcframework.servlet.v5.aop.intercept.MyMethodInvocation;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class MyMethodBeforeAdviceInterceptor extends MyAbstractAspectJAdvice implements MyMethodInterceptor {

    private MyJoinPoint jp;

    public MyMethodBeforeAdviceInterceptor(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    public void before(Method method, Object[] arguments, Object aThis) throws Throwable {
        invokeAdviceMethod(this.jp, null, null);
    }

    @Override
    public Object invoke(MyMethodInvocation mi) throws Throwable {
        jp = mi;
        this.before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
