-- ================================
-- Delete All Data Script
-- ================================
-- This script deletes all data from rooms, customers, and reservations tables.
-- The users table is preserved.
-- 
-- WARNING: This action cannot be undone!
-- ================================

-- Delete all reservations first (due to foreign key constraints)
DELETE FROM reservations;

-- Delete all customers
DELETE FROM customers;

-- Delete all rooms
DELETE FROM rooms;

-- Reset auto-increment counters
DELETE FROM sqlite_sequence WHERE name IN ('reservations', 'customers', 'rooms');

-- Verify deletion
SELECT 'Reservations count: ' || COUNT(*) FROM reservations;
SELECT 'Customers count: ' || COUNT(*) FROM customers;
SELECT 'Rooms count: ' || COUNT(*) FROM rooms;

