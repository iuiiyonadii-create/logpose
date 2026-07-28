@echo off
title LogPose PC Monitor - THAMIS
color 0a
cd /d "%~dp0"
echo Iniciando Monitor de Inteligencia LogPose...
python logpose_pc_gui.py
if %errorlevel% neq 0 (
    echo.
    echo ERROR: No se pudo iniciar el monitor.
    echo Asegurate de tener Python instalado y en el PATH.
    pause
)
