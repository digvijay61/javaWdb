package quickmart.gui;

import quickmart.management.ItemManager;
// import quickmart.management.TransactionManager; // No longer needed for ID generation
import quickmart.models.*;
import quickmart.payment.PaymentMethods;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors; // For filtering items

public class BuyerPanel extends JPanel {

    private final QuickMartGUI mainFrame;
    private final ItemManager itemManager;
    private Buyer currentBuyer; // Keep track of logged-in buyer

    private JTable availableItemsTable;
    private ItemTableModel availableItemsTableModel; // Use full model for display consistency

    private JTable cartTable;
    private CartTableModel cartTableModel; // Simpler model for cart
    private JLabel totalLabel;
    private JLabel welcomeLabel;

    // Removed viewCartButton and removeFromCartButton declarations
    private JButton viewAllButton, addToCartButton, checkoutButton, logoutButton;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(); // Currency formatter

    public BuyerPanel(QuickMartGUI mainFrame, ItemManager itemManager) {
        this.mainFrame = mainFrame;
        this.itemManager = itemManager;
        initComponents();
        loadAvailableItems(); // Load items initially
    }

    public void setCurrentBuyer(Buyer buyer) {
        this.currentBuyer = buyer;
        if (this.currentBuyer != null) {
             if (this.currentBuyer.getCart() == null) {
                 this.currentBuyer = new Buyer(buyer.getName(), buyer.getEmail(), buyer.getPassword());
                 System.err.println("Warning: Buyer object cart was null, re-initialized.");
             }
             this.currentBuyer.getCart().clearCart();
             welcomeLabel.setText("Welcome, " + currentBuyer.getName() + "!");
             loadAvailableItems();
             updateCartView(); // This calls enableBuyerControls internally
        } else {
             welcomeLabel.setText("Buyer Panel");
             enableBuyerControls(false); // Explicitly disable when logging out
             availableItemsTableModel.setItems(new ArrayList<>());
             cartTableModel.setItems(new ArrayList<>());
             totalLabel.setText("Total: " + currencyFormatter.format(0.00));
        }
    }

    /**
     * Enables or disables buyer controls based on login status and component states.
     */
    private void enableBuyerControls() {
        boolean loggedIn = (currentBuyer != null);

        // Determine current state *inside* this method
        boolean itemSelectedAvailable = loggedIn && (availableItemsTable.getSelectedRow() != -1);
        // boolean itemSelectedCart = loggedIn && (cartTable.getSelectedRow() != -1); // No longer needed
        boolean cartNotEmpty = loggedIn && (currentBuyer.getCart() != null && !currentBuyer.getCart().isEmpty());

        addToCartButton.setEnabled(itemSelectedAvailable);
        checkoutButton.setEnabled(cartNotEmpty);
        // removeFromCartButton removed
        // viewCartButton removed
        logoutButton.setEnabled(true); // Logout always possible
        viewAllButton.setEnabled(true); // Refresh always possible
    }

    // Overloaded version used for explicit disable on logout
    private void enableBuyerControls(boolean enable) {
        if (!enable) {
            // Disable everything except potentially logout/refresh
            addToCartButton.setEnabled(false);
            checkoutButton.setEnabled(false);
            // removeFromCartButton removed
            logoutButton.setEnabled(true);
            viewAllButton.setEnabled(true);
             if (availableItemsTable != null) availableItemsTable.clearSelection();
             if (cartTable != null) cartTable.clearSelection();
        } else {
            // If enabling (login), rely on the state-determining version
            enableBuyerControls();
        }
    }


    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        // --- Top Panel: Welcome and Buttons ---
        JPanel topPanel = new JPanel(new BorderLayout(20, 5));
        welcomeLabel = new JLabel("Buyer Panel", SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        viewAllButton = new JButton("Refresh Available Items");
        logoutButton = new JButton("Logout");
        // Style top buttons
        Font topButtonFont = new Font("Arial", Font.PLAIN, 12);
        viewAllButton.setFont(topButtonFont);
        logoutButton.setFont(topButtonFont);
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);

        topButtonPanel.add(viewAllButton);
        topButtonPanel.add(logoutButton);
        topPanel.add(topButtonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Split Pane ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.65);
        splitPane.setDividerLocation(350);

        // Available Items Panel
        JPanel availableItemsPanel = new JPanel(new BorderLayout(5, 5));
        availableItemsPanel.setBorder(BorderFactory.createTitledBorder("Available Items"));
        availableItemsTableModel = new ItemTableModel();
        availableItemsTable = new JTable(availableItemsTableModel);
        availableItemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableItemsTable.setAutoCreateRowSorter(true);
        availableItemsTable.setFillsViewportHeight(true);
        availableItemsTable.setRowHeight(25);
        // Set column widths
        availableItemsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        availableItemsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        availableItemsTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        availableItemsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        availableItemsTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        availableItemsTable.getColumnModel().getColumn(5).setPreferredWidth(60);

        // Listener for available items table selection
        availableItemsTable.getSelectionModel().addListSelectionListener(e -> {
             if (!e.getValueIsAdjusting()) {
                 enableBuyerControls(); // Update button states based on current selections/state
             }
        });

        JScrollPane availableScrollPane = new JScrollPane(availableItemsTable);
        availableItemsPanel.add(availableScrollPane, BorderLayout.CENTER);

        addToCartButton = new JButton("Add Selected Item to Cart");
        JPanel addBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addBtnPanel.add(addToCartButton);
        availableItemsPanel.add(addBtnPanel, BorderLayout.SOUTH);


        // Cart Panel
        JPanel cartPanel = new JPanel(new BorderLayout(5, 5));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Your Cart"));
        cartTableModel = new CartTableModel();
        cartTable = new JTable(cartTableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Keep selection if needed visually
        cartTable.setFillsViewportHeight(true);
        cartTable.setRowHeight(25);
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(100);

        // REMOVED cartTable selection listener as it's no longer needed for the button

        JScrollPane cartScrollPane = new JScrollPane(cartTable);
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);

        // Cart Bottom Panel (Total, Checkout ONLY)
        JPanel cartBottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5)); // Simple FlowLayout now
        // removeFromCartButton REMOVED from layout
        totalLabel = new JLabel("Total: " + currencyFormatter.format(0.00));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        checkoutButton = new JButton("Proceed to Checkout");
        cartBottomPanel.add(totalLabel);
        cartBottomPanel.add(checkoutButton);
        cartPanel.add(cartBottomPanel, BorderLayout.SOUTH);

        // Add panels to split pane
        splitPane.setTopComponent(availableItemsPanel);
        splitPane.setBottomComponent(cartPanel);
        add(splitPane, BorderLayout.CENTER);

        enableBuyerControls(false); // Disable controls initially

        // --- Action Listeners ---
        viewAllButton.addActionListener(e -> loadAvailableItems());
        addToCartButton.addActionListener(e -> addItemToCart());
        // viewCartButton listener REMOVED
        // removeFromCartButton listener REMOVED
        checkoutButton.addActionListener(e -> checkout());
        logoutButton.addActionListener(e -> logout());
    }

    // --- Methods ---

    void loadAvailableItems() {
        try {
            List<Item> allItems = itemManager.getAllItems();
            List<Item> itemsToShow = allItems;
            if (currentBuyer != null) {
                itemsToShow = allItems.stream()
                                      .filter(item -> item.getSellerId() != currentBuyer.getUserId())
                                      .collect(Collectors.toList());
            }
            availableItemsTableModel.setItems(itemsToShow);
            enableBuyerControls(); // Refresh button states AFTER loading

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading available items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

     private void addItemToCart() {
        if (currentBuyer == null) return;

        int selectedRow = availableItemsTable.getSelectedRow();
        if (selectedRow == -1) return;

        try {
            int modelRow = availableItemsTable.convertRowIndexToModel(selectedRow);
            Item selectedItem = availableItemsTableModel.getItemAt(modelRow);

            if (selectedItem != null) {
                 if(selectedItem.getSellerId() == currentBuyer.getUserId()) {
                     JOptionPane.showMessageDialog(this, "You cannot add your own listing to the cart.", "Action Denied", JOptionPane.WARNING_MESSAGE);
                     return;
                 }
                 boolean alreadyInCart = currentBuyer.getCart().getItems().stream()
                                           .anyMatch(item -> item.getItemId() == selectedItem.getItemId());
                 if (alreadyInCart) {
                      JOptionPane.showMessageDialog(this, "'" + selectedItem.getTitle() + "' is already in your cart.", "Item Exists", JOptionPane.INFORMATION_MESSAGE);
                      return;
                 }

                currentBuyer.getCart().addItem(selectedItem);
                updateCartView(); // Update cart display immediately (this calls enableBuyerControls)
            } else {
                 JOptionPane.showMessageDialog(this, "Could not retrieve selected item details.", "Internal Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding item to cart: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // REMOVED removeItemFromCart() method entirely
    /*
     private void removeItemFromCart() {
        // ... implementation removed ...
     }
    */

    // This method updates the cart display and related controls
    void updateCartView() {
         if (currentBuyer == null) {
             cartTableModel.setItems(new ArrayList<>());
             totalLabel.setText("Total: " + currencyFormatter.format(0.00));
             enableBuyerControls(false);
             return;
         }
        Cart cart = currentBuyer.getCart();
        cartTableModel.setItems(new ArrayList<>(cart.getItems()));
        totalLabel.setText("Total: " + currencyFormatter.format(cart.getTotalPrice()));
        enableBuyerControls(); // Re-evaluate ALL button states based on current cart & selection status
        // No need to manage remove button state here anymore
    }

    private void checkout() {
        // --- Keep the checkout logic exactly the same as the fully corrected version ---
        // It correctly uses the Transaction constructor without an ID.
        if (currentBuyer == null || currentBuyer.getCart().isEmpty()) return;

        Cart cart = currentBuyer.getCart();
        double total = cart.getTotalPrice();

        String[] paymentOptions = {"Cash", "Online Payment"};
        int paymentChoice = JOptionPane.showOptionDialog(this,
                String.format("Confirm Purchase\n\nTotal Amount: %s\n\nSelect Payment Method:", currencyFormatter.format(total)),
                "Checkout - Confirm Payment",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                paymentOptions, paymentOptions[0]);

        if (paymentChoice == JOptionPane.CLOSED_OPTION) return;

        PaymentMethods.PaymentStrategy paymentStrategy;
        String paymentMethodType;

        if (paymentChoice == 0) { // Cash
            paymentStrategy = new PaymentMethods.CashPayment();
            paymentMethodType = "Cash";
        } else { // Online Payment
            JTextField cardNumberField = new JTextField(16);
            JPasswordField cvvField = new JPasswordField(3);
            JTextField expiryMonthField = new JTextField("MM", 2);
            JTextField expiryYearField = new JTextField("YY", 2);
            JPanel cardPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 3, 3, 3); gbc.anchor = GridBagConstraints.WEST;
            gbc.gridx = 0; gbc.gridy = 0; cardPanel.add(new JLabel("Card Number:"), gbc);
            gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; cardPanel.add(cardNumberField, gbc);
            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; cardPanel.add(new JLabel("CVV:"), gbc);
            gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; cardPanel.add(cvvField, gbc);
            gbc.gridx = 0; gbc.gridy = 2; cardPanel.add(new JLabel("Expiry (MM/YY):"), gbc);
            gbc.gridx = 1; gbc.gridy = 2; cardPanel.add(expiryMonthField, gbc);
            gbc.gridx = 2; gbc.gridy = 2; cardPanel.add(new JLabel("/"), gbc);
            gbc.gridx = 3; gbc.gridy = 2; cardPanel.add(expiryYearField, gbc);

            int result = JOptionPane.showConfirmDialog(this, cardPanel, "Enter Online Payment Details",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) return;

            String cardNum = cardNumberField.getText().trim();
            String cvv = new String(cvvField.getPassword()).trim();
            String expiryMonth = expiryMonthField.getText().trim();
            String expiryYear = expiryYearField.getText().trim();
            String expiry = expiryMonth + "/" + expiryYear;

            // Basic Validation
             if (cardNum.isEmpty() || cvv.isEmpty() || expiryMonth.isEmpty() || expiryYear.isEmpty()) {
                  JOptionPane.showMessageDialog(this, "All card details are required.", "Input Error", JOptionPane.ERROR_MESSAGE); return;
             }
             if (!cardNum.matches("^\\d{13,19}$")) {
                  JOptionPane.showMessageDialog(this, "Invalid card number format (13-19 digits).", "Input Error", JOptionPane.ERROR_MESSAGE); return;
             }
             if (!cvv.matches("^\\d{3}$")) {
                 JOptionPane.showMessageDialog(this, "Invalid CVV format (must be 3 digits).", "Input Error", JOptionPane.ERROR_MESSAGE); return;
             }
             if (!expiryMonth.matches("^(0[1-9]|1[0-2])$") || !expiryYear.matches("^\\d{2}$")) {
                 JOptionPane.showMessageDialog(this, "Invalid expiry date format (must be MM and YY).", "Input Error", JOptionPane.ERROR_MESSAGE); return;
             }

            paymentStrategy = new PaymentMethods.OnlinePayment(cardNum, cvv, expiry);
            paymentMethodType = "Online";
        }

        try {
             System.out.println("DEBUG: Processing transaction...");
             List<Item> itemsForTransaction = new ArrayList<>(cart.getItems());

             Transaction transaction = new Transaction(
                     currentBuyer,
                     itemsForTransaction,
                     paymentStrategy);

             System.out.println("DEBUG: Transaction object created/processed, ID: " + transaction.getTransactionId() + ", Overall Success: " + transaction.isSuccessful());

            if (transaction.isSuccessful()) {
                JOptionPane.showMessageDialog(this,
                        "Checkout Successful!\n\nTransaction ID: " + transaction.getTransactionId() +
                        "\nAmount Paid: " + currencyFormatter.format(total) +
                        "\nPayment Method: " + paymentMethodType,
                        "Purchase Complete", JOptionPane.INFORMATION_MESSAGE);
                cart.clearCart();
                updateCartView();
            } else {
                 if (transaction.getTransactionId() <= 0) {
                     JOptionPane.showMessageDialog(this,
                        "Checkout Failed.\nCould not save transaction details to the database.\nPlease try again later or contact support.",
                        "Checkout Failed - System Error", JOptionPane.ERROR_MESSAGE);
                 } else {
                      JOptionPane.showMessageDialog(this,
                        "Payment Failed.\nYour bank may have declined the transaction.\nPlease check your details or try another method.",
                        "Checkout Failed - Payment Declined", JOptionPane.ERROR_MESSAGE);
                 }
            }
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "A system error occurred during checkout:\n" + e.getMessage(), "Checkout Error", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
        }
    }


     private void logout() {
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            setCurrentBuyer(null);
            mainFrame.showPanel(QuickMartGUI.MAIN_PANEL);
        }
    }
}