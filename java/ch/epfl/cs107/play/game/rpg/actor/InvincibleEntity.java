package ch.epfl.cs107.play.game.rpg.actor;

import ch.epfl.cs107.play.game.areagame.actor.Interactable;

public interface InvincibleEntity extends Interactable {
    float INVICIBILITY_DURATION = 1.5f; //in seconds

    /**
     * Method returning if the entity is invincible
     * @return (boolean): if the entity is invincible
     */
    boolean isInvincible();
}
