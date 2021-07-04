package com.spring.demo.service.impl;


import com.spring.demo.service.IAddService;
import com.spring.demo.service.IModifyService;
import com.spring.demo.service.IQueryService;
import com.spring.mvcframework.annotation.MyAutowired;
import com.spring.mvcframework.annotation.MyService;

@MyService
public class AddService implements IAddService {

    @MyAutowired
    private IModifyService modifyService;
    @MyAutowired
    private IQueryService queryService;

}
