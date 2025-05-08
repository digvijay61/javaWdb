package quickmart.gui;

import javax.swing.table.AbstractTableModel;
import quickmart.models.User;
import quickmart.models.Buyer; // Needed for instanceof check
import quickmart.models.Seller; // Needed for instanceof check
import java.util.List;
import java.util.ArrayList;

public class UserTableModel extends AbstractTableModel {

    private final String[] columnNames = {"User ID", "Name", "Email", "Role"};
    private List<User> users;

    public UserTableModel() {
        this.users = new ArrayList<>();
    }

    public void setUsers(List<User> users) {
        // Create a defensive copy if external modification is a concern
        this.users = new ArrayList<>(users);
        fireTableDataChanged(); // Notify JTable about the data change
    }

    public User getUserAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < users.size()) {
            return users.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return users.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        User user = users.get(rowIndex);
        switch (columnIndex) {
            case 0: return user.getUserId();
            case 1: return user.getName();
            case 2: return user.getEmail();
            case 3:
                if (user instanceof Buyer) return "Buyer";
                if (user instanceof Seller) return "Seller";
                return "Unknown"; // Should not happen with current structure
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return Integer.class; // User ID
            case 1: return String.class;  // Name
            case 2: return String.class;  // Email
            case 3: return String.class;  // Role
            default: return Object.class;
        }
    }

    // Make cells non-editable by default in the table
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    // Helper method to get the underlying list (use with caution)
    public List<User> getUsers() {
        return users;
    }
}