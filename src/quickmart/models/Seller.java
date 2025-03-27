package quickmart.models;

import quickmart.management.ItemManager;
import java.util.List;
import java.util.Scanner;

public abstract class Seller extends User {
    public Seller(int userId, String name, String email, String password) {
        super(userId, name, email, password);
    }

    public static void sellerMenu(Seller seller, Scanner scanner) {
        while (true) {
            System.out.println("\nSeller Menu:");
            System.out.println("1. View My Items");
            System.out.println("2. Add an Item");
            System.out.println("3. Logout");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewItems();
                    break;
                case 2:
                    addNewItem(scanner);
                    break;
                case 3:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private static void viewItems() {
        List<Item> items = ItemManager.getAllItems(); // ✅ Fixed return type issue
        if (items.isEmpty()) {
            System.out.println("❌ No items available.");
            return;
        }
        System.out.println("\n📦 Your Items:");
        for (Item item : items) {
            item.displayItem();
        }
    }

    private static void addNewItem(Scanner scanner) {
        System.out.print("Enter item title: ");
        String title = scanner.nextLine();

        System.out.print("Enter item description: ");
        String description = scanner.nextLine();

        System.out.print("Enter item price: ");
        double price = scanner.nextDouble();

        // ✅ Fixed incorrect method call: now passing correct parameters
        ItemManager.addItem(title, description, price);
        System.out.println("✅ Item added successfully!");
    }
}
