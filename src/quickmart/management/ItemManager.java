package quickmart.management;

import quickmart.models.Item;
import java.util.ArrayList;
import java.util.List;

public class ItemManager {
    private static List<Item> itemList = new ArrayList<>();

    // ✅ Fix: Ensure this method returns a List<Item>
    public static List<Item> getAllItems() {
        return itemList;  // 🔥 FIXED: Now returns the list instead of void
    }

    // ✅ Ensure `getItemById` returns an Item
    public static Item getItemById(int id) {
        for (Item item : itemList) {
            if (item.getItemId() == id) {
                return item;
            }
        }
        return null; // Item not found
    }

    // ✅ Fix: Ensure `addItem()` uses the correct parameters
    public static void addItem(String title, String description, double price) {
        int newId = itemList.size() + 1; // Generate unique ID
        Item newItem = new Item(newId, title, description, price, false, 0); // 🔥 FIXED: Correct constructor usage
        itemList.add(newItem);
    }
}
