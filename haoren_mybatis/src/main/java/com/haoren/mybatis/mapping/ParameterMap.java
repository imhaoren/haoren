package com.haoren.mybatis.mapping;

import lombok.Data;

import java.util.List;

@Data
public class ParameterMap {

    private String id;
    private Class<?> type;
    private List<ParameterMapping> parameterMappings;
}
