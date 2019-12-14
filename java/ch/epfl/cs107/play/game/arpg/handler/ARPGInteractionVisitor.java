package ch.epfl.cs107.play.game.arpg.handler;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.ARPGBehavior;
import ch.epfl.cs107.play.game.arpg.actor.*;
import ch.epfl.cs107.play.game.rpg.actor.Monster;
import ch.epfl.cs107.play.game.rpg.handler.RPGInteractionVisitor;

public interface ARPGInteractionVisitor extends RPGInteractionVisitor {
    default void interactWith(ARPGBehavior.ARPGCell cell) {
        // by default the interaction is empty
    }

    default void interactWith(ARPGPlayer player) {
        // by default the interaction is empty
    }

    default void interactWith(Grass grass) {
        // by default the interaction is empty
    }

    default void interactWith(Coin coin) {
        // by default the interaction is empty
    }

    default void interactWith(Heart heart) {
        // by default the interaction is empty
    }

    default void interactWith(CastleKey key) {
        // by default the interaction is empty
    }

    default void interactWith(Monster monster) {
        // by default the interaction is empty
    }

    default void interactWith(Bomb bomb) {
        // by default the interaction is empty
    }

    default void interactWith(FireSpell fireSpell) {
        //by default the interaction is empty
    }

    default void interactWith(Orb orb) {
        //by default the interaction is empty
    }

    default void interactWith(Chest chest) {
        //by default the interaction is empty
    }

    default void interactWith(Shop shop, Orientation orientation) { //We need an orientation to make sure the shop can be interacted with in its orientation (See ARPGPlayer.java and Shop.java)
        //by default the interaction is empty
    }
}
