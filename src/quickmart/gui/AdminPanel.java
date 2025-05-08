package quickmart.gui;

import quickmart.management.ItemManager;
import quickmart.management.UserManager;
import quickmart.models.Item;
import quickmart.models.User; // Required for User management features

import javax.swing.*;
import javax.swing.table.TableRowSorter; // Import TableRowSorter
import javax.swing.event.ListSelectionEvent; // Import event classes
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class AdminPanel extends JPanel {

    private final QuickMartGUI mainFrame;
    private final ItemManager itemManager;
    private final UserManager userManager;

    // --- Item Management Components ---
    private JTable itemsTable;
    private ItemTableModel itemTableModel;
    private JTextField itemTitleField, itemDescField, itemPriceField, itemIdField;
    private JCheckBox itemForRentCheckbox;
    private JTextField itemSellerIdField;
    private JButton itemLoadButton, itemUpdateButton, itemDeleteButton, itemClearButton;

    // --- User Management Components ---
    private JTable userTable; // Use JTable instead of JTextArea
    private UserTableModel userTableModel; // Use the new model
    private JButton userLoadButton, userDeleteButton;

    private JButton backButton;


    public AdminPanel(QuickMartGUI mainFrame, ItemManager itemManager, UserManager userManager) {
        this.mainFrame = mainFrame;
        this.itemManager = itemManager;
        this.userManager = userManager;
        initComponents();
        // Initial load is now handled by onPanelShown when the panel becomes visible
    }

    /**
     * Method called when this panel is shown by the CardLayout.
     * Use this to refresh data. Accessible from QuickMartGUI.
     */
    public void onPanelShown() { // Changed to public for clarity, package-private also works
        System.out.println("DEBUG: AdminPanel shown. Reloading data...");
        loadItems();
        loadUsers(); // Load users when the admin panel is displayed
    }


    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        // --- Top Panel: Back Button ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("← Back to Main Menu");
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        // --- Center: Tabbed Pane for Items/Users ---
        JTabbedPane tabbedPane = new JTabbedPane();

        // --- Item Management Tab ---
        JPanel itemManagementPanel = createItemManagementPanel();
        tabbedPane.addTab("Manage Items", itemManagementPanel);

        // --- User Management Tab ---
        JPanel userManagementPanel = createUserManagementPanel(); // Use helper method
        tabbedPane.addTab("Manage Users", userManagementPanel);

        add(tabbedPane, BorderLayout.CENTER);


        // --- Action Listeners ---
        backButton.addActionListener(e -> {
            clearItemInputFields(); // Clear fields when leaving
            mainFrame.showPanel(QuickMartGUI.MAIN_PANEL);
             });

        // Item Listeners
        itemLoadButton.addActionListener(e -> loadItems());
        itemUpdateButton.addActionListener(e -> updateItem());
        itemDeleteButton.addActionListener(e -> deleteItem());
        itemClearButton.addActionListener(e -> clearItemInputFields());

         // User Listeners
         userLoadButton.addActionListener(e -> loadUsers());
         userDeleteButton.addActionListener(e -> deleteUser());

        // Add listener to item table selection
        itemsTable.getSelectionModel().addListSelectionListener(e -> {
             if (!e.getValueIsAdjusting()) {
                 populateItemFieldsFromSelectedRow();
                 // Enable/disable buttons based on selection
                 boolean rowSelected = itemsTable.getSelectedRow() != -1;
                 itemUpdateButton.setEnabled(rowSelected);
                 itemDeleteButton.setEnabled(rowSelected);
             }
        });

         // Add listener to user table selection
         userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Enable delete button only if a row is selected
                userDeleteButton.setEnabled(userTable.getSelectedRow() != -1);
            }
        });

    }

    // Helper method to create Item Management Panel with corrected layout
    private JPanel createItemManagementPanel() {
        JPanel itemManagementPanel = new JPanel(new BorderLayout(10, 10));
        itemManagementPanel.setBorder(BorderFactory.createTitledBorder("Item Management"));

        // Item Table (Keep this part as is)
        itemTableModel = new ItemTableModel();
        itemsTable = new JTable(itemTableModel);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsTable.setAutoCreateRowSorter(true);
        itemsTable.setFillsViewportHeight(true);
        itemsTable.setRowHeight(25);
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        itemsTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        itemsTable.getColumnModel().getColumn(5).setPreferredWidth(60);

        JScrollPane itemScrollPane = new JScrollPane(itemsTable);
        itemManagementPanel.add(itemScrollPane, BorderLayout.CENTER);

        // --- Item Control Panel (Fields and Buttons) - CORRECTED LAYOUT ---
        JPanel itemControlPanel = new JPanel(new GridBagLayout());
        itemControlPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5); // Padding between components
        gbc.anchor = GridBagConstraints.WEST; // Default anchor

        // Row 0: Item ID and Reload Button
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST; itemControlPanel.add(new JLabel("Item ID:"), gbc); // Label right-aligned
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5; // Field takes some space
        itemIdField = new JTextField(8); itemIdField.setEditable(false); itemControlPanel.add(itemIdField, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; // Button doesn't expand
        itemLoadButton = new JButton("Reload Item List"); itemControlPanel.add(itemLoadButton, gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        // Row 1: Title
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; itemControlPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; // Field takes most space
        itemTitleField = new JTextField(25); itemControlPanel.add(itemTitleField, gbc);
        gbc.gridwidth = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE; // Reset

        // Row 2: Description
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; itemControlPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; // Field takes most space
        itemDescField = new JTextField(25); itemControlPanel.add(itemDescField, gbc);
        gbc.gridwidth = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE; // Reset

        // Row 3: Price and For Rent (Now aligned better)
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; itemControlPanel.add(new JLabel("Price ($):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5; // Price field takes half remaining space
        itemPriceField = new JTextField(8); itemControlPanel.add(itemPriceField, gbc);
        gbc.gridx = 2; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; // Label takes minimal space
        itemControlPanel.add(new JLabel("For Rent:"), gbc);
        gbc.gridx = 3; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0; // Checkbox takes minimal space
        itemForRentCheckbox = new JCheckBox(); itemControlPanel.add(itemForRentCheckbox, gbc);
        gbc.weightx = 0; // Reset weightx

        // Row 4: Seller ID
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST; itemControlPanel.add(new JLabel("Seller ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5; // Field takes some space
        itemSellerIdField = new JTextField(8); itemSellerIdField.setToolTipText("Enter the User ID of the seller (-1 for System/Default)"); itemControlPanel.add(itemSellerIdField, gbc);
        // Leave columns 2 and 3 empty on this row, reset weightx
        gbc.gridx = 2; gbc.weightx = 0;
        gbc.gridx = 3; gbc.weightx = 0;

        // Row 5: Buttons Row
        JPanel itemActionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        itemClearButton = new JButton("Clear Form");
        itemUpdateButton = new JButton("Update Item"); itemUpdateButton.setEnabled(false);
        itemDeleteButton = new JButton("Delete Item"); itemDeleteButton.setEnabled(false);
        itemActionButtonPanel.add(itemClearButton);
        itemActionButtonPanel.add(itemUpdateButton);
        itemActionButtonPanel.add(itemDeleteButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(10, 5, 5, 5); // More space above buttons
        itemControlPanel.add(itemActionButtonPanel, gbc);
        // --- End of Corrected Layout ---

        itemManagementPanel.add(itemControlPanel, BorderLayout.SOUTH);
        return itemManagementPanel;
    }

     // Helper method to create User Management Panel
    private JPanel createUserManagementPanel() {
        JPanel userManagementPanel = new JPanel(new BorderLayout(10, 10));
        userManagementPanel.setBorder(BorderFactory.createTitledBorder("User Management"));

        // User Table
        userTableModel = new UserTableModel();
        userTable = new JTable(userTableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setAutoCreateRowSorter(true);
        userTable.setFillsViewportHeight(true);
        userTable.setRowHeight(25);
        // Set user table column widths (adjust as needed)
        userTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        userTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        userTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Email
        userTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Role


        JScrollPane userScrollPane = new JScrollPane(userTable);
        userManagementPanel.add(userScrollPane, BorderLayout.CENTER);

        // User Control Panel
        JPanel userControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        userLoadButton = new JButton("Reload User List");
        userDeleteButton = new JButton("Delete Selected User");
        userDeleteButton.setEnabled(false); // Initially disabled
        userDeleteButton.setBackground(new Color(220, 53, 69)); // Red delete button
        userDeleteButton.setForeground(Color.WHITE);
        userControlPanel.add(userLoadButton);
        userControlPanel.add(userDeleteButton);

        userManagementPanel.add(userControlPanel, BorderLayout.SOUTH);
        return userManagementPanel;
    }

    private void populateItemFieldsFromSelectedRow() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = itemsTable.convertRowIndexToModel(selectedRow);
            Item selectedItem = itemTableModel.getItemAt(modelRow);
            if (selectedItem != null) {
                itemIdField.setText(String.valueOf(selectedItem.getItemId()));
                itemTitleField.setText(selectedItem.getTitle());
                itemDescField.setText(selectedItem.getDescription());
                // *** FIX: Get price directly from the Item object ***
                itemPriceField.setText(String.valueOf(selectedItem.getPrice()));
                // *** END FIX ***
                itemForRentCheckbox.setSelected(selectedItem.isForRent());
                itemSellerIdField.setText(String.valueOf(selectedItem.getSellerId()));
            }
        } else {
             clearItemInputFields();
        }
        // Button enable/disable state is handled by the listener directly
    }

    // Changed access modifier to package-private (no keyword) or public
    void loadItems() {
        try {
            System.out.println("DEBUG: AdminPanel loading items...");
            List<Item> allItems = itemManager.getAllItems();
            itemTableModel.setItems(allItems);
            clearItemInputFields();
             System.out.println("DEBUG: AdminPanel items loaded: " + allItems.size());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

     // Changed access modifier to package-private (no keyword) or public
     void loadUsers() {
         try {
             System.out.println("DEBUG: AdminPanel loading users...");
             List<User> allUsers = userManager.getAllUsers();
             userTableModel.setUsers(allUsers);
             userDeleteButton.setEnabled(false);
             userTable.clearSelection();
              System.out.println("DEBUG: AdminPanel users loaded: " + allUsers.size());
         } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Error loading user list: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
         }
     }


    private void updateItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) return; // Button should be disabled

        try {
            int modelRow = itemsTable.convertRowIndexToModel(selectedRow);
            Item selectedItem = itemTableModel.getItemAt(modelRow);
            if (selectedItem == null) return;

            int itemId = selectedItem.getItemId();
            String title = itemTitleField.getText().trim();
            String description = itemDescField.getText().trim();
            String priceStr = itemPriceField.getText().trim();
            boolean isForRent = itemForRentCheckbox.isSelected();
            String sellerIdStr = itemSellerIdField.getText().trim();


            if (title.isEmpty() || description.isEmpty() || priceStr.isEmpty() || sellerIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields (Title, Description, Price, Seller ID) are required for update.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double price;
             int sellerId;
            try {
                price = Double.parseDouble(priceStr);
                 sellerId = Integer.parseInt(sellerIdStr);
                if (price <= 0) {
                    JOptionPane.showMessageDialog(this, "Price must be a positive value.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Optional: Add check if sellerId is a valid user ID
                // if (sellerId != -1 && userManager.getUserById(sellerId) == null) { // Requires getUserById in UserManager
                //    JOptionPane.showMessageDialog(this, "Invalid Seller ID. User does not exist.", "Input Error", JOptionPane.ERROR_MESSAGE);
                //    return;
                // }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid format for Price or Seller ID. Please enter numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Item updatedItem = new Item(itemId, title, description, price, isForRent, sellerId);

            itemManager.updateItem(updatedItem);
            JOptionPane.showMessageDialog(this, "Item updated successfully!", "Admin Success", JOptionPane.INFORMATION_MESSAGE);
            loadItems();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating item: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) return; // Button should be disabled

        int modelRow = itemsTable.convertRowIndexToModel(selectedRow);
        Item selectedItem = itemTableModel.getItemAt(modelRow);
        if (selectedItem == null) return;

        int itemId = selectedItem.getItemId();

        int confirmation = JOptionPane.showConfirmDialog(this,
                "ADMIN ACTION: Are you sure you want to permanently delete item:\n'"
                + selectedItem.getTitle() + "' (ID: " + itemId + ")?\n"
                + "This action cannot be undone and may fail if the item is in a transaction.",
                "Confirm Item Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                itemManager.deleteItem(itemId);
                 // Feedback based on ItemManager's console output
                 JOptionPane.showMessageDialog(this, "Attempted to delete item ID: " + itemId + ".\nCheck console output for confirmation.", "Item Deletion", JOptionPane.INFORMATION_MESSAGE);
                 loadItems();
            } catch (Exception ex) {
                 if (ex.getMessage() != null && (ex.getMessage().toLowerCase().contains("foreign key constraint") || ex.getMessage().toLowerCase().contains("cannot delete or update a parent row"))) {
                      JOptionPane.showMessageDialog(this, "Cannot delete item ID " + itemId + ".\nIt is likely referenced in existing transactions.", "Deletion Failed", JOptionPane.ERROR_MESSAGE);
                 } else {
                    JOptionPane.showMessageDialog(this, "Error deleting item: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                 }
                ex.printStackTrace();
            }
        }
    }

    private void clearItemInputFields() {
        itemIdField.setText("");
        itemTitleField.setText("");
        itemDescField.setText("");
        itemPriceField.setText("");
        itemForRentCheckbox.setSelected(false);
        itemSellerIdField.setText("");
        itemsTable.clearSelection(); // This triggers the listener to disable buttons
    }

     private void deleteUser() {
         int selectedRow = userTable.getSelectedRow();
         if (selectedRow == -1) {
              JOptionPane.showMessageDialog(this, "Please select a user from the list to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
             return;
         }

        try {
            int modelRow = userTable.convertRowIndexToModel(selectedRow);
            User userToDelete = userTableModel.getUserAt(modelRow);

            if (userToDelete == null) {
                 JOptionPane.showMessageDialog(this, "Could not retrieve selected user data.", "Internal Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int userId = userToDelete.getUserId();
            String userName = userToDelete.getName();

            if (userId == -1) {
                 JOptionPane.showMessageDialog(this, "The special 'System' user (ID: -1) cannot be deleted.", "Action Denied", JOptionPane.WARNING_MESSAGE);
                 return;
            }

            int confirmation = JOptionPane.showConfirmDialog(this,
                 "ADMIN ACTION: Are you sure you want to permanently delete user:\n"
                 + "'" + userName + "' (ID: " + userId + ")?\n"
                 + "This may fail if the user has associated items or transactions.\n"
                 + "This action cannot be undone.",
                 "Confirm User Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);


            if (confirmation == JOptionPane.YES_OPTION) {
                // UserManager's deleteUser method includes dependency checks and feedback
                userManager.deleteUser(userId);
                // Refresh the list AFTER the attempt. Feedback is handled by UserManager.
                loadUsers();
            }

        } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, "Error processing user deletion: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             ex.printStackTrace();
        }
    }

} // End of AdminPanel class