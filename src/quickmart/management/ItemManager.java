package quickmart.management;

import quickmart.models.Item;
import quickmart.storage.ItemStorage;
import java.util.List;

public class ItemManager {

    // ✅ Fix: Get all items from ItemStorage
    public static List<Item> getAllItems() {
        return ItemStorage.getAllItems();
    }

    // ✅ Ensure `getItemById` gets item from ItemStorage
    public static Item getItemById(int id) {
        return ItemStorage.getItemById(id);
    }

    // ✅ Fix: Ensure `addItem()` uses the correct parameters and adds to ItemStorage
    public static void addItem(String title, String description, double price) {
        ItemStorage.addItem(title, description, price);
    }
}