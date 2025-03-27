package quickmart.models;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private Buyer buyer;
    private List<Item> items;

    public Cart(Buyer buyer) {
        this.buyer = buyer;
        this.items = new ArrayList<>();
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public void removeItem(Item item) {
        this.items.remove(item);
    }

    public double getTotalPrice() {
        double total = 0;
        for (Item item : items) {
            total += item.getPrice();
        }
        return total;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clearCart() {
        this.items.clear();
    }
}