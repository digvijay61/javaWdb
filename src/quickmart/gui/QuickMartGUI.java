package quickmart.gui;

import quickmart.management.ItemManager;
import quickmart.management.UserManager;
import quickmart.utils.DBUtil; // Import DBUtil```

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

public class QuickMartGUI extends JFrame {

    // Panel Identifiers for CardLayout
    public static final String MAIN_PANEL = "MainPanel";
    public static final String REGISTER_PANEL = "RegisterPanel";
    public static final String LOGIN_PANEL = "LoginPanel";
    public static final String BUYER_PANEL = "BuyerPanel";
    public static final String SELLER_PANEL = "SellerPanel";
    public static final String ADMIN_PANEL = "AdminPanel";

    private CardLayout cardLayout;
    private JPanel mainContainer; // Panel holding all other panels

    // Panels
    private MainPanel mainPanel;
    private RegisterPanel registerPanel;
    private LoginPanel loginPanel;
    private BuyerPanel buyerPanel;
    private SellerPanel sellerPanel;
    private AdminPanel adminPanel;

    // Managers
    private final UserManager userManager;
    private final ItemManager itemManager;
    // TransactionManager is used internally by BuyerPanel/Transaction, no direct GUI interaction needed usually

    public QuickMartGUI() {
        // Initialize Managers first
        userManager = new UserManager();
        itemManager = new ItemManager();

        // Initialize DB (Optional here, could be done in main)
        initializeDatabase();

        setTitle("QuickMart Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600)); // Set a reasonable minimum size
        setLocationRelativeTo(null); // Center the window

        initComponents();
    }

     private void initializeDatabase() {
        try {
            // Ensure connection is established and tables/default items are ready
            DBUtil.getConnection(); // Establishes connection, creates DB/Tables if needed
            DBUtil.loadDefaultItems(); // Load default items if table is empty
        } catch (SQLException | IOException e) {
             JOptionPane.showMessageDialog(this,
                    "Database Initialization Failed: " + e.getMessage() +
                    "\nPlease ensure MySQL server is running and configured correctly.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
             // Consider exiting if DB is essential
             System.exit(1);
        }
    }


    private void initComponents() {
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Initialize Panels (pass managers and main frame reference)
        mainPanel = new MainPanel(this);
        registerPanel = new RegisterPanel(this, userManager);
        loginPanel = new LoginPanel(this, userManager);
        buyerPanel = new BuyerPanel(this, itemManager);
        sellerPanel = new SellerPanel(this, itemManager);
        adminPanel = new AdminPanel(this, itemManager, userManager); // Pass both managers

        // Add panels to the container
        mainContainer.add(mainPanel, MAIN_PANEL);
        mainContainer.add(registerPanel, REGISTER_PANEL);
        mainContainer.add(loginPanel, LOGIN_PANEL);
        mainContainer.add(buyerPanel, BUYER_PANEL);
        mainContainer.add(sellerPanel, SELLER_PANEL);
        mainContainer.add(adminPanel, ADMIN_PANEL);

        // Add the main container to the frame
        add(mainContainer);

        // Show the initial panel
        cardLayout.show(mainContainer, MAIN_PANEL);
    }

    // Method to switch panels
    public void showPanel(String panelName) {
        cardLayout.show(mainContainer, panelName);
        // Refresh data if necessary when showing certain panels
        switch (panelName) {
            case BUYER_PANEL:
                buyerPanel.loadAvailableItems(); // Reload items when buyer panel is shown
                buyerPanel.updateCartView(); // Ensure cart is up-to-date
                break;
            case SELLER_PANEL:
                // Seller panel reloads data based on login/viewMyItems action
                if (sellerPanel.currentSeller == null) { // If logged out or just showing panel
                    sellerPanel.loadItems(); // Show all items if not logged in
                } else {
                    sellerPanel.viewMyItems(); // Show seller's items if logged in
                }  break;
            case ADMIN_PANEL:
                adminPanel.loadItems(); // Reload items for admin view
                // TODO: Load users if user management table is implemented
                break;
        }
    }

     // Accessor methods for panels if needed (e.g., to set current user)
    public BuyerPanel getBuyerPanel() {
        return buyerPanel;
    }

    public SellerPanel getSellerPanel() {
        return sellerPanel;
    }


    // Method to exit the application
    public void exitApplication() {
         int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit QuickMart?", "Confirm Exit",
                JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            // Close DB connection before exiting
            DBUtil.closeConnection();
            System.exit(0);
        }
    }

    // Main method to launch the GUI
    public static void main(String[] args) {
        // Set Look and Feel (optional, for better appearance)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Failed to set system LookAndFeel: " + e.getMessage());
        }

        // Run the GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            QuickMartGUI gui = new QuickMartGUI();
            gui.setVisible(true);
        });
    }
}