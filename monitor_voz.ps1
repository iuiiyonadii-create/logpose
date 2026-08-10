Clear-Host
Write-Host '======================================================================' -ForegroundColor Yellow
Write-Host ' 🗣️ TELEPROMPTER DE VOZ EN TIEMPO REAL (LOGPOSE)' -ForegroundColor Yellow
Write-Host '======================================================================' -ForegroundColor Yellow
Write-Host 'Habla por el casco...' -ForegroundColor Gray
Write-Host ''

cmd /c "adb logcat -v time | findstr /i /c:""Sherpa local resolvió"" /c:""Vosk: Centinela detectó"" /c:""Dispatcher: Reproduciendo"" /c:""Gate: Bloqueado"""
