Rakibul Islam Sifat  
Student ID: 242-15-353  
DIU Email: sifat242-15-353@diu.edu.bd  
Personal Email: rakibulislamsifat0@gmail.com  

⚠️ **Note:** This project is created for **Daffodil International University (DIU) students**, specifically for **OOP course work**.  

# Hotel Reservation System

A JavaFX-based hotel reservation management system with SQLite database.

## Requirements

- **Java JDK 25 (only supported version)**  
- **Maven 3.6+** (for building from source)

## Running on Windows

### ⚠️ IMPORTANT: Correct Directory

**The project is in the `hotel-reservation` subdirectory!**

If you're in: `D:\Downloads\Hotel Reservation System\Hotel Reservation System`
You need to navigate to: `D:\Downloads\Hotel Reservation System\Hotel Reservation System\hotel-reservation`

### Option 1: Easiest - Double-click START_HERE.bat

1. **Go to the parent directory** (where you see `START_HERE.bat`)
2. **Double-click `START_HERE.bat`**
   - This automatically navigates to the correct folder and runs the app

### Option 2: Using Maven (Recommended for Development)

1. **Install Java JDK 25** if not already installed
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

### Option 3: Using Batch File in hotel-reservation folder

1. **Navigate to `hotel-reservation` folder first**
2. **Double-click `run-windows.bat`** in the `hotel-reservation` folder
   - This will automatically build and run the application

## Building for Distribution

### Create Standalone JAR (Fat JAR)

The project is configured to create a JAR with all dependencies included.

```cmd
mvn clean package
```

The JAR will be created at: `target/hotel-reservation-1.0-SNAPSHOT.jar`

### Create Windows Executable (.exe)

To create a Windows executable, you can use `jpackage` (requires JDK 14+, tested with JDK 25):

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
- Install Java JDK 25 and add it to PATH
- Verify with: `java -version`

### "Maven not found" error
- Install Maven and add it to PATH
- Verify with: `mvn -version`

### "no POM in this directory" or "No plugin found for prefix 'javafx'"
- **You're in the wrong directory!**
- Navigate to the `hotel-reservation` subdirectory first
- Or use `START_HERE.bat` from the parent directory

### Application won't start
- Make sure Java JDK 25 is installed
- Make sure you're in the `hotel-reservation` directory (where `pom.xml` is located)
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

