package com.haoren.mybatis.mapping;

import lombok.Data;

@Data
public class ParameterMapping {

    private String content;

    public ParameterMapping(String content) {
        this.content = content;
    }
}