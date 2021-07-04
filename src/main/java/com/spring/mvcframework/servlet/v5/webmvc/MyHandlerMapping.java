package com.spring.mvcframework.servlet.v5.webmvc;

import lombok.Getter;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class MyHandlerMapping {

    @Getter
    private Object controller;

    @Getter
    protected Method method;

    @Getter
    protected Pattern pattern;

    public MyHandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

}
