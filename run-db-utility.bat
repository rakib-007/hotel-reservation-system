@echo off
echo ========================================
echo Database Management Utility
echo ========================================
echo.
cd /d "%~dp0"
mvn exec:java -Dexec.mainClass="com.hotelapp.utils.DatabaseUtility"
pause

