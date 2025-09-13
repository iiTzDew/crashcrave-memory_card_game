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
    private int cardWidth = 45;
    private int cardHeight = 64;

    private ArrayList<Card> cardSet; //create a deck of cards with cardnames and cardimageicons
    private ImageIcon cardBackImageIcon;
  
    private int boardWidth = columns*cardWidth;
    private int boardHeight = rows*cardHeight;

    private RoundedLabel textLabel = new RoundedLabel("", 15);
    private JPanel textPanel = new JPanel();
    private JPanel boardPanel = new JPanel();
    private JPanel restartGamePanel = new JPanel();
    private RoundedButton restartButton = new RoundedButton("", 15);
    private RoundedLabel marksLabel = new RoundedLabel("", 15);

    private int errorCount = 0;
    private ArrayList<JButton> board;
    private Timer hideCardTimer;
    private boolean gameReady = false;
    private JButton card1Selected;
    private JButton card2Selected;

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

        setLayout(new BorderLayout());
        setSize(boardWidth, boardHeight);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("ERRORS: " + Integer.toString(errorCount));

        // Create MARKS display with same format as ERRORS
        marksLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        marksLabel.setHorizontalAlignment(JLabel.CENTER);
        marksLabel.setText("MARKS: " + Integer.toString(0));

        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.setLayout(new GridLayout(1, 2)); // 1 row, 2 columns for 50-50 split
        textPanel.add(marksLabel);
        textPanel.add(textLabel);
        add(textPanel, BorderLayout.NORTH);

        // Mode selection
        String[] options = {"Single Player", "Host Multiplayer", "Join Multiplayer"};
        int choice = JOptionPane.showOptionDialog(null, "Choose mode", "Game Mode",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
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
        } else {
            isMultiplayer = true;
            String hostIP = "localhost";
            int port = 12345;
            boolean isServer = (choice == 1);

            // Get player name BEFORE network setup
            String myName = JOptionPane.showInputDialog("Enter your name:");
            if (myName == null || myName.trim().isEmpty()) {
                myName = isServer ? "Host Player" : "Guest Player";
            }

            if (!isServer) {
                hostIP = JOptionPane.showInputDialog("Enter host IP:");
                if (hostIP == null || hostIP.trim().isEmpty()) {
                    hostIP = "localhost";
                }
            }

            try {
                if (isServer) {
                    JOptionPane.showMessageDialog(null, "Waiting for another player to join...\nServer started on port " + port);
                }
                network = new NetworkHandler(this, isServer, hostIP, port);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Connection failed: " + e.getMessage());
                System.exit(1);
            }

            String opponentName = isServer ? "Client" : "Host";

            if (isServer) {
                // Host setup: Host is Player 1, Client is Player 2
                player1 = new LocalPlayer(myName, this);        // Host = Player 1
                player2 = new RemotePlayer(opponentName, this); // Client = Player 2
                currentPlayer = player1;
                isMyTurn = true; // Host starts first
                shuffleSeed = new Random().nextLong();
                network.sendMessage("BOARD:" + shuffleSeed);
                shuffleCards(new Random(shuffleSeed));
                System.out.println("HOST: I am " + player1.getName() + " (Player 1), opponent is " + player2.getName() + " (Player 2)");
            } else {
                // Client setup: Host is Player 1, Client is Player 2  
                player1 = new RemotePlayer(opponentName, this); // Host = Player 1
                player2 = new LocalPlayer(myName, this);        // Client = Player 2
                currentPlayer = player1; // Host (Player 1) starts first
                isMyTurn = false; // Client waits for host's turn
                System.out.println("CLIENT: I am " + player2.getName() + " (Player 2), opponent is " + player1.getName() + " (Player 1)");
                // Shuffle will be set when BOARD message received
            }

            updateScores();
        }

        //cardgame board
        board = new ArrayList<JButton>();
        boardPanel.setLayout(new GridLayout(rows, columns));
        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true);
            tile.setIcon(cardSet.get(i).getImageIcon());
            tile.setFocusable(false);
            tile.addActionListener(this);
            board.add(tile);
            boardPanel.add(tile);
        }
        add(boardPanel);

        //restart game button 
        restartButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restartButton.setText("Restart Game");
        restartButton.setPreferredSize(new Dimension(boardWidth, 30));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameReady) {
                    return;
                }
                gameReady = false;
                restartButton.setEnabled(false);
                card1Selected = null;
                card2Selected = null;
                errorCount = 0;
                if (isMultiplayer) {
                    player1.score = 0;
                    player2.score = 0;
                    shuffleSeed = new Random().nextLong();
                    shuffleCards(new Random(shuffleSeed));
                    network.sendMessage("RESTART:" + shuffleSeed);
                    currentPlayer = player1;
                    isMyTurn = (currentPlayer instanceof LocalPlayer);
                } else {
                    shuffleCards(new Random());
                }
                // Re-assign buttons with new cards
                for (int i = 0; i < board.size(); i++) {
                    board.get(i).setIcon(cardSet.get(i).getImageIcon());
                }
                updateScores();
                hideCardTimer.start();
            }
        });
        restartGamePanel.add(restartButton);
        add(restartGamePanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);

        //start game 
        hideCardTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });        
        hideCardTimer.setRepeats(false);
        hideCardTimer.start();
    }

    // Implement start() from Game (abstraction)
    @Override
    public void start() {
        // Game starts in constructor
    }

    // Implement actionPerformed() from ActionListener (polymorphism)
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameReady) {
            System.out.println("Game not ready yet...");
            return;
        }
        
        System.out.println("DEBUG: currentPlayer=" + (currentPlayer != null ? currentPlayer.getName() : "null") + 
                          ", isMyTurn=" + isMyTurn + 
                          ", isMultiplayer=" + isMultiplayer +
                          ", currentPlayer type=" + (currentPlayer != null ? currentPlayer.getClass().getSimpleName() : "null"));
        
        if (isMultiplayer && !isMyTurn) {
            System.out.println("Not your turn! Current player: " + currentPlayer.getName());
            return;
        }
        
        JButton tile = (JButton) e.getSource();
        if (tile.getIcon() == cardBackImageIcon) {
            int index = board.indexOf(tile);
            System.out.println("Player " + currentPlayer.getName() + " clicked card " + index);
            currentPlayer.makeMove(index);
        }
    }

    public void flipCard(int index) {
        JButton tile = board.get(index);
        tile.setIcon(cardSet.get(index).getImageIcon());

        if (card1Selected == null) {
            card1Selected = tile;
        } else if (card2Selected == null) {
            card2Selected = tile;
            checkForMatch();
        }
    }

    private void checkForMatch() {
        boolean match = card1Selected.getIcon().equals(card2Selected.getIcon());
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
            // Player continues - NO turn switch
        } else {
            // MISS: Show cards briefly, then switch turns
            errorCount++;
            System.out.println(currentPlayer.getName() + " missed! Turn will switch after hiding cards.");
            if (isMultiplayer) {
                network.sendMessage("MATCH:false");
                // Important: Only start timer on the current player's side
                hideCardTimer.start();
            } else {
                textLabel.setText("ERRORS: " + errorCount);
                hideCardTimer.start();
            }
        }

        if (allCardsMatched()) {
            String winner = isMultiplayer ? (player1.getScore() > player2.getScore() ? player1.getName() : player2.getName()) : "You";
            JOptionPane.showMessageDialog(null, "Game Over! Winner: " + winner);
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
        System.out.println("Turn switched to: " + currentPlayer.getName() + ", isMyTurn: " + isMyTurn);
        network.sendMessage("TURN:" + currentPlayer.getName());
    }

    private void updateScores() {
        if (isMultiplayer) {
            marksLabel.setText(player1.getName() + ": " + player1.getScore());
            textLabel.setText(player2.getName() + ": " + player2.getScore());
        } else {
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
                // NO turn switch - opponent continues
            } else {
                // Opponent missed - we need to hide cards and switch turn
                errorCount++;
                updateScores();
                System.out.println(currentPlayer.getName() + " missed! Starting hide timer on receiving side.");
                // Start hide timer on the receiving side too
                hideCardTimer.start();
            }
        } else if (message.startsWith("TURN:")) {
            String turnName = message.split(":")[1];
            System.out.println("Received TURN message: " + turnName);
            currentPlayer = turnName.equals(player1.getName()) ? player1 : player2;
            isMyTurn = (currentPlayer instanceof LocalPlayer);
            System.out.println("Updated turn - Current: " + currentPlayer.getName() + ", isMyTurn: " + isMyTurn);
        } else if (message.startsWith("BOARD:")) {
            shuffleSeed = Long.parseLong(message.split(":")[1]);
            shuffleCards(new Random(shuffleSeed));
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardSet.get(i).getImageIcon());
            }
            hideCardTimer.start();
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
        for(String cardName: cardlist){
            //load the card images 
            java.net.URL imageUrl = getClass().getResource("CrashCraveimages/" + cardName + ".jpg");
            if (imageUrl == null) {
                imageUrl = getClass().getResource("./CrashCraveimages/" + cardName + ".jpg");
                if (imageUrl == null) {
                    System.out.println("Warning: Image not found: " + cardName + ".jpg");
                    continue;
                }
            }
            Image cardImg = new ImageIcon(imageUrl).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));

            Card card = new Card(cardName, cardImageIcon);
            cardSet.add(card);
        }
        cardSet.addAll(cardSet); 

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
        if(gameReady && card1Selected != null && card2Selected != null){
            // Hide the mismatched cards
            card1Selected.setIcon(cardBackImageIcon);
            card2Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
            card2Selected = null;
            
            // Switch turns after hiding mismatched cards
            if (isMultiplayer) {
                // Only the player who made the move should send the turn switch
                if (isMyTurn) {
                    switchTurn();
                    System.out.println("Cards hidden, I switched the turn!");
                } else {
                    System.out.println("Cards hidden, waiting for turn update from opponent.");
                }
            }
        } else {
            // Initial game setup - hide all cards
            for(int i=0; i<board.size(); i++){
                board.get(i).setIcon(cardBackImageIcon);
            }
            gameReady = true; 
            restartButton.setEnabled(true);
            if (currentPlayer != null) {
                System.out.println("Game is ready! " + currentPlayer.getName() + " starts first.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CrashCrave();
            }
        });
    }
}