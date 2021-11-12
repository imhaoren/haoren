package com.haoren.mybatis.mapper;

import com.haoren.mybatis.entity.User;

import java.util.List;

public interface UserMapper {

    User selectOne(String id);

    List<User> selectAll();
}
