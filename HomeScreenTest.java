public class HomeScreenTest {
    public static void main(String[] args) {
        System.out.println("=== HOME SCREEN QUIT BUTTON TEST ===");
        System.out.println("Testing the updated button text on the first page");
        System.out.println();
        System.out.println("CHANGE MADE:");
        System.out.println("✓ Changed 'EXIT' button to 'QUIT' button");
        System.out.println("✓ Updated variable name from 'exitButton' to 'quitButton'");
        System.out.println("✓ Updated comment to reflect the change");
        System.out.println();
        System.out.println("WHAT TO EXPECT:");
        System.out.println("1. The HomeScreen should display with background image");
        System.out.println("2. Two buttons should appear:");
        System.out.println("   - 'START' button (unchanged)");
        System.out.println("   - 'QUIT' button (previously 'EXIT')");
        System.out.println("3. Both buttons should have the same styling");
        System.out.println("4. 'QUIT' button should exit the application when clicked");
        System.out.println();
        System.out.println("Starting HomeScreen...");
        
        // Start the HomeScreen
        new HomeScreen();
    }
}