package com.spring.demo.aspect;

import com.spring.mvcframework.servlet.v5.aop.aspect.MyJoinPoint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogAspect {

    //调用一个方法之前，执行before方法
    public void before(MyJoinPoint joinPoint) {
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(), System.currentTimeMillis());
        log.info("Invoker Before Method !!!");
    }

    //调用一个方法之后，执行after方法
    public void after(MyJoinPoint joinPoint) {

        long startTime = (Long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        log.info("Invoker After Method !!!" + "use time:" + (endTime - startTime));
    }

    public void afterThrowing(MyJoinPoint joinPoint, Throwable tx) {
        log.info("出现异常");
    }
}
