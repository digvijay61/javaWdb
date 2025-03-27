package quickmart.models;

public class Item {
    private int itemId;
    private String title;
    private String description;
    private double price;
    private boolean isForRent;
    private int sellerId;

    public Item(int itemId, String title, String description, double price, boolean isForRent, int sellerId) {
        this.itemId = itemId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.isForRent = isForRent;
        this.sellerId = sellerId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public boolean isForRent() {
        return isForRent;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void displayItem() {
        System.out.println("📦 Item ID: " + itemId + " | Title: " + title);
        System.out.println("   💰 Price: $" + price + " | Rentable: " + (isForRent ? "Yes" : "No"));
        System.out.println("   📝 Description: " + description);
    }
}
