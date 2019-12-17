package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.rpg.actor.Inventory;
import ch.epfl.cs107.play.game.rpg.InventoryItem;

public class ARPGInventory extends Inventory {
    private final static int MAXMONEY = 999;
    private int money;
    private int fortune;

    public ARPGInventory(int startMoney, int maxWeight) {
        super(maxWeight);

        if (startMoney <= MAXMONEY) {
            money = startMoney;
            fortune = startMoney;
        } else {
            money = MAXMONEY;
            fortune = MAXMONEY;
        }
    }

    /**
     * This method adds the given item a number of times, also given, in the inventory.
     * @param item (InventoryItem): The item to add
     * @param quantity (int): The quantity we need to add
     * @return (boolean): If the adding has happened or not.
     */
    @Override
    protected boolean add(InventoryItem item, int quantity) {
        if (super.add(item,quantity)) {
            fortune += quantity * item.getPrice();
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method removes the given item a number of times, also given, from the inventory.
     * @param item (InventoryItem): The item to remove
     * @param quantity (int): The quantity we need to remove
     * @return (boolean): If the removing has happened or not.
     */
    @Override
    protected boolean remove(InventoryItem item, int quantity) {
        if (super.remove(item, quantity)) {
            fortune -= quantity * item.getPrice();
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method adds the given amount of money in the inventory
     * @param money (int): The amount of money to add
     */
    protected void addMoney(int money) {
        this.money += money;
        fortune += money;

        if (this.money > MAXMONEY) {
            fortune -= this.money + MAXMONEY;
            this.money = MAXMONEY;
        }
    }

    /**
     * This method removes the given amount of money from the inventory
     * @param money (int): The amount of money to remove
     */
    protected boolean removeMoney(int money) {
        if (money > this.money) {
            return false;
        } else {
            addMoney(-money);
            return true;
        }
    }

    public int getMoney() {
        return money;
    }

    public int getFortune() {
        return fortune;
    }
}
