package com.haoren.ioc.annotation;

import com.haoren.ioc.factory.BeanFactory;
import com.haoren.ioc.factory.BeanFactoryAware;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

public class AutowiredAnnotationBeanPostProcessor implements BeanFactoryAware {

    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet<>();

    @Autowired
    private BeanFactory beanFactory;

    public AutowiredAnnotationBeanPostProcessor() {
        this.autowiredAnnotationTypes.add(Autowired.class);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register() {

    }
}
