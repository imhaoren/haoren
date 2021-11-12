package com.haoren.rpc.netty.server;

import com.alibaba.fastjson.JSON;
import com.haoren.rpc.constant.RpcConstant;
import com.haoren.rpc.mo.RequestMO;
import com.haoren.rpc.mo.ResponseMO;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private Map<String, Object> serviceMap;

    public NettyServerHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端连接成功,address={}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端断开连接,address={}", ctx.channel().remoteAddress());
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RequestMO requestMO = JSON.parseObject(msg.toString(), RequestMO.class);
        log.info("methodName={}", requestMO.getMethodName());
        if (RpcConstant.heart_beat.equals(requestMO.getMethodName())) {
            log.info("客户端心跳信息className={},methodName={}", requestMO.getClassName(), requestMO.getMethodName());
        } else {
            log.info("rpc客户端请求接口className={}, methodName={}", requestMO.getClassName(), requestMO.getMethodName());
            ResponseMO responseMO = new ResponseMO();
            responseMO.setRequestId(requestMO.getId());
            try {
                Object result = handler(requestMO);
                responseMO.setData(result);
            } catch (Exception e) {
                e.printStackTrace();
                responseMO.setCode(1);
                responseMO.setError_msg(e.toString());
                log.error("RPC Server handle request error", e);
            }
            ctx.writeAndFlush(responseMO);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("发送心跳消息");
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                log.info("客户端已超过60秒未读写数据,关闭连接{}", ctx.channel().remoteAddress());
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info(cause.getMessage());
        ctx.close();
    }

    private Object handler(RequestMO requestMO) throws Exception {
        String className = requestMO.getClassName();
        Object serviceBean = serviceMap.get(className);
        if (null != serviceBean) {
            Class<?> serviceBeanClass = serviceBean.getClass();
            String methodName = requestMO.getMethodName();
            Class<?>[] parameterTypes = requestMO.getParameterTypes();
            Object[] parameters = requestMO.getParameters();
            Method method = serviceBeanClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(serviceBean, getParameters(parameterTypes, parameters));
        } else {
            throw new Exception("未找到服务接口,请检查配置" + className + "#" + requestMO.getMethodName());
        }
    }

    private Object[] getParameters(Class<?>[] parameterTypes, Object[] parameters) {
        if (null == parameters || parameters.length == 0) {
            return parameters;
        } else {
            Object[] new_parameters = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                new_parameters[i] = JSON.parseObject(parameters[i].toString(), parameterTypes[i]);
            }
            return new_parameters;
        }
    }
}
