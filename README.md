# CrashCrave Memory Card Game

A Java Swing-based memory card matching game with both single-player and multiplayer modes.

## Features

### Single Player Mode
- Match pairs of food-themed cards
- Track your score (matches) and errors
- 5x8 grid with 20 different card types (40 total cards)
- Restart game functionality

### Multiplayer Mode
- Two-player network gameplay
- Host/Join functionality
- Real-time score tracking for both players
- Synchronized game state between players
- Turn-based gameplay

## How to Compile and Run

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Windows PowerShell (or command prompt)

### Compilation
```powershell
javac *.java
```

### Running the Game
```powershell
java CrashCrave
```

### Alternative - Using App.java
```powershell
java App
```

## Game Modes

### 1. Single Player
- Select "Single Player" when prompted
- Click cards to reveal them
- Match pairs to score points
- Try to complete the game with minimum errors

### 2. Host Multiplayer
- Select "Host Multiplayer" when prompted
- Enter your name
- Wait for another player to join
- Game starts automatically when both players are connected

### 3. Join Multiplayer
- Select "Join Multiplayer" when prompted
- Enter the host's IP address (default: localhost)
- Enter your name
- Game starts when connection is established

## Game Rules

1. **Card Matching**: Click two cards to reveal them
2. **Scoring**: 
   - Successful match = +1 point
   - Failed match = +1 error (single player) or turn switches (multiplayer)
3. **Winning**: Player with most matches wins (multiplayer)
4. **Restart**: Click "Restart Game" to start over

## Technical Implementation

### Object-Oriented Design Features
- **Inheritance**: Player class hierarchy (LocalPlayer, RemotePlayer)
- **Polymorphism**: Game interface, Player abstract class
- **Encapsulation**: Card class with private fields
- **Abstraction**: Game and ICard interfaces

### Network Architecture
- Client-Server model using TCP sockets
- ObjectOutputStream/ObjectInputStream for message passing
- Threaded message listening
- Synchronized game state

### Class Structure
```
CrashCrave (main game class)
├── Player (abstract)
│   ├── LocalPlayer (human player)
│   └── RemotePlayer (network player)
├── NetworkHandler (network communication)
├── Card (implements ICard)
├── RoundedButton (custom UI component)
└── RoundedLabel (custom UI component)
```

## File Structure
```
src/
├── CrashCrave.java      # Main game class
├── App.java             # Alternative entry point
├── Player.java          # Abstract player class
├── LocalPlayer.java     # Local human player
├── RemotePlayer.java    # Remote network player
├── NetworkHandler.java  # Network communication
├── TestGame.java        # Test runner
└── CrashCraveimages/    # Game images folder
    ├── back.jpg         # Card back image
    ├── burger.jpg       # Food card images
    ├── cake.jpg
    └── ... (other food images)
```

## Troubleshooting

### Common Issues
1. **Image not found errors**: Ensure all images are in `CrashCraveimages/` folder
2. **Network connection failed**: Check firewall settings and IP addresses
3. **Port already in use**: Try restarting the application or use different port

### Image Requirements
- Images should be in JPG format
- Place all card images in `CrashCraveimages/` folder
- Required images: back.jpg (card back) + 20 unique food images

## Multiplayer Network Protocol

### Message Types
- `FLIP:index` - Card flip notification
- `MATCH:boolean` - Match result
- `TURN:playerName` - Turn change
- `BOARD:seed` - Board shuffle seed
- `RESTART:seed` - Game restart with new seed

### Default Network Settings
- **Host IP**: localhost (127.0.0.1)
- **Port**: 12345
- **Protocol**: TCP

## Future Enhancements
- [ ] Difficulty levels (different grid sizes)
- [ ] Sound effects
- [ ] Animation effects
- [ ] Tournament mode
- [ ] Save/Load game progress
- [ ] Custom themes
