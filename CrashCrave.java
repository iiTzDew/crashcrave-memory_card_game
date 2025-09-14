// the main game class 
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.net.*;
import java.io.*;
import javax.swing.*;

// Custom rounded button class
class RoundedButton extends JButton {
    private int radius;
    
    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setForeground(Color.WHITE); // Set text color to white
    }
    
    @Override
    public Color getForeground() {
        return Color.WHITE; // Always return white text color
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Always draw black background regardless of button state
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        
        // Draw white border
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        
        g2.dispose();
        super.paintComponent(g);
    }
}

// Custom rounded label class
class RoundedLabel extends JLabel {
    private int radius;
    
    public RoundedLabel(String text, int radius) {
        super(text);
        this.radius = radius;
        setOpaque(false);
        setHorizontalAlignment(JLabel.CENTER);
        setForeground(Color.WHITE); // Set text color to white
    }
    
    @Override
    public Color getForeground() {
        return Color.WHITE; // Always return white text color
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Always draw black background
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        
        // Draw white border
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        
        g2.dispose();
        super.paintComponent(g);
    }
}

// Step 1: Abstraction and Polymorphism
interface Game {
    void start();
}

interface ICard {
    String getName();
    ImageIcon getImageIcon();
}

public class CrashCrave extends JFrame implements ActionListener, Game {
    // Step 2: Card implements ICard and uses encapsulation
    private class Card implements ICard {
        private String cardName;
        private ImageIcon cardImageIcon;

        public Card(String cardName, ImageIcon cardImageIcon){
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }

        @Override
        public String getName() {
            return cardName;
        }

        @Override
        public ImageIcon getImageIcon() {
            return cardImageIcon;
        }

        @Override
        public String toString(){
            return cardName;
        }
    }

    private String[] cardlist = { //card names 
        "burger","coffee","cookies","fries","pizza","popcorn","bread","brownie",
        "cake","chocolates","chocos","cinnomanroll","donut","hotchoco","icecream",
        "iclaires","salad","sandwitch","spagatti","waffle"
    };

    private int rows = 5;
    private int columns = 8;
    private int cardWidth = 65;  // Increased from 45
    private int cardHeight = 90; // Increased from 64

    private ArrayList<Card> cardSet; //create a deck of cards with cardnames and cardimageicons
    private ImageIcon cardBackImageIcon;
  
    private int boardWidth = columns*cardWidth;
    private int boardHeight = rows*cardHeight;

    private RoundedLabel textLabel = new RoundedLabel("", 15);
    private JPanel textPanel = new JPanel();
    private JPanel boardPanel = new JPanel();
    private JPanel restartGamePanel = new JPanel();
    private RoundedButton restartButton = new RoundedButton("", 15);
    private RoundedButton quitButton = new RoundedButton("", 15);
    private RoundedLabel marksLabel = new RoundedLabel("", 15);

    private int errorCount = 0;
    private ArrayList<JButton> board;
    private Timer hideCardTimer;
    private boolean gameReady = false;
    private JButton card1Selected;
    private JButton card2Selected;
    
    // Game over overlay components
    private JPanel gameOverOverlay;
    private boolean gameOverShown = false;

    // New fields for multiplayer
    private Player player1; // Host
    private Player player2; // Client
    private Player currentPlayer;
    private NetworkHandler network;
    private boolean isMultiplayer = false;
    private boolean isMyTurn = false;
    private long shuffleSeed = 0;

    public CrashCrave() {
        setUpCards();
        initializeUI();
        
        // Default behavior - show mode selection dialog
        showModeSelection();
        
        // Don't call setupGameBoard() here - it will be called after mode selection
        pack();
        setVisible(true);
        
        // Initialize timer but don't start it yet
        hideCardTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });        
        hideCardTimer.setRepeats(false);
    }
    
    // Constructor for HomeScreen integration
    public CrashCrave(boolean showDialog) {
        setUpCards();
        initializeUI();
        
        if (showDialog) {
            showModeSelection();
            setupGameBoard();
            pack();
            setVisible(true);
            
            // Initialize and start timer
            hideCardTimer = new Timer(1500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    hideCards();
                }
            });        
            hideCardTimer.setRepeats(false);
            hideCardTimer.start();
        } else {
            // Just initialize timer but don't start it yet - will be started when game mode is selected
            hideCardTimer = new Timer(1500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    hideCards();
                }
            });        
            hideCardTimer.setRepeats(false);
        }
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        // Increase window size to better showcase the background
        setSize(Math.max(boardWidth + 100, 700), Math.max(boardHeight + 150, 600));
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("CrashCrave - Memory Card Game");

        textLabel.setFont(new Font("Arial", Font.BOLD, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("ERRORS: " + Integer.toString(errorCount));
        textLabel.setForeground(Color.WHITE);
        textLabel.setOpaque(true);
        textLabel.setBackground(new Color(0, 0, 0, 180)); // Semi-transparent black background

        // Create MARKS display with same format as ERRORS
        marksLabel.setFont(new Font("Arial", Font.BOLD, 20));
        marksLabel.setHorizontalAlignment(JLabel.CENTER);
        marksLabel.setText("MARKS: " + Integer.toString(0));
        marksLabel.setForeground(Color.WHITE);
        marksLabel.setOpaque(true);
        marksLabel.setBackground(new Color(0, 0, 0, 180)); // Semi-transparent black background

        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.setLayout(new GridLayout(1, 2)); // 1 row, 2 columns for 50-50 split
        textPanel.add(marksLabel);
        textPanel.add(textLabel);
        add(textPanel, BorderLayout.NORTH);
    }
    
    private void showModeSelection() {
        // Mode selection
        String[] options = {"Single Player", "Host Multiplayer", "Join Multiplayer"};
        int choice = JOptionPane.showOptionDialog(null, "Choose mode", "Game Mode",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            startSinglePlayerMode();
            setupGameBoard();
            pack();
            // Don't start the hide timer - cards should already be hidden
            gameReady = true;
            restartButton.setEnabled(true);
        } else if (choice == 1) {
            startMultiplayerHostMode();
            setupGameBoard();
            pack();
            // Don't start the hide timer - cards should already be hidden
            gameReady = true;
            restartButton.setEnabled(true);
        } else if (choice == 2) {
            startMultiplayerClientMode();
            setupGameBoard();
            pack();
            // Don't start the hide timer - cards should already be hidden
            gameReady = true;
            restartButton.setEnabled(true);
        }
    }
    
    // Public methods for HomeScreen integration
    public void startSinglePlayer() {
        startSinglePlayerMode();
        setupGameBoard();
        
        // Ensure timer is properly initialized for single player
        if (hideCardTimer == null) {
            hideCardTimer = new Timer(1500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Timer triggered - calling hideCards()");
                    hideCards();
                }
            });        
            hideCardTimer.setRepeats(false);
        }
        
        pack(); // Repack after setting up board
        setVisible(true); // Make sure it's visible
        // Don't start hide timer - cards should already be hidden
        gameReady = true;
        restartButton.setEnabled(true);
        quitButton.setEnabled(true);
        System.out.println("Single player mode started - cards will flip back when they don't match");
    }
    
    public void startMultiplayerHost() {
        startMultiplayerHostMode();
        setupGameBoard();
        pack(); // Repack after setting up board
        setVisible(true); // Make sure it's visible
        // Don't start hide timer - cards should already be hidden
        gameReady = true;
        restartButton.setEnabled(true);
    }
    
    public void startMultiplayerHost(String playerName) {
        startMultiplayerHostMode(playerName);
        setupGameBoard();
        pack(); // Repack after setting up board
        setVisible(true); // Make sure it's visible
        // Don't start hide timer - cards should already be hidden
        gameReady = true;
        restartButton.setEnabled(true);
    }
    
    public void startMultiplayerClient() {
        startMultiplayerClientMode();
        setupGameBoard();
        pack(); // Repack after setting up board
        setVisible(true); // Make sure it's visible
        // Don't start hide timer - cards should already be hidden
        gameReady = true;
        restartButton.setEnabled(true);
    }
    
    public void startMultiplayerClient(String playerName, String hostIP) {
        startMultiplayerClientMode(playerName, hostIP);
        setupGameBoard();
        pack(); // Repack after setting up board
        setVisible(true); // Make sure it's visible
        // Don't start hide timer - cards should already be hidden
        gameReady = true;
        restartButton.setEnabled(true);
    }
    
    private void startSinglePlayerMode() {
        isMultiplayer = false;
        shuffleCards(new Random()); // Random shuffle for single
        marksLabel.setText("MARKS: 0");
        textLabel.setText("ERRORS: 0");
        
        // Initialize single player
        player1 = new LocalPlayer("Player", this) {
            @Override
            public void makeMove(int cardIndex) {
                // Single player - just flip the card locally
                game.flipCard(cardIndex);
            }
        };
        currentPlayer = player1;
        isMyTurn = true;
    }
    
    private void startMultiplayerHostMode() {
        isMultiplayer = true;
        String hostIP = "localhost";
        int port = 12345;

        // Get player name BEFORE network setup
        String myName = JOptionPane.showInputDialog("Enter your name:");
        if (myName == null || myName.trim().isEmpty()) {
            myName = "Host Player";
        }

        try {
            JOptionPane.showMessageDialog(null, "Waiting for another player to join...\nServer started on port " + port);
            network = new NetworkHandler(this, true, hostIP, port);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection failed: " + e.getMessage());
            System.exit(1);
        }

        String opponentName = "Client";

        // Host setup: Host is Player 1, Client is Player 2
        player1 = new LocalPlayer(myName, this);        // Host = Player 1
        player2 = new RemotePlayer(opponentName, this); // Client = Player 2
        currentPlayer = player1;
        isMyTurn = true; // Host starts first
        shuffleSeed = new Random().nextLong();
        network.sendMessage("BOARD:" + shuffleSeed);
        shuffleCards(new Random(shuffleSeed));
        System.out.println("HOST: I am " + player1.getName() + " (Player 1), opponent is " + player2.getName() + " (Player 2)");

        updateScores();
        gameReady = true; // Enable game interaction for host
    }
    
    private void startMultiplayerHostMode(String playerName) {
        isMultiplayer = true;
        String hostIP = "localhost";
        int port = 12345;

        try {
            // Don't show dialog - HomeScreen will handle this
            network = new NetworkHandler(this, true, hostIP, port);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection failed: " + e.getMessage());
            System.exit(1);
        }

        String opponentName = "Client";

        // Host setup: Host is Player 1, Client is Player 2
        player1 = new LocalPlayer(playerName, this);        // Host = Player 1
        player2 = new RemotePlayer(opponentName, this); // Client = Player 2
        currentPlayer = player1;
        isMyTurn = true; // Host starts first
        shuffleSeed = new Random().nextLong();
        network.sendMessage("BOARD:" + shuffleSeed);
        shuffleCards(new Random(shuffleSeed));
        System.out.println("HOST: I am " + player1.getName() + " (Player 1), opponent is " + player2.getName() + " (Player 2)");

        updateScores();
        gameReady = true; // Enable game interaction for host
    }
    
    private void startMultiplayerClientMode() {
        isMultiplayer = true;
        String hostIP = "localhost";
        int port = 12345;

        // Get player name BEFORE network setup
        String myName = JOptionPane.showInputDialog("Enter your name:");
        if (myName == null || myName.trim().isEmpty()) {
            myName = "Guest Player";
        }

        hostIP = JOptionPane.showInputDialog("Enter host IP:");
        if (hostIP == null || hostIP.trim().isEmpty()) {
            hostIP = "localhost";
        }

        try {
            network = new NetworkHandler(this, false, hostIP, port);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection failed: " + e.getMessage());
            System.exit(1);
        }

        String opponentName = "Host";

        // Client setup: Host is Player 1, Client is Player 2  
        player1 = new RemotePlayer(opponentName, this); // Host = Player 1
        player2 = new LocalPlayer(myName, this);        // Client = Player 2
        currentPlayer = player1; // Host (Player 1) starts first
        isMyTurn = false; // Client waits for host's turn
        System.out.println("CLIENT: I am " + player2.getName() + " (Player 2), opponent is " + player1.getName() + " (Player 1)");

        updateScores();
        // Client will get gameReady = true when BOARD message is received
    }
    
    private void startMultiplayerClientMode(String playerName, String hostIP) {
        isMultiplayer = true;
        int port = 12345;

        try {
            network = new NetworkHandler(this, false, hostIP, port);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection failed: " + e.getMessage());
            System.exit(1);
        }

        String opponentName = "Host";

        // Client setup: Host is Player 1, Client is Player 2  
        player1 = new RemotePlayer(opponentName, this); // Host = Player 1
        player2 = new LocalPlayer(playerName, this);        // Client = Player 2
        currentPlayer = player1; // Host (Player 1) starts first
        isMyTurn = false; // Client waits for host's turn
        System.out.println("CLIENT: I am " + player2.getName() + " (Player 2), opponent is " + player1.getName() + " (Player 1)");

        updateScores();
        // Client will get gameReady = true when BOARD message is received
    }
    
    private void setupGameBoard() {
        // Remove previous components completely
        this.getContentPane().removeAll();
        
        // Recreate the text panel (MARKS and ERRORS)
        textPanel = new JPanel();
        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.setLayout(new GridLayout(1, 2));
        textPanel.setOpaque(false); // Make transparent to show background
        textPanel.add(marksLabel);
        textPanel.add(textLabel);
        add(textPanel, BorderLayout.NORTH);
        
        // Create a main panel with background image
        JPanel mainGamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load and draw HomeScreen background image
                try {
                    ImageIcon bgImage = new ImageIcon(getClass().getResource("homeScreenImage/HomeScreen.png"));
                    if (bgImage.getImageLoadStatus() == MediaTracker.COMPLETE) {
                        // Draw background image scaled to fit the panel
                        g.drawImage(bgImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                        
                        // Add a semi-transparent overlay to make cards more visible
                        g.setColor(new Color(0, 0, 0, 100)); // 100/255 = ~40% transparency
                        g.fillRect(0, 0, getWidth(), getHeight());
                    } else {
                        // Fallback if image not found
                        g.setColor(new Color(45, 45, 45));
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                } catch (Exception e) {
                    // Fallback design
                    g.setColor(new Color(45, 45, 45));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    System.out.println("Could not load background image: " + e.getMessage());
                }
            }
        };
        mainGamePanel.setLayout(new BorderLayout());
        
        // Recreate the board panel
        boardPanel = new JPanel();
        boardPanel.removeAll();
        boardPanel.setOpaque(false); // Make transparent to show background
        board = new ArrayList<JButton>();
        boardPanel.setLayout(new GridLayout(rows, columns));
        
        // Add some padding around the card grid
        JPanel paddedBoardPanel = new JPanel(new BorderLayout());
        paddedBoardPanel.setOpaque(false); // Make transparent
        paddedBoardPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        paddedBoardPanel.add(boardPanel, BorderLayout.CENTER);
        
        // Ensure we create exactly rows * columns buttons
        int totalSlots = rows * columns;
        for (int i = 0; i < totalSlots; i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true);
            // Add white borders around cards for better definition against background
            tile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1)
            ));
            // Show all cards face down at the start
            tile.setIcon(cardBackImageIcon);
            tile.setFocusable(false);
            tile.addActionListener(this);
            board.add(tile);
            boardPanel.add(tile);
        }
        
        // Add the padded board panel to the main game panel
        mainGamePanel.add(paddedBoardPanel, BorderLayout.CENTER);
        add(mainGamePanel, BorderLayout.CENTER);

        // Recreate restart button panel
        restartGamePanel = new JPanel();
        restartGamePanel.setLayout(new GridLayout(1, 2, 10, 0)); // 1 row, 2 columns with 10px gap
        restartGamePanel.setOpaque(false); // Make transparent to show background
        
        // Setup Restart Button
        restartButton.setFont(new Font("Arial", Font.BOLD, 16));
        restartButton.setText("Restart Game");
        restartButton.setPreferredSize(new Dimension(boardWidth/2 - 5, 35));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);
        restartButton.setForeground(Color.WHITE);
        restartButton.setBackground(new Color(34, 139, 34)); // Forest Green
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameReady) {
                    return;
                }
                gameReady = false;
                restartButton.setEnabled(false);
                quitButton.setEnabled(false);
                card1Selected = null;
                card2Selected = null;
                errorCount = 0;
                
                // Reset game over overlay
                removeGameOverOverlay();
                
                if (isMultiplayer) {
                    player1.score = 0;
                    player2.score = 0;
                    shuffleSeed = new Random().nextLong();
                    shuffleCards(new Random(shuffleSeed));
                    network.sendMessage("RESTART:" + shuffleSeed);
                    currentPlayer = player1;
                    isMyTurn = (currentPlayer instanceof LocalPlayer);
                } else {
                    // Single player restart
                    if (player1 != null) {
                        player1.score = 0;
                    }
                    shuffleCards(new Random());
                }
                // Re-assign buttons with new cards - start face down
                for (int i = 0; i < board.size(); i++) {
                    board.get(i).setIcon(cardBackImageIcon);
                }
                updateScores();
                gameReady = true;
                restartButton.setEnabled(true);
                quitButton.setEnabled(true);
            }
        });
        
        // Setup Quit Button
        quitButton.setFont(new Font("Arial", Font.BOLD, 16));
        quitButton.setText("Quit Game");
        quitButton.setPreferredSize(new Dimension(boardWidth/2 - 5, 35));
        quitButton.setFocusable(false);
        quitButton.setEnabled(true); // Always enabled
        quitButton.setForeground(Color.WHITE);
        quitButton.setBackground(new Color(220, 20, 60)); // Crimson Red
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show confirmation dialog
                int choice = JOptionPane.showConfirmDialog(
                    CrashCrave.this,
                    "Are you sure you want to quit the game?",
                    "Quit Game",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (choice == JOptionPane.YES_OPTION) {
                    // Close network connections if in multiplayer mode
                    if (isMultiplayer && network != null) {
                        try {
                            network.close();
                        } catch (Exception ex) {
                            System.out.println("Error closing network: " + ex.getMessage());
                        }
                    }
                    // Exit the application
                    System.exit(0);
                }
            }
        });
        
        restartGamePanel.add(restartButton);
        restartGamePanel.add(quitButton);
        add(restartGamePanel, BorderLayout.SOUTH);

        //start game 
        hideCardTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });        
        hideCardTimer.setRepeats(false);
        // Don't start the timer automatically - cards should already be hidden
    }

    // Implement start() from Game (abstraction)
    @Override
    public void start() {
        // Game starts in constructor
    }

    // Implement actionPerformed() from ActionListener (polymorphism)
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("=== CARD CLICK DEBUG ===");
        System.out.println("gameReady: " + gameReady);
        System.out.println("hideCardTimer.isRunning(): " + hideCardTimer.isRunning());
        System.out.println("isMultiplayer: " + isMultiplayer);
        System.out.println("isMyTurn: " + isMyTurn);
        System.out.println("currentPlayer: " + (currentPlayer != null ? currentPlayer.getName() : "null"));
        
        if (!gameReady) {
            System.out.println("Game not ready yet...");
            return;
        }
        
        // Don't allow new clicks while timer is running (cards are being hidden)
        if (hideCardTimer.isRunning()) {
            System.out.println("Timer is running, ignoring click until cards are hidden");
            return;
        }
        
        if (isMultiplayer && !isMyTurn) {
            System.out.println("Not your turn! Current player: " + currentPlayer.getName());
            return;
        }
        
        JButton tile = (JButton) e.getSource();
        if (tile.getIcon() == cardBackImageIcon) {
            int index = board.indexOf(tile);
            System.out.println("Player " + currentPlayer.getName() + " clicked card " + index);
            currentPlayer.makeMove(index);
        } else {
            System.out.println("Card already revealed, ignoring click");
        }
    }

    public void flipCard(int index) {
        JButton tile = board.get(index);
        
        // Prevent clicking the same card twice or clicking when timer is running
        if (tile == card1Selected || tile == card2Selected) {
            System.out.println("Card already selected, ignoring click");
            return;
        }
        
        // Stop any running timer to prevent conflicts
        if (hideCardTimer.isRunning()) {
            hideCardTimer.stop();
            System.out.println("Stopped running timer due to new card selection");
        }
        
        tile.setIcon(cardSet.get(index).getImageIcon());

        if (card1Selected == null) {
            card1Selected = tile;
            System.out.println("First card selected at index " + index);
        } else if (card2Selected == null) {
            card2Selected = tile;
            System.out.println("Second card selected at index " + index + ", checking for match...");
            checkForMatch();
        }
    }

    private void checkForMatch() {
        // Ensure we have both cards selected
        if (card1Selected == null || card2Selected == null) {
            System.out.println("ERROR: checkForMatch called without both cards selected!");
            return;
        }
        
        // Get the indices of the selected cards to compare their actual card objects
        int index1 = board.indexOf(card1Selected);
        int index2 = board.indexOf(card2Selected);
        
        // Validate indices
        if (index1 == -1 || index2 == -1) {
            System.out.println("ERROR: Invalid card indices in checkForMatch!");
            return;
        }
        
        // Compare the card names instead of ImageIcons for more reliable matching
        boolean match = cardSet.get(index1).getName().equals(cardSet.get(index2).getName());
        
        System.out.println("Comparing cards: " + cardSet.get(index1).getName() + " vs " + cardSet.get(index2).getName() + " -> " + (match ? "MATCH" : "NO MATCH"));
        
        if (match) {
            // MATCH: Player scores and continues playing
            currentPlayer.incrementScore();
            updateScores();
            if (isMultiplayer) {
                network.sendMessage("MATCH:true");
            }
            card1Selected = null;
            card2Selected = null;
            System.out.println(currentPlayer.getName() + " got a match! They continue playing.");
            
            // CRITICAL FIX: Re-enable game for next selection after match
            gameReady = true;
            System.out.println("gameReady set to true after match - player can continue!");
            
            // Player continues - NO turn switch
            
            // Check if all cards are matched
            if (allCardsMatched()) {
                System.out.println("Game Over! All cards matched.");
                showGameOverOverlay();
                // Don't set gameReady = false - let the overlay handle game end
            }
        } else {
            // MISS: Show cards briefly, then hide them
            errorCount++;
            updateScores(); // Update error count display
            System.out.println("=== MISS DETECTED ===");
            System.out.println(currentPlayer.getName() + " missed! Cards will be hidden after delay.");
            System.out.println("gameReady before timer: " + gameReady);
            System.out.println("isMyTurn before timer: " + isMyTurn);
            
            if (isMultiplayer) {
                network.sendMessage("MATCH:false");
                System.out.println("Starting hide timer for multiplayer miss...");
                // Use consistent timer approach for multiplayer
                hideCardTimer.stop(); // Stop any running timer first
                hideCardTimer.restart(); // Restart with fresh delay
            } else {
                // Single player - hide the cards after a delay to show the mismatch
                System.out.println("Starting hide timer for single player mode");
                hideCardTimer.stop();
                hideCardTimer.restart();
            }
        }

        if (allCardsMatched()) {
            showGameOverOverlay();
        }
    }

    private boolean allCardsMatched() {
        for (JButton b : board) {
            if (b.getIcon() == cardBackImageIcon) return false;
        }
        return true;
    }

    private void switchTurn() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
        isMyTurn = (currentPlayer instanceof LocalPlayer);
        System.out.println("=== TURN SWITCH DEBUG ===");
        System.out.println("Turn switched to: " + currentPlayer.getName() + ", isMyTurn: " + isMyTurn);
        System.out.println("Player1: " + player1.getName() + " (Local: " + (player1 instanceof LocalPlayer) + ")");
        System.out.println("Player2: " + player2.getName() + " (Local: " + (player2 instanceof LocalPlayer) + ")");
        System.out.println("gameReady: " + gameReady);
        network.sendMessage("TURN:" + currentPlayer.getName());
    }

    private void updateScores() {
        if (isMultiplayer) {
            marksLabel.setText(player1.getName() + ": " + player1.getScore());
            textLabel.setText(player2.getName() + ": " + player2.getScore());
        } else {
            // Single player mode - show marks and errors
            marksLabel.setText("MARKS: " + (player1 != null ? player1.getScore() : 0));
            textLabel.setText("ERRORS: " + errorCount);
        }
    }

    public void handleReceivedMessage(String message) {
        if (message.startsWith("FLIP:")) {
            int index = Integer.parseInt(message.split(":")[1]);
            flipCard(index);
        } else if (message.startsWith("MATCH:")) {
            boolean match = Boolean.parseBoolean(message.split(":")[1]);
            if (match) {
                // Opponent made a match - they continue playing
                currentPlayer.incrementScore();
                updateScores();
                card1Selected = null;
                card2Selected = null;
                System.out.println(currentPlayer.getName() + " got a match and continues!");
                
                // CRITICAL FIX: Enable game for opponent to continue
                gameReady = true;
                System.out.println("gameReady set to true after opponent match!");
                System.out.println("DEBUG: After opponent match - gameReady: " + gameReady + ", isMyTurn: " + isMyTurn);
                // NO turn switch - opponent continues
            } else {
                // Opponent missed - we need to hide cards and switch turn
                errorCount++;
                updateScores();
                System.out.println(currentPlayer.getName() + " missed! Starting hide timer on receiving side.");
                System.out.println("DEBUG: After opponent miss - gameReady: " + gameReady + ", isMyTurn: " + isMyTurn);
                // Start hide timer on the receiving side too
                hideCardTimer.start();
            }
        } else if (message.startsWith("TURN:")) {
            String turnName = message.split(":")[1];
            System.out.println("=== RECEIVED TURN MESSAGE ===");
            System.out.println("Received TURN message: " + turnName);
            currentPlayer = turnName.equals(player1.getName()) ? player1 : player2;
            isMyTurn = (currentPlayer instanceof LocalPlayer);
            System.out.println("Updated turn - Current: " + currentPlayer.getName() + ", isMyTurn: " + isMyTurn);
            System.out.println("gameReady: " + gameReady);
            System.out.println("Player1: " + player1.getName() + " (Local: " + (player1 instanceof LocalPlayer) + ")");
            System.out.println("Player2: " + player2.getName() + " (Local: " + (player2 instanceof LocalPlayer) + ")");
        } else if (message.startsWith("BOARD:")) {
            shuffleSeed = Long.parseLong(message.split(":")[1]);
            shuffleCards(new Random(shuffleSeed));
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardSet.get(i).getImageIcon());
            }
            hideCardTimer.start();
            gameReady = true; // Enable game interaction for client
        } else if (message.startsWith("RESTART:")) {
            shuffleSeed = Long.parseLong(message.split(":")[1]);
            player1.score = 0;
            player2.score = 0;
            shuffleCards(new Random(shuffleSeed));
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardSet.get(i).getImageIcon());
            }
            currentPlayer = player1;
            isMyTurn = (currentPlayer instanceof LocalPlayer);
            updateScores();
            hideCardTimer.start();
        }
    }

    public void sendMoveToRemote(int index) {
        if (network != null) {
            network.sendMessage("FLIP:" + index);
        }
    }

    void setUpCards(){
        cardSet = new ArrayList<Card>();
        
        // Only use first 20 cards to fit in 5x8 grid (40 total with pairs)
        String[] selectedCards = new String[20];
        System.arraycopy(cardlist, 0, selectedCards, 0, 20);
        
        // Create a default image in case some images can't be loaded
        ImageIcon defaultIcon = null;
        
        for(String cardName: selectedCards){
            //load the card images 
            java.net.URL imageUrl = getClass().getResource("CrashCraveimages/" + cardName + ".jpg");
            if (imageUrl == null) {
                imageUrl = getClass().getResource("./CrashCraveimages/" + cardName + ".jpg");
            }
            
            ImageIcon cardImageIcon;
            if (imageUrl != null) {
                Image cardImg = new ImageIcon(imageUrl).getImage();
                cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
            } else {
                System.out.println("Warning: Image not found: " + cardName + ".jpg - using default");
                // Create a simple colored icon as fallback
                if (defaultIcon == null) {
                    defaultIcon = new ImageIcon();
                }
                cardImageIcon = defaultIcon;
            }

            Card card = new Card(cardName, cardImageIcon);
            cardSet.add(card);
        }
        
        // Ensure we have exactly 20 cards even if some images failed to load
        while (cardSet.size() < 20) {
            Card dummyCard = new Card("dummy" + cardSet.size(), defaultIcon);
            cardSet.add(dummyCard);
        }
        
        // Create pairs for memory game - duplicate each card once
        ArrayList<Card> cardPairs = new ArrayList<Card>();
        for (Card card : cardSet) {
            cardPairs.add(card); // First copy
            cardPairs.add(card); // Second copy (pair)
        }
        cardSet = cardPairs; 

        //load the back card image 
        java.net.URL backImageUrl = getClass().getResource("CrashCraveimages/back.jpg");
        if (backImageUrl == null) {
            backImageUrl = getClass().getResource("./CrashCraveimages/back.jpg");
        }
        if (backImageUrl != null) {
            Image cardBackImg = new ImageIcon(backImageUrl).getImage();
            cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
        } else {
            System.out.println("Error: Back image not found!");
        }
    }    

    void shuffleCards(Random rand){
        for(int i=0; i<cardSet.size(); i++){
            int j = rand.nextInt(cardSet.size());
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }
    }

    void hideCards(){
        System.out.println("hideCards() called - gameReady: " + gameReady + 
            ", card1Selected: " + (card1Selected != null ? "present" : "null") + 
            ", card2Selected: " + (card2Selected != null ? "present" : "null"));
            
        if(gameReady && card1Selected != null && card2Selected != null){
            // Hide the mismatched cards by setting them back to card back image
            System.out.println("Hiding mismatched cards...");
            
            // Double-check that these are still valid buttons and not already hidden
            if (card1Selected.getIcon() != cardBackImageIcon) {
                card1Selected.setIcon(cardBackImageIcon);
                System.out.println("Card 1 flipped back to back side");
            }
            
            if (card2Selected.getIcon() != cardBackImageIcon) {
                card2Selected.setIcon(cardBackImageIcon);
                System.out.println("Card 2 flipped back to back side");
            }
            
            // Reset selections
            card1Selected = null;
            card2Selected = null;
            
            System.out.println("Cards cleared! Game ready for next turn.");
            
            // CRITICAL: Always switch turns after hiding mismatched cards in multiplayer
            if (isMultiplayer) {
                System.out.println("=== SWITCHING TURNS AFTER MISS ===");
                System.out.println("Before switch - Current: " + currentPlayer.getName() + ", isMyTurn: " + isMyTurn);
                switchTurn();
                System.out.println("After switch - Current: " + currentPlayer.getName() + ", isMyTurn: " + isMyTurn);
                System.out.println("Turn switched successfully!");
            } else {
                // Single player - just continue playing after hiding cards
                System.out.println("Single player: Cards hidden, ready for next selection!");
            }
        } else if (gameReady) {
            // This might be initial game setup - hide all cards
            System.out.println("Hiding all cards (initial setup)");
            for(int i=0; i<board.size(); i++){
                board.get(i).setIcon(cardBackImageIcon);
            }
            gameReady = true; 
            restartButton.setEnabled(true);
            quitButton.setEnabled(true);
            if (currentPlayer != null) {
                System.out.println("Game is ready! " + currentPlayer.getName() + " starts first.");
            }
        } else {
            System.out.println("WARNING: hideCards() called but conditions not met!");
        }
    }
    
    private void showGameOverOverlay() {
        if (gameOverShown) return; // Prevent showing multiple times
        gameOverShown = true;
        
        // Create the overlay panel
        gameOverOverlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw semi-transparent black background
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 0, 0, 180)); // Semi-transparent black
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gameOverOverlay.setLayout(null);
        gameOverOverlay.setOpaque(false);
        
        // Create the black horizontal box
        JPanel blackBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                // Add subtle border
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        };
        blackBox.setLayout(new BoxLayout(blackBox, BoxLayout.Y_AXIS));
        blackBox.setOpaque(false);
        
        // Create game over text
        String winner = isMultiplayer ? (player1.getScore() > player2.getScore() ? player1.getName() : player2.getName()) : "You Won!";
        JLabel winnerLabel = new JLabel(winner, JLabel.CENTER);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 36));
        winnerLabel.setForeground(Color.WHITE);
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create score information
        int matches = player1 != null ? player1.getScore() : 0;
        JLabel scoreLabel = new JLabel("Matches: " + matches + " | Errors: " + errorCount, JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add spacing and components to black box
        blackBox.add(Box.createVerticalGlue());
        blackBox.add(winnerLabel);
        blackBox.add(Box.createRigidArea(new Dimension(0, 10)));
        blackBox.add(scoreLabel);
        blackBox.add(Box.createVerticalGlue());
        
        // Position the black box (edge to edge horizontally, centered vertically)
        int overlayWidth = getContentPane().getWidth();
        int overlayHeight = getContentPane().getHeight();
        int boxHeight = 120;
        int boxY = (overlayHeight - boxHeight) / 2;
        
        blackBox.setBounds(0, boxY, overlayWidth, boxHeight);
        gameOverOverlay.setBounds(0, 0, overlayWidth, overlayHeight);
        
        // Add the black box to the overlay
        gameOverOverlay.add(blackBox);
        
        // Add click listener to close overlay
        gameOverOverlay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                removeGameOverOverlay();
            }
        });
        
        // Add the overlay to the frame (on top of everything)
        getLayeredPane().add(gameOverOverlay, JLayeredPane.MODAL_LAYER);
        
        // Repaint to show the overlay
        repaint();
    }
    
    private void removeGameOverOverlay() {
        if (gameOverOverlay != null) {
            getLayeredPane().remove(gameOverOverlay);
            gameOverOverlay = null;
            gameOverShown = false;
            repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new HomeScreen();
            }
        });
    }
}