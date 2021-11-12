package com.haoren.mybatis.conig;

import com.haoren.mybatis.statement.MappedStatement;
import lombok.Data;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Configuration {

    private DataSource dataSource;

    private Map<String, MappedStatement> mappedStatementMap = new ConcurrentHashMap<>();
}
