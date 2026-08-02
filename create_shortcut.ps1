$wsh = New-Object -ComObject WScript.Shell
$shortcut = $wsh.CreateShortcut('C:\Users\uriel\Desktop\THAMIS Lab OS.lnk')
$shortcut.TargetPath = 'C:\Users\uriel\.gemini\antigravity\scratch\logpose'
$shortcut.WorkingDirectory = 'C:\Users\uriel\.gemini\antigravity\scratch\logpose'
$shortcut.Description = 'THAMIS Lab OS — Mission Control Platform'
$shortcut.Save()
Write-Host "Shortcut created successfully!"
