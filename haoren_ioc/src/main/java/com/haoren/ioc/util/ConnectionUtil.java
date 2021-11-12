package com.haoren.ioc.util;

import java.sql.Connection;

public class ConnectionUtil {

    //存储当前线程的数据库连接
    private ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    public Connection getCurrentConnection() {
        Connection connection = threadLocal.get();
        if (null == connection) {
            // TODO
            DruidUtil.getInstance().getConnection();
        }
        return connection;
    }
}
