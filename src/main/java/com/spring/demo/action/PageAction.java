package com.spring.demo.action;


import com.spring.demo.service.IQueryService;
import com.spring.mvcframework.annotation.MyAutowired;
import com.spring.mvcframework.annotation.MyController;
import com.spring.mvcframework.annotation.MyRequestMapping;
import com.spring.mvcframework.annotation.MyRequestParam;
import com.spring.mvcframework.servlet.v4.mvc.webmvc.MyModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 公布接口url
 */
@MyController
@MyRequestMapping("/")
public class PageAction {

    @MyAutowired
    IQueryService queryService;

    @MyRequestMapping("/first.html")
    public MyModelAndView query(@MyRequestParam("teacher") String teacher){
        String result = queryService.query(teacher);
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new MyModelAndView("first.html",model);
    }

}
