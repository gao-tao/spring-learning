package com.spring.demo.service.impl;

import com.spring.demo.service.IModifyService;
import com.spring.demo.service.IQueryService;
import com.spring.mvcframework.annotation.MyAutowired;
import com.spring.mvcframework.annotation.MyService;
import lombok.extern.slf4j.Slf4j;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 查询业务
 *
 */
@MyService
@Slf4j
public class QueryService implements IQueryService {

	@MyAutowired
	private IModifyService modifyService;

	/**
	 * 查询
	 */
	public String query(String name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		String json = "{\"name\":\"" + name + "\",\"time\":\"" + time + "\"}";
		log.info("这是在业务方法中打印的：" + json);
		return json;
	}

}
