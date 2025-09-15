import javax.swing.*;
import java.awt.event.*;

/**
 * Test class to verify the multiplayer turn flow
 */
public class MultiplayerTurnTest {
    public static void main(String[] args) {
        System.out.println("=== MULTIPLAYER TURN FLOW TEST ===");
        System.out.println();
        System.out.println("This test demonstrates the correct multiplayer turn flow:");
        System.out.println("1. Host always starts first");
        System.out.println("2. If no match, turn switches to opponent");
        System.out.println("3. If match, current player continues");
        System.out.println("4. Game continues until all pairs are found");
        System.out.println();
        
        String[] options = {"Start Host", "Start Client", "Exit"};
        int choice = JOptionPane.showOptionDialog(null, 
            "Choose your role to test multiplayer turn flow", 
            "Multiplayer Turn Test",
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.INFORMATION_MESSAGE, 
            null, options, options[0]);

        switch (choice) {
            case 0: // Host
                System.out.println("Starting as HOST...");
                System.out.println("HOST: You will start first!");
                System.out.println("HOST: When you make a match, you continue playing");
                System.out.println("HOST: When you miss, turn goes to client");
                startHost();
                break;
            case 1: // Client
                System.out.println("Starting as CLIENT...");
                System.out.println("CLIENT: You will wait for host to start");
                System.out.println("CLIENT: When host misses, it becomes your turn");
                System.out.println("CLIENT: When you match, you continue playing");
                startClient();
                break;
            default:
                System.out.println("Exiting test...");
                System.exit(0);
        }
    }
    
    private static void startHost() {
        CrashCrave game = new CrashCrave(false);
        game.startMultiplayerHost("Test Host");
        game.setVisible(true);
    }
    
    private static void startClient() {
        String hostIP = JOptionPane.showInputDialog("Enter host IP (default: localhost):");
        if (hostIP == null || hostIP.trim().isEmpty()) {
            hostIP = "localhost";
        }
        
        CrashCrave game = new CrashCrave(false);
        game.startMultiplayerClient("Test Client", hostIP);
        game.setVisible(true);
    }
}