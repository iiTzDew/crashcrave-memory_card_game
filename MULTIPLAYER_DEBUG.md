# Multiplayer Turn Management Debug Guide

## Current Implementation Status
✅ **Turn switching logic implemented correctly**
✅ **Match = player continues, Miss = turn switches**  
✅ **Network synchronization in place**
✅ **Single player mode fixed**

## How to Test Multiplayer Properly

### Step 1: Start Host
```powershell
java CrashCrave
```
- Select **"Host Multiplayer"** (NOT Single Player!)
- Enter your name (e.g., "Alice")
- See "Waiting for another player..." dialog
- **Wait for client to connect**

### Step 2: Start Client (Different Terminal)
```powershell
java CrashCrave
```
- Select **"Join Multiplayer"**
- Enter your name (e.g., "Bob") 
- Enter **"localhost"** as IP
- Game should start immediately

### Step 3: Test Turn Management
1. **Host (Alice) should be able to click cards first**
2. **Client (Bob) should see "Not your turn!" in console**
3. **When Alice misses → Bob gets turn**
4. **When Bob misses → Alice gets turn**
5. **When someone matches → they continue**

## Debug Console Messages to Look For

### Successful Host Start:
```
Game is ready! Alice starts first.
```

### Successful Turn Switching:
```
Player Alice clicked card 5
Alice missed! Turn will switch.
Cards hidden, turn switched!
Turn switched to: Bob, isMyTurn: false
```

### Client Console When It's Not Their Turn:
```
Not your turn! Current player: Alice
```

### When Someone Gets a Match:
```
Alice got a match! They continue playing.
```

## Common Issues & Solutions

### Issue 1: "Not working" = Single Player Selected
**Problem**: If you select "Single Player", there's no turn management
**Solution**: Must select "Host Multiplayer" or "Join Multiplayer"

### Issue 2: Both Players Can Click
**Problem**: Network connection failed or not established
**Solution**: Check that both selected multiplayer modes and connected

### Issue 3: No Console Messages
**Problem**: Game might be in wrong mode
**Solution**: Look for the debug messages listed above

### Issue 4: Cards Don't Respond
**Problem**: Game not ready or turn management issue
**Solution**: Wait for "Game is ready!" message

## Testing Checklist

- [ ] Host selected "Host Multiplayer"
- [ ] Client selected "Join Multiplayer"  
- [ ] Client entered "localhost" as IP
- [ ] Both players entered names
- [ ] Console shows "Game is ready!" message
- [ ] Host can click cards initially
- [ ] Client sees "Not your turn!" when clicking
- [ ] Turn switches after misses
- [ ] Player continues after matches

## Expected Turn Flow Example

```
Host starts → Host misses → Client turn → Client misses → 
Host turn → Host matches → Host continues → Host matches → 
Host continues → Host misses → Client turn → etc.
```

This implements perfect memory game rules!
