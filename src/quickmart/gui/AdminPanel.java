package quickmart.gui;

import quickmart.management.ItemManager;
import quickmart.management.UserManager;
import quickmart.models.Item;
import quickmart.models.User; // Import User if you create UserTableModel

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.ArrayList; // Import ArrayList

public class AdminPanel extends JPanel {

    private final QuickMartGUI mainFrame;
    private final ItemManager itemManager;
    private final UserManager userManager;

    // --- Item Management Components ---
    private JTable itemsTable;
    private ItemTableModel itemTableModel;
    private JTextField itemTitleField, itemDescField, itemPriceField, itemIdField;
    private JButton itemLoadButton, itemUpdateButton, itemDeleteButton;

    // --- User Management Components (Optional - Add JTable, Model, Fields, Buttons if needed) ---
    // private JTable userTable;
    // private UserTableModel userTableModel;
    // private JTextField userIdField, userNameField, userEmailField;
    // private JButton userLoadButton, userDeleteButton;

    private JButton backButton;


    public AdminPanel(QuickMartGUI mainFrame, ItemManager itemManager, UserManager userManager) {
        this.mainFrame = mainFrame;
        this.itemManager = itemManager;
        this.userManager = userManager; // Store UserManager
        initComponents();
        loadItems(); // Initial load
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // --- Top Panel: Back Button ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("Back to Main Menu");
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        // --- Center: Tabbed Pane for Items/Users ---
        JTabbedPane tabbedPane = new JTabbedPane();

        // --- Item Management Tab ---
        JPanel itemManagementPanel = new JPanel(new BorderLayout(10, 10));
        itemManagementPanel.setBorder(BorderFactory.createTitledBorder("Item Management"));

        // Item Table
        itemTableModel = new ItemTableModel();
        itemsTable = new JTable(itemTableModel);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsTable.setAutoCreateRowSorter(true);
        itemsTable.setFillsViewportHeight(true);
        itemsTable.getSelectionModel().addListSelectionListener(e -> {
             if (!e.getValueIsAdjusting()) populateItemFieldsFromSelectedRow();
        });
        JScrollPane itemScrollPane = new JScrollPane(itemsTable);
        itemManagementPanel.add(itemScrollPane, BorderLayout.CENTER);

        // Item Control Panel (Fields and Buttons)
        JPanel itemControlPanel = new JPanel(new GridBagLayout());
        itemControlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; itemControlPanel.add(new JLabel("Item ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; itemIdField = new JTextField(8); itemIdField.setEditable(false); itemControlPanel.add(itemIdField, gbc);
        gbc.gridx = 2; gbc.gridy = 0; itemLoadButton = new JButton("Reload Items"); itemControlPanel.add(itemLoadButton, gbc); // Add reload button

        gbc.gridx = 0; gbc.gridy = 1; itemControlPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; itemTitleField = new JTextField(20); itemControlPanel.add(itemTitleField, gbc); gbc.gridwidth=1; gbc.fill=GridBagConstraints.NONE;

        gbc.gridx = 0; gbc.gridy = 2; itemControlPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; itemDescField = new JTextField(20); itemControlPanel.add(itemDescField, gbc); gbc.gridwidth=1; gbc.fill=GridBagConstraints.NONE;

        gbc.gridx = 0; gbc.gridy = 3; itemControlPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; itemPriceField = new JTextField(8); itemControlPanel.add(itemPriceField, gbc);

        // Buttons Row
        JPanel itemActionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        itemUpdateButton = new JButton("Update Item"); itemUpdateButton.setEnabled(false); // Initially disabled
        itemDeleteButton = new JButton("Delete Item"); itemDeleteButton.setEnabled(false); // Initially disabled
        itemActionButtonPanel.add(itemUpdateButton);
        itemActionButtonPanel.add(itemDeleteButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.CENTER;
        itemControlPanel.add(itemActionButtonPanel, gbc);


        itemManagementPanel.add(itemControlPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Manage Items", itemManagementPanel);

        // --- User Management Tab (Placeholder - Implement fully if needed) ---
        JPanel userManagementPanel = new JPanel(new BorderLayout(10, 10));
        userManagementPanel.setBorder(BorderFactory.createTitledBorder("User Management (Placeholder)"));
        userManagementPanel.add(new JLabel("User management features (delete user) can be added here.", SwingConstants.CENTER), BorderLayout.CENTER);
        // TODO: Add JTable for users, UserTableModel, fields, and buttons similar to item management
         JButton deleteUserButton = new JButton("Delete User by ID");
         userManagementPanel.add(deleteUserButton, BorderLayout.SOUTH);
         deleteUserButton.addActionListener(e -> deleteUser()); // Add listener


        tabbedPane.addTab("Manage Users", userManagementPanel);

        add(tabbedPane, BorderLayout.CENTER);


        // --- Action Listeners ---
        backButton.addActionListener(e -> mainFrame.showPanel(QuickMartGUI.MAIN_PANEL));
        itemLoadButton.addActionListener(e -> loadItems());
        itemUpdateButton.addActionListener(e -> updateItem());
        itemDeleteButton.addActionListener(e -> deleteItem());

        // Add other listeners (e.g., user management) if implemented
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
                itemPriceField.setText(String.valueOf(selectedItem.getPrice()));
                itemUpdateButton.setEnabled(true); // Enable buttons on selection
                itemDeleteButton.setEnabled(true);
            }
        } else {
            // Clear fields and disable buttons if no row is selected
            clearItemInputFields();
            itemUpdateButton.setEnabled(false);
            itemDeleteButton.setEnabled(false);
        }
    }

    void loadItems() {
        try {
            List<Item> allItems = itemManager.getAllItems();
            itemTableModel.setItems(allItems);
            clearItemInputFields(); // Clear fields after loading
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int modelRow = itemsTable.convertRowIndexToModel(selectedRow);
            Item selectedItem = itemTableModel.getItemAt(modelRow);
            if (selectedItem == null) return; // Should not happen if row selected

            int itemId = selectedItem.getItemId();
            String title = itemTitleField.getText().trim();
            String description = itemDescField.getText().trim();
            String priceStr = itemPriceField.getText().trim();

            if (title.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title, Description, and Price cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double price = Double.parseDouble(priceStr);
             if (price < 0) {
                 JOptionPane.showMessageDialog(this, "Price cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            // Create updated item object (keep original isForRent and sellerId)
            Item updatedItem = new Item(itemId, title, description, price, selectedItem.isForRent(), selectedItem.getSellerId());

            itemManager.updateItem(updatedItem);
            JOptionPane.showMessageDialog(this, "Item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadItems(); // Refresh view
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating item: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int modelRow = itemsTable.convertRowIndexToModel(selectedRow);
        Item selectedItem = itemTableModel.getItemAt(modelRow);
        if (selectedItem == null) return;

        int itemId = selectedItem.getItemId();

        int confirmation = JOptionPane.showConfirmDialog(this,
                "ADMIN ACTION: Are you sure you want to delete item '" + selectedItem.getTitle() + "' (ID: " + itemId + ")?",
                "Confirm Item Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                itemManager.deleteItem(itemId);
                JOptionPane.showMessageDialog(this, "Item deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadItems(); // Refresh view
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting item: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void clearItemInputFields() {
        itemIdField.setText("");
        itemTitleField.setText("");
        itemDescField.setText("");
        itemPriceField.setText("");
        itemsTable.clearSelection();
        itemUpdateButton.setEnabled(false);
        itemDeleteButton.setEnabled(false);
    }

     // --- User Management Methods (Example) ---
     private void deleteUser() {
        String userIdStr = JOptionPane.showInputDialog(this, "Enter User ID to delete:");
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
             return; // User cancelled or entered nothing
        }

        try {
            int userId = Integer.parseInt(userIdStr.trim());

            if (userId == -1) { // Prevent deleting the system user
                 JOptionPane.showMessageDialog(this, "Cannot delete the System user.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

             // Optional: Add a lookup here to show the user's name before confirming deletion
             // User userToDelete = userManager.getUserById(userId); // Need getUserById in UserManager
             // if (userToDelete == null) { ... show error ... }
             // String confirmMsg = "ADMIN ACTION: Delete user '" + userToDelete.getName() + "' (ID: " + userId + ")?";

            int confirmation = JOptionPane.showConfirmDialog(this,
                 "ADMIN ACTION: Are you sure you want to delete User ID: " + userId + "?\n(Ensure no items depend on this user if they are a seller!)",
                 "Confirm User Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);


            if (confirmation == JOptionPane.YES_OPTION) {
                userManager.deleteUser(userId);
                // Assuming deleteUser shows its own success/failure message via System.out
                // For GUI, it's better if deleteUser throws an exception on failure or returns a boolean
                JOptionPane.showMessageDialog(this, "Attempted to delete user ID: " + userId + ".\nCheck console or implement return status for confirmation.", "User Deletion", JOptionPane.INFORMATION_MESSAGE);
                // TODO: Reload user table if implemented
            }

        } catch (NumberFormatException ex) {
             JOptionPane.showMessageDialog(this, "Invalid User ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
             ex.printStackTrace();
        }
    }

}