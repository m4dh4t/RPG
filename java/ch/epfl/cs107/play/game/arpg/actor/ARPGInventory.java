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

    @Override
    protected boolean add(InventoryItem item, int quantity) {
        if (super.add(item,quantity)) {
            fortune += quantity * item.getPrice();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean remove(InventoryItem item, int quantity) {
        if (super.remove(item, quantity)) {
            fortune -= quantity * item.getPrice();
            return true;
        } else {
            return false;
        }
    }

    protected void addMoney(int money) {
        this.money += money;
        fortune += money;

        if (this.money > MAXMONEY) {
            fortune -= this.money + MAXMONEY;
            this.money = MAXMONEY;
        }
    }

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
