package com.haoren.rpc.registry;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServiceRegistry {

    @Value("${registry.address}")
    private String registryAddress;
    @Value("${registry.path}")
    private String zk_registry_path;

    public void registry(String data) {
        if (null != data) {
            ZkClient zkClient = connectZkServer();
            addRootNode(zkClient);
            createNode(zkClient, data);
        }
    }

    private ZkClient connectZkServer() {
        return new ZkClient(registryAddress, 20000, 20000);
    }

    private void addRootNode(ZkClient client) {
        boolean exists = client.exists(zk_registry_path);
        if (!exists) {
            client.createPersistent(zk_registry_path);
            log.info("创建zookeeper主节点{}", zk_registry_path);
        }
    }

    private void createNode(ZkClient client, String data) {
        String path = client.create(zk_registry_path + "/provider", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        log.info("创建zookeeper数据节点path={},data={}", path, data);
    }
}
