package com.haoren.rpc.netty.server;

import com.haoren.rpc.annotaton.RpcService;
import com.haoren.rpc.codec.JSONDecoder;
import com.haoren.rpc.codec.JSONEncoder;
import com.haoren.rpc.constant.RpcConstant;
import com.haoren.rpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class NettyServer implements ApplicationContextAware, InitializingBean {

    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(4);

    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    @Value("${rpc.server.address}")
    private String serverAddress;
    @Value("${rpc.type}")
    private String rpcType;

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RpcService.class);
        for (Map.Entry<String, Object> stringObjectEntry : beansWithAnnotation.entrySet()) {
            Object serviceBean = stringObjectEntry.getValue();
            Class<?> clz = serviceBean.getClass();
            Class<?>[] interfaces = clz.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                String serviceName = anInterface.getName();
                log.info("加载服务类:{}", serviceName);
                serviceMap.put(serviceName, serviceBean);
            }
        }
        log.info("服务类已加载完成serviceMap={}", serviceMap);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    private void start() {
        NettyServerHandler nettyServerHandler = new NettyServerHandler(serviceMap);
        new Thread(() -> {
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline pipeline = socketChannel.pipeline();
                                pipeline.addLast(new IdleStateHandler(0, 0, 60));
                                pipeline.addLast(new JSONEncoder());
                                pipeline.addLast(new JSONDecoder());
                                pipeline.addLast(nettyServerHandler);
                            }
                        });

                String[] arrays = serverAddress.split(":");
                String host = arrays[0];
                int port = Integer.parseInt(arrays[1]);
                ChannelFuture channelFuture = bootstrap.bind(host, port);
                log.info("rpc服务器启动，监听端口{}", port);
                serviceRegistry.registry(serverAddress);
                channelFuture.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }).start();
    }
}
