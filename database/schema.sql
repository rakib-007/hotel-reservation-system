-- ================================
-- Hotel Reservation System Schema
-- SQLite Database
-- ================================

PRAGMA foreign_keys = ON;

-- ================================
-- Rooms table
-- ================================
CREATE TABLE IF NOT EXISTS rooms (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  room_number TEXT UNIQUE NOT NULL,
  type TEXT NOT NULL,
  price REAL NOT NULL,
  status TEXT NOT NULL DEFAULT 'FREE', -- FREE, BOOKED, MAINTENANCE
  created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_rooms_type ON rooms(type);

-- ================================
-- Customers table
-- ================================
CREATE TABLE IF NOT EXISTS customers (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  phone TEXT,
  email TEXT,
  address TEXT,
  nid_passport TEXT,
  created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- Reservations table
-- Dates stored as ISO: yyyy-MM-dd
-- ================================
CREATE TABLE IF NOT EXISTS reservations (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  customer_id INTEGER NOT NULL,
  room_id INTEGER NOT NULL,
  checkin TEXT NOT NULL,      -- inclusive
  checkout TEXT NOT NULL,     -- exclusive
  status TEXT NOT NULL,       -- CONFIRMED, CANCELLED, COMPLETED
  total REAL NOT NULL,
  created_at TEXT DEFAULT CURRENT_TIMESTAMP,

  FOREIGN KEY(customer_id) REFERENCES customers(id) ON DELETE CASCADE,
  FOREIGN KEY(room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_res_room ON reservations(room_id);
CREATE INDEX IF NOT EXISTS idx_res_checkin ON reservations(checkin);

-- ================================
-- Users table (for login system)
-- ================================
CREATE TABLE IF NOT EXISTS users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  username TEXT UNIQUE NOT NULL,
  password TEXT NOT NULL,     -- ⚠ plain text for demo only
  role TEXT DEFAULT 'STAFF',
  created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Seed default admin user (username=admin, password=admin)
INSERT OR IGNORE INTO users (username, password, role)
VALUES ('admin', 'admin', 'ADMIN');

-- ================================
-- OPTIONAL: Sample Rooms (Uncomment to use)
-- ================================
-- INSERT INTO rooms (room_number, type, price, status) VALUES ('101', 'Single', 25.00, 'FREE');
-- INSERT INTO rooms (room_number, type, price, status) VALUES ('102', 'Double', 40.00, 'FREE');
-- INSERT INTO rooms (room_number, type, price, status) VALUES ('201', 'Deluxe', 75.00, 'FREE');
-- INSERT INTO rooms (room_number, type, price, status) VALUES ('301', 'Suite', 120.00, 'MAINTENANCE');

-- ================================
-- Migration: Add new customer fields and update room status
-- ================================
ALTER TABLE customers ADD COLUMN address TEXT;
ALTER TABLE customers ADD COLUMN nid_passport TEXT;
UPDATE rooms SET status = 'BOOKED' WHERE status = 'OCCUPIED';

-- ================================
-- SQL Overlap Logic (reference)
-- ================================
-- SELECT COUNT(*) FROM reservations
-- WHERE room_id = :roomId
--   AND status = 'CONFIRMED'
--   AND NOT (checkout <= :checkin OR checkin >= :checkout);
