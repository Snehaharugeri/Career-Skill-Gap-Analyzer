package com.career.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/career_skill_gap?serverTimezone=UTC";
    private static final String USER = "root";
    // Recommended: set MYSQL_PASSWORD as an environment variable rather than hard-coding it.
    private static final String PASSWORD = System.getenv().getOrDefault("MYSQL_PASSWORD", "");

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
