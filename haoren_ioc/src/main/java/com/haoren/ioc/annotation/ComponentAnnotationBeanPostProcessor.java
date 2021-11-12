package com.haoren.ioc.annotation;

import com.haoren.ioc.factory.BeanFactory;
import com.haoren.ioc.factory.BeanFactoryAware;

public class ComponentAnnotationBeanPostProcessor implements BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {

    }
}
