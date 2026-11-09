@echo off
setlocal
cd /d "%~dp0"
if not exist build mkdir build
javac -d build src\*.java
if errorlevel 1 (
  echo Compilation failed. Install JDK 17 or newer and ensure javac is on PATH.
  pause
  exit /b 1
)
java -cp build UniversityApp
if errorlevel 1 pause
