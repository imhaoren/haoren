package com.haoren.mybatis.session.defaults;

import com.haoren.mybatis.conig.Configuration;
import com.haoren.mybatis.session.SqlSession;
import com.haoren.mybatis.session.SqlSessionFactory;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
