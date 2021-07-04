package com.spring.mvcframework.servlet.v5.aop;

import com.spring.mvcframework.servlet.v5.aop.support.MyAdvisedSupport;

public class MyDefaultAopProxyFactory {

    public MyAopProxy createAopProxy(MyAdvisedSupport config) throws Exception{
        Class targetClass = config.getTargetClass();
        if(targetClass.getInterfaces().length > 0){
            return new MyJdkDynamicAopProxy(config);
        }
        return new MyCgLlibAopProxy();
    }
}
