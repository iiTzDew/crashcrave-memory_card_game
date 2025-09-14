@echo off
title CrashCrave - CLIENT (Player 2)
color 0B
echo ===================================
echo   CRASHCRAVE MULTIPLAYER CLIENT
echo ===================================
echo.
echo Instructions:
echo 1. This PC will be the CLIENT (Player 2)
echo 2. Make sure HOST PC is already running
echo 3. When game starts, select "Join Multiplayer"
echo 4. Enter the HOST PC's IP address
echo 5. CLIENT waits for HOST to play first
echo.
echo Starting CLIENT in 2 seconds...
timeout /t 2 >nul
echo.
echo Loading game...
java HomeScreen
pause