package com.haoren.sharding.controller;

import com.haoren.sharding.entity.UserDO;
import com.haoren.sharding.mapper.UserMapper;
import com.haoren.sharding.util.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private SnowflakeUtil snowflakeUtil;

    @Autowired
    private UserMapper userMapper;


    @GetMapping("/insert/{username}/{password}")
    public void insert(@PathVariable("username") String username, @PathVariable("password") String password) {
        List<Long> ids = new ArrayList<>();
        //验证随机分布，先生成5000个id，然后保存到数据库
        for (int i = 0; i < 5000; i++) {
            long id = snowflakeUtil.nextId();
            ids.add(id);
        }
        for (Long id : ids) {
            UserDO userDO = new UserDO();
            userDO.setId(id);
            userDO.setUsername(username);
            userDO.setPassword(password);
            userMapper.insert(userDO);

        }
    }

    @GetMapping("/find/{id}")
    public UserDO find(@PathVariable("id") String id) {
        return userMapper.findById(id);
    }
}
