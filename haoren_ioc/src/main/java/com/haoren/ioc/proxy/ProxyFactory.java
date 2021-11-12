package com.haoren.ioc.proxy;


import com.haoren.ioc.annotation.Autowired;
import com.haoren.ioc.transaction.TransactionManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyFactory {

    @Autowired
    private TransactionManager transactionManager;

    public Object getProxy(Object target) {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), target.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object result = null;
                try {
                    transactionManager.beginTransaction();
                    result = method.invoke(target, args);
                    transactionManager.commitTransaction();
                } catch (Exception e) {
                    transactionManager.rollback();
                    e.printStackTrace();
                }
                return result;
            }
        });
    }
}
