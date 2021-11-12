package com.haoren.mybatis.mapping;

import lombok.Data;

import java.util.List;

@Data
public class BoundSql {

    private String sql;
    private List<ParameterMapping> parameterMappings;
//    private final Map<String, Object> additionalParameters;

    public BoundSql(String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
    }
}
