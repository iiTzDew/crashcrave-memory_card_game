import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomeScreen extends JFrame {
    
    private JPanel buttonPanel;
    private boolean isMainMenu = true; // Track which page we're on
    private String playerName = "";
    private String hostIP = "";
    private int gameMode = -1; // 0=single, 1=host, 2=join
    
    public HomeScreen() {
        setTitle("CrashCrave - Memory Card Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Create main panel with background image
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load and draw background image
                try {
                    ImageIcon bgImage = new ImageIcon(getClass().getResource("homeScreenImage/HomeScreen.png"));
                    if (bgImage.getImageLoadStatus() == MediaTracker.COMPLETE) {
                        g.drawImage(bgImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                    } else {
                        // Fallback if image not found
                        g.setColor(new Color(45, 45, 45));
                        g.fillRect(0, 0, getWidth(), getHeight());
                        g.setColor(Color.WHITE);
                        g.setFont(new Font("Arial", Font.BOLD, 48));
                        FontMetrics fm = g.getFontMetrics();
                        String title = "CRASH CRAVE";
                        int x = (getWidth() - fm.stringWidth(title)) / 2;
                        int y = (getHeight() / 3);
                        g.drawString(title, x, y);
                        
                        g.setFont(new Font("Arial", Font.PLAIN, 24));
                        g.setColor(Color.LIGHT_GRAY);
                        fm = g.getFontMetrics();
                        String subtitle = "Memory Card Game";
                        x = (getWidth() - fm.stringWidth(subtitle)) / 2;
                        y += 60;
                        g.drawString(subtitle, x, y);
                    }
                } catch (Exception e) {
                    // Fallback design
                    g.setColor(new Color(45, 45, 45));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 48));
                    FontMetrics fm = g.getFontMetrics();
                    String title = "CRASH CRAVE";
                    int x = (getWidth() - fm.stringWidth(title)) / 2;
                    int y = (getHeight() / 3);
                    g.drawString(title, x, y);
                }
            }
        };
        
        mainPanel.setLayout(new BorderLayout());
        
        // Create button panel
        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Make transparent to show background
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 30, 50));
        
        // Show main menu initially
        showMainMenu();
        
        // Add button panel to main panel
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Set window size
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                // Clear the component area first to prevent text bleeding
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Fill background completely to prevent shadows
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw the text centered
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                String buttonText = getText();
                int x = (getWidth() - fm.stringWidth(buttonText)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(buttonText, x, y);
                
                g2d.dispose();
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 0, 255)); // Fully opaque black
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 3),
            BorderFactory.createEmptyBorder(18, 35, 18, 35)
        ));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false); // We handle drawing ourselves
        button.setOpaque(false); // We handle opacity ourselves
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(320, 70));
        
        // Add hover effect with custom rendering
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 70, 70, 255)); // Fully opaque gray
                button.repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 0, 0, 255)); // Fully opaque black
                button.repaint();
            }
        });
        
        return button;
    }
    
    private JButton createSmallButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                // Clear the component area first to prevent text bleeding
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Fill background completely to prevent shadows
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw the text centered
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                String buttonText = getText();
                int x = (getWidth() - fm.stringWidth(buttonText)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(buttonText, x, y);
                
                g2d.dispose();
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 16)); // Smaller font
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 0, 255)); // Fully opaque black
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2), // Thinner border
            BorderFactory.createEmptyBorder(12, 25, 12, 25) // Less padding
        ));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false); // We handle drawing ourselves
        button.setOpaque(false); // We handle opacity ourselves
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 50)); // Smaller size
        
        // Add hover effect with custom rendering
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 70, 70, 255)); // Fully opaque gray
                button.repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 0, 0, 255)); // Fully opaque black
                button.repaint();
            }
        });
        
        return button;
    }
    
    private void showMainMenu() {
        // Clear existing buttons
        buttonPanel.removeAll();
        
        // Add maximum space at the top to push buttons to bottom
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        
        // Create main menu buttons - START and EXIT
        JButton startButton = createStyledButton("START");
        JButton exitButton = createStyledButton("EXIT");
        
        // Add action listeners
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGameModeSelection();
            }
        });
        
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Add buttons to panel with spacing
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(exitButton);
        
        // Add space at the bottom
        buttonPanel.add(Box.createVerticalGlue());
        
        // Refresh the panel
        buttonPanel.revalidate();
        buttonPanel.repaint();
        
        isMainMenu = true;
    }
    
    private void showGameModeSelection() {
        // Clear existing buttons
        buttonPanel.removeAll();
        
        // Add maximum space at the top to push buttons to bottom
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        
        // Create game mode buttons
        JButton singlePlayerButton = createStyledButton("SINGLE PLAYER");
        JButton multiplayerButton = createStyledButton("MULTIPLAYER");
        JButton backButton = createStyledButton("BACK");
        
        // Add action listeners
        singlePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close home screen
                SwingUtilities.invokeLater(() -> {
                    CrashCrave game = new CrashCrave(false);
                    game.startSinglePlayer();
                });
            }
        });
        
        multiplayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMultiplayerOptions();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMainMenu();
            }
        });
        
        // Add buttons to panel with spacing
        buttonPanel.add(singlePlayerButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(multiplayerButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(backButton);
        
        // Add space at the bottom
        buttonPanel.add(Box.createVerticalGlue());
        
        // Refresh the panel
        buttonPanel.revalidate();
        buttonPanel.repaint();
        
        isMainMenu = false;
    }
    
    private void showMultiplayerOptions() {
        // Clear existing buttons
        buttonPanel.removeAll();
        
        // Add maximum space at the top to push buttons to bottom
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        
        // Create multiplayer option buttons
        JButton hostButton = createStyledButton("HOST MULTIPLAYER");
        JButton joinButton = createStyledButton("JOIN MULTIPLAYER");
        JButton backButton = createStyledButton("BACK");
        
        // Add action listeners
        hostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameMode = 1; // Host multiplayer
                showNameInput();
            }
        });
        
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameMode = 2; // Join multiplayer
                showNameInput();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGameModeSelection();
            }
        });
        
        // Add buttons to panel with spacing
        buttonPanel.add(hostButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(joinButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(backButton);
        
        // Add space at the bottom
        buttonPanel.add(Box.createVerticalGlue());
        
        // Refresh the panel
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }
    
    private void showNameInput() {
        // Clear existing buttons
        buttonPanel.removeAll();
        
        // Add much more space at the top to push everything well below the logo
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        
        // Create a background box for the input section
        JPanel inputBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Semi-transparent black background
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Border
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 15, 15);
            }
        };
        inputBox.setLayout(new BoxLayout(inputBox, BoxLayout.Y_AXIS));
        inputBox.setOpaque(false);
        inputBox.setMaximumSize(new Dimension(500, 250));
        inputBox.setPreferredSize(new Dimension(500, 250));
        inputBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add padding inside the box
        inputBox.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Create name input components
        JLabel nameLabel = new JLabel("Enter your name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 28)); // Larger font
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 18));
        nameField.setMaximumSize(new Dimension(400, 40));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField.setHorizontalAlignment(JTextField.CENTER);
        
        // Create smaller buttons
        JButton okButton = createSmallButton("OK");
        JButton cancelButton = createSmallButton("CANCEL");
        
        // Add components to the input box
        inputBox.add(nameLabel);
        inputBox.add(Box.createRigidArea(new Dimension(0, 20)));
        inputBox.add(nameField);
        inputBox.add(Box.createRigidArea(new Dimension(0, 25)));
        inputBox.add(okButton);
        inputBox.add(Box.createRigidArea(new Dimension(0, 10)));
        inputBox.add(cancelButton);
        
        // Add action listeners
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerName = nameField.getText().trim();
                if (playerName.isEmpty()) {
                    playerName = gameMode == 1 ? "Host Player" : "Guest Player";
                }
                
                if (gameMode == 1) {
                    // Host - start game directly
                    startGame();
                } else if (gameMode == 2) {
                    // Join - ask for IP
                    showIPInput();
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMultiplayerOptions();
            }
        });
        
        // Add the input box to the main panel
        buttonPanel.add(inputBox);
        
        // Add space at the bottom
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        
        // Refresh the panel
        buttonPanel.revalidate();
        buttonPanel.repaint();
        
        // Focus on text field
        nameField.requestFocus();
    }
    
    private void showIPInput() {
        // Clear existing buttons
        buttonPanel.removeAll();
        
        // Add much more space at the top to push everything well below the logo
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        
        // Create a background box for the input section
        JPanel inputBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Semi-transparent black background
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Border
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 15, 15);
            }
        };
        inputBox.setLayout(new BoxLayout(inputBox, BoxLayout.Y_AXIS));
        inputBox.setOpaque(false);
        inputBox.setMaximumSize(new Dimension(500, 250));
        inputBox.setPreferredSize(new Dimension(500, 250));
        inputBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add padding inside the box
        inputBox.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Create IP input components
        JLabel ipLabel = new JLabel("Enter host IP:");
        ipLabel.setFont(new Font("Arial", Font.BOLD, 28)); // Larger font
        ipLabel.setForeground(Color.WHITE);
        ipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextField ipField = new JTextField("localhost", 20);
        ipField.setFont(new Font("Arial", Font.PLAIN, 18));
        ipField.setMaximumSize(new Dimension(400, 40));
        ipField.setAlignmentX(Component.CENTER_ALIGNMENT);
        ipField.setHorizontalAlignment(JTextField.CENTER);
        
        // Create smaller buttons
        JButton okButton = createSmallButton("OK");
        JButton cancelButton = createSmallButton("CANCEL");
        
        // Add components to the input box
        inputBox.add(ipLabel);
        inputBox.add(Box.createRigidArea(new Dimension(0, 20)));
        inputBox.add(ipField);
        inputBox.add(Box.createRigidArea(new Dimension(0, 25)));
        inputBox.add(okButton);
        inputBox.add(Box.createRigidArea(new Dimension(0, 10)));
        inputBox.add(cancelButton);
        
        // Add action listeners
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hostIP = ipField.getText().trim();
                if (hostIP.isEmpty()) {
                    hostIP = "localhost";
                }
                startGame();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNameInput();
            }
        });
        
        // Add the input box to the main panel
        buttonPanel.add(inputBox);
        
        // Add space at the bottom
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        
        // Refresh the panel
        buttonPanel.revalidate();
        buttonPanel.repaint();
        
        // Focus on text field
        ipField.requestFocus();
    }
    
    private void startGame() {
        if (gameMode == 1) {
            // Host multiplayer - show waiting screen first
            showWaitingScreen();
        } else {
            // Single player or join multiplayer - start directly
            dispose(); // Close home screen
            SwingUtilities.invokeLater(() -> {
                if (gameMode == 2) {
                    // Join multiplayer
                    CrashCrave game = new CrashCrave(false);
                    game.startMultiplayerClient(playerName, hostIP);
                }
            });
        }
    }
    
    private void showWaitingScreen() {
        // Clear existing buttons
        buttonPanel.removeAll();
        
        // Add much more space at the top to push everything well below the logo
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        
        // Create a background box for the waiting section (same style as input boxes)
        JPanel waitingBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Semi-transparent black background
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Border
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 15, 15);
            }
        };
        waitingBox.setLayout(new BoxLayout(waitingBox, BoxLayout.Y_AXIS));
        waitingBox.setOpaque(false);
        waitingBox.setMaximumSize(new Dimension(500, 280)); // Same width as input boxes, adjusted height
        waitingBox.setPreferredSize(new Dimension(500, 280));
        waitingBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add padding inside the box (same as input boxes)
        waitingBox.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Create waiting message (same style as input labels)
        JLabel waitingLabel1 = new JLabel("Waiting for another player to join...");
        waitingLabel1.setFont(new Font("Arial", Font.BOLD, 28)); // Same font size as input labels
        waitingLabel1.setForeground(Color.WHITE);
        waitingLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel waitingLabel2 = new JLabel("Server started on port 12345");
        waitingLabel2.setFont(new Font("Arial", Font.PLAIN, 20)); // Slightly larger for better visibility
        waitingLabel2.setForeground(Color.LIGHT_GRAY);
        waitingLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create smaller buttons (same as input screens)
        JButton startHostButton = createSmallButton("START HOST");
        JButton cancelButton = createSmallButton("CANCEL");
        
        // Add components to the waiting box (same spacing as input boxes)
        waitingBox.add(waitingLabel1);
        waitingBox.add(Box.createRigidArea(new Dimension(0, 20))); // Same spacing as input boxes
        waitingBox.add(waitingLabel2);
        waitingBox.add(Box.createRigidArea(new Dimension(0, 25))); // Same spacing as input boxes
        waitingBox.add(startHostButton);
        waitingBox.add(Box.createRigidArea(new Dimension(0, 10))); // Same spacing as input boxes
        waitingBox.add(cancelButton);
        
        // Add action listeners
        startHostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close home screen
                SwingUtilities.invokeLater(() -> {
                    CrashCrave game = new CrashCrave(false);
                    game.startMultiplayerHost(playerName);
                });
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNameInput();
            }
        });
        
        // Add the waiting box to the main panel
        buttonPanel.add(waitingBox);
        
        // Add space at the bottom
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        
        // Refresh the panel
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HomeScreen();
        });
    }
}
