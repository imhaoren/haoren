package com.haoren.rpc.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RpcBeanDefinitionRegistryPostProcessorConfigurer implements EnvironmentAware, BeanDefinitionRegistryPostProcessor {

    /**
     * BeanDefinitionRegistryPostProcessor或ImportBeanDefinitionRegistrar接口无法通过@Autowired和@Value注入，因为执行接口方法是还没解析@Autowired和@Value注解
     * 为了获取相关熟悉值，可以实现EnvironmentAware接口来获取application.yml里的相关属性，实现通过配置文件配置
     */
    private String basePackage;

    @Override
    public void setEnvironment(Environment environment) {
        this.basePackage = environment.getProperty("base.package");
    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        ClassPathRpcScanner scanner = new ClassPathRpcScanner(beanDefinitionRegistry);

        scanner.setAnnotationClass(null);
        scanner.registerFilters();

        scanner.scan(StringUtils.tokenizeToStringArray(basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
