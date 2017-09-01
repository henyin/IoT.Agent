package com.ydttech.iot.util.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public interface IDBUtil {

    String MQYSL_DRIVER_NBAME = "com.mysql.jdbc.Driver";
    String SQLITE_DRIVER_NAME = "org.sqlite.JDBC";

    Connection createConn(String url, boolean isAutoCommit) throws SQLException;
    Statement createStmt(int resSetType, int resConcurrency) throws SQLException;
    void stopConn() throws SQLException;

}
