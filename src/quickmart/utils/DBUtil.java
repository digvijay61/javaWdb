package quickmart.utils;

import java.sql.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DBUtil {

    private static Connection connection;

    // Hardcoded part of the URL (up to the database name)
    private static final String DB_BASE_URL = "jdbc:mysql://localhost:3306/";

    // Static variable to store the database name (only ask once)
    private static String databaseName = "quickmart";
    private static String databaseUsername = "root";
    private static String databasePassword = "1234";

    // Method to get the database name from the user (called only once)
    private static String getDatabaseNameFromUser() {
        if (databaseName == null) { // Ask only if databaseName is not already set
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the database name to use or create: ");
            databaseName = scanner.nextLine(); // Store the database name
        }
        return databaseName;
    }

    public static Connection getConnection() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            String user = databaseUsername; // Using hardcoded values
            String password = databasePassword; // Using hardcoded values

            // Construct the full database URL
            String url = DB_BASE_URL + databaseName;

            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // Load the MySQL driver

                // Create the database if it doesn't exist
                createDatabaseIfNotExist(databaseName, user, password);

                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Database connection established to: " + databaseName);
                createTablesIfNotExist();
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC driver not found. Make sure it's in your classpath.");
                throw new SQLException("MySQL JDBC driver not found", e);
            }
             catch (SQLException e) {
                System.err.println("Error creating database: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    // Method to create the database if it doesn't exist
    private static boolean createDatabaseIfNotExist(String databaseName, String user, String password) throws SQLException, IOException {
        String urlWithoutDatabase = DB_BASE_URL;

        try (Connection connectionWithoutDatabase = DriverManager.getConnection(urlWithoutDatabase, user, password);
             Statement statement = connectionWithoutDatabase.createStatement()) {

            String sqlCreateDatabase = "CREATE DATABASE IF NOT EXISTS " + databaseName;
            statement.executeUpdate(sqlCreateDatabase);
            return true;

        } catch (SQLException e) {
            System.err.println("Error creating database: " + e.getMessage());
            return false;
        }
    }

    private static void createTablesIfNotExist() throws SQLException, IOException {
        try (Statement statement = getConnection().createStatement()) {
            statement.executeUpdate(SQL_CREATE_USERS_TABLE);
            // Create a "System" user with userId -1 (for default items)
            String sqlCreateSystemUser = "INSERT IGNORE INTO Users (userId, name, email, password, role) VALUES (-1, 'System', 'system@quickmart.com', 'password', 'seller')";
            statement.executeUpdate(sqlCreateSystemUser);

            statement.executeUpdate(SQL_CREATE_ITEMS_TABLE);
            statement.executeUpdate(SQL_CREATE_TRANSACTIONS_TABLE);
            statement.executeUpdate(SQL_CREATE_TRANSACTION_ITEMS_TABLE);
            //System.out.println("Tables created or already exist."); remove
        }
    }

    // Helper function to execute a query and process the ResultSet
    public static <T> List<T> executeQuery(String sql, ResultSetHandler<T> handler, Object... params) throws SQLException, IOException {
        List<T> results = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(handler.handle(rs));
                }
            }

        }
        return results;
    }

    // Helper function to execute an update (INSERT, UPDATE, DELETE)
    public static int executeUpdate(String sql, Object... params) throws SQLException, IOException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            return pstmt.executeUpdate(); // Returns the number of rows affected
        }
    }

    //Helper function to execute an insert and return generated keys
    public static ResultSet executeInsert(String sql, Object... params) throws SQLException, IOException {
        PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
        pstmt.executeUpdate();
        return pstmt.getGeneratedKeys(); // Returns the generated keys
    }

    // New Helper function to delete a record
    public static int executeDelete(String sql, Object... params) throws SQLException, IOException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            return pstmt.executeUpdate(); // Returns the number of rows affected
        }
    }

    // Interface for handling the ResultSet and mapping to an object
    public interface ResultSetHandler<T> {
        T handle(ResultSet rs) throws SQLException;
    }


    private static final String SQL_CREATE_USERS_TABLE =
            "CREATE TABLE IF NOT EXISTS Users (" +
                    "userId INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "email VARCHAR(255) UNIQUE NOT NULL," +
                    "password VARCHAR(255) NOT NULL," +
                    "role ENUM('buyer', 'seller') NOT NULL" +
                    ")";

    private static final String SQL_CREATE_ITEMS_TABLE =
            "CREATE TABLE IF NOT EXISTS Items (" +
                    "itemId INT AUTO_INCREMENT PRIMARY KEY," +
                    "title VARCHAR(255) NOT NULL," +
                    "description TEXT," +
                    "price DECIMAL(10, 2) NOT NULL," +
                    "isForRent BOOLEAN NOT NULL DEFAULT FALSE," +
                    "sellerId INT," +
                    "FOREIGN KEY (sellerId) REFERENCES Users(userId)" +
                    ")";

    private static final String SQL_CREATE_TRANSACTIONS_TABLE =
            "CREATE TABLE IF NOT EXISTS Transactions (" +
                    "transactionId INT AUTO_INCREMENT PRIMARY KEY," +
                    "buyerId INT," +
                    "transactionDate DATETIME NOT NULL," +
                    "totalAmount DECIMAL(10, 2) NOT NULL," +
                    "paymentMethodType VARCHAR(50) NOT NULL," +
                    "paymentDetails TEXT," +
                    "isSuccessful BOOLEAN NOT NULL," +
                    "FOREIGN KEY (buyerId) REFERENCES Users(userId)" +
                    ")";

    private static final String SQL_CREATE_TRANSACTION_ITEMS_TABLE =
            "CREATE TABLE IF NOT EXISTS TransactionItems (" +
                    "transactionId INT," +
                    "itemId INT," +
                    "quantity INT NOT NULL DEFAULT 1," +
                    "PRIMARY KEY (transactionId, itemId)," +
                    "FOREIGN KEY (transactionId) REFERENCES Transactions(transactionId)," +
                    "FOREIGN KEY (itemId) REFERENCES Items(itemId)" +
                    ")";



    //Method to load default items
    public static void loadDefaultItems() throws SQLException, IOException {
        // Check if items are already loaded
        String checkSql = "SELECT COUNT(*) FROM Items";
        int itemCount = 0;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next()) {
                itemCount = rs.getInt(1);
            }
        }

        if (itemCount == 0) {
            // Load default items
            String insertSql = "INSERT INTO Items (title, description, price, isForRent, sellerId) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

                // Football
                pstmt.setString(1, "Football");
                pstmt.setString(2, "High-quality synthetic leather football. Ideal for matches and practice.");
                pstmt.setDouble(3, 20.0);
                pstmt.setBoolean(4, false);
                pstmt.setInt(5, -1);  //This id exists now
                pstmt.executeUpdate();

                // Badminton Racket
                pstmt.setString(1, "Badminton Racket");
                pstmt.setString(2, "Lightweight aluminum racket with comfortable grip. Perfect for beginners.");
                pstmt.setDouble(3, 15.0);
                pstmt.setBoolean(4, false);
                pstmt.setInt(5, -1);  //This id exists now
                pstmt.executeUpdate();

                // Cricket Bat
                pstmt.setString(1, "Cricket Bat");
                pstmt.setString(2, "Grade A willow cricket bat for power and precision shots.");
                pstmt.setDouble(3, 50.0);
                pstmt.setBoolean(4, false);
                pstmt.setInt(5, -1);  //This id exists now
                pstmt.executeUpdate();

                // Yoga Mat
                pstmt.setString(1, "Yoga Mat");
                pstmt.setString(2, "Non-slip, durable yoga mat. Great for workouts and meditation.");
                pstmt.setDouble(3, 10.0);
                pstmt.setBoolean(4, false);
                pstmt.setInt(5, -1);  //This id exists now
                pstmt.executeUpdate();

                // Tennis Ball Pack
                pstmt.setString(1, "Tennis Ball Pack");
                pstmt.setString(2, "Durable, high-bounce tennis balls for all court types.");
                pstmt.setDouble(3, 8.0);
                pstmt.setBoolean(4, false);
                pstmt.setInt(5, -1);  //This id exists now
                pstmt.executeUpdate();

                // Notebook
                pstmt.setString(1, "Notebook");
                pstmt.setString(2, "Spiral-bound notebook with smooth, high-quality paper.");
                pstmt.setDouble(3, 5.0);
                pstmt.setBoolean(4, false);
                pstmt.setInt(5, -1);  //This id exists now
                pstmt.executeUpdate();

                // Ballpoint Pen Set
                pstmt.setString(1, "Ballpoint Pen Set");
                pstmt.setString(2, "Assorted colors, smooth ink flow, and comfortable grip.");
                pstmt.setDouble(3, 3.0);
                pstmt.setBoolean(4, false);
                pstmt.setInt(5, -1);  //This id exists now
                pstmt.executeUpdate();

                // Sketchbook
                pstmt.setString(1, "Sketchbook");
                pstmt.setString(2, "Ideal for drawing and sketching. 100 GSM paper.");
                pstmt.setDouble(3, 7.0);
                pstmt.setBoolean(4, false);
                pstmt.setInt(5, -1);  //This id exists now
                pstmt.executeUpdate();

                // Highlighter Pack
                pstmt.setString(1, "Highlighter Pack");
                pstmt.setString(2, "Vibrant and long-lasting colors, perfect for study notes.");
                pstmt.setDouble(3, 4.0);
                pstmt.setBoolean(4, false);
                pstmt.setInt(5, -1);  //This id exists now
                pstmt.executeUpdate();

                // Geometry Box
                pstmt.setString(1, "Geometry Box");
                pstmt.setString(2, "Complete set with compass, protractor, and ruler.");
                pstmt.setDouble(3, 6.0);
                pstmt.setBoolean(4, false);
                pstmt.setInt(5, -1);  //This id exists now
                pstmt.executeUpdate();

                System.out.println("Default items loaded successfully.");
            }
        } else {
            System.out.println("Default items already loaded.");
        }
    }
}