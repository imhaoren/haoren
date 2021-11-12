package com.haoren.mybatis.executor;

import com.haoren.mybatis.conig.Configuration;
import com.haoren.mybatis.statement.MappedStatement;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public interface Executor {

    <T> List<T> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws SQLException, IntrospectionException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchFieldException;
}
