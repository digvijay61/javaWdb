package quickmart.management;

import quickmart.models.User;
import quickmart.models.Buyer;
import quickmart.models.Seller;
import quickmart.utils.DBUtil;

import java.io.IOException;
import java.sql.*;

public class UserManager {


    public void registerUser(User user) {
        String sql = "INSERT INTO Users (userId, name, email, password, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, (user instanceof Buyer) ? "buyer" : "seller");
            pstmt.executeUpdate();

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
                    if ("buyer".equals(role)) {
                        return new Buyer(rs.getInt("userId"), rs.getString("name"), rs.getString("email"), rs.getString("password")) {
                            @Override
                            public void displayInfo() {
                                System.out.println("Buyer: " + getName());
                            }
                        };
                    } else {
                        return new Seller(rs.getInt("userId"), rs.getString("name"), rs.getString("email"), rs.getString("password")) {
                            @Override
                            public void displayInfo() {
                                System.out.println("Seller: " + getName());
                            }
                        };
                    }
                }
                return null;
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public int getNextUserId() {
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
    }

}