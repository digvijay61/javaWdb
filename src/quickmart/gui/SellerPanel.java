package quickmart.gui;

import quickmart.management.ItemManager;
import quickmart.models.Item;
import quickmart.models.Seller;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SellerPanel extends JPanel {

    private final QuickMartGUI mainFrame;
    private final ItemManager itemManager;
    Seller currentSeller; // To store logged-in seller info

    private JTable itemsTable;
    private ItemTableModel itemTableModel;
    private JTextField titleField, descriptionField, priceField, itemIdField;
    private JButton addButton, updateButton, deleteButton, viewAllButton, viewMyButton, logoutButton;

    public SellerPanel(QuickMartGUI mainFrame, ItemManager itemManager) {
        this.mainFrame = mainFrame;
        this.itemManager = itemManager;
        initComponents();
        loadItems(); // Initial load (can be empty)
    }

    public void setCurrentSeller(Seller seller) {
        this.currentSeller = seller;
        if (currentSeller != null) {
            viewMyItems(); // Load seller's items when they log in
            enableSellerControls(true);
        } else {
            itemTableModel.setItems(new ArrayList<>()); // Clear table on logout
             enableSellerControls(false);
        }
    }

     private void enableSellerControls(boolean enable) {
        addButton.setEnabled(enable);
        updateButton.setEnabled(enable);
        deleteButton.setEnabled(enable);
        viewMyButton.setEnabled(enable);
        itemIdField.setEnabled(enable);
        titleField.setEnabled(enable);
        descriptionField.setEnabled(enable);
        priceField.setEnabled(enable);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // --- Top Panel: Buttons ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        viewAllButton = new JButton("View All Items");
        viewMyButton = new JButton("View My Items");
        logoutButton = new JButton("Logout");
        topPanel.add(viewAllButton);
        topPanel.add(viewMyButton);
        topPanel.add(logoutButton);
        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Table ---
        itemTableModel = new ItemTableModel();
        itemsTable = new JTable(itemTableModel);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsTable.setAutoCreateRowSorter(true); // Enable sorting
        itemsTable.setFillsViewportHeight(true); // Use available height

        // Add listener to populate fields when a row is selected
        itemsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFieldsFromSelectedRow();
            }
        });

        JScrollPane scrollPane = new JScrollPane(itemsTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel: Input Fields and Action Buttons ---
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Item Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Labels and Fields
        gbc.gridx = 0; gbc.gridy = 0; bottomPanel.add(new JLabel("Item ID (for Update/Delete):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; itemIdField = new JTextField(10); itemIdField.setEditable(false); bottomPanel.add(itemIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; bottomPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth=3; gbc.fill = GridBagConstraints.HORIZONTAL; titleField = new JTextField(25); bottomPanel.add(titleField, gbc);
        gbc.gridwidth=1; gbc.fill = GridBagConstraints.NONE; // reset

        gbc.gridx = 0; gbc.gridy = 2; bottomPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth=3; gbc.fill = GridBagConstraints.HORIZONTAL; descriptionField = new JTextField(25); bottomPanel.add(descriptionField, gbc);
        gbc.gridwidth=1; gbc.fill = GridBagConstraints.NONE; // reset

        gbc.gridx = 0; gbc.gridy = 3; bottomPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; priceField = new JTextField(10); bottomPanel.add(priceField, gbc);

        // Action Buttons Panel
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Add New Item");
        updateButton = new JButton("Update Selected Item");
        deleteButton = new JButton("Delete Selected Item");
        actionButtonPanel.add(addButton);
        actionButtonPanel.add(updateButton);
        actionButtonPanel.add(deleteButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.HORIZONTAL;
        bottomPanel.add(actionButtonPanel, gbc);

        add(bottomPanel, BorderLayout.SOUTH);

         enableSellerControls(false); // Disable controls initially until login

        // --- Action Listeners ---
        viewAllButton.addActionListener(e -> loadItems());
        viewMyButton.addActionListener(e -> viewMyItems());
        addButton.addActionListener(e -> addItem());
        updateButton.addActionListener(e -> updateItem());
        deleteButton.addActionListener(e -> deleteItem());
        logoutButton.addActionListener(e -> logout());
    }

     private void populateFieldsFromSelectedRow() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow != -1) {
            // Convert view row index to model row index in case of sorting/filtering
            int modelRow = itemsTable.convertRowIndexToModel(selectedRow);
            Item selectedItem = itemTableModel.getItemAt(modelRow);
            if (selectedItem != null) {
                 // Only allow editing/deleting if the item belongs to the current seller
                 boolean enableEdit = (currentSeller != null && selectedItem.getSellerId() == currentSeller.getUserId());

                 itemIdField.setText(String.valueOf(selectedItem.getItemId()));
                 titleField.setText(selectedItem.getTitle());
                 descriptionField.setText(selectedItem.getDescription());
                 priceField.setText(String.valueOf(selectedItem.getPrice()));

                 titleField.setEditable(enableEdit);
                 descriptionField.setEditable(enableEdit);
                 priceField.setEditable(enableEdit);
                 updateButton.setEnabled(enableEdit);
                 deleteButton.setEnabled(enableEdit);

            }
        } else {
             // No row selected, clear fields and disable update/delete
             clearInputFields();
             itemIdField.setText("");
             titleField.setEditable(currentSeller != null); // Can add new if logged in
             descriptionField.setEditable(currentSeller != null);
             priceField.setEditable(currentSeller != null);
             updateButton.setEnabled(false);
             deleteButton.setEnabled(false);
        }
    }


    void loadItems() {
        try {
            List<Item> allItems = itemManager.getAllItems();
            itemTableModel.setItems(allItems);
             clearInputFields(); // Clear fields after loading
             populateFieldsFromSelectedRow(); // Update button states etc. based on selection
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    void viewMyItems() {
         if (currentSeller == null) {
             JOptionPane.showMessageDialog(this, "Please login as a seller first.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
         }
         try {
            List<Item> allItems = itemManager.getAllItems();
            List<Item> myItems = new ArrayList<>();
            for (Item item : allItems) {
                if (item.getSellerId() == currentSeller.getUserId()) {
                    myItems.add(item);
                }
            }
            itemTableModel.setItems(myItems);
            clearInputFields(); // Clear fields after loading
            populateFieldsFromSelectedRow(); // Update button states etc. based on selection
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading your items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addItem() {
        if (currentSeller == null) {
             JOptionPane.showMessageDialog(this, "Please login as a seller first.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
         }

        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String priceStr = priceField.getText().trim();

        if (title.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title, Description, and Price are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            if (price < 0) {
                 JOptionPane.showMessageDialog(this, "Price cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            // Create item with current seller's ID, itemId will be auto-generated by DB
            Item newItem = new Item(0, title, description, price, false, currentSeller.getUserId());
            itemManager.addItem(newItem); // Use the addItem(Item) method
            JOptionPane.showMessageDialog(this, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            viewMyItems(); // Refresh the view to show the new item
            clearInputFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding item: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateItem() {
         if (currentSeller == null) {
             JOptionPane.showMessageDialog(this, "Please login as a seller first.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
         }
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int modelRow = itemsTable.convertRowIndexToModel(selectedRow);
            Item selectedItem = itemTableModel.getItemAt(modelRow);

            if (selectedItem == null || selectedItem.getSellerId() != currentSeller.getUserId()) {
                 JOptionPane.showMessageDialog(this, "You can only update your own items.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            int itemId = selectedItem.getItemId();
            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();
            String priceStr = priceField.getText().trim();


            if (title.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title, Description, and Price cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double price = Double.parseDouble(priceStr);
            if (price < 0) {
                 JOptionPane.showMessageDialog(this, "Price cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            // Create updated item object (assuming isForRent and sellerId remain same)
            Item updatedItem = new Item(itemId, title, description, price, selectedItem.isForRent(), currentSeller.getUserId());

            itemManager.updateItem(updatedItem);
            JOptionPane.showMessageDialog(this, "Item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            viewMyItems(); // Refresh view
            clearInputFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating item: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteItem() {
         if (currentSeller == null) {
             JOptionPane.showMessageDialog(this, "Please login as a seller first.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
         }
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int modelRow = itemsTable.convertRowIndexToModel(selectedRow);
        Item selectedItem = itemTableModel.getItemAt(modelRow);

        if (selectedItem == null || selectedItem.getSellerId() != currentSeller.getUserId()) {
            JOptionPane.showMessageDialog(this, "You can only delete your own items.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int itemId = selectedItem.getItemId();

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete item '" + selectedItem.getTitle() + "' (ID: " + itemId + ")?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                itemManager.deleteItem(itemId);
                JOptionPane.showMessageDialog(this, "Item deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                viewMyItems(); // Refresh view
                clearInputFields();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting item: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

     private void clearInputFields() {
        itemIdField.setText("");
        titleField.setText("");
        descriptionField.setText("");
        priceField.setText("");
        itemsTable.clearSelection();

        // Reset editability and button states based on login status
        boolean loggedIn = (currentSeller != null);
        titleField.setEditable(loggedIn);
        descriptionField.setEditable(loggedIn);
        priceField.setEditable(loggedIn);
        addButton.setEnabled(loggedIn);
        updateButton.setEnabled(false); // Disabled until a row is selected
        deleteButton.setEnabled(false); // Disabled until a row is selected
    }


    private void logout() {
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            setCurrentSeller(null); // Clear seller info
            mainFrame.showPanel(QuickMartGUI.MAIN_PANEL);
        }
    }

    void loadAllItems() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}