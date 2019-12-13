package ch.epfl.cs107.play.game.rpg.actor;

import ch.epfl.cs107.play.game.areagame.actor.Interactable;

public interface FlyableEntity extends Interactable {

    default boolean canFly() {
        return true;
    }

    @Override
    default boolean takeCellSpace() {
        return true;
    }
}
