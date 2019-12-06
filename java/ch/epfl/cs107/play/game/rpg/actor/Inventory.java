package ch.epfl.cs107.play.game.rpg.actor;

import ch.epfl.cs107.play.game.rpg.InventoryItem;

import java.util.ArrayList;

public class Inventory {
    private float maxWeight;
    private float currentWeight;
    private ArrayList<InventoryItem> inventory;
    private ArrayList<Integer> inventoryStock;
    
    protected Inventory(float maxWeight){
        currentWeight = 0;
        this.maxWeight = maxWeight;
        inventory = new ArrayList<>();
        inventoryStock = new ArrayList<>();
    }

    protected boolean addItem(InventoryItem inventoryItem, int qte) {
        float itemTotalWeight = inventoryItem.getWeight()*qte;
        if(currentWeight + itemTotalWeight <= maxWeight) {
            if (!isStocked(inventoryItem)) {
                inventory.add(inventoryItem);
                inventoryStock.add(qte);
            } else {
                int itemIndex = inventory.indexOf(inventoryItem);
                int itemStock = inventoryStock.get(itemIndex);
                inventoryStock.set(itemIndex, itemStock + qte);
            }

            currentWeight += itemTotalWeight;
            return true;
        } else {
            return false;
        }
    }

    protected boolean removeItem(InventoryItem inventoryItem, int qte) {
        if(isStocked(inventoryItem)){
            float itemTotalWeight = inventoryItem.getWeight()*qte;
            int itemIndex = inventory.indexOf(inventoryItem);
            int itemStock = inventoryStock.get(itemIndex);
            if(qte <= itemStock){
                inventoryStock.set(itemIndex, itemStock - qte);
                currentWeight -= itemTotalWeight;
                if(inventoryStock.get(itemIndex) <= 0){
                    inventoryStock.remove(itemIndex);
                    inventory.remove(itemIndex);
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isStocked(InventoryItem inventoryItem){
        return inventory.indexOf(inventoryItem) != -1;
    }

    public int getInventoryValue(){
        int totalValue = 0;

        for(int i = 0; i < inventory.size(); i++){
            totalValue += inventory.get(i).getPrice() * inventoryStock.get(i);
        }

        return totalValue;
    }

    protected ArrayList<InventoryItem> getInventory() {
        return inventory;
    }

    public interface Holder{
        boolean possess(InventoryItem inventoryItem);
    }
}
