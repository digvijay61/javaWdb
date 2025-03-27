package quickmart.models;

import quickmart.payment.PaymentMethods;
import quickmart.payment.PaymentMethods.PaymentStrategy;

import java.util.Date;
import java.util.List;

public class Transaction {
    private int transactionId;
    private Buyer buyer;
    private List<Item> items;
    private Date transactionDate;
    private PaymentMethods.PaymentStrategy paymentMethod;
    private double totalAmount;
    private boolean isSuccessful;

    public Transaction(int transactionId, Buyer buyer, List<Item> items, PaymentMethods.PaymentStrategy paymentMethod) {
        this.transactionId = transactionId;
        this.buyer = buyer;
        this.items = items;
        this.transactionDate = new Date(); // Current timestamp
        this.paymentMethod = paymentMethod;
        this.totalAmount = calculateTotalAmount();
        this.isSuccessful = paymentMethod.processPayment(this.totalAmount);
    }

    private double calculateTotalAmount() {
        double total = 0;
        for (Item item : items) {
            total += item.getPrice();
        }
        return total;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public List<Item> getItems() {
        return items;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    private PaymentMethods.PaymentStrategy getPaymentMethod() {
        return paymentMethod;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void displayTransactionDetails() {
        System.out.println("\n🧾 Transaction Details:");
        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Buyer: " + buyer.getName());
        System.out.println("Transaction Date: " + transactionDate);
        System.out.println("Total Amount: $" + totalAmount);
        System.out.println("Payment Method: " + paymentMethod.getClass().getSimpleName());
        System.out.println("Transaction Status: " + (isSuccessful ? "Successful" : "Failed"));
        if (isSuccessful) {
            System.out.println("✅ Transaction completed successfully!");
        } else {
            System.out.println("❌ Transaction failed. Please check your payment details.");
        }
    }
}