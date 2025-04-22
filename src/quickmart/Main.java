// GROUP MEMBERS:
// DIGVIJAYSINH VANSIYA
// DHANRAJ SHITOLE
// ARYANSINGH RAJPUT
package quickmart;

import quickmart.gui.QuickMartGUI; // Import the main GUI frame class
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Main class to launch the QuickMart Swing GUI application.
 */
public class Main {

    /**
     * The main entry point of the application.
     * Sets up the look and feel and launches the GUI on the Event Dispatch Thread.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        System.out.println("QuickMart Application Starting...");

        // Set Look and Feel (Optional, but recommended for better UI consistency)
        try {
            // Attempt to use Nimbus for a more modern look
            boolean nimbusFound = false;
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    nimbusFound = true;
                    System.out.println("Using Nimbus LookAndFeel.");
                    break;
                }
            }
            // Fallback to system LookAndFeel if Nimbus is not available
            if (!nimbusFound) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.out.println("Using System LookAndFeel: " + UIManager.getLookAndFeel().getName());
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // Log error if LookAndFeel setting fails, but continue with default
            System.err.println("Warning: Failed to set preferred LookAndFeel: " + e.getMessage());
        }

        // --- IMPORTANT ---
        // Run the GUI creation and display logic on the Event Dispatch Thread (EDT)
        // This is crucial for Swing applications to prevent threading issues.
        SwingUtilities.invokeLater(() -> {
            System.out.println("Creating QuickMartGUI instance on EDT...");
            // Create the main GUI frame. The constructor now handles DB initialization.
            QuickMartGUI gui = new QuickMartGUI();
            System.out.println("Setting QuickMartGUI visible on EDT...");
            // Make the GUI visible to the user.
            gui.setVisible(true);
            System.out.println("QuickMartGUI should now be visible.");
        });

        System.out.println("Main thread finished (GUI launch delegated to EDT).");

        // NO console logic (Scanner, userManager, itemManager, menus) should remain here.
        // Database initialization (DBUtil.loadDefaultItems, DBUtil.getConnection)
        // is now handled within the QuickMartGUI constructor to ensure it happens
        // before the GUI components that might depend on it are fully initialized.
    }

    // All previous static methods (registerUser, loginUser, adminMenu, etc.)
    // MUST be removed from this file as their logic is now part of the GUI panels.
}