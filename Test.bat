@echo off
setlocal
cd /d "%~dp0"
if not exist build mkdir build
javac -d build src\*.java
if errorlevel 1 exit /b 1
java -cp build UniversitySystemTest
exit /b %errorlevel%
