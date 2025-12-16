package com.hotelapp.utils;

/**
 * Quick utility to reset admin user.
 * Run this main method if admin login is not working.
 */
public class ResetAdminUser {
    public static void main(String[] args) {
        System.out.println("Initializing database...");
        DBInit.initDatabase();
        
        System.out.println("\nEnsuring admin user exists...");
        try {
            DBManager.ensureAdminUser("admin");
            System.out.println("✓ Admin user created/reset successfully!");
            System.out.println("  Username: admin");
            System.out.println("  Password: admin");
            System.out.println("\nYou can now login with these credentials.");
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

