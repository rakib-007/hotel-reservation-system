package com.hotelapp.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Simple helper to obtain JDBC connections to the local SQLite DB.
 * Use try-with-resources where possible:
 *
 * try (Connection c = DBUtil.getConnection()) { ... }
 */
public class DBUtil {
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DBInit.getJdbcUrl());
    }
}
