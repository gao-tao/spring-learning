package com.spring.mvcframework.servlet.v5.webmvc;

import com.spring.mvcframework.annotation.MyRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MyHandlerAdapter {

    public MyModelAndView handle(HttpServletRequest req, HttpServletResponse resp, MyHandlerMapping handler) throws Exception {

        Method method = handler.getMethod();

        //1、先把形参的位置和参数名字建立映射关系，并缓存下来
        Map<String, Integer> paramIndexMapping = new HashMap<String, Integer>();


        Annotation[][] pa = method.getParameterAnnotations();

        for (int i = 0; i < pa.length; i++) {
            for (Annotation a : pa[i]) {
                if (a instanceof MyRequestParam) {
                    String paraName = ((MyRequestParam) a).value();
                    if (!"".equals(paraName.trim())) {
                        paramIndexMapping.put(paraName, i);
                    }
                }
            }
        }


        Class<?>[] paramTypes = method.getParameterTypes();

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];

            if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                paramIndexMapping.put(type.getName(), i);
            }
        }

        //根据参数位置匹配参数名字，从url中取到参数名字对应的值
        Object[] paramValues = new Object[paramTypes.length];

        //http://localhost/demo/query?name=snail&name=Tomcat&name=123
        Map<String, String[]> params = req.getParameterMap();

        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", ",");

            if (!paramIndexMapping.containsKey(param.getKey())) {
                continue;
            }

            int index = paramIndexMapping.get(param.getKey());
            paramValues[index] = caseStringValue(value, paramTypes[index]);
        }

        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }

        Object returnValue = method.invoke(handler.getController(), paramValues);
        if (returnValue == null || returnValue instanceof Void) {
            return null;
        }

        boolean isModelAndView = handler.getMethod().getReturnType() == MyModelAndView.class;
        if (isModelAndView) {
            return (MyModelAndView) returnValue;
        }
        return null;
    }

    //传过来的参数都是String类型的，Http就是基于字符串协议
    //只需要把String转换为任意类型就可以了
    private Object caseStringValue(String value, Class<?> paramType) {
        if (String.class == paramType) {
            return value;
        }
        if (Integer.class == paramType) {
            return Integer.valueOf(value);
        } else if (Double.class == paramType) {
            return Double.valueOf(value);
        } else {
            if (value != null) {
                return value;
            }
            return null;
        }
    }

}
