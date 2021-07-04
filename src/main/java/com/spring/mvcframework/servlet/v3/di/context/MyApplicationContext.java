package com.spring.mvcframework.servlet.v3.di.context;

import com.spring.mvcframework.annotation.MyAutowired;
import com.spring.mvcframework.annotation.MyController;
import com.spring.mvcframework.annotation.MyService;
import com.spring.mvcframework.servlet.v3.di.beans.MyBeanWrapper;
import com.spring.mvcframework.servlet.v3.di.beans.config.MyBeanDefinition;
import com.spring.mvcframework.servlet.v3.di.beans.support.MyBeanDefinitionReader;
import com.spring.mvcframework.servlet.v3.di.beans.support.MyDefaultListableBeanFactory;
import com.spring.mvcframework.servlet.v3.di.core.MyBeanFactory;

import java.lang.reflect.Field;
import java.util.*;

public class MyApplicationContext implements MyBeanFactory {

    private MyDefaultListableBeanFactory registry = new MyDefaultListableBeanFactory();

    //循环依赖的标识，当前正在创建的BeanName,Mark一下
    private Set<String> singletonsCurrentlyInCreation = new HashSet<String>();

    //一级缓存：保存成熟的Bean
    private Map<String, Object> singletonObjects = new HashMap<String, Object>();

    //二级缓存：保存早期的Bean
    private Map<String, Object> earlySingletonObjects = new HashMap<String, Object>();

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

        //循环调用getBean()方法
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

        //enter 判断实体类是否已经被加载过了
        Object singleton = getSingleton(beanName, beanDefinition);

        if (singleton != null) {
            return singleton;
        }

        if (!singletonsCurrentlyInCreation.contains(beanName)) {
            singletonsCurrentlyInCreation.add(beanName);
        }

        //2、反射实例化对象
        Object instance = instantiateBean(beanName, beanDefinition);

        //input to singletonObjects cache
        this.singletonObjects.put(beanName, instance);

        //3、将返回的Bean的对象封装成BeanWrapper
        MyBeanWrapper beanWrapper = new MyBeanWrapper(instance);

        //4、执行依赖注入
        populateBean(beanName, beanDefinition, beanWrapper);

        //5、保存到IOC容器中
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);

        return beanWrapper.getWapperedInstance();
    }

    private Object getSingleton(String beanName, MyBeanDefinition beanDefinition) {

        //先去一级缓存里拿
        Object bean = singletonObjects.get(beanName);
        //如果一级缓存中没有，但是又有创建标识，说明就是循环依赖
        if (bean == null && singletonsCurrentlyInCreation.contains(beanName)) {

            bean = earlySingletonObjects.get(beanName);

            //如果二级缓存也没有，那就从三级缓存拿
            if (bean == null) {
                bean = instantiateBean(beanName, beanDefinition);

                //将创建出来的对象重新放入到二级缓存中
                earlySingletonObjects.put(beanName, bean);

            }
        }
        return bean;
    }

    private void populateBean(String beanName, MyBeanDefinition beanDefinition, MyBeanWrapper beanWrapper) {

        Object instance = beanWrapper.getWapperedInstance();

        Class<?> clazz = beanWrapper.getWrappedClass();

        if (!(clazz.isAnnotationPresent(MyController.class) || clazz.isAnnotationPresent(MyService.class))) {
            return;
        }

        //忽略字段的修饰符，不管你是 private / protected / public / default
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
//                if (this.factoryBeanInstanceCache.get(autowireBeanName) == null) {
//                    continue;
//                }
                //相当于 demoAction.demoService = ioc.get("com.spring.demo.service.IDemoService")
//                field.set(instance, this.factoryBeanInstanceCache.get(autowireBeanName).getWapperedInstance());

                field.set(instance, getBean(autowireBeanName));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Object instantiateBean(String beanName, MyBeanDefinition beanDefinition) {

        if (beanDefinition.isSingleton() && this.factoryBeanObjectCache.containsKey(beanName)) {
            return this.factoryBeanObjectCache.get(beanName);
        }

        String className = beanDefinition.getBeanClassName();

        Object instance = null;

        try {

            Class<?> clazz = Class.forName(className);
            instance = clazz.newInstance();

            //如果是代理对象，触发Aop的逻辑
            this.factoryBeanObjectCache.put(beanName, instance);

            this.factoryBeanObjectCache.put(clazz.getName(), instance);
            for (Class<?> i : clazz.getInterfaces()) {
                this.factoryBeanObjectCache.put(i.getName(), instance);
            }

//            this.factoryBeanObjectCache.put(beanName, instance);
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
