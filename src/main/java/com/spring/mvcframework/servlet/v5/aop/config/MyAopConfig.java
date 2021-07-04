package com.spring.mvcframework.servlet.v5.aop.config;

import lombok.Data;

@Data
public class MyAopConfig {

    private String pointCut;

    private String aspectClass;

    private String aspectBefore;

    private String aspectAfter;

    private String aspectAfterThrow;

    private String aspectAfterThrowingName;
}
