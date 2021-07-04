package com.spring.mvcframework.servlet.v4.mvc.webmvc;

import lombok.Getter;

import java.util.Map;

public class MyModelAndView {

    @Getter
    private String viewName;

    @Getter
    private Map<String,?> model;

    public MyModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public MyModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
