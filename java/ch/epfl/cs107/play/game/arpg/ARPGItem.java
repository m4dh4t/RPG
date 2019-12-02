package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.game.areagame.actor.Sprite;

public enum ARPGItem implements GetInventoryItem {
    ARROW("Arrow", 0, 0, "zelda/arrow.icon"),
    SWORD("Sword", 0, 0,"zelda/sword.icon"),
    STAFF("Staff", 0, 0,"zelda/staff_water.icon"),
    BOW("Bow",0,0,"zelda/bow.icon"),
    BOMB("Bomb",0,0,"zelda/bomb"),
    CASTLEKEY("CastleKey",0,0,"zelda/key");

    private String spriteName;

    ARPGItem(String name, float weight, int price, String spriteName) {
        setName(name);
        setWeight(weight);
        setPrice(price);
        this.spriteName = spriteName;
    }
}
