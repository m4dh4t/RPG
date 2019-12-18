package ch.epfl.cs107.play.game.rpg.actor;

import ch.epfl.cs107.play.game.areagame.actor.Interactable;

public interface FlyableEntity extends Interactable {

    /**
     * Method returning if the entity can fly
     * @return (boolean): if the entity can fly
     */
    default boolean canFly() {
        return true;
    }

    @Override
    default boolean takeCellSpace() {
        return true;
    }
}
