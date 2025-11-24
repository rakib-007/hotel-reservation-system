@echo off
echo ========================================
echo Hotel Reservation System
echo ========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java JDK 17 or higher
    echo Download from: https://adoptium.net/
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven 3.6 or higher
    echo Download from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo Building and running the application...
echo.

REM Change to the script's directory
cd /d "%~dp0"

REM Run the application
mvn clean javafx:run

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Failed to run the application
    pause
    exit /b 1
)

