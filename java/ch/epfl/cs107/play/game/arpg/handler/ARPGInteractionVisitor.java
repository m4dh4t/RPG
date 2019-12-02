package ch.epfl.cs107.play.game.arpg.handler;

import ch.epfl.cs107.play.game.arpg.ARPGBehavior;
import ch.epfl.cs107.play.game.arpg.actor.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
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
}
