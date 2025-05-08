// In src/quickmart/management/UserManager.java

package quickmart.management;

import quickmart.models.User;
import quickmart.models.Buyer;
import quickmart.models.Seller;
import quickmart.utils.DBUtil;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList; // Import ArrayList
import java.util.List;    // Import List
import javax.swing.JOptionPane;

public class UserManager {

    public void registerUser(User user) {
        String sql = "INSERT INTO Users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Get generated keys
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, (user instanceof Buyer) ? "buyer" : "seller");
            pstmt.executeUpdate();

            // Fetch the generated userId
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    user.setUserId(userId); // Set the userId on the User object
                    System.out.println("DEBUG: User registered with ID: " + userId);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

        } catch (SQLException | IOException e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            // Consider throwing a custom exception or returning a boolean status
        }
    }

   public User getUserByEmail(String email) {
    String sql = "SELECT * FROM Users WHERE email = ?";
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, email);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToUser(rs); // Use helper method
            }
            return null; // User not found
        }
    } catch (SQLException | IOException e) {
        System.err.println("Error getting user by email: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}

    // --- NEW METHOD: Get all users ---
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY userId"; // Order for consistency
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = mapResultSetToUser(rs); // Use helper method
                if (user != null) {
                    userList.add(user);
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
            e.printStackTrace();
            // Return empty list on error, or throw exception
        }
        System.out.println("DEBUG: Fetched " + userList.size() + " users from DB.");
        return userList;
    }

     // --- NEW HELPER METHOD: Map ResultSet row to User object ---
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String password = rs.getString("password"); // Fetch password for validation logic
        int userId = rs.getInt("userId");

        User user;
        if ("buyer".equalsIgnoreCase(role)) {
            user = new Buyer(name, email, password) {
                @Override public void displayInfo() { /* Not needed for GUI */ }
            };
        } else if ("seller".equalsIgnoreCase(role)) {
            user = new Seller(name, email, password) {
                @Override public void displayInfo() { /* Not needed for GUI */ }
            };
        } else {
            System.err.println("Warning: Unknown role '" + role + "' found for user ID " + userId);
            // Decide how to handle unknown roles - skip user or create a default?
            return null; // Skip unknown roles for now
        }
        user.setUserId(userId); // IMPORTANT: Set the ID fetched from DB
        return user;
    }


    // Delete user method (existing)
    public void deleteUser(int userId) {
        // Optional: Add checks before deleting (e.g., check if user has items/transactions)
        String checkItemsSql = "SELECT COUNT(*) FROM Items WHERE sellerId = ?";
        String checkTransactionsSql = "SELECT COUNT(*) FROM Transactions WHERE buyerId = ?";
        boolean hasDependencies = false;

        try (Connection conn = DBUtil.getConnection()) {
             // Check for associated items (if user is potentially a seller)
             try (PreparedStatement pstmtCheckItems = conn.prepareStatement(checkItemsSql)) {
                 pstmtCheckItems.setInt(1, userId);
                 ResultSet rsItems = pstmtCheckItems.executeQuery();
                 if (rsItems.next() && rsItems.getInt(1) > 0) {
                     hasDependencies = true;
                     System.out.println("User ID " + userId + " has associated items.");
                     JOptionPane.showMessageDialog(null, "Cannot delete User ID " + userId + ".\nUser has associated items listed.", "Deletion Failed", JOptionPane.ERROR_MESSAGE);
                     return; // Prevent deletion
                 }
             }
             // Check for associated transactions (if user is potentially a buyer)
             try (PreparedStatement pstmtCheckTrans = conn.prepareStatement(checkTransactionsSql)) {
                 pstmtCheckTrans.setInt(1, userId);
                 ResultSet rsTrans = pstmtCheckTrans.executeQuery();
                 if (rsTrans.next() && rsTrans.getInt(1) > 0) {
                     hasDependencies = true;
                      System.out.println("User ID " + userId + " has associated transactions.");
                     JOptionPane.showMessageDialog(null, "Cannot delete User ID " + userId + ".\nUser has associated purchase transactions.", "Deletion Failed", JOptionPane.ERROR_MESSAGE);
                     return; // Prevent deletion
                 }
             }

        } catch (SQLException | IOException e) {
             System.err.println("Error checking dependencies for user ID " + userId + ": " + e.getMessage());
             JOptionPane.showMessageDialog(null, "Error checking dependencies before deleting user.\nSee console for details.", "Deletion Error", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
             return; // Prevent deletion if check fails
        }


        // Proceed with deletion only if no dependencies found
        String deleteSql = "DELETE FROM Users WHERE userId = ?";
        try {
            int rowsAffected = DBUtil.executeUpdate(deleteSql, userId);
            if (rowsAffected > 0) {
                System.out.println("User deleted successfully from DB (ID: " + userId + ")");
                 JOptionPane.showMessageDialog(null, "User ID " + userId + " deleted successfully.", "User Deleted", JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.out.println("User not found in DB (ID: " + userId + ")");
                JOptionPane.showMessageDialog(null, "User ID " + userId + " not found.", "Deletion Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error deleting user ID " + userId + ": " + e.getMessage());
             JOptionPane.showMessageDialog(null, "Error deleting user ID " + userId + ".\nSee console for details.", "Deletion Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            // Handle potential foreign key constraint violations if checks above missed something
            if (e instanceof SQLException && ((SQLException)e).getSQLState().startsWith("23")) { // SQLState for integrity constraint violation
                 JOptionPane.showMessageDialog(null, "Cannot delete User ID " + userId + " due to existing references (e.g., items or transactions).", "Deletion Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // Update user method (existing)
    public void updateUser(User user) {
        String sql = "UPDATE Users SET name = ?, email = ?, password = ?, role = ? WHERE userId = ?";
        try {
            String role = (user instanceof Buyer) ? "buyer" : "seller";
            int rowsAffected = DBUtil.executeUpdate(sql, user.getName(), user.getEmail(), user.getPassword(), role, user.getUserId());
            if (rowsAffected > 0) {
                System.out.println("User updated successfully!");
            } else {
                System.out.println("User not found for update.");
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
    }
}