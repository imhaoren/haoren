package com.haoren.rpc.netty.client.conn;

import com.haoren.rpc.netty.client.NettyClient;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ConnectManage {

    @Autowired
    private NettyClient client;

    private AtomicInteger roundRobin = new AtomicInteger(0);
    private CopyOnWriteArrayList<Channel> channels = new CopyOnWriteArrayList<>();
    private Map<SocketAddress, Channel> channelNodes = new ConcurrentHashMap<>();

    public Channel chooseChannel() {
        if (channels.size() > 0) {
            int size = channels.size();
            int index = (roundRobin.getAndAdd(1) + size) % size;
            return channels.get(index);
        } else {
            return null;
        }
    }

    public synchronized void updateConnectSever(List<String> addressList) {
        if (null == addressList || addressList.size() == 0) {
            log.info("没有可用的服务器节点,全部服务节点已关闭");
            for (Channel channel : channels) {
                SocketAddress socketAddress = channel.remoteAddress();
                Channel handler_node = channelNodes.get(socketAddress);
                handler_node.close();
            }
            channels.clear();
            channelNodes.clear();
            return;
        }
        Set<SocketAddress> newAllServerNodeSet = new HashSet<>();
        for (String address : addressList) {
            String[] arrays = address.split(":");
            String host = arrays[0];
            int port = Integer.parseInt(arrays[1]);
            SocketAddress remotePeer = new InetSocketAddress(host, port);
            newAllServerNodeSet.add(remotePeer);
        }
        for (SocketAddress serverNodeAddress : newAllServerNodeSet) {
            Channel channel = channelNodes.get(serverNodeAddress);
            if (null != channel && channel.isOpen()) {
                log.info("当前服务节点已存在,无需重新连接{}", serverNodeAddress);
            } else {
                connectServerNode(serverNodeAddress);
            }
        }
        for (int i = 0; i < channels.size(); i++) {
            Channel channel = channels.get(i);
            SocketAddress remotePeer = channel.remoteAddress();
            if (!newAllServerNodeSet.contains(remotePeer)) {
                log.info("删除失效的服务节点{}", remotePeer);
                Channel channel_node = channelNodes.get(remotePeer);
                if (null != channel_node) {
                    channel_node.close();
                }
                channels.remove(channel);
                channelNodes.remove(remotePeer);
            }
        }
    }

    private void connectServerNode(SocketAddress address) {
        try {
            Channel channel = client.doConnect(address);
            addChannel(channel, address);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.info("未能成功连接到服务器:{}", address);
        }
    }

    private void addChannel(Channel channel, SocketAddress address) {
        log.info("加入Channel到连接管理器.{}", address);
        channels.add(channel);
        channelNodes.put(address, channel);
    }

    public void removeChannel(Channel channel) {
        log.info("从连接管理器中移除失效Channel{}", channel.remoteAddress());
        SocketAddress remotePeer = channel.remoteAddress();
        channelNodes.remove(remotePeer);
        channels.remove(channel);
    }
}
