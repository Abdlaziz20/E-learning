package com.miniProjectApp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton JDBC connection to XAMPP MySQL
 * ─────────────────────────────────────────
 * Make sure:
 *  1. XAMPP is running (Apache + MySQL)
 *  2. You created the database using database.sql
 *  3. mysql-connector-java.jar is on the classpath
 */
public class DBConnection {

    // ── Connection Settings ──────────────────────────────────────────────
    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "elearning_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "";          // Default XAMPP: no password
    private static final String DRIVER   = "com.mysql.cj.jdbc.Driver";

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE +
        "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    // ── Singleton instance ───────────────────────────────────────────────
    private static Connection connection = null;

    private DBConnection() {}   // prevent instantiation

    /**
     * Returns a live JDBC Connection (creates it on first call).
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Connected to " + DATABASE);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. " +
                    "Add mysql-connector-java.jar to your project. Error: " + e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Safely close the connection.
     */
    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Test the connection – useful at app startup.
     */
    public static boolean testConnection() {
        try {
            Connection c = getConnection();
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            System.err.println("[DB] Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
