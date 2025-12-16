package com.hotelapp.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Utility to list all users in the database.
 * Run this to verify which users exist and their details.
 * 
 * Usage: mvn compile exec:java -Dexec.mainClass="com.hotelapp.utils.ListUsers"
 */
public class ListUsers {
    public static void main(String[] args) {
        try {
            System.out.println("Initializing database...");
            DBInit.initDatabase();
            
            System.out.println("\nListing all users in the database:");
            System.out.println("=====================================");
            
            try (Connection conn = DBUtil.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, username, role, created_at FROM users ORDER BY id")) {
                
                boolean hasUsers = false;
                while (rs.next()) {
                    hasUsers = true;
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String role = rs.getString("role");
                    String createdAt = rs.getString("created_at");
                    
                    System.out.println("\nUser ID: " + id);
                    System.out.println("  Username: " + username);
                    System.out.println("  Role: " + role);
                    System.out.println("  Created: " + createdAt);
                }
                
                if (!hasUsers) {
                    System.out.println("\nNo users found in the database!");
                    System.out.println("Run FixAdminUser utility to create the admin user.");
                } else {
                    System.out.println("\n=====================================");
                    System.out.println("Total users: " + (rs.getRow() > 0 ? rs.getRow() : 0));
                }
            }
        } catch (Exception e) {
            System.err.println("\nError: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

