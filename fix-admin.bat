@echo off
echo ========================================
echo Fix Admin User - Quick Utility
echo ========================================
echo.
echo This will ensure the admin user exists with default credentials:
echo   Username: admin
echo   Password: admin
echo.
cd /d "%~dp0"
mvn exec:java -Dexec.mainClass="com.hotelapp.utils.FixAdminUser"
echo.
pause

