package com.haoren.rpc.netty.client;

import com.alibaba.fastjson.JSON;
import com.haoren.rpc.constant.RpcConstant;
import com.haoren.rpc.mo.RequestMO;
import com.haoren.rpc.mo.ResponseMO;
import com.haoren.rpc.netty.client.conn.ConnectManage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

@Slf4j
@ChannelHandler.Sharable
@Component
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private static final Map<String, SynchronousQueue<Object>> queueMap = new ConcurrentHashMap<>();

    @Value("${rpc.type}")
    private String rpcType;
    @Autowired
    ConnectManage connectManage;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("已连接到rpc服务器{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("已断开rpc服务器{}", ctx.channel().remoteAddress());
        ctx.channel().close();
        connectManage.removeChannel(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ResponseMO responseMO = JSON.parseObject(msg.toString(), ResponseMO.class);
        String requestId = responseMO.getRequestId();
        SynchronousQueue<Object> queue = queueMap.get(requestId);
        queue.put(responseMO);
        queueMap.remove(requestId);
    }

    public SynchronousQueue<Object> sendRequest(RequestMO requestMO, Channel channel) {
        SynchronousQueue<Object> queue = new SynchronousQueue<>();
        queueMap.put(requestMO.getId(), queue);
        channel.writeAndFlush(requestMO);
        return queue;
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("发送心跳消息");
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                RequestMO requestMO = new RequestMO();
                requestMO.setClassName(rpcType);
                requestMO.setMethodName(RpcConstant.heart_beat);
                ctx.channel().writeAndFlush(requestMO);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("RPC通信服务器发生异常{}", cause.getMessage());
        ctx.channel().close();
    }
}
