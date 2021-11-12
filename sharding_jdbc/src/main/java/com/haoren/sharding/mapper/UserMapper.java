package com.haoren.sharding.mapper;

import com.haoren.sharding.entity.UserDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {

    @Insert("insert into user(id, username, password) values(${user.id},#{user.username},#{user.password})")
    void insert(@Param("user") UserDO userDO);

    @Select("select * from user where id = #{id}")
    UserDO findById(@Param("id") String id);
}
