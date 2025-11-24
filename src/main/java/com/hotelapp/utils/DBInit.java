package com.hotelapp.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * DBInit - robust, defensive initialization of SQLite DB from database/schema.sql.
 */
public class DBInit {
    private static final Path DB_FOLDER = Paths.get("database");
    private static final Path SCHEMA_FILE = DB_FOLDER.resolve("schema.sql");
    private static final Path DB_FILE = DB_FOLDER.resolve("hotel.db");
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_FILE.toString();

    public static void initDatabase() {
        try {
            if (!Files.exists(DB_FOLDER)) {
                Files.createDirectories(DB_FOLDER);
            }

            try (Connection conn = DriverManager.getConnection(JDBC_URL)) {
                // enable foreign keys
                try (Statement pragma = conn.createStatement()) {
                    pragma.execute("PRAGMA foreign_keys = ON;");
                }

                if (Files.exists(SCHEMA_FILE)) {
                    String sql = Files.readString(SCHEMA_FILE, StandardCharsets.UTF_8);
                    String[] statements = sql.split(";");
                    for (String raw : statements) {
                        String stmt = raw.trim();
                        if (stmt.isEmpty()) continue;
                        // skip SQL comment-only lines
                        if (stmt.startsWith("--")) continue;
                        try (Statement s = conn.createStatement()) {
                            s.execute(stmt);
                        } catch (Exception e) {
                            System.out.println("Warning: skipping SQL statement due to: " + e.getMessage());
                        }
                    }
                } else {
                    System.out.println("Warning: schema.sql not found at: " + SCHEMA_FILE.toAbsolutePath());
                }

                // Migrate existing database: add new columns if they don't exist
                try (Statement migrate = conn.createStatement()) {
                    // Check if customers table exists
                    var tableCheck = migrate.executeQuery(
                            "SELECT name FROM sqlite_master WHERE type='table' AND name='customers';");
                    boolean customersTableExists = tableCheck.next();
                    tableCheck.close();
                    
                    if (customersTableExists) {
                        // Check if address column exists by querying table info
                        var colCheck = migrate.executeQuery("PRAGMA table_info(customers);");
                        boolean hasAddress = false;
                        boolean hasNidPassport = false;
                        while (colCheck.next()) {
                            String colName = colCheck.getString("name");
                            if ("address".equalsIgnoreCase(colName)) hasAddress = true;
                            if ("nid_passport".equalsIgnoreCase(colName)) hasNidPassport = true;
                        }
                        colCheck.close();
                        
                        // Add address column if it doesn't exist
                        if (!hasAddress) {
                            try {
                                migrate.execute("ALTER TABLE customers ADD COLUMN address TEXT");
                                System.out.println("Migration: Added 'address' column to customers table.");
                            } catch (Exception ex) {
                                System.out.println("Migration: Failed to add address column: " + ex.getMessage());
                            }
                        }
                        
                        // Add nid_passport column if it doesn't exist
                        if (!hasNidPassport) {
                            try {
                                migrate.execute("ALTER TABLE customers ADD COLUMN nid_passport TEXT");
                                System.out.println("Migration: Added 'nid_passport' column to customers table.");
                            } catch (Exception ex) {
                                System.out.println("Migration: Failed to add nid_passport column: " + ex.getMessage());
                            }
                        }
                    }
                    
                    // Update room status from OCCUPIED to BOOKED if needed
                    try {
                        int updated = migrate.executeUpdate("UPDATE rooms SET status = 'BOOKED' WHERE status = 'OCCUPIED'");
                        if (updated > 0) {
                            System.out.println("Migration: Updated " + updated + " room(s) status from OCCUPIED to BOOKED.");
                        }
                    } catch (Exception ex) {
                        // Ignore if no rows to update or table doesn't exist
                    }
                } catch (Exception e) {
                    System.out.println("Migration check error: " + e.getMessage());
                }

                // seed sample rooms safely if rooms table exists and is empty
                try (Statement check = conn.createStatement()) {
                    var rs = check.executeQuery(
                            "SELECT name FROM sqlite_master WHERE type='table' AND name='rooms';");
                    boolean roomsTableExists = rs.next();
                    rs.close();

                    if (roomsTableExists) {
                        var rs2 = check.executeQuery("SELECT COUNT(*) AS cnt FROM rooms;");
                        int cnt = 0;
                        if (rs2.next()) cnt = rs2.getInt("cnt");
                        rs2.close();

                        if (cnt == 0) {
                            try (Statement seed = conn.createStatement()) {
                                seed.executeUpdate("INSERT OR IGNORE INTO rooms (room_number, type, price, status) VALUES " +
                                        "('101','Single',25.0,'FREE')," +
                                        "('102','Double',40.0,'FREE')," +
                                        "('201','Deluxe',75.0,'FREE')," +
                                        "('301','Suite',120.0,'MAINTENANCE');");
                                System.out.println("Seeded sample rooms.");
                            } catch (Exception se) {
                                System.out.println("Seeding failed: " + se.getMessage());
                            }
                        }
                    } else {
                        System.out.println("rooms table not present yet - skipping seed.");
                    }
                } catch (Exception e) {
                    System.out.println("Seed check error: " + e.getMessage());
                }
            }

            System.out.println("DB init complete at: " + DB_FILE.toAbsolutePath());
        } catch (IOException ioe) {
            System.err.println("I/O error during DB init: " + ioe.getMessage());
            ioe.printStackTrace();
        } catch (Exception ex) {
            System.err.println("DB init error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static String getJdbcUrl() {
        return JDBC_URL;
    }
}
