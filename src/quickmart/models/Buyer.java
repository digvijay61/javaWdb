package quickmart.models;

import quickmart.management.ItemManager;
import quickmart.management.TransactionManager;
import quickmart.payment.PaymentMethods;
import java.util.List;
import java.util.Scanner;

public class Buyer extends User {

    private static final ItemManager itemManager = new ItemManager(); // Use ItemManager for item operations

    private Cart cart;

    public Buyer(int userId, String name, String email, String password) {
        super(userId, name, email, password);
        this.cart = new Cart(this); // Initialize cart for the buyer
    }

    public Cart getCart() {
        return cart;
    }

    public static void buyerMenu(Buyer buyer, Scanner scanner) {
        while (true) {
            System.out.println("\nBuyer Menu:");
            System.out.println("1. View Available Items");
            System.out.println("2. Add Item to Cart");
            System.out.println("3. View Cart");
            System.out.println("4. Checkout");
            System.out.println("5. Logout");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewAvailableItems();
                    break;
                case 2:
                    addItemToCart(buyer, scanner);
                    break;
                case 3:
                    viewCart(buyer);
                    break;
                case 4:
                    checkout(buyer, scanner);
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private static void viewAvailableItems() {
        List<Item> items = itemManager.getAllItems();
        if (items.isEmpty()) {
            System.out.println("❌ No items available.");
            return;
        }
        System.out.println("\n📦 Available Items:");
        for (Item item : items) {
            item.displayItem();
        }
    }

    private static void addItemToCart(Buyer buyer, Scanner scanner) {
        System.out.print("Enter Item ID to add to cart: ");
        int itemId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Item item = itemManager.getItemById(itemId);
        if (item != null) {
            buyer.getCart().addItem(item);
            System.out.println("✅ Item added to cart: " + item.getTitle());
        } else {
            System.out.println("❌ Item not found.");
        }
    }

    private static void viewCart(Buyer buyer) {
        Cart cart = buyer.getCart();
        if (cart.isEmpty()) {
            System.out.println("🛒 Your cart is empty.");
            return;
        }

        System.out.println("\n🛒 Your Cart:");
        List<Item> items = cart.getItems();
        for (Item item : items) {
            item.displayItem();
        }
        System.out.println("Total: " + cart.getTotalPrice());
    }

    private static void checkout(Buyer buyer, Scanner scanner) {
        Cart cart = buyer.getCart();
        if (cart.isEmpty()) {
            System.out.println("🛒 Your cart is empty. Add items before checkout.");
            return;
        }

        System.out.println("\n💰 Checkout:");
        System.out.println("Total amount: " + cart.getTotalPrice());
        System.out.println("Choose payment method:");
        System.out.println("1. Cash");
        System.out.println("2. Online Payment");
        System.out.print("Enter choice: ");
        int paymentChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        PaymentMethods.PaymentStrategy paymentStrategy;
        if (paymentChoice == 1) {
            paymentStrategy = new PaymentMethods.CashPayment();
        } else if (paymentChoice == 2) {
            System.out.print("Enter card number: ");
            String cardNumber = scanner.nextLine();
            System.out.print("Enter CVV: ");
            String cvv = scanner.nextLine();
            System.out.print("Enter expiry date (MM/YY): ");
            String expiryDate = scanner.nextLine();
            paymentStrategy = new PaymentMethods.OnlinePayment(cardNumber, cvv, expiryDate);
        } else {
            System.out.println("❌ Invalid payment choice. Checkout cancelled.");
            return;
        }
        //Create a new transaction
        Transaction transaction = new Transaction(TransactionManager.getNextTransactionId(), buyer, cart.getItems(), paymentStrategy);

        //Add it to Transaction manager
        TransactionManager.addTransaction(transaction);

        //Display transaction Details
        transaction.displayTransactionDetails();

        if (transaction.isSuccessful()) {
            cart.clearCart(); // Clear the cart after successful checkout
            System.out.println("✅ Thank you for your purchase!");
        } else {
            System.out.println("❌ Payment failed. Please try again.");
        }
    }

    @Override
    public void displayInfo() {
        System.out.println("🛒 Buyer: " + getName() + " | Email: " + getEmail());
    }
}