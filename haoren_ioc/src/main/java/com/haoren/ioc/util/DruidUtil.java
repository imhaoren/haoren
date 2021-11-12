package com.haoren.ioc.util;

import java.sql.Connection;

public class DruidUtil {

    public static DruidUtil getInstance() {
        return new DruidUtil();
    }

    public Connection getConnection() {
        System.out.println("getConnection");
        return null;
    }
}
