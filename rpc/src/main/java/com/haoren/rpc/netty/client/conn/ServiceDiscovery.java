package com.haoren.rpc.netty.client.conn;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ServiceDiscovery {

    @Value("${registry.address}")
    private String registryAddress;
    @Value("${registry.path}")
    private String zk_registry_path;
    @Autowired
    private ConnectManage connectManage;

    private volatile List<String> addressList = new ArrayList<>();

    private ZkClient client;


    @PostConstruct
    public void init() {
        client = connectServer();
        if (null != client) {
            watchNode(client);
        }
    }

    private ZkClient connectServer() {
        return new ZkClient(registryAddress, 20000, 20000);
    }

    private void watchNode(ZkClient client) {
        List<String> nodeList = client.subscribeChildChanges(zk_registry_path, (s, nodes) -> {
            log.info("监听到子节点数据变化{}", JSONObject.toJSONString(nodes));
            addressList.clear();
            getNodeData(nodes);
            updateConnectServer();
        });
        getNodeData(nodeList);
        updateConnectServer();
        log.info("已发现服务列表addressList={}", addressList);
    }

    private void getNodeData(List<String> nodes) {
        log.info("rpc子节点数据为{}", JSONObject.toJSONString(nodes));
        for (String node : nodes) {
            String address = client.readData(zk_registry_path + "/" + node);
            addressList.add(address);
        }
    }

    private void updateConnectServer() {
        connectManage.updateConnectSever(addressList);
    }
}