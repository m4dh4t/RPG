package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.area.ARPGArea;
import ch.epfl.cs107.play.game.rpg.InventoryItem;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.Collections;
import java.util.List;

public enum ARPGItem implements InventoryItem {
    ARROW("Arrow", 0.5f, 1, "zelda/arrow.icon"),
    SWORD("Sword", 1.5f, 10, "zelda/sword.icon"),
    STAFF("Staff", 2.5f, 50, "zelda/staff_water.icon"),
    BOW("Bow", 1.5f, 20, "zelda/bow.icon"),
    BOMB("Bomb", 15.f, 30, "zelda/bomb"),
    CASTLEKEY("Castle Key", 0.1f, 100, "zelda/key");

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

    public boolean use(Area area, DiscreteCoordinates position, Orientation orientation){
        List<DiscreteCoordinates> frontCells = Collections.singletonList(position.jump(orientation.toVector()));
        if(this == BOMB){
            Actor newBomb = new Bomb(area, position.jump(orientation.toVector()), 10);
            if(area.canEnterAreaCells((Interactable) newBomb, frontCells)){
                return area.registerActor(newBomb);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
