package quickmart.payment;

import java.util.Random;
import java.util.Scanner;

public class PaymentMethods {

    public interface PaymentStrategy {
        boolean processPayment(double amount);
    }

    public static class CashPayment implements PaymentStrategy {
        @Override
        public boolean processPayment(double amount) {
            System.out.println("Processing cash payment of $" + amount);
            // In real-world, you would handle cash handling, verification, etc.
            return true;  // Assume cash payment always succeeds.
        }
    }

    public static class OnlinePayment implements PaymentStrategy {
        private String cardNumber;
        private String cvv;
        private String expiryDate;

        public OnlinePayment(String cardNumber, String cvv, String expiryDate) {
            this.cardNumber = cardNumber;
            this.cvv = cvv;
            this.expiryDate = expiryDate;
        }

        @Override
        public boolean processPayment(double amount) {
            System.out.println("Processing online payment of $" + amount + " using card ending in " + cardNumber.substring(cardNumber.length() - 4));

            // Simulate online payment processing
            Random random = new Random();
            boolean paymentSuccessful = random.nextBoolean();

            if (paymentSuccessful) {
                System.out.println("✅ Payment successful!");
                return true;
            } else {
                System.out.println("❌ Payment failed! Please try again.");
                return false;
            }
        }
    }
}