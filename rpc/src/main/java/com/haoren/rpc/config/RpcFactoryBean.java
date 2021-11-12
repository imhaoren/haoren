package com.haoren.rpc.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

public class RpcFactoryBean<T> implements FactoryBean<T> {

    private Class<?> rpcInterface;

    @Autowired
    RpcFactory<T> factory;

    public RpcFactoryBean() {
    }

    public RpcFactoryBean(Class<?> rpcInterface) {
        this.rpcInterface = rpcInterface;
    }

    @Override
    public T getObject() throws Exception {
        return getRpc();
    }

    @Override
    public Class<?> getObjectType() {
        return this.rpcInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public <T> T getRpc() {
        return (T) Proxy.newProxyInstance(rpcInterface.getClassLoader(), new Class[]{rpcInterface}, factory);
    }
}
