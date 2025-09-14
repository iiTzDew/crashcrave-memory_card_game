@echo off
title Memory Game Flow Test
color 0C
echo ====================================
echo     MEMORY GAME FLOW TEST
echo ====================================
echo.
echo Expected Behavior:
echo 1. Host starts first
echo 2. Select 2 cards - if MATCH: same player continues
echo 3. Select 2 cards - if MISS: cards flip back, turn switches
echo 4. Repeat until all cards matched
echo.
echo Testing Single Player Mode for Flow...
echo.
timeout /t 2 >nul
java HomeScreen
pause