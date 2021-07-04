package com.spring.mvcframework.servlet.v2.ioc.beans.support;

import com.spring.mvcframework.servlet.v2.ioc.beans.config.MyBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 读取配置文件 扫描包路径下的的类 包装成MyBeanDefinition类
 */
public class MyBeanDefinitionReader {

    //保存用户配置好的配置文件
    private Properties contextConfig = new Properties();

    //缓存从包路径下扫描的全类名，需要被注册地BeanClass
    private List<String> registryBeanClass = new ArrayList<String>();


    public MyBeanDefinitionReader(String... locations) {

        //1、加载Properties文件
        doLoadConfig(locations[0]);

        //扫描相关的类
        doScanner(contextConfig.getProperty("scanPackage"));
    }

    public List<MyBeanDefinition> loadBeanDefinitions() {

        List<MyBeanDefinition> result = new ArrayList<MyBeanDefinition>();

        try {
            for (String className : registryBeanClass) {
                Class<?> beanClass = Class.forName(className);

                //beanClass本事是接口的话，不作处理
                if (beanClass.isInterface()) {
                    continue;
                }

                //1、默认类名首字母小写的情况
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));

                //如果是一个接口，就用实现类（默认这里一个类只有一个接口）
                for (Class<?> i : beanClass.getInterfaces()) {
                    result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private MyBeanDefinition doCreateBeanDefinition(String factoryBeanName, String factoryClassName) {

        MyBeanDefinition beanDefinition = new MyBeanDefinition();
        beanDefinition.setFactoryBeanName(factoryBeanName);
        beanDefinition.setBeanClassName(factoryClassName);
        return beanDefinition;
    }

    private String toLowerFirstCase(String simpleName) {

        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return chars.toString();
    }

    private void doScanner(String scanPackage) {

        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));

        File classPath = new File(url.getFile());

        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {

                //取反，减少代码嵌套
                if (!file.getName().endsWith(".class")) {
                    continue;
                }

                //包名、类名
                String className = (scanPackage + "." + file.getName().replace(".class", ""));

                //实例化，要用到 Class.forName(className);
                registryBeanClass.add(className);
            }
        }
    }


    private void doLoadConfig(String contextConfigLocation) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation.replaceAll("classpath:", ""));
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
