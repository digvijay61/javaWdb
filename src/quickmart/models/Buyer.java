package quickmart.models;

import quickmart.management.ItemManager;
import java.util.List;
import java.util.Scanner;

public abstract class Buyer extends User {
    public Buyer(int userId, String name, String email, String password) {
        super(userId, name, email, password);
    }

    public static void buyerMenu(Buyer buyer, Scanner scanner) {
        while (true) {
            System.out.println("\nBuyer Menu:");
            System.out.println("1. View Available Items");
            System.out.println("2. Buy/Rent an Item");
            System.out.println("3. Logout");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    viewAvailableItems();
                    break;
                case 2:
                    buyOrRentItem(scanner);
                    break;
                case 3:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private static void viewAvailableItems() {
        List<Item> items = ItemManager.getAllItems();
        if (items.isEmpty()) {
            System.out.println("❌ No items available.");
            return;
        }
        System.out.println("\n📦 Available Items:");
        for (Item item : items) {
            item.displayItem();
        }
    }

    private static void buyOrRentItem(Scanner scanner) {
        System.out.print("Enter Item ID to Buy/Rent: ");
        int itemId = scanner.nextInt();

        Item item = ItemManager.getItemById(itemId);
        if (item != null) {
            System.out.println("✅ You have successfully selected: " + item.getTitle());
            System.out.println("Price: $" + item.getPrice() + " | Rentable: " + (item.isForRent() ? "Yes" : "No"));
        } else {
            System.out.println("❌ Item not found.");
        }
    }
}
