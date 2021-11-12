package com.haoren.mybatis.test;

import com.haoren.mybatis.entity.User;
import com.haoren.mybatis.mapper.UserMapper;
import com.haoren.mybatis.resources.Resources;
import com.haoren.mybatis.session.SqlSession;
import com.haoren.mybatis.session.SqlSessionFactory;
import com.haoren.mybatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.List;

public class Test {

    public static void main(String[] args) throws Exception {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = userMapper.selectOne("1");
        System.out.println(user);
        List<User> users = userMapper.selectAll();
        System.out.println(users);
    }
}
