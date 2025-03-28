package quickmart.models;

import quickmart.management.ItemManager;

import java.util.List;
import java.util.Scanner;

public class Seller extends User {

    public Seller(String name, String email, String password) {
        super(name, email, password);
    }

    private static final ItemManager itemManager = new ItemManager(); // Use ItemManager for item operations

    public static void sellerMenu(Seller seller, Scanner scanner) {
        while (true) {
            System.out.println("\nSeller Menu:");
            System.out.println("1. View My Items");
            System.out.println("2. Add an Item");
            System.out.println("3. Delete an Item");
            System.out.println("4. Update an Item");
            System.out.println("5. Logout");
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
                    deleteItem(scanner); // Add this line
                    break;
                case 4:
                    updateItem(scanner); // Add this line
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private static void viewItems() {
        List<Item> items = itemManager.getAllItems();
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
        itemManager.addItem(title, description, price);
        System.out.println("✅ Item added successfully!");
    }

    private static void deleteItem(Scanner scanner) {
        System.out.print("Enter Item ID to delete: ");
        int itemIdToDelete = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        itemManager.deleteItem(itemIdToDelete);
    }

    private static void updateItem(Scanner scanner) {
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

    @Override
    public void displayInfo() {
        System.out.println("👤 Seller: " + getName() + " | Email: " + getEmail());
    }
}