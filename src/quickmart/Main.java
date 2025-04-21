//GROUP MEMBERS:
//DIGVIJAYSINH VANSIYA
//DHANRAJ SHITOLE
//ARYANSINGH RAJPUT
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
import quickmart.models.Item;

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
            System.out.println("3. Admin");
            System.out.println("4. Exit");
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
                    adminMenu(scanner, itemManager, userManager);
                    break;
                case 4:
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

    // Admin Menu
    private static void adminMenu(Scanner scanner, ItemManager itemManager, UserManager userManager) {
        while (true) {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. Delete Item");
            System.out.println("2. Update Item");
            System.out.println("3. Delete User");
            System.out.println("4. Exit Admin Menu");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    deleteItemByAdmin(scanner, itemManager);
                    break;
                case 2:
                    updateItemByAdmin(scanner, itemManager);
                    break;
                case 3:
                    deleteUserByAdmin(scanner, userManager);
                    break;
                case 4:
                    System.out.println("Exiting Admin Menu...");
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private static void deleteItemByAdmin(Scanner scanner, ItemManager itemManager) {
        System.out.print("Enter Item ID to delete: ");
        int itemIdToDelete = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        itemManager.deleteItem(itemIdToDelete);
    }

    private static void updateItemByAdmin(Scanner scanner, ItemManager itemManager) {
        System.out.print("Enter Item ID to update: ");
        int itemIdToUpdate = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Item item = itemManager.getItemById(itemIdToUpdate);
        if (item == null) {
            System.out.println("❌ Item not found.");
            return;
        }

        System.out.print("Enter new item title (leave blank to keep current: " + item.getTitle() + "): ");
        String newTitle = scanner.nextLine();
        if (!newTitle.isEmpty()) {
            item = new Item(item.getItemId(), newTitle, item.getDescription(), item.getPrice(), item.isForRent(), item.getSellerId());
        }

        System.out.print("Enter new item description (leave blank to keep current: " + item.getDescription() + "): ");
        String newDescription = scanner.nextLine();
        if (!newDescription.isEmpty()) {
            item = new Item(item.getItemId(), item.getTitle(), newDescription, item.getPrice(), item.isForRent(), item.getSellerId());
        }

        System.out.print("Enter new item price (leave blank to keep current: " + item.getPrice() + "): ");
        String newPriceStr = scanner.nextLine();
        if (!newPriceStr.isEmpty()) {
            try {
                double newPrice = Double.parseDouble(newPriceStr);
                item = new Item(item.getItemId(), item.getTitle(), item.getDescription(), newPrice, item.isForRent(), item.getSellerId());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid price format. Price not updated.");
            }
        }

        System.out.print("Is the item for rent? (true/false, leave blank to keep current: " + item.isForRent() + "): ");
        String newIsForRentStr = scanner.nextLine();
        if (!newIsForRentStr.isEmpty()) {
            try {
                boolean newIsForRent = Boolean.parseBoolean(newIsForRentStr);
                item = new Item(item.getItemId(), item.getTitle(), item.getDescription(), item.getPrice(), newIsForRent, item.getSellerId());
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Invalid boolean format. Rent status not updated.");
            }
        }

        itemManager.updateItem(item);
    }

    private static void deleteUserByAdmin(Scanner scanner, UserManager userManager) {
        System.out.print("Enter User ID to delete: ");
        int userIdToDelete = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        userManager.deleteUser(userIdToDelete);
    }
}