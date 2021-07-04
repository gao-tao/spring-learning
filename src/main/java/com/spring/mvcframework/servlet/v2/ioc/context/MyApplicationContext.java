package com.spring.mvcframework.servlet.v2.ioc.context;

import com.spring.mvcframework.annotation.MyAutowired;
import com.spring.mvcframework.annotation.MyController;
import com.spring.mvcframework.annotation.MyService;
import com.spring.mvcframework.servlet.v2.ioc.beans.MyBeanWrapper;
import com.spring.mvcframework.servlet.v2.ioc.beans.config.MyBeanDefinition;
import com.spring.mvcframework.servlet.v2.ioc.beans.support.MyBeanDefinitionReader;
import com.spring.mvcframework.servlet.v2.ioc.beans.support.MyDefaultListableBeanFactory;
import com.spring.mvcframework.servlet.v2.ioc.core.MyBeanFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyApplicationContext implements MyBeanFactory {

    private MyDefaultListableBeanFactory registry = new MyDefaultListableBeanFactory();

    //三级缓存（终极缓存）
    private Map<String, MyBeanWrapper> factoryBeanInstanceCache = new HashMap<String, MyBeanWrapper>();

    private Map<String, Object> factoryBeanObjectCache = new HashMap<String, Object>();

    private MyBeanDefinitionReader reader;

    public MyApplicationContext(String... configLocations) {

        //加载配置文件
        reader = new MyBeanDefinitionReader(configLocations);

        try {
            //解析配置文件，将所有的配置信息封装成BeanDefinition对象
            List<MyBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

            //所有的配置信息缓存起来
            this.registry.doRegisterBeanDefinition(beanDefinitions);

            //4.加载非延迟加载的所有Bean
            doLoadInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void doLoadInstance() {

        for (Map.Entry<String, MyBeanDefinition> entry : this.registry.beanDefinitionMap.entrySet()) {

            String beanName = entry.getKey();

            if (!entry.getValue().isLazyInit()) {
                getBean(beanName);
            }
        }

    }


    @Override
    public Object getBean(Class beanClass) {
        return getBean(beanClass.getName());
    }

    /**
     * 从IOC容器中获得一个Bean对象
     *
     * @param beanName
     * @return
     */
    @Override
    public Object getBean(String beanName) {

        //1.先拿到BeanDefinition配置信息
        MyBeanDefinition beanDefinition = registry.beanDefinitionMap.get(beanName);

        //2、反射实例化对象
        Object instance = instantiateBean(beanName, beanDefinition);

        //3、将返回的Bean的对象封装成BeanWrapper
        MyBeanWrapper beanWrapper = new MyBeanWrapper(instance);

        //4、执行依赖注入
        populateBean(beanName, beanDefinition, beanWrapper);

        //5、保存到IOC容器中
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);

        return beanWrapper.getWapperedInstance();
    }

    private void populateBean(String beanName, MyBeanDefinition beanDefinition, MyBeanWrapper beanWrapper) {

        Object instance = beanWrapper.getWapperedInstance();

        Class<?> clazz = beanWrapper.getWrappedClass();

        if (!clazz.isAnnotationPresent(MyController.class) || clazz.isAnnotationPresent(MyService.class)) {
            return;
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(MyAutowired.class)) {
                continue;
            }

            MyAutowired autowired = field.getAnnotation(MyAutowired.class);

            String autowireBeanName = autowired.value().trim();

            if ("".equals(autowireBeanName)) {
                autowireBeanName = field.getType().getName();
            }

            //强制访问
            field.setAccessible(true);

            try {
                if (this.factoryBeanInstanceCache.get(autowireBeanName) == null) {
                    continue;
                }
                //相当于 demoAction.demoService = ioc.get("com.spring.demo.service.IDemoService")
                field.set(instance, this.factoryBeanInstanceCache.get(autowireBeanName).getWapperedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Object instantiateBean(String beanName, MyBeanDefinition beanDefinition) {

        String className = beanDefinition.getBeanClassName();

        Object instance = null;

        try {

            Class<?> clazz = Class.forName(className);
            instance = clazz.newInstance();

            //如果是代理对象，触发Aop的逻辑

            this.factoryBeanObjectCache.put(beanName, instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public int getBeanDefinitionCount() {
        return this.registry.beanDefinitionMap.size();
    }

    public String[] getBeanDefinitionNames() {
        return this.registry.beanDefinitionMap.keySet().toArray(new String[0]);
    }

}
