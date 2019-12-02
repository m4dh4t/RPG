package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.rpg.Inventory;
import ch.epfl.cs107.play.game.rpg.InventoryItem;

public class ARPGInventory extends Inventory {
    private int money;
    private int fortune;

    public ARPGInventory(int startMoney) {
        super(100);
        money = startMoney;
        fortune = startMoney;
    }

    @Override
    protected boolean add(InventoryItem item, int quantity) {
        if (super.add(item,quantity)) {
            fortune += quantity * item.getWeight();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean remove(InventoryItem item, int quantity) {
        if (super.remove(item, quantity)) {
            fortune -= quantity * item.getWeight();
            return true;
        } else {
            return false;
        }
    }

    protected void addMoney(int money) {
        this.money += money;
        fortune += money;
    }

    public int getMoney() {
        return money;
    }

    public int getFortune() {
        return fortune;
    }
}
