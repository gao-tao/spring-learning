package com.spring.mvcframework.servlet.v5.aop.aspect;

import com.spring.mvcframework.servlet.v5.aop.intercept.MyMethodInterceptor;
import com.spring.mvcframework.servlet.v5.aop.intercept.MyMethodInvocation;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class MyAspectJAfterThrowingAdvice extends MyAbstractAspectJAdvice implements MyMethodInterceptor {

    @Setter
    private String throwName;

    public MyAspectJAfterThrowingAdvice(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    @Override
    public Object invoke(MyMethodInvocation mi) throws Throwable {

        try {
            return mi.proceed();
        }catch (Throwable ex){
            invokeAdviceMethod(mi,null,ex);
            throw ex;
        }
    }


}
