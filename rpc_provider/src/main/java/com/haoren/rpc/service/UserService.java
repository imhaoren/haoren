package com.haoren.rpc.service;

import com.haoren.rpc.entity.UserDO;

public interface UserService {

    UserDO findById(String id);
}
