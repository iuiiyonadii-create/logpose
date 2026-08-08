@echo off
title THAMIS Lab OS - Launching...
cd /d "%~dp0"
echo Starting THAMIS Lab Mission Control...
call gradlew.bat :ui:mission-control:run
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Labs failed to start.
    pause
)
