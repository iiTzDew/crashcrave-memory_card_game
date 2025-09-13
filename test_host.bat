@echo off
echo Starting CrashCrave Multiplayer Test
echo.
echo Instructions:
echo 1. This will start the HOST (Player 1)
echo 2. Open another terminal and run: java CrashCrave
echo 3. In the second terminal, select "Join Multiplayer"
echo 4. Enter "localhost" as IP address
echo.
echo Expected behavior:
echo - Host should start first and be able to click cards
echo - Client should see "Not your turn!" messages
echo - When host misses, client should get turn
echo - When client misses, host should get turn back
echo - When someone matches, they continue playing
echo.
echo Starting HOST in 3 seconds...
timeout /t 3 >nul
java CrashCrave
