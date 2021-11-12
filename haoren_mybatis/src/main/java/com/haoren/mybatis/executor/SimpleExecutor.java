package com.haoren.mybatis.executor;

import com.haoren.mybatis.conig.Configuration;
import com.haoren.mybatis.mapping.BoundSql;
import com.haoren.mybatis.mapping.ParameterMapping;
import com.haoren.mybatis.parsing.GenericTokenParser;
import com.haoren.mybatis.parsing.ParameterMappingTokenHandler;
import com.haoren.mybatis.statement.MappedStatement;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor {


    @Override
    public <T> List<T> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws SQLException, IntrospectionException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchFieldException {
        Connection connection = configuration.getDataSource().getConnection();
        String sqlText = mappedStatement.getSqlText();
        BoundSql boundSql = getBoundSql(sqlText);

        //获取预处理对象
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
        //设置参数
        for (int i = 0; i < boundSql.getParameterMappings().size(); i++) {
            preparedStatement.setObject(i + 1, params[0]);
        }
        //执行sql
        ResultSet resultSet = preparedStatement.executeQuery();
        //封装返回结果集
        Class<?> resultType = mappedStatement.getResultType();
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            T t = (T) resultType.newInstance();
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                //字段名
                String columnName = metaData.getColumnName(i);
                //字段值
                Object value = resultSet.getObject(columnName);
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultType);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(t, value);
            }
            list.add(t);
        }
        return list;
    }

    private BoundSql getBoundSql(String sqlText) {
        //标记处理类：配置标记解析器来完成对占位符的解析处理工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        //解析出来的sql
        String parseSql = genericTokenParser.parse(sqlText);
        //#{}里面解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        return new BoundSql(parseSql, parameterMappings);
    }
}
