package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.arpg.ARPGItem;
import ch.epfl.cs107.play.game.rpg.actor.Inventory;
import ch.epfl.cs107.play.game.rpg.InventoryItem;

public class ARPGInventory extends Inventory {
    private int money;
    private int currentItemIndex;

    public ARPGInventory(int money) {
        super(1000);
        this.money = money;
        currentItemIndex = -1;
    }

    public void addMoney(int money){
        this.money += money;
    }

    public int getMoney() {
        return money;
    }

    public int getFortune() {
        return (money + getInventoryValue());
    }

    @Override
    protected boolean addItem(InventoryItem inventoryItem, int qte) {
        return super.addItem(inventoryItem, qte);
    }

    @Override
    protected boolean removeItem(InventoryItem inventoryItem, int qte) {
        return super.removeItem(inventoryItem, qte);
    }

    public ARPGItem nextItem(){
        ++currentItemIndex;
        if(currentItemIndex >= getInventory().size()){
            currentItemIndex = 0;
        }
        return (ARPGItem) getInventory().get(currentItemIndex);
    }
}
