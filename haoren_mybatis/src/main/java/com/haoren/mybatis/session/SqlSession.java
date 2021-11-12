package com.haoren.mybatis.session;

import java.util.List;

public interface SqlSession {

    <T> T selectOne(String statementId, Object... params) throws Exception;

    <T> List<T> selectList(String statementId, Object... params) throws Exception;

    <T> T getMapper(Class<?> mapperClass);
}
