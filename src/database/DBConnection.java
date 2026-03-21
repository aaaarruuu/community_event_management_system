//package database;
//
//import java.sql.*;
//import javax.swing.JOptionPane;
//
///**
// * Database Connection Manager
// * Handles all database connectivity using JDBC
// */
//public class DBConnection {
//
//    // Database credentials
//    private static final String DB_URL = "jdbc:mysql://localhost:3306/community_events_db";
//    private static final String DB_USER = "root";
//    private static final String DB_PASSWORD = "@Aryan1310"; // Change as per your MySQL setup
//
//    private static Connection connection = null;
//
//    /**
//     * Establishes connection to MySQL database
//     * @return Connection object
//     */
//    public static Connection getConnection() {
//        try {
//            if (connection == null || connection.isClosed()) {
//                // Load MySQL JDBC Driver
//                Class.forName("com.mysql.cj.jdbc.Driver");
//
//                // Establish connection
//                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//                System.out.println("Database connected successfully!");
//            }
//        } catch (ClassNotFoundException e) {
//            JOptionPane.showMessageDialog(null,
//                    "MySQL JDBC Driver not found!\n" + e.getMessage(),
//                    "Driver Error",
//                    JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(null,
//                    "Database connection failed!\n" + e.getMessage(),
//                    "Connection Error",
//                    JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
//        return connection;
//    }
//
//    /**
//     * Closes database connection
//     */
//    public static void closeConnection() {
//        try {
//            if (connection != null && !connection.isClosed()) {
//                connection.close();
//                System.out.println("Database connection closed.");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Test database connection
//     * @return true if connection successful, false otherwise
//     */
//    public static boolean testConnection() {
//        try {
//            Connection conn = getConnection();
//            return conn != null && !conn.isClosed();
//        } catch (SQLException e) {
//            return false;
//        }
//    }
//
//    /**
//     * Execute SELECT query and return ResultSet
//     * @param query SQL SELECT query
//     * @return ResultSet
//     */
//    public static ResultSet executeQuery(String query) {
//        try {
//            Connection conn = getConnection();
//            Statement stmt = conn.createStatement();
//            return stmt.executeQuery(query);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * Execute INSERT, UPDATE, DELETE queries
//     * @param query SQL query
//     * @return number of rows affected
//     */
//    public static int executeUpdate(String query) {
//        try {
//            Connection conn = getConnection();
//            Statement stmt = conn.createStatement();
//            return stmt.executeUpdate(query);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return -1;
//        }
//    }
//}


package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/community_event_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "@Aryan1310"; // ⚠️ CHANGE THIS!

    // Connection instance (Singleton pattern)
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Create connection
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                System.out.println("✅ Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            System.err.println("Make sure mysql-connector-java-8.0.28.jar is in classpath");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            System.err.println("Check database URL, username, and password");
            e.printStackTrace();
        }

        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection");
            e.printStackTrace();
        }
    }

    public static boolean testConnection() {
        Connection conn = getConnection();
        return conn != null;
    }

    public static void main(String[] args) {
        System.out.println("Testing Database Connection...");
        System.out.println("================================");

        if (testConnection()) {
            System.out.println("✅ SUCCESS! Database is connected.");
            System.out.println("You can now use the application.");
        } else {
            System.out.println("❌ FAILED! Could not connect to database.");
            System.out.println("\nTroubleshooting:");
            System.out.println("1. Make sure MySQL is running");
            System.out.println("2. Check database name: community_event_db");
            System.out.println("3. Update DB_PASSWORD in DBConnection.java");
            System.out.println("4. Verify mysql-connector-java jar is in classpath");
        }

        closeConnection();
    }
}