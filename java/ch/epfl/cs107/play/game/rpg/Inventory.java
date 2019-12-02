package ch.epfl.cs107.play.game.rpg;

import java.util.ArrayList;

public class Inventory {
    private final float MAXWEIGHT;
    private float weight;
    private ArrayList<InventoryItem> items;

    protected Inventory(float maxWeight) {
        MAXWEIGHT = maxWeight;
        items = new ArrayList<>();
    }

    protected boolean add(InventoryItem item, int quantity) {
        if (weight + quantity * item.getWeight() > MAXWEIGHT) {
            return false;
        }

        for (int i = 0; i < quantity; ++i) {
            if (weight + item.getWeight() <= MAXWEIGHT) {
                items.add(item);
                weight += item.getWeight();
            }
        }

        return true;
    }

    protected boolean remove(InventoryItem item, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            if (!items.contains(item)) {
                return false;
            }
        }


        for (int i = 0; i < quantity; ++i) {
            if (items.contains(item)) {
                items.remove(item);
                weight -= item.getWeight();
            }
        }

        return true;
    }

    public boolean isInInventory(InventoryItem item) {
        return items.contains(item);
    }
}
