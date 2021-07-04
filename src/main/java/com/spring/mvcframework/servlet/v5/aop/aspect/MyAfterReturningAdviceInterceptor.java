package com.spring.mvcframework.servlet.v5.aop.aspect;

import com.spring.mvcframework.servlet.v5.aop.intercept.MyMethodInterceptor;
import com.spring.mvcframework.servlet.v5.aop.intercept.MyMethodInvocation;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class MyAfterReturningAdviceInterceptor extends MyAbstractAspectJAdvice implements MyMethodInterceptor {

    private MyJoinPoint jp;

    public MyAfterReturningAdviceInterceptor(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    private void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        this.invokeAdviceMethod(this.jp, returnValue, null);
    }


    @Override
    public Object invoke(MyMethodInvocation mi) throws Throwable {
        jp = mi;
        Object retVal = mi.proceed();
        this.afterReturning(retVal, mi.getMethod(), mi.getArguments(),mi.getThis());
        return retVal;
    }
}
