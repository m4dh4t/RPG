package ch.epfl.cs107.play.game.rpg.actor;

import ch.epfl.cs107.play.game.rpg.InventoryItem;

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

        items.replace(item, items.getOrDefault(item,0) + quantity);
        items.putIfAbsent(item, quantity);
        weight += quantity * item.getWeight();

        return true;
    }

    protected boolean remove(InventoryItem item, int quantity) {
        if (!items.containsKey(item) || items.getOrDefault(item,0) < quantity) {
            return false;
        }

        items.replace(item, items.get(item) - quantity);

        if (items.get(item) == 0) {
            items.remove(item);
        }

        return true;
    }

    public boolean isInInventory(InventoryItem item) {
        return items.containsKey(item);
    }

    public int howMany(InventoryItem item) {
        return items.getOrDefault(item, 0);
    }

    public InventoryItem switchItem(InventoryItem currentItem) {
        InventoryItem[] array = items.keySet().toArray(new InventoryItem[0]);

        if (items.isEmpty()) {
            return null;
        }

        int index = -1;

        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals(currentItem)) {
                index = i;
            }
        }

        return array[(index + 1)%array.length];
    }

    public interface Holder {
        boolean possess(InventoryItem item);
    }
}
