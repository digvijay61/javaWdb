// In src/quickmart/management/ItemManager.java

package quickmart.management;

import quickmart.models.Item;
import quickmart.utils.DBUtil;

import java.sql.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemManager {


    public List<Item> getAllItems() {
        String sql = "SELECT * FROM Items";
        List<Item> items = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("itemId"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getBoolean("isForRent"),
                        rs.getInt("sellerId")
                );
                items.add(item);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    public Item getItemById(int id) {
        String sql = "SELECT * FROM Items WHERE itemId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Item(
                            rs.getInt("itemId"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            rs.getBoolean("isForRent"),
                            rs.getInt("sellerId")
                    );
                }
                return null;
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addItem(Item item) {
        String sql = "INSERT INTO Items (title, description, price, isForRent, sellerId) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getTitle());
            pstmt.setString(2, item.getDescription());
            pstmt.setDouble(3, item.getPrice());
            pstmt.setBoolean(4, item.isForRent());
            pstmt.setInt(5, item.getSellerId());
            pstmt.executeUpdate();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }


    public void addItem(String title, String description, double price) {
        String sql = "INSERT INTO Items (title, description, price, isForRent, sellerId) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setDouble(3, price);
            pstmt.setBoolean(4, false);
            pstmt.setInt(5, -1);
            pstmt.executeUpdate();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    // New method to delete an item by ID
    public void deleteItem(int itemId) {
        String sql = "DELETE FROM Items WHERE itemId = ?";
        try {
            int rowsAffected = DBUtil.executeUpdate(sql, itemId);
            if (rowsAffected > 0) {
                System.out.println("Item deleted successfully!");
            } else {
                System.out.println("Item not found.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.err.println("Error deleting item: " + e.getMessage());
        }
    }


    // New method to update an item
    public void updateItem(Item item) {
        String sql = "UPDATE Items SET title = ?, description = ?, price = ?, isForRent = ?, sellerId = ? WHERE itemId = ?";
        try {
            int rowsAffected = DBUtil.executeUpdate(sql, item.getTitle(), item.getDescription(), item.getPrice(), item.isForRent(), item.getSellerId(), item.getItemId());
            if (rowsAffected > 0) {
                System.out.println("Item updated successfully!");
            } else {
                System.out.println("Item not found.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.err.println("Error updating item: " + e.getMessage());
        }
    }
}