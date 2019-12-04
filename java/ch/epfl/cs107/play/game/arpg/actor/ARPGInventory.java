package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.rpg.actor.Inventory;
import ch.epfl.cs107.play.game.rpg.InventoryItem;

public class ARPGInventory extends Inventory {
    private int money;

    public ARPGInventory(int money) {
        super(1000);
        this.money = money;
    }

    public void addMoney(int money){
        money += money;
    }

    public int getMoney() {
        return money;
    }

    public int getFortune() {
        return (money + getInventoryValue());
    }
}
