package com.haoren.mybatis.parsing;

import com.haoren.mybatis.mapping.ParameterMapping;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ParameterMappingTokenHandler implements TokenHandler {

    private List<ParameterMapping> parameterMappings = new ArrayList<>();

    public String handleToken(String content) {
        parameterMappings.add(buildParameterMapping(content));
        return "?";
    }

    private ParameterMapping buildParameterMapping(String content) {
        ParameterMapping parameterMapping = new ParameterMapping(content);
        return parameterMapping;
    }
}
