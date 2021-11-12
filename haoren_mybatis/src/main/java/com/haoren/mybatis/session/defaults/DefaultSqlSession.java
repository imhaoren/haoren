package com.haoren.mybatis.session.defaults;

import com.haoren.mybatis.conig.Configuration;
import com.haoren.mybatis.executor.SimpleExecutor;
import com.haoren.mybatis.session.SqlSession;
import com.haoren.mybatis.statement.MappedStatement;

import java.lang.reflect.*;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {
        List<Object> objects = selectList(statementId, params);
        if (objects.size() != 1) {
            throw new RuntimeException("查询结果为空或者返回结果过多");
        }
        return (T) objects.get(0);
    }

    @Override
    public <T> List<T> selectList(String statementId, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        List<Object> list = simpleExecutor.query(configuration, mappedStatement, params);
        return (List<T>) list;
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        Object proxyInstance = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();
                String statementId = className + "." + methodName;
                Type returnType = method.getGenericReturnType();
                //是否进行了泛型参数化(集合返回selectList,单个对象返回selectOne)
                if (returnType instanceof ParameterizedType) {
                    return selectList(statementId, args);
                }
                return selectOne(statementId, args);
            }
        });
        return (T) proxyInstance;
    }
}
