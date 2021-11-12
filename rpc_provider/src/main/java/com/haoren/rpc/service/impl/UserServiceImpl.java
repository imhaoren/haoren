package com.haoren.rpc.service.impl;

import com.haoren.rpc.annotaton.RpcService;
import com.haoren.rpc.entity.UserDO;
import com.haoren.rpc.service.UserService;

@RpcService
public class UserServiceImpl implements UserService {

    @Override
    public UserDO findById(String id) {
        UserDO userDO = new UserDO();
        userDO.setId("1");
        userDO.setUsername("chenhaoren");
        return userDO;
    }
}
