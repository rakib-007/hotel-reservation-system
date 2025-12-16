@echo off
echo ========================================
echo Reset Admin User
echo ========================================
echo.
echo This will ensure the admin user exists.
echo Default credentials: admin / admin
echo.
cd /d "%~dp0"
mvn exec:java -Dexec.mainClass="com.hotelapp.utils.ResetAdminUser"
pause

