package quickmart;

import quickmart.models.Seller;
import quickmart.models.Buyer;
import quickmart.models.User;
import quickmart.management.ItemManager;
import quickmart.management.UserManager;
import quickmart.utils.DBUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    private static final UserManager userManager = new UserManager();
    private static final ItemManager itemManager = new ItemManager();

    public static void main(String[] args) {

        try {
            // Load default items when the program starts
            DBUtil.loadDefaultItems();

        } catch (SQLException | IOException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            return; // Exit if the database initialization fails
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nWelcome to QuickMart!");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerUser(scanner, userManager);
                    break;
                case 2:
                    loginUser(scanner, userManager);
                    break;
                case 3:
                    System.out.println("Exiting QuickMart...");
                    try {
                        DBUtil.closeConnection(); // Close the database connection on exit
                    } catch (Exception e) {
                        System.err.println("Error closing database connection: " + e.getMessage());
                    }
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private static void registerUser(Scanner scanner, UserManager userManager) {
        System.out.println("\n🔹 Register as a New User");

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        String email;
        while (true) {
            System.out.print("Enter Email: ");
            email = scanner.nextLine();
            if (email.contains("@") && email.endsWith(".com")) {
                break;
            } else {
                System.out.println("❌ Invalid email format! Email must contain '@' and end with '.com'. Try again.");
            }
        }

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        System.out.println("Choose Role:");
        System.out.println("1. Seller");
        System.out.println("2. Buyer");
        System.out.print("Enter choice: ");
        int role = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        User user;
        if (role == 1) {
            user = new Seller(name, email, password) {
                @Override
                public void displayInfo() {
                    System.out.println("👤 Seller: " + getName() + " | Email: " + getEmail());
                }
            };
        } else {
            user = new Buyer(name, email, password) {
                @Override
                public void displayInfo() {
                    System.out.println("🛒 Buyer: " + getName() + " | Email: " + getEmail());
                }
            };
        }

        // Register the user
        userManager.registerUser(user);
        System.out.println("✅ Registration successful!");
    }

    private static void loginUser(Scanner scanner, UserManager userManager) {
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User user = userManager.getUserByEmail(email);

        if (user == null || !user.validatePassword(password)) {
            System.out.println("❌ Invalid email or password! Please try again.");
            return;
        }

        if (user instanceof Seller) {
            Seller.sellerMenu((Seller) user, scanner);
        } else if (user instanceof Buyer) {
            Buyer.buyerMenu((Buyer) user, scanner);
        } else {
            System.out.println("❌ Unknown user type!");
        }
    }
}