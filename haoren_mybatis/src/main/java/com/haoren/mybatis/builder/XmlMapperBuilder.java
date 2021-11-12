package com.haoren.mybatis.builder;

import com.haoren.mybatis.conig.Configuration;
import com.haoren.mybatis.statement.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

public class XmlMapperBuilder {

    private final Configuration configuration;

    public XmlMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void builder(InputStream inputStream) throws DocumentException, ClassNotFoundException {
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();
        String namespace = rootElement.attributeValue("namespace");
        List<Element> selectElements = rootElement.selectNodes("//select");
        for (Element selectElement : selectElements) {
            String id = selectElement.attributeValue("id");
            String parameterType = selectElement.attributeValue("parameterType");
            String resultType = selectElement.attributeValue("resultType");
            String sqlText = selectElement.getTextTrim();
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setId(id);
            if (null != parameterType && !"".equals(parameterType)) {
                mappedStatement.setParameterType(getClass(parameterType));
            }
            if (null != resultType && !"".equals(resultType)) {
                mappedStatement.setResultType(getClass(resultType));
            }
            mappedStatement.setSqlText(sqlText);
            this.configuration.getMappedStatementMap().put(namespace + "." + id, mappedStatement);
        }
    }

    private Class<?> getClass(String parameterType) throws ClassNotFoundException {
        return Class.forName(parameterType);
    }
}
