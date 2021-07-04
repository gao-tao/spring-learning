package com.spring.mvcframework.servlet.v3.di;

import com.spring.mvcframework.annotation.MyController;
import com.spring.mvcframework.annotation.MyRequestMapping;
import com.spring.mvcframework.annotation.MyRequestParam;
import com.spring.mvcframework.servlet.v3.di.context.MyApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyDispatcherServlet extends HttpServlet {

    private MyApplicationContext applicationContext = null;

    //不使用Map
    // 使用Map的话，key只能是url
    // Handler 本身的功能就是把url和method对应关心，已经具备了Map的功能
    private List<Handler> handlerMapping = new ArrayList<Handler>();

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
        //5、初始化HandlerMapping
        initHandlerMapping();

        System.out.println("My Spring framework is init.");
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Handler handler = getHandler(req);
        if (handler == null) {
            resp.getWriter().write("404 Not Found!!");
            return;
        }

        //获得方法的形参列表
        Class<?>[] paramTypes = handler.getParamTypes();

        Object[] paramValues = new Object[paramTypes.length];

        Map<String, String[]> params = req.getParameterMap();

        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", ",");

            if (!handler.paramIndexMapping.containsKey(param.getKey())) {
                continue;
            }

            int index = handler.paramIndexMapping.get(param.getKey());
            paramValues[index] = convert(paramTypes[index], value);
        }

        if (handler.paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = handler.paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        if (handler.paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = handler.paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }

        Object returnValue = handler.method.invoke(handler.controller, paramValues);
        if (returnValue == null || returnValue instanceof Void) {
            return;
        }

        resp.getWriter().write(returnValue.toString());
    }

    //传过来的参数都是String类型的，Http就是基于字符串协议
    //只需要把String转换为任意类型就可以了
    private Object convert(Class<?> type, String value) {

        //这里可以使用策略模式修改
        if (Integer.class == type) {
            return Integer.valueOf(value);
        }
        if (Double.class == type) {
            return Double.valueOf(value);
        }
        return value;
    }

    private Handler getHandler(HttpServletRequest req) {
        if (handlerMapping.isEmpty()) {
            return null;
        }

        //绝对路径
        String url = req.getRequestURI();

        //处理成相对路径
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");

        for (Handler handler : this.handlerMapping) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }


    //初始化url和Method的一对一对应关系
    private void initHandlerMapping() {

        if (applicationContext.getBeanDefinitionCount() == 0) {
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

                if (!method.isAnnotationPresent(MyRequestMapping.class)) {
                    continue;
                }

                //优化
                //demo///query
                String url = ("/" + baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");

                Pattern pattern = Pattern.compile(url);

                this.handlerMapping.add(new Handler(pattern, method, instance));

                System.out.println("Mapped: " + pattern + "," + method);
            }
        }
    }

    /**
     * 首字母大写变小写
     *
     * @param simpleName
     * @return
     */
    private String toLowerFirstCase(String simpleName) {

        char[] chars = simpleName.toCharArray();

        //默认都是规范命名 首字母大写
        //通过计算ASCII码，转化大小写
        chars[0] += 32;
        return String.valueOf(chars);
    }

    //保存一个url和一个Method的关系
    public class Handler {

        private Pattern pattern; //正则
        private Method method;
        private Object controller;
        private Class<?>[] paramTypes;


        public Pattern getPattern() {
            return pattern;
        }

        public Method getMethod() {
            return method;
        }

        public Object getController() {
            return controller;
        }

        public Class<?>[] getParamTypes() {
            return paramTypes;
        }

        //形参列表
        //参数的名字作为key，参数的顺序，位置作为值
        private Map<String, Integer> paramIndexMapping;

        public Handler(Pattern pattern, Method method, Object controller) {
            this.pattern = pattern;
            this.method = method;
            this.controller = controller;

            paramTypes = method.getParameterTypes();

            paramIndexMapping = new HashMap<String, Integer>();

            putParamIndexMapping(method);
        }

        private void putParamIndexMapping(Method method) {

            //提取方法中加了注解的参数
            //把方法上的注解拿到，得到的是一个二维数组
            //因为一个参数可以有多个注解，而一个方法又有多个参数

            Annotation[][] pa = method.getParameterAnnotations();
            for (int i = 0; i < pa.length; i++) {
                for (Annotation a : pa[i]) {
                    if (a instanceof MyRequestParam) {
                        String paramName = ((MyRequestParam) a).value();
                        if (!"".equals(paramName.trim())) {
                            paramIndexMapping.put(paramName, i);
                        }
                    }
                }
            }

            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> type = parameterTypes[i];
                if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                    paramIndexMapping.put(type.getName(), i);
                }
            }

        }
    }

}
