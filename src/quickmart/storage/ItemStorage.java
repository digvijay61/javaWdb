package quickmart.storage;

import quickmart.models.Item;
import java.util.ArrayList;
import java.util.List;

public class ItemStorage {
    private static final List<Item> items = new ArrayList<>();
    private static int nextItemId = 1;

    // Load Default Items
    public static void loadDefaultItems() {
        addItem(new Item(nextItemId++, "Football", "High-quality synthetic leather football. Ideal for matches and practice.", 20.0, false, -1));
        addItem(new Item(nextItemId++, "Badminton Racket", "Lightweight aluminum racket with comfortable grip. Perfect for beginners.", 15.0, false, -1));
        addItem(new Item(nextItemId++, "Cricket Bat", "Grade A willow cricket bat for power and precision shots.", 50.0, false, -1));
        addItem(new Item(nextItemId++, "Yoga Mat", "Non-slip, durable yoga mat. Great for workouts and meditation.", 10.0, false, -1));
        addItem(new Item(nextItemId++, "Tennis Ball Pack", "Durable, high-bounce tennis balls for all court types.", 8.0, false, -1));

        addItem(new Item(nextItemId++, "Notebook", "Spiral-bound notebook with smooth, high-quality paper.", 5.0, false, -1));
        addItem(new Item(nextItemId++, "Ballpoint Pen Set", "Assorted colors, smooth ink flow, and comfortable grip.", 3.0, false, -1));
        addItem(new Item(nextItemId++, "Sketchbook", "Ideal for drawing and sketching. 100 GSM paper.", 7.0, false, -1));
        addItem(new Item(nextItemId++, "Highlighter Pack", "Vibrant and long-lasting colors, perfect for study notes.", 4.0, false, -1));
        addItem(new Item(nextItemId++, "Geometry Box", "Complete set with compass, protractor, and ruler.", 6.0, false, -1));
    }

    // Add Item to List
    public static void addItem(Item item) {
        items.add(item);
    }

    public static void addItem(String title, String description, double price) {
        Item newItem = new Item(nextItemId++, title, description, price, false, -1);
        items.add(newItem);
    }


    public static Item getItemById(int id) {
        for (Item item : items) {
            if (item.getItemId() == id) {
                return item;
            }
        }
        return null; // Item not found
    }

    // Get All Items
    public static List<Item> getAllItems() {
        return new ArrayList<>(items); // Return a new list to avoid modification
    }

    public static int getNextItemId() {
        return nextItemId;
    }
}