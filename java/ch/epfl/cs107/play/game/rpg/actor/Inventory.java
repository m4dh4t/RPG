package ch.epfl.cs107.play.game.rpg.actor;

import ch.epfl.cs107.play.game.rpg.InventoryItem;

import java.util.Map;
import java.util.TreeMap;

public class Inventory {
    private final float MAXWEIGHT;
    private float weight;
    private Map<InventoryItem, Integer> items;

    protected Inventory(float maxWeight) {
        MAXWEIGHT = maxWeight;
        items = new TreeMap<>();
    }

    protected boolean add(InventoryItem item, int quantity) {
        if (weight + quantity * item.getWeight() > MAXWEIGHT || quantity <= 0) {
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
        weight -= quantity * item.getWeight();

        if (items.get(item) == 0) {
            items.remove(item);
        }

        return true;
    }

    public boolean isInInventory(InventoryItem item) {
        if (item == null) {
            return false;
        }
        return items.containsKey(item);
    }

    public InventoryItem[] getItems() {
        return items.keySet().toArray(new InventoryItem[0]);
    }

    public int getQuantity(InventoryItem item) {
        return items.getOrDefault(item, 0);
    }

    public InventoryItem switchItem(InventoryItem currentItem) {
        if (items.isEmpty()) {
            return null;
        }

        int index = -1;

        for (int i = 0; i < getItems().length; ++i) {
            if (getItems()[i].equals(currentItem)) {
                index = i;
            }
        }

        return getItems()[(index + 1) % getItems().length];
    }

    public interface Holder {
        boolean possess(InventoryItem item);
    }
}
