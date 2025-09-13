import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomeScreen extends JFrame {
    
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
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Make transparent to show background
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 30, 50));
        
        // Add much more space at the top to push buttons down further
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(Box.createVerticalGlue());
        
        // Create stylized buttons - only START and EXIT
        JButton startButton = createStyledButton("START");
        JButton exitButton = createStyledButton("EXIT");
        
        // Add action listeners
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show mode selection when START is clicked
                showModeSelection();
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
        
        // Add space at the bottom but less than at the top to push buttons lower
        buttonPanel.add(Box.createVerticalGlue());
        
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
    
    private void showModeSelection() {
        // Mode selection dialog
        String[] options = {"Single Player", "Host Multiplayer", "Join Multiplayer"};
        int choice = JOptionPane.showOptionDialog(this, "Choose game mode", "Game Mode",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice >= 0) { // User made a selection (didn't cancel)
            dispose(); // Close home screen
            SwingUtilities.invokeLater(() -> {
                CrashCrave game = new CrashCrave(false); // Don't show mode dialog
                if (choice == 0) {
                    game.startSinglePlayer();
                } else if (choice == 1) {
                    game.startMultiplayerHost();
                } else if (choice == 2) {
                    game.startMultiplayerClient();
                }
            });
        }
        // If user cancels, stay on home screen
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HomeScreen();
        });
    }
}
