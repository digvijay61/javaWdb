package quickmart.gui;

import quickmart.management.ItemManager;
import quickmart.management.TransactionManager;
import quickmart.models.*;
import quickmart.payment.PaymentMethods;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class BuyerPanel extends JPanel {

    private final QuickMartGUI mainFrame;
    private final ItemManager itemManager;
    private Buyer currentBuyer;

    private JTable availableItemsTable;
    private ItemTableModel availableItemsTableModel;

    private JTable cartTable;
    private CartTableModel cartTableModel;
    private JLabel totalLabel;

    private JButton viewAllButton, addToCartButton, viewCartButton, checkoutButton, logoutButton;

    public BuyerPanel(QuickMartGUI mainFrame, ItemManager itemManager) {
        this.mainFrame = mainFrame;
        this.itemManager = itemManager;
        initComponents();
        loadAvailableItems();
    }

    public void setCurrentBuyer(Buyer buyer) {
        this.currentBuyer = buyer;
        if (this.currentBuyer != null) {
             // Reset cart when a new buyer logs in
             this.currentBuyer.getCart().clearCart(); // Use buyer's cart
             updateCartView();
             enableBuyerControls(true);
        } else {
             enableBuyerControls(false);
             // Optionally clear tables if needed on logout
             availableItemsTableModel.setItems(new ArrayList<>());
             cartTableModel.setItems(new ArrayList<>());
             totalLabel.setText("Total: $0.00");
        }
    }

    private void enableBuyerControls(boolean enable) {
         addToCartButton.setEnabled(enable);
         checkoutButton.setEnabled(enable);
         // viewCartButton doesn't strictly need disabling, but can be
         viewCartButton.setEnabled(enable);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // --- Top Panel: Buttons ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        viewAllButton = new JButton("View Available Items");
        viewCartButton = new JButton("View Cart / Refresh Cart"); // Combined functionality
        logoutButton = new JButton("Logout");
        topPanel.add(viewAllButton);
        topPanel.add(viewCartButton);
        topPanel.add(logoutButton);
        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Split Pane for Available Items and Cart ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6); // Give more space to available items initially

        // Available Items Table
        JPanel availableItemsPanel = new JPanel(new BorderLayout(5, 5));
        availableItemsPanel.setBorder(BorderFactory.createTitledBorder("Available Items"));
        availableItemsTableModel = new ItemTableModel(); // Using ItemTableModel
        availableItemsTable = new JTable(availableItemsTableModel);
        availableItemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableItemsTable.setAutoCreateRowSorter(true);
        availableItemsTable.setFillsViewportHeight(true);
        JScrollPane availableScrollPane = new JScrollPane(availableItemsTable);
        availableItemsPanel.add(availableScrollPane, BorderLayout.CENTER);

        addToCartButton = new JButton("Add Selected Item to Cart");
        JPanel addBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Panel for the button
        addBtnPanel.add(addToCartButton);
        availableItemsPanel.add(addBtnPanel, BorderLayout.SOUTH); // Add button panel below table


        // Cart Table
        JPanel cartPanel = new JPanel(new BorderLayout(5, 5));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Your Cart"));
        cartTableModel = new CartTableModel(); // Using CartTableModel
        cartTable = new JTable(cartTableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Can select items to potentially remove later
        cartTable.setFillsViewportHeight(true);
        JScrollPane cartScrollPane = new JScrollPane(cartTable);
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);

        JPanel cartBottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        checkoutButton = new JButton("Checkout");
        cartBottomPanel.add(totalLabel);
        cartBottomPanel.add(checkoutButton);
        cartPanel.add(cartBottomPanel, BorderLayout.SOUTH);

        splitPane.setTopComponent(availableItemsPanel);
        splitPane.setBottomComponent(cartPanel);

        add(splitPane, BorderLayout.CENTER);

        enableBuyerControls(false); // Disable controls until login

        // --- Action Listeners ---
        viewAllButton.addActionListener(e -> loadAvailableItems());
        addToCartButton.addActionListener(e -> addItemToCart());
        viewCartButton.addActionListener(e -> updateCartView()); // Refresh cart view
        checkoutButton.addActionListener(e -> checkout());
        logoutButton.addActionListener(e -> logout());
    }

    void loadAvailableItems() {
        try {
            List<Item> allItems = itemManager.getAllItems();
            // Optionally filter out items owned by the current buyer if necessary
            availableItemsTableModel.setItems(allItems);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading available items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

     private void addItemToCart() {
        if (currentBuyer == null) {
            JOptionPane.showMessageDialog(this, "Please login first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int selectedRow = availableItemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to add.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int modelRow = availableItemsTable.convertRowIndexToModel(selectedRow);
            // Use the correct model here
            Item selectedItem = ((ItemTableModel) availableItemsTableModel).getItemAt(modelRow);


            if (selectedItem != null) {
                 // Prevent buyer from adding their own items if they are also a seller
                 if(selectedItem.getSellerId() == currentBuyer.getUserId()) {
                     JOptionPane.showMessageDialog(this, "You cannot add your own item to the cart.", "Error", JOptionPane.ERROR_MESSAGE);
                     return;
                 }

                currentBuyer.getCart().addItem(selectedItem);
                JOptionPane.showMessageDialog(this, "'" + selectedItem.getTitle() + "' added to cart.", "Item Added", JOptionPane.INFORMATION_MESSAGE);
                updateCartView(); // Update cart display immediately
            } else {
                 JOptionPane.showMessageDialog(this, "Could not retrieve selected item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding item to cart: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    void updateCartView() {
         if (currentBuyer == null) {
             // Clear cart view if logged out
             cartTableModel.setItems(new ArrayList<>());
             totalLabel.setText("Total: $0.00");
             return;
         }
        Cart cart = currentBuyer.getCart();
        cartTableModel.setItems(new ArrayList<>(cart.getItems())); // Pass a copy
        totalLabel.setText(String.format("Total: $%.2f", cart.getTotalPrice()));
    }

    private void checkout() {
        if (currentBuyer == null) {
            JOptionPane.showMessageDialog(this, "Please login first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cart cart = currentBuyer.getCart();
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.", "Checkout Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = cart.getTotalPrice();

        // Payment Method Selection Dialog
        String[] paymentOptions = {"Cash", "Online Payment"};
        int paymentChoice = JOptionPane.showOptionDialog(this,
                String.format("Total Amount: $%.2f\nSelect Payment Method:", total),
                "Checkout - Payment",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                paymentOptions, paymentOptions[0]);

        if (paymentChoice == JOptionPane.CLOSED_OPTION) {
            JOptionPane.showMessageDialog(this, "Checkout cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            return; // User closed the dialog
        }

        PaymentMethods.PaymentStrategy paymentStrategy;
        String paymentMethodType;

        if (paymentChoice == 0) { // Cash
            paymentStrategy = new PaymentMethods.CashPayment();
            paymentMethodType = "Cash";
        } else { // Online Payment
            // Simple Online Payment Input (Not Secure!)
            JTextField cardNumberField = new JTextField(16);
            JTextField cvvField = new JPasswordField(3); // Use JPasswordField
            JTextField expiryDateField = new JTextField("MM/YY", 5);

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Card Number:"));
            panel.add(cardNumberField);
            panel.add(new JLabel("CVV:"));
            panel.add(cvvField);
            panel.add(new JLabel("Expiry Date (MM/YY):"));
            panel.add(expiryDateField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Enter Online Payment Details",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                 String cardNum = cardNumberField.getText();
                 String cvv = new String(((JPasswordField) cvvField).getPassword()); // Get password
                 String expiry = expiryDateField.getText();

                 if (cardNum.isEmpty() || cvv.isEmpty() || expiry.isEmpty() || expiry.equals("MM/YY")) {
                      JOptionPane.showMessageDialog(this, "Card details incomplete. Checkout cancelled.", "Error", JOptionPane.ERROR_MESSAGE);
                      return;
                 }
                // Basic validation example (add more robust checks)
                 if (cardNum.length() < 13 || cardNum.length() > 19 || !cardNum.matches("\\d+")) {
                      JOptionPane.showMessageDialog(this, "Invalid card number format. Checkout cancelled.", "Error", JOptionPane.ERROR_MESSAGE);
                      return;
                 }
                 if (cvv.length() != 3 || !cvv.matches("\\d+")) {
                     JOptionPane.showMessageDialog(this, "Invalid CVV format (must be 3 digits). Checkout cancelled.", "Error", JOptionPane.ERROR_MESSAGE);
                      return;
                 }
                 if (!expiry.matches("\\d{2}/\\d{2}")) {
                     JOptionPane.showMessageDialog(this, "Invalid expiry date format (must be MM/YY). Checkout cancelled.", "Error", JOptionPane.ERROR_MESSAGE);
                      return;
                 }


                paymentStrategy = new PaymentMethods.OnlinePayment(cardNum, cvv, expiry);
                paymentMethodType = "Online";
            } else {
                JOptionPane.showMessageDialog(this, "Checkout cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                return; // User cancelled card details input
            }
        }

        // Process Transaction (Simulate)
        try {
             System.out.println("DEBUG: Creating transaction...");
             // Create a copy of items for the transaction
             List<Item> itemsForTransaction = new ArrayList<>(cart.getItems());

             // Transaction creation now handles DB saving and payment processing simulation
             Transaction transaction = new Transaction(
                     TransactionManager.getNextTransactionId(), // Use manager for ID generation
                     currentBuyer,
                     itemsForTransaction, // Pass the copy
                     paymentStrategy);

              System.out.println("DEBUG: Transaction object created, ID: " + transaction.getTransactionId() + ", Successful: " + transaction.isSuccessful());

            // Display result based on simulated payment success
            if (transaction.isSuccessful()) {
                JOptionPane.showMessageDialog(this,
                        "Checkout Successful!\nTransaction ID: " + transaction.getTransactionId() +
                        "\nAmount Paid: $" + String.format("%.2f", total) +
                        "\nPayment Method: " + paymentMethodType,
                        "Purchase Complete", JOptionPane.INFORMATION_MESSAGE);
                cart.clearCart(); // Clear the cart in memory
                updateCartView(); // Update GUI
                loadAvailableItems(); // Refresh available items (optional, if stock changes)
            } else {
                JOptionPane.showMessageDialog(this,
                        "Payment Failed. Please check your details or try another method.",
                        "Checkout Failed", JOptionPane.ERROR_MESSAGE);
                 // Don't clear cart if payment failed
            }

        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Error during checkout process: " + e.getMessage(), "Checkout Error", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
        }

    }


     private void logout() {
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            setCurrentBuyer(null); // Clear buyer info
            mainFrame.showPanel(QuickMartGUI.MAIN_PANEL);
        }
    }
}