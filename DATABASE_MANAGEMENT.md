# Database Management Guide

This guide explains how to delete all data from the database and change the admin password.

## Method 1: Using SQL Scripts (Manual)

### Delete All Data

1. Open the database file: `database/hotel.db` using a SQLite browser (e.g., DB Browser for SQLite)
2. Run the SQL script: `database/delete_all_data.sql`
   - Or execute these commands directly:
   ```sql
   DELETE FROM reservations;
   DELETE FROM customers;
   DELETE FROM rooms;
   DELETE FROM sqlite_sequence WHERE name IN ('reservations', 'customers', 'rooms');
   ```

**Note:** This will delete all rooms, customers, and reservations. The `users` table (including admin login) will be preserved.

### Change Admin Password

1. Open the database file: `database/hotel.db` using a SQLite browser
2. Run the SQL script: `database/change_admin_password.sql`
   - **Important:** Edit the script first and replace `'your_new_password'` with your desired password
   - Or execute this command directly:
   ```sql
   UPDATE users SET password = 'your_new_password' WHERE username = 'admin';
   ```

## Method 2: Using the Database Utility Application

A standalone JavaFX utility application is available to manage the database:

1. **Run the utility application:**
   ```bash
   cd hotel-reservation
   mvn exec:java -Dexec.mainClass="com.hotelapp.utils.DatabaseUtility"
   ```

2. **Delete All Data:**
   - Click the "Delete All Data" button
   - Confirm the deletion in the popup dialog

3. **Change Admin Password:**
   - Enter the new password in the password field
   - Click "Change Password"

## Method 3: Using Java Code (Programmatic)

You can also use the `DBManager` utility class in your code:

```java
import com.hotelapp.utils.DBManager;

// Delete all data
try {
    DBManager.deleteAllData();
    System.out.println("All data deleted successfully");
} catch (SQLException e) {
    e.printStackTrace();
}

// Change admin password
try {
    DBManager.changeAdminPassword("new_password_here");
    System.out.println("Password changed successfully");
} catch (SQLException e) {
    e.printStackTrace();
}
```

## Default Login Credentials

- **Username:** `admin`
- **Password:** `admin` (default)

## Important Notes

- **Backup First:** Always backup your database file (`database/hotel.db`) before performing destructive operations
- **Password Security:** The current implementation stores passwords in plain text. For production use, consider implementing password hashing
- **Database Location:** The database file is located at: `hotel-reservation/database/hotel.db`

## Using SQLite Command Line

If you have SQLite installed, you can also use the command line:

```bash
# Navigate to the database directory
cd hotel-reservation/database

# Open SQLite
sqlite3 hotel.db

# Delete all data
DELETE FROM reservations;
DELETE FROM customers;
DELETE FROM rooms;
DELETE FROM sqlite_sequence WHERE name IN ('reservations', 'customers', 'rooms');

# Change admin password
UPDATE users SET password = 'new_password' WHERE username = 'admin';

# Exit SQLite
.quit
```

