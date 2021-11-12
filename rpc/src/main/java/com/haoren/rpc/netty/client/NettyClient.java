package com.haoren.rpc.netty.client;

import com.alibaba.fastjson.JSONArray;
import com.haoren.rpc.codec.JSONDecoder;
import com.haoren.rpc.codec.JSONEncoder;
import com.haoren.rpc.mo.RequestMO;
import com.haoren.rpc.mo.ResponseMO;
import com.haoren.rpc.netty.client.conn.ConnectManage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.SocketAddress;
import java.util.concurrent.SynchronousQueue;

@Slf4j
@Component
public class NettyClient implements InitializingBean {

    private EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();

    @Autowired
    NettyClientHandler clientHandler;
    @Autowired
    ConnectManage connectManage;

    public NettyClient() {
    }

    @PreDestroy
    public void destroy() {
        log.info("rpc客户端退出，释放资源");
        group.shutdownGracefully();
    }

    public String send(RequestMO requestMO) throws InterruptedException {
        Channel channel = connectManage.chooseChannel();
        log.info("channel={}", channel);
        if (null != channel && channel.isActive()) {
            SynchronousQueue<Object> queue = clientHandler.sendRequest(requestMO, channel);
            Object result = queue.take();
            return JSONArray.toJSONString(result);
        } else {
            ResponseMO res = new ResponseMO();
            res.setCode(1);
            res.setError_msg("未正确连接到服务器.请检查相关配置信息!");
            return JSONArray.toJSONString(res);
        }
    }

    public Channel doConnect(SocketAddress address) throws InterruptedException {
        ChannelFuture future = bootstrap.connect(address);
        return future.sync().channel();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        bootstrap.group(group).
                channel(NioSocketChannel.class).
                option(ChannelOption.TCP_NODELAY, true).
                option(ChannelOption.SO_KEEPALIVE, true).
                handler(new ChannelInitializer<SocketChannel>() {
                    //创建NIOSocketChannel成功后，在进行初始化时，将它的ChannelHandler设置到ChannelPipeline中，用于处理网络IO事件
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 0, 30));
                        pipeline.addLast(new JSONEncoder());
                        pipeline.addLast(new JSONDecoder());
                        pipeline.addLast("handler", clientHandler);
                    }
                });
    }
}
