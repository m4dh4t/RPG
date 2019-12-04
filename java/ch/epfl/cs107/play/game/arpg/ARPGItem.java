package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.rpg.InventoryItem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.Collections;

public enum ARPGItem implements InventoryItem {
    ARROW("Arrow", 0, 0, "zelda/arrow.icon"),
    SWORD("Sword", 0, 0,"zelda/sword.icon"),
    STAFF("Staff", 0, 0,"zelda/staff_water.icon"),
    BOW("Bow",0,0,"zelda/bow.icon"),
    BOMB("Bomb",0,0,"zelda/bomb"),
    CASTLEKEY("CastleKey",0,0,"zelda/key");

    private String name;
    private float weight;
    private int price;
    private String spriteName;

    ARPGItem(String name, float weight, int price, String spriteName) {
        this.name = name;
        this.weight = weight;
        this.price = price;
        this.spriteName = spriteName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public int getPrice() {
        return price;
    }

    public boolean interaction(Area area, DiscreteCoordinates coordinates) {
        switch(this) {
            case BOMB :
                Bomb bomb = new Bomb(area, coordinates,20);
                if (area.canEnterAreaCells(bomb, Collections.singletonList(coordinates))) {
                    area.registerActor(bomb);
                    return true;
                } else {
                    return false;
                }
            default:
                return false;
        }
    }
}
