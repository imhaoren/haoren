package com.haoren.mybatis.statement;

import lombok.Data;

@Data
public class MappedStatement {

    private String id;

    private Class<?> parameterType;

    private Class<?> resultType;

    private String sqlText;
}
