$wsh = New-Object -ComObject WScript.Shell
$desktop = [System.Environment]::GetFolderPath('Desktop')
$shortcut = $wsh.CreateShortcut("$desktop\THAMIS Labs.lnk")
$shortcut.TargetPath = "C:\projects\LogPose4\Launch_Labs.bat"
$shortcut.WorkingDirectory = "C:\projects\LogPose4"
$shortcut.IconLocation = "cmd.exe"
$shortcut.Description = "Launch THAMIS Lab Mission Control Platform"
$shortcut.Save()
Write-Host "Desktop shortcut created successfully at: $desktop\THAMIS Labs.lnk"
