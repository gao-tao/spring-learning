//package com.spring.demo.action;
//
//import com.spring.demo.service.IDemoService;
//import com.spring.mvcframework.annotation.MyAutowired;
//import com.spring.mvcframework.annotation.MyController;
//import com.spring.mvcframework.annotation.MyRequestMapping;
//import com.spring.mvcframework.annotation.MyRequestParam;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
////@MyController
////@MyRequestMapping("/demo")
//public class DemoAction {
//
//    @MyAutowired
//    private IDemoService demoService;
//
//    @MyRequestMapping("/query")
//    public void query(HttpServletRequest req, HttpServletResponse resp,
//                      @MyRequestParam("name") String name){
////		String result = demoService.get(name);
//        String result = "My name is " + name;
//        try {
//            resp.getWriter().write(result);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @MyRequestMapping("/add")
//    public void add(HttpServletRequest req, HttpServletResponse resp,
//                    @MyRequestParam("a") Integer a, @MyRequestParam("b") Integer b){
//        try {
//            resp.getWriter().write(a + "+" + b + "=" + (a + b));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @MyRequestMapping("/sub")
//    public void sub(HttpServletRequest req, HttpServletResponse resp,
//                    @MyRequestParam("a") Double a, @MyRequestParam("b") Double b){
//        try {
//            resp.getWriter().write(a + "-" + b + "=" + (a - b));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @MyRequestMapping("/remove")
//    public String  remove(@MyRequestParam("id") Integer id){
//        return "" + id;
//    }
//}
