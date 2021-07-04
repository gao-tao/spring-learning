//package com.spring.demo.action;
//
//import com.spring.demo.service.IDemoService;
//import com.spring.mvcframework.annotation.MyController;
//import com.spring.mvcframework.annotation.MyRequestMapping;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@MyController("/useraction")
//public class UserAction {
//
//    private IDemoService demoService;
//
//    @MyRequestMapping("/edit")
//    public void edit(HttpServletRequest req, HttpServletResponse resp,
//                     String name){
//        String result = demoService.get(name);
//        try {
//            resp.getWriter().write(result);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
