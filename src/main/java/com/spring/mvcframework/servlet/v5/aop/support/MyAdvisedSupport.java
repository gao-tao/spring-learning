package com.spring.mvcframework.servlet.v5.aop.support;

import com.spring.mvcframework.servlet.v5.aop.aspect.MyAfterReturningAdviceInterceptor;
import com.spring.mvcframework.servlet.v5.aop.aspect.MyAspectJAfterThrowingAdvice;
import com.spring.mvcframework.servlet.v5.aop.aspect.MyMethodBeforeAdviceInterceptor;
import com.spring.mvcframework.servlet.v5.aop.config.MyAopConfig;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class MyAdvisedSupport {

    private MyAopConfig config;
    private Object target;
    private Class targetClass;
    private Pattern pointCutClassPattern;

    private Map<Method, List<Object>> methodCache;

    public MyAdvisedSupport(MyAopConfig config) {
        this.config = config;
    }

    //解析配置文件的方法
    private void parse() {

        //把Spring的Excpress变成java能够识别的正则表达式
        String pointCut = config.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");

        //保存专门匹配的Class的正则  public .* com.spring.demo.service..*Service..*(.*)
        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ") + 1));

        //享元的共享池
        methodCache = new HashMap<Method, List<Object>>();

        //保存专门匹配方法的正则
        Pattern pointCutPattern = Pattern.compile(pointCut);

        try {
            Class<?> aspectClass = Class.forName(this.config.getAspectClass());

            Map<String, Method> aspectMethods = new HashMap<String, Method>();

            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(), method);
            }

            for (Method method : this.targetClass.getMethods()) {
                String methodString = method.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }

                Matcher matcher = pointCutPattern.matcher(methodString);
                if (matcher.matches()) {
                    LinkedList<Object> advices = new LinkedList<Object>();

                    if (!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))) {
                        advices.add(new MyMethodBeforeAdviceInterceptor(aspectClass.newInstance(), aspectMethods.get(config.getAspectBefore())));
                    }

                    if (!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))) {
                        advices.add(new MyAfterReturningAdviceInterceptor(aspectClass.newInstance(), aspectMethods.get(config.getAspectAfter())));
                    }

                    if (!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))) {
                        MyAspectJAfterThrowingAdvice advice = new MyAspectJAfterThrowingAdvice(aspectClass.newInstance(), aspectMethods.get(config.getAspectAfterThrow()));
                        advice.setThrowName(config.getAspectAfterThrowingName());
                        advices.add(advice);
                    }

                    //跟目标代理类的业务方法和Advices建立一对多关联关系，以便在Proxy类中获得
                    methodCache.put(method, advices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Object> getInterceptorAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception {

        //从缓存中获取
        List<Object> cached = this.methodCache.get(method);

        //缓存未命中，则进行下一步处理
        if (cached == null) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);
            this.methodCache.put(m, cached);
        }
        return cached;
    }

    public boolean pointCutMath(){
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

    public void setTargetClass(Class<?> targetClass){
        this.targetClass = targetClass;
        parse();
    }
}
