package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.game.rpg.InventoryItem;

public interface GetInventoryItem {
    InventoryItem item = new InventoryItem();

    default void setName(String name) {
        item.setName(name);
    }

    default void setWeight(float weight) {
        item.setWeight(weight);
    }

    default void setPrice(int price) {
        item.setPrice(price);
    }
}
