@echo off
REM Terminal ID Generator for KALCPOS
REM Generates a unique identifier for this installation

if exist ".terminal-id" (
    echo Terminal ID already exists:
    type .terminal-id
) else (
    powershell -Command "[guid]::NewGuid().ToString()" > .terminal-id 2>nul
    if errorlevel 1 (
        REM Fallback using random
        set /p TERMINAL_ID=<nul
        powershell -Command "$id = -join ((1..36) | ForEach-Object {('a','b','c','d','e','f','0','1','2','3','4','5','6','7','8','9' | Get-Random)}; Write-Host $id"
    )
    echo Terminal ID generated:
    type .terminal-id
)
