package ch.epfl.cs107.play.game.rpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public abstract class TriggerableEntity extends AreaEntity {
    private boolean triggered;

    /**
     * TriggerableEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity in the Area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public TriggerableEntity(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        triggered = false;
    }

    /**
     * A triggerable entity can be triggered with this method
     */
    public void trigger(){
        triggered = true;
    }

    /**
     * Returns if the entity is triggered
     * @return (boolean): if the entity is triggered
     */
    public boolean isTriggered() {
        return triggered;
    }
}
