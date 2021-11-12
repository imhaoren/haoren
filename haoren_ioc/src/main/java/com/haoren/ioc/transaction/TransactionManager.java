package com.haoren.ioc.transaction;


import com.haoren.ioc.annotation.Autowired;
import com.haoren.ioc.util.ConnectionUtil;

import java.sql.SQLException;

public class TransactionManager {

    @Autowired
    private ConnectionUtil connectionUtil;

    public void beginTransaction() throws SQLException {
        connectionUtil.getCurrentConnection().setAutoCommit(false);
    }

    public void commitTransaction() throws SQLException {
        connectionUtil.getCurrentConnection().commit();
    }

    public void rollback() throws SQLException {
        connectionUtil.getCurrentConnection().rollback();
    }
}
