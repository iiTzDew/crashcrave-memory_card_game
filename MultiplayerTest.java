public class MultiplayerTest {
    public static void main(String[] args) {
        System.out.println("=== MULTIPLAYER TURN TEST ===");
        System.out.println("This test will help debug the turn switching issue.");
        System.out.println();
        
        String mode = args.length > 0 ? args[0] : "host";
        
        if (mode.equals("host")) {
            System.out.println("STARTING HOST MODE");
            System.out.println("1. You should be able to click cards initially");
            System.out.println("2. When you miss, watch for turn switch messages");
            System.out.println("3. After turn switch, you should see 'Not your turn!'");
        } else {
            System.out.println("STARTING CLIENT MODE");
            System.out.println("1. You should see 'Not your turn!' initially");
            System.out.println("2. Wait for host to miss");
            System.out.println("3. Then you should be able to click cards");
        }
        
        System.out.println();
        System.out.println("Starting game...");
        
        // Start the actual game
        new CrashCrave();
    }
}
