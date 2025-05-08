package quickmart.gui;

import quickmart.management.ItemManager;
import quickmart.management.UserManager;
import quickmart.utils.DBUtil;

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

    public QuickMartGUI() {
        // Initialize Managers first
        userManager = new UserManager();
        itemManager = new ItemManager();

        // Initialize DB Connection and Load Defaults
        initializeDatabase();

        setTitle("QuickMart Application");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Handle exit via listener
        setMinimumSize(new Dimension(850, 650));
        setPreferredSize(new Dimension(900, 700));
        setLocationRelativeTo(null); // Center the window

        initComponents(); // Initialize GUI components AFTER managers and DB are ready

        // Add window listener for closing confirmation
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                exitApplication();
            }
        });

        pack(); // Adjust frame size to fit preferred sizes of components
    }

     private void initializeDatabase() {
        System.out.println("Initializing Database Connection...");
        try {
            DBUtil.getConnection();
            System.out.println("DB Connection successful.");
            DBUtil.loadDefaultItems();
             System.out.println("Default items checked/loaded.");
        } catch (SQLException | IOException e) {
             System.err.println("FATAL: Database Initialization Failed: " + e.getMessage());
             JOptionPane.showMessageDialog(null,
                    "Database Initialization Failed: " + e.getMessage() +
                    "\nPlease ensure your MySQL server is running and the credentials\n" +
                    "in src/quickmart/utils/DBUtil.java are correct.\n\nThe application will now exit.",
                    "Database Connection Error", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
             System.exit(1);
        }
         System.out.println("Database Initialization Complete.");
    }


    private void initComponents() {
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        System.out.println("Initializing GUI Panels...");
        mainPanel = new MainPanel(this);
        registerPanel = new RegisterPanel(this, userManager);
        loginPanel = new LoginPanel(this, userManager);
        buyerPanel = new BuyerPanel(this, itemManager);
        sellerPanel = new SellerPanel(this, itemManager);
        adminPanel = new AdminPanel(this, itemManager, userManager);
        System.out.println("GUI Panels Initialized.");

        mainContainer.add(mainPanel, MAIN_PANEL);
        mainContainer.add(registerPanel, REGISTER_PANEL);
        mainContainer.add(loginPanel, LOGIN_PANEL);
        mainContainer.add(buyerPanel, BUYER_PANEL);
        mainContainer.add(sellerPanel, SELLER_PANEL);
        mainContainer.add(adminPanel, ADMIN_PANEL);

        setContentPane(mainContainer);

        cardLayout.show(mainContainer, MAIN_PANEL);
        System.out.println("Initial Panel Set to Main Menu.");
    }

    public void showPanel(String panelName) {
        System.out.println("Switching to panel: " + panelName);
        cardLayout.show(mainContainer, panelName);
         switch (panelName) {
            case BUYER_PANEL:
                if (buyerPanel != null) {
                    buyerPanel.loadAvailableItems();
                    buyerPanel.updateCartView();
                }
                break;
            case SELLER_PANEL:
                if (sellerPanel != null) {
                     if (sellerPanel.currentSeller == null) {
                        sellerPanel.loadAllItems();
                    } else {
                        sellerPanel.viewMyItems();
                    }
                }
                break;
            case ADMIN_PANEL:
                if (adminPanel != null) {
                    adminPanel.onPanelShown();
                }
                break;
            case LOGIN_PANEL:
                 if(loginPanel != null) loginPanel.clearFields();
                 break;
            case REGISTER_PANEL:
                 if(registerPanel != null) registerPanel.clearFields();
                 break;
         }
    }

    public BuyerPanel getBuyerPanel() {
        return buyerPanel;
    }

    public SellerPanel getSellerPanel() {
        return sellerPanel;
    }

    public void exitApplication() {
         int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit QuickMart?", "Confirm Exit",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            System.out.println("Exiting application...");
            DBUtil.closeConnection();
             System.out.println("DB connection closed.");
            dispose();
            System.exit(0);
        }
    }

    // *** NO main METHOD HERE ***

} // End of QuickMartGUI class