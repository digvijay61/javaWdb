package quickmart.gui;

import javax.swing.table.AbstractTableModel;
import quickmart.models.Item;
import java.util.List;
import java.util.ArrayList;

public class CartTableModel extends AbstractTableModel {

    private final String[] columnNames = {"ID", "Title", "Price"};
    private List<Item> items;

    public CartTableModel() {
        this.items = new ArrayList<>(); // Initialize with empty list
    }

    public void setItems(List<Item> items) {
        this.items = items;
        fireTableDataChanged(); // Notify the table that the data has changed
    }

     public Item getItemAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < items.size()) {
            return items.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return items.size();
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
        Item item = items.get(rowIndex);
        switch (columnIndex) {
            case 0: return item.getItemId();
            case 1: return item.getTitle();
            case 2: return item.getPrice();
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return Integer.class;
            case 2: return Double.class;
            default: return String.class;
        }
    }
}