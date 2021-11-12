package com.haoren.mybatis.builder;

import com.alibaba.druid.pool.DruidDataSource;
import com.haoren.mybatis.conig.Configuration;
import com.haoren.mybatis.resources.Resources;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class XmlConfigBuilder {

    private final Configuration configuration;

    public XmlConfigBuilder() {
        this.configuration = new Configuration();
    }

    public Configuration builder(InputStream inputStream) throws DocumentException, ClassNotFoundException {
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();
        List<Element> propertyElements = rootElement.selectNodes("//property");
        Properties properties = new Properties();
        for (Element propertyElement : propertyElements) {
            String name = propertyElement.attributeValue("name");
            String value = propertyElement.attributeValue("value");
            properties.setProperty(name, value);
        }
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(properties.getProperty("driverClass"));
        druidDataSource.setUrl(properties.getProperty("jdbcUrl"));
        druidDataSource.setUsername(properties.getProperty("username"));
        druidDataSource.setPassword(properties.getProperty("password"));
        this.configuration.setDataSource(druidDataSource);

        XmlMapperBuilder builder = new XmlMapperBuilder(this.configuration);
        List<Element> mapperElements = rootElement.selectNodes("//mapper");
        for (Element mapperElement : mapperElements) {
            String mapperPath = mapperElement.attributeValue("resource");
            builder.builder(Resources.getResourceAsStream(mapperPath));
        }
        return configuration;
    }
}
