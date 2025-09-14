@echo off
title CrashCrave - HOST (Player 1)
color 0A
echo ===================================
echo    CRASHCRAVE MULTIPLAYER HOST
echo ===================================
echo.
echo Instructions:
echo 1. This PC will be the HOST (Player 1)
echo 2. Start the CLIENT PC after this loads
echo 3. On CLIENT PC, run "multiplayer_client.bat"
echo 4. HOST plays first when game starts
echo.
echo Your IP Address:
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /i "IPv4"') do echo   %%a
echo.
echo Starting HOST in 3 seconds...
timeout /t 3 >nul
echo.
echo Loading game...
java HomeScreen
pause