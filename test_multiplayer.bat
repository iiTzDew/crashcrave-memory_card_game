@echo off
echo Testing Multiplayer Functionality
echo.
echo Instructions:
echo 1. Run this script to start the host
echo 2. In another terminal, run: java Main
echo 3. Choose "Join Multiplayer" and connect to localhost
echo 4. Test the turn flow:
echo    - Host should start first
echo    - If host doesn't match, turn goes to client
echo    - If client doesn't match, turn goes back to host
echo    - When someone matches, they continue playing
echo.
echo Starting Host...
java -cp . CrashCrave