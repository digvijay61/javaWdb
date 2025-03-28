package quickmart.management;

import quickmart.models.User;
import quickmart.models.Buyer;
import quickmart.models.Seller;
import quickmart.utils.DBUtil;

import java.io.IOException;
import java.sql.*;

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
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

   public User getUserByEmail(String email) {
    String sql = "SELECT * FROM Users WHERE email = ?";
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, email);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                String role = rs.getString("role");
                User user;
                if ("buyer".equals(role)) {
                    user = new Buyer(rs.getString("name"), rs.getString("email"), rs.getString("password")) {
                        @Override
                        public void displayInfo() {
                            System.out.println("Buyer: " + getName());
                        }
                    };
                } else {
                    user = new Seller(rs.getString("name"), rs.getString("email"), rs.getString("password")) {
                        @Override
                        public void displayInfo() {
                            System.out.println("Seller: " + getName());
                        }
                    };
                }
                user.setUserId(rs.getInt("userId"));
                return user;
            }
            return null;
        }
    } catch (SQLException | IOException e) {
        e.printStackTrace();
        return null;
    }
}


    // No longer needed because the database handles auto-increment
    /*public int getNextUserId() {
        String sql = "SELECT MAX(userId) FROM Users";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1) + 1;
            } else {
                return 1; // If the table is empty, start with ID 1
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return -1; //Indicate Error
        }
    }*/

}