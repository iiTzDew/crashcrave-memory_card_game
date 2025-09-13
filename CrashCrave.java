
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
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
    private int marksCount = 0;
    private ArrayList<JButton> board;
    private Timer hideCardTimer;
    private boolean gameReady = false;
    private JButton card1Selected;
    private JButton card2Selected;



    public CrashCrave() {
        setUpCards();
        shuffleCards();

        setLayout(new BorderLayout()); // Use JFrame methods directly
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
        marksLabel.setText("MARKS: " + Integer.toString(marksCount));

        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.setLayout(new GridLayout(1, 2)); // 1 row, 2 columns for 50-50 split
        textPanel.add(marksLabel);
        textPanel.add(textLabel);
        add(textPanel, BorderLayout.NORTH);

        //cardgame board
        board =  new ArrayList<JButton>();
        boardPanel.setLayout(new GridLayout(rows, columns));
        for (int i =0; i<cardSet.size(); i++){
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true);
            tile.setIcon(cardSet.get(i).getImageIcon());
            tile.setFocusable(false);
            tile.addActionListener(this); // Use ActionListener polymorphism
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
                if (!gameReady){
                    return;
                }
                gameReady = false;
                restartButton.setEnabled(false);
                card1Selected = null;
                card2Selected = null;
                shuffleCards();
                //Re assign buttons with new cards
                for(int i=0; i<board.size(); i++){
                    board.get(i).setIcon(cardSet.get(i).getImageIcon());
                }
                errorCount = 0;
                marksCount = 0;
                textLabel.setText("ERRORS: " + Integer.toString(errorCount));
                marksLabel.setText("MARKS: " + Integer.toString(marksCount));
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
        if(!gameReady) {
            return;
        }
        JButton tile = (JButton) e.getSource();
        if (tile.getIcon() == cardBackImageIcon){
            if(card1Selected == null){
                card1Selected = tile;
                int index = board.indexOf(card1Selected);
                card1Selected.setIcon(cardSet.get(index).getImageIcon());
            }
            else if(card2Selected == null){
                card2Selected = tile;
                int index = board.indexOf(card2Selected);
                card2Selected.setIcon(cardSet.get(index).getImageIcon());
                if(card1Selected.getIcon() != card2Selected.getIcon()){
                    errorCount++;
                    textLabel.setText("ERRORS: " + Integer.toString(errorCount));
                    hideCardTimer.start();
                } else {
                    marksCount++;
                    marksLabel.setText("MARKS: " + Integer.toString(marksCount));
                    card1Selected = null;
                    card2Selected = null;
                }
            }
        }
    }

    void setUpCards(){
        cardSet = new ArrayList<Card>();
        for(String cardName: cardlist){
            //load the card images 
            java.net.URL imageUrl = getClass().getResource("CrashCraveimages/" + cardName + ".jpg");
            if (imageUrl == null) {
                // Try alternative path
                imageUrl = getClass().getResource("./CrashCraveimages/" + cardName + ".jpg");
                if (imageUrl == null) {
                    System.out.println("Warning: Image not found: " + cardName + ".jpg");
                    continue; // Skip this image
                }
            }
            Image cardImg = new ImageIcon(imageUrl).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));

            //create card object and add to the cardset
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

    void shuffleCards(){
        System.out.println(cardSet);
        //Shuffle
        for(int i=0; i<cardSet.size(); i++){
            int j=(int) (Math.random()*cardSet.size()); //Get random index
            //Swap
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }
        System.out.println(cardSet);
    }

    void hideCards(){
        if(gameReady && card1Selected != null && card2Selected != null){
            card1Selected.setIcon(cardBackImageIcon);
            card2Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
            card2Selected = null;
        }
        else{
           for(int i=0; i<board.size(); i++){
            board.get(i).setIcon(cardBackImageIcon);
        }
        gameReady = true; 
        restartButton.setEnabled(true);
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