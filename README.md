# Hotel Reservation System

A JavaFX-based hotel reservation management system with SQLite database.

## Requirements

- **Java JDK 17 or higher** (JDK 25 recommended)
- **Maven 3.6+** (for building from source)

## Running on Windows

### Option 1: Using Maven (Recommended for Development)

1. **Install Java JDK 17+** if not already installed
   - Download from: https://adoptium.net/
   - Verify installation: Open Command Prompt and run `java -version`

2. **Install Maven** if not already installed
   - Download from: https://maven.apache.org/download.cgi
   - Add Maven to PATH
   - Verify: Run `mvn -version` in Command Prompt

3. **Navigate to project directory**:
   ```cmd
   cd "hotel-reservation"
   ```

4. **Run the application**:
   ```cmd
   mvn clean javafx:run
   ```

### Option 2: Using Pre-built JAR (After Building)

1. **Build the executable JAR**:
   ```cmd
   mvn clean package
   ```

2. **Run the JAR**:
   ```cmd
   java --module-path "target/modules" --add-modules javafx.controls,javafx.fxml -jar target/hotel-reservation-1.0-SNAPSHOT.jar
   ```

   Or simply double-click `run-windows.bat` (see below)

### Option 3: Using Batch File (Easiest)

1. **Double-click `run-windows.bat`** in the project root
   - This will automatically build and run the application

## Building for Distribution

### Create Standalone JAR (Fat JAR)

The project is configured to create a JAR with all dependencies included.

```cmd
mvn clean package
```

The JAR will be created at: `target/hotel-reservation-1.0-SNAPSHOT.jar`

### Create Windows Executable (.exe)

To create a Windows executable, you can use `jpackage` (requires JDK 14+):

```cmd
jpackage --input target --name "Hotel Reservation System" --main-jar hotel-reservation-1.0-SNAPSHOT.jar --main-class com.hotelapp.MainApp --type exe --win-dir-chooser --win-menu --win-shortcut
```

## Default Login Credentials

- **Username:** `admin`
- **Password:** `admin`

## Database

- Database file: `database/hotel.db`
- Schema: `database/schema.sql`
- Database is automatically created on first run

## Features

- Room Management
- Guest Management
- Reservation Booking
- Guest Information Search
- Purchase History
- Dashboard with Statistics

## Troubleshooting

### "Java not found" error
- Install Java JDK 17+ and add it to PATH
- Verify with: `java -version`

### "Maven not found" error
- Install Maven and add it to PATH
- Verify with: `mvn -version`

### Application won't start
- Make sure Java JDK 17+ is installed
- Check that all dependencies are downloaded: `mvn clean compile`
- Check console for error messages

## Project Structure

```
hotel-reservation/
├── src/main/java/com/hotelapp/
│   ├── MainApp.java
│   ├── controllers/
│   ├── models/
│   ├── dao/
│   ├── services/
│   └── utils/
├── src/main/resources/
│   ├── fxml/
│   └── styles/
├── database/
│   ├── schema.sql
│   └── hotel.db
└── pom.xml
```

