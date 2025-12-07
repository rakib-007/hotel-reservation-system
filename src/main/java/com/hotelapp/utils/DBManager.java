package com.hotelapp.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for database management operations:
 * - Delete all data from database
 * - Change admin password
 */
public class DBManager {

    /**
     * Deletes all data from rooms, customers, and reservations tables.
     * Keeps the users table intact.
     */
    public static void deleteAllData() throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete in order to respect foreign key constraints
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("DELETE FROM reservations");
                    stmt.executeUpdate("DELETE FROM customers");
                    stmt.executeUpdate("DELETE FROM rooms");
                    // Reset auto-increment counters
                    stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name IN ('reservations', 'customers', 'rooms')");
                }
                conn.commit();
                System.out.println("All data deleted successfully.");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Changes the admin password.
     * @param newPassword The new password to set
     * @throws SQLException If the update fails
     */
    public static void changeAdminPassword(String newPassword) throws SQLException {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        String sql = "UPDATE users SET password = ? WHERE username = 'admin'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword.trim());
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Admin user not found. Please ensure admin user exists.");
            }
            System.out.println("Admin password changed successfully.");
        }
    }

    /**
     * Creates admin user if it doesn't exist, or updates password if it does.
     * @param password The password to set
     * @throws SQLException If the operation fails
     */
    public static void ensureAdminUser(String password) throws SQLException {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        String sql = "INSERT OR REPLACE INTO users (username, password, role) VALUES ('admin', ?, 'ADMIN')";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, password.trim());
            ps.executeUpdate();
            System.out.println("Admin user ensured (created or updated).");
        }
    }
}

