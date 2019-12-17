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
        //We used a TreeMap and not a HashMap because we wanted to have the keys of the map sorted
        //(especially useful when shopping)
    }

    /**
     * Method used to add an item in the inventory. We can add a certain quantity at once.
     * @param item (InventoryItem): The item to add
     * @param quantity (int): the quantity we want to add
     * @return (boolean): if the item has been added
     */
    protected boolean add(InventoryItem item, int quantity) {
        if (weight + quantity * item.getWeight() > MAXWEIGHT || quantity <= 0) {
            return false;
        }

        /*If the item exists, it replaces it with the new value and items.putIfAbstent(...) will have no effect.
        If it does not, items.replace(...) will have no effect and items.putIfAbsent(...) will create a new
        key assigned to the quantity. We made sure to use getOrDefault here in case item is not items, because
        if it is the case and if we had had used items.get(item) instead, the compiler would have thrown an
        exception because item does not exist in items and items.get(item) throws an exception if item is not in
        items.
         */
        items.replace(item, items.getOrDefault(item,0) + quantity);
        items.putIfAbsent(item, quantity);
        weight += quantity * item.getWeight(); //Make sure it actualizes the current weight of the inventory

        return true;
    }

    /**
     * Method used to remove an item from the inventory. We can remove a certain quantity at once.
     * @param item (InventoryItem): The item to remove
     * @param quantity (int): the quantity we want to remove
     * @return (boolean): if the item has been removed
     */
    protected boolean remove(InventoryItem item, int quantity) {
        if (!items.containsKey(item) || items.get(item) < quantity) {
            /*In general, if item is not in the key set of items, items.get(item) will throw an exception.
            But here we first check if items contains item so we are sure that items contains item
            if we reach the second condition.
            Why did we not just put : if (items.getOrDefault(item, 0) < quantity) {...} ?
            Because if the quantity is 0 and the item is not in items, we would get 0 < 0 which is false
            and we would continue the program. When the program reaches line 65, the compiler will throw an
            exception because item is not in items ! (Method items.get(item) will throw the exception, as
            explained above.)
            */
            return false;
        }

        items.replace(item, items.get(item) - quantity);
        weight -= quantity * item.getWeight();

        if (items.get(item) == 0) {
            items.remove(item);
        }

        return true;
    }

    /**
     * Checks if the item is in the inventory.
     * @param item (InventoryItem): The item we want to check
     * @return (boolean): if the item is in the inventory
     */
    public boolean isInInventory(InventoryItem item) {
        if (item == null) {
            return false;
        }
        return items.containsKey(item);
    }

    /**
     * Very useful methods to get every item in an array and freely play with their indexes
     * @return (InventoryItem[]): The array containing every key of the map (every item of the inventory)
     */
    public InventoryItem[] getItems() {
        return items.keySet().toArray(new InventoryItem[0]);
    }

    /**
     * Returns the quantity of a given item in the inventory.
     * @param item (InventoryItem): The item which we want to know the quantity.
     * @return (int): The number of times item is in the inventory.
     */
    public int getQuantity(InventoryItem item) {
        return items.getOrDefault(item, 0);
    }

    /**
     * Method used to switch items.
     * @param currentItem (InventoryItem): The current item.
     * @return (InventoryItem): The item next to the current item given.
     */
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

    //Represents a holder. Need to override the method possess.
    public interface Holder {
        boolean possess(InventoryItem item);
    }
}
