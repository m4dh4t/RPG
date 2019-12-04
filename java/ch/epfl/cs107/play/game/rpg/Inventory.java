package ch.epfl.cs107.play.game.rpg;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private final float MAXWEIGHT;
    private float weight;
    private Map<InventoryItem, Integer> items;

    protected Inventory(float maxWeight) {
        MAXWEIGHT = maxWeight;
        items = new HashMap<>();
    }

    protected boolean add(InventoryItem item, int quantity) {
        if (weight + quantity * item.getWeight() > MAXWEIGHT) {
            return false;
        }

        items.replace(item, items.getOrDefault(item, 0) + quantity);
        items.putIfAbsent(item, quantity);
        weight += quantity * item.getWeight();

        return true;
    }

    protected boolean remove(InventoryItem item, int quantity) {
        if (!items.containsKey(item) || items.getOrDefault(item,0) < quantity) {
            return false;
        }

        items.replace(item, items.getOrDefault(item,0) - quantity);

        return true;
    }

    public boolean isInInventory(InventoryItem item) {
        return items.containsKey(item);
    }

    public InventoryItem switchItem(InventoryItem currentItem) {
        InventoryItem[] array = items.keySet().toArray(new InventoryItem[0]);

        int index = -1;

        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals(currentItem)) {
                index = i;
            }
        }

        return array[(index + 1)%array.length];
    }

    public interface Holder {

        default boolean possess(InventoryItem item, Inventory inventory) {
            return inventory.isInInventory(item);
        }
    }
}
