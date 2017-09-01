package com.ydttech.iot.util.db;

import java.sql.*;

public class DBUtil implements IDBUtil {

    private String driverName;
    private Connection connection;
    private Statement statement;

    public DBUtil(String driverName) throws ClassNotFoundException {

        this.driverName = driverName;

        Class.forName(driverName);
    }

    @Override
    public Connection createConn(String url, boolean isAutoCommit) throws SQLException {

        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(isAutoCommit);
        }

        return connection;
    }

    @Override
    public Statement createStmt(int resSetType, int resConcurrency) throws SQLException {

        if (statement == null || statement.isClosed()) {
            if (driverName.equals(MQYSL_DRIVER_NBAME)) {
                statement = connection.createStatement(
                        resSetType, resConcurrency
                );
            } else if (driverName.equals(SQLITE_DRIVER_NAME)) {
                statement = connection.createStatement();
            } else
                statement = connection.createStatement();
        }

        return statement;
    }

    @Override
    public void stopConn() throws SQLException {

        if (statement != null && !statement.isClosed())
            statement.close();
        if (connection != null && !connection.isClosed())
            connection.close();
    }
}
