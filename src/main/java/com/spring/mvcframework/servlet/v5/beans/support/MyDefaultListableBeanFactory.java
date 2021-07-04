package com.spring.mvcframework.servlet.v5.beans.support;

import com.spring.mvcframework.servlet.v5.beans.config.MyBeanDefinition;
import com.spring.mvcframework.servlet.v5.core.MyBeanFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDefaultListableBeanFactory implements MyBeanFactory {

    public Map<String, MyBeanDefinition> beanDefinitionMap = new HashMap<String, MyBeanDefinition>();

    @Override
    public Object getBean(String name) {
        return null;
    }

    @Override
    public Object getBean(Class beanClass) {
        return null;
    }

    public void doRegisterBeanDefinition(List<MyBeanDefinition> beanDefinitions) throws Exception {

        for (MyBeanDefinition beanDefinition : beanDefinitions) {
            if (this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The" + beanDefinition.getFactoryBeanName() + " is exists!!");
            }
            this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }

    }
}
