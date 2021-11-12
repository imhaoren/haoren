package com.haoren.ioc.proxy;

import com.haoren.ioc.util.DruidUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class IocInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("开始代理");
        Object o1 = methodProxy.invokeSuper(o, objects);
        System.out.println("代理结束");
        return o1;
    }

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(DruidUtil.class);
        enhancer.setCallback(new IocInterceptor());
        DruidUtil druidUtil = (DruidUtil) enhancer.create();
        System.out.println(druidUtil);
    }
}
