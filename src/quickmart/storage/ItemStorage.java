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

    // Fix: Add Item to List
    public static void addItem(Item item) {
        items.add(item);
    }

    // Get All Items
    public static List<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    public static int getNextItemId() {
        return nextItemId;
    }
}
