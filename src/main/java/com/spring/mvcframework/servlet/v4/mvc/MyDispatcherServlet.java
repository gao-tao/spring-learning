package com.spring.mvcframework.servlet.v4.mvc;

import com.spring.mvcframework.annotation.MyController;
import com.spring.mvcframework.annotation.MyRequestMapping;
import com.spring.mvcframework.annotation.MyRequestParam;
import com.spring.mvcframework.servlet.v4.mvc.context.MyApplicationContext;
import com.spring.mvcframework.servlet.v4.mvc.webmvc.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyDispatcherServlet extends HttpServlet {

    private MyApplicationContext applicationContext = null;

    //保存Controller中URL和Method的对应关系
    private List<MyHandlerMapping> handlerMappings = new ArrayList<MyHandlerMapping>();

    private Map<MyHandlerMapping, MyHandlerAdapter> handlerAdapters = new HashMap<MyHandlerMapping, MyHandlerAdapter>();

    private List<MyViewResolver> viewResolvers = new ArrayList<MyViewResolver>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //6、调用，运行阶段
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Exception,Detail : " + Arrays.toString(e.getStackTrace()));
        }

    }

    @Override
    public void init(ServletConfig config) throws ServletException {


        applicationContext = new MyApplicationContext(config.getInitParameter("contextConfigLocation"));

        //===========MVC功能============
        initStrategies(applicationContext);

        System.out.println("My Spring framework is init.");
    }

    //初始化策略
    protected void initStrategies(MyApplicationContext context) {

        //handlerMapping
        initHandlerMapping(context);

        //初始化参数适配器
        initHandlerAdapters(context);

        //初始化视图转换器
        initViewResolvers(context);
    }

    private void initViewResolvers(MyApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);

        for (File file : templateRootDir.listFiles()) {
            this.viewResolvers.add(new MyViewResolver(templateRoot));
        }
    }

    private void initHandlerAdapters(MyApplicationContext context) {
        for (MyHandlerMapping handlerMapping : handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new MyHandlerAdapter());
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1、根据URL 拿到对应的Handler
        MyHandlerMapping handler = getHandler(req);

        if (handler == null) {
            processDispatchResult(req, resp, new MyModelAndView("404"));
            return;
        }

        //2、根据HandlerMapping拿到HandlerAdapter
        MyHandlerAdapter ha = getHandlerAdapter(handler);

        //3、根据HandlerMapping拿到对应的ModelAndView
        MyModelAndView mv = ha.handle(req, resp, handler);

        //4、根据ViewResolver 找到对应View对象
        //通过View对象渲染页面，并返回
        processDispatchResult(req, resp, mv);
    }

    private MyHandlerAdapter getHandlerAdapter(MyHandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()) {
            return null;
        }
        MyHandlerAdapter ha = this.handlerAdapters.get(handler);
        return ha;
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, MyModelAndView mv) throws Exception {

        if (null == mv) {
            return;
        }
        if (this.viewResolvers.isEmpty()) {
            return;
        }

        for (MyViewResolver viewResolver : this.viewResolvers) {
            MyView view = viewResolver.resolveViewName(mv.getViewName());
            view.render(mv.getModel(), req, resp);
            return;
        }
    }

    private MyHandlerMapping getHandler(HttpServletRequest req) {

        //绝对路径
        String url = req.getRequestURI();
        //处理成相对路径
        String contextPath = req.getContextPath();

        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");

        for (MyHandlerMapping handlerMapping : this.handlerMappings) {
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handlerMapping;
        }
        return null;
    }


    //初始化url和Method的一对一对应关系
    private void initHandlerMapping(MyApplicationContext context) {

        if (this.applicationContext.getBeanDefinitionCount() == 0) {
            return;
        }

        for (String beanName : applicationContext.getBeanDefinitionNames()) {

            Object instance = this.applicationContext.getBean(beanName);
            Class<?> clazz = instance.getClass();
            if (!clazz.isAnnotationPresent(MyController.class)) {
                continue;
            }

            //保存写在类上的MyRequestMapping("/demo")
            String baseUrl = "";
            if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
                MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
                baseUrl = requestMapping.value();
            }

            //默认获取所有的public方法
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(MyRequestMapping.class)) {
                    continue;
                }

                MyRequestMapping requestMapping = method.getAnnotation(MyRequestMapping.class);

                //优化
                //demo///query
                String url = ("/" + baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/")
                        .replaceAll("\\*", ".*")
                        .replaceAll("/+", "/");

                Pattern pattern = Pattern.compile(url);

                handlerMappings.add(new MyHandlerMapping(instance, method, pattern));

                System.out.println("Mapped: " + pattern + "," + method);
            }
        }
    }
}
