package com.haoren.rpc.controller;

import com.alibaba.fastjson.JSONObject;
import com.haoren.rpc.entity.UserDO;
import com.haoren.rpc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/findById/{id}")
    public UserDO findUserById(@PathVariable("id") String id) {
        log.info("根据id查询用户id={}", id);
        UserDO userDO = userService.findById(id);
        log.info("userDO={}", JSONObject.toJSONString(userDO));
        return userDO;
    }
}
