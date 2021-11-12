package com.haoren.mybatis.session;

import com.haoren.mybatis.builder.XmlConfigBuilder;
import com.haoren.mybatis.conig.Configuration;
import com.haoren.mybatis.session.defaults.DefaultSqlSessionFactory;
import org.dom4j.DocumentException;

import java.io.InputStream;

public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(InputStream inputStream) throws ClassNotFoundException, DocumentException {
        XmlConfigBuilder xmlConfigBuilder = new XmlConfigBuilder();
        Configuration configuration = xmlConfigBuilder.builder(inputStream);
        return new DefaultSqlSessionFactory(configuration);
    }
}
