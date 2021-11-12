package com.haoren.rpc.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haoren.rpc.mo.RequestMO;
import com.haoren.rpc.mo.ResponseMO;
import com.haoren.rpc.netty.client.NettyClient;
import com.haoren.rpc.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

@Slf4j
@Component
public class RpcFactory<T> implements InvocationHandler {

    @Autowired
    private NettyClient client;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RequestMO requestMO = new RequestMO();
        requestMO.setClassName(method.getDeclaringClass().getName());
        requestMO.setMethodName(method.getName());
        requestMO.setParameters(args);
        requestMO.setParameterTypes(method.getParameterTypes());
        requestMO.setId(IdUtil.getId());
        log.info("className={},methodName={}", requestMO.getClassName(), requestMO.getMethodName());
        String result = client.send(requestMO);
        Class<?> returnType = method.getReturnType();
        ResponseMO responseMO = JSONObject.parseObject(result, ResponseMO.class);
        if (1 == responseMO.getCode()) {
            throw new Exception(responseMO.getError_msg());
        }
        if (returnType.isPrimitive() || String.class.isAssignableFrom(returnType)) {
            return responseMO.getData();
        } else if (Collection.class.isAssignableFrom(returnType)) {
            return JSONArray.parseArray(responseMO.getData().toString(), Object.class);
        } else if (Map.class.isAssignableFrom(returnType)) {
            return JSON.parseObject(responseMO.getData().toString(), Map.class);
        } else {
            Object data = responseMO.getData();
            return JSONObject.parseObject(data.toString(), returnType);
        }
    }
}
