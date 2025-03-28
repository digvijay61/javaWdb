package quickmart.management;

import quickmart.models.Transaction;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    private static List<Transaction> transactions = new ArrayList<>();
    private static int nextTransactionId = 1;

    public static void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        System.out.println("DEBUG: Transaction added. Total transactions: " + transactions.size()); // Add this
    }

    public static int getNextTransactionId() {
        return nextTransactionId++;
    }
}