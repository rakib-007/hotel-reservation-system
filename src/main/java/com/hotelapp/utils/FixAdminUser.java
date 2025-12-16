package com.hotelapp.utils;

/**
 * Quick utility to fix/reset admin user in the database.
 * Run this class to ensure admin user exists with default credentials.
 * 
 * Usage: mvn exec:java -Dexec.mainClass="com.hotelapp.utils.FixAdminUser"
 */
public class FixAdminUser {
    public static void main(String[] args) {
        try {
            System.out.println("Initializing database...");
            DBInit.initDatabase();
            
            System.out.println("\nEnsuring admin user exists...");
            DBManager.ensureDefaultAdminUser();
            
            System.out.println("\n✓ Success! Admin user is now available.");
            System.out.println("  Username: admin");
            System.out.println("  Password: admin");
            System.out.println("\nYou can now login to the application.");
        } catch (Exception e) {
            System.err.println("\n✗ Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

