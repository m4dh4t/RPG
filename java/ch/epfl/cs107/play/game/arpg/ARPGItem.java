package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.*;
import ch.epfl.cs107.play.game.rpg.InventoryItem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.Collections;
import java.util.List;

public enum ARPGItem implements InventoryItem {
    ARROW("Arrow", 1.f, 10, "zelda/arrow.icon"),
    SWORD("Sword", 15.f, 100, "zelda/sword.icon"),
    STAFF("Staff", 15.f, 300, "zelda/staff_water.icon"),
    BOW("Bow", 10.f, 150, "zelda/bow.icon"),
    BOMB("Bomb", 25.f, 200, "zelda/bomb"),
    CASTLEKEY("Castle Key", 0.1f, 200, "zelda/key"),
    WINGS("Wings", 10.f, 250, "wings"),
    CHESTKEY("Chest Key", 0.1f, 150, "chestKey");

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

    public String getSpriteName(){
        return spriteName;
    }

    /**
     * This method is called when we want to use the current item.
     * @param area (Area): The area in which we want to register the item.
     * @param orientation (Orientation): The orientation of the player to know his front cell
     * @param position (DiscreteCoordinates): The position of the player to calculate where to register the item
     *                 (in addition to his orientation).
     * @return (boolean): if the item has actually been used.
     */
    public boolean use(Area area, Orientation orientation, DiscreteCoordinates position){
        List<DiscreteCoordinates> frontCell = Collections.singletonList(position.jump(orientation.toVector()));

        switch (this){
            case BOMB:
                Bomb bomb = new Bomb(area, position.jump(orientation.toVector()), 3);
                if(area.canEnterAreaCells(bomb, frontCell)){
                    area.registerActor(bomb);
                    return true;
                } else {
                    return false;
                }
            case BOW:
                return true;
            case ARROW:
                Arrow arrow = new Arrow(area, orientation, position.jump(orientation.toVector()), 5f, 5f);
                if(area.canEnterAreaCells(arrow, frontCell)){
                    area.registerActor(arrow);
                    return true;
                } else {
                    return false;
                }
            case STAFF:
                MagicWaterProjectile magicWaterProjectile = new MagicWaterProjectile(area, orientation, position.jump(orientation.toVector()), 5f, 5f);
                if(area.canEnterAreaCells(magicWaterProjectile, frontCell)){
                    area.registerActor(magicWaterProjectile);
                    return true;
                } else {
                    return false;
                }
            case SWORD:
                Sword sword = new Sword(area, orientation, position.jump(orientation.toVector()));
                if(area.canEnterAreaCells(sword, frontCell)){
                    area.registerActor(sword);
                    return true;
                } else {
                    return false;
                }
            default:
                return false;
        }
    }
}
