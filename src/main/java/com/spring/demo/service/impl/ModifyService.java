package com.spring.demo.service.impl;

import com.spring.demo.service.IAddService;
import com.spring.demo.service.IModifyService;
import com.spring.demo.service.IQueryService;
import com.spring.mvcframework.annotation.MyAutowired;
import com.spring.mvcframework.annotation.MyService;

/**
 * 增删改业务
 *
 */
@MyService
public class ModifyService implements IModifyService {

//	@MyAutowired private IQueryService queryService;
//	@MyAutowired private IAddService addService;

	/**
	 * 增加
	 */
	public String add(String name,String addr) throws Exception{
		throw new Exception("这是故意抛出来的异常");

//		return "modifyService add,name=" + name + ",addr=" + addr;
	}

	/**
	 * 修改
	 */
	public String edit(Integer id,String name) {
		return "modifyService edit,id=" + id + ",name=" + name;
	}

	/**
	 * 删除
	 */
	public String remove(Integer id) {
		return "modifyService id=" + id;
	}
	
}
