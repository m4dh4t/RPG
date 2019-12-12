package ch.epfl.cs107.play.game.rpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.Collections;
import java.util.List;

public abstract class Projectile extends MovableAreaEntity implements Interactor {
    private final float MOVE_DURATION;
    private final float MAX_TRAVEL;
    private float currentTravel;

    /**
     * Projectile constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     */
    public Projectile(Area area, Orientation orientation, DiscreteCoordinates position, float moveSpeed, float maxTravel) {
        super(area, orientation, position);
        MOVE_DURATION = moveSpeed;
        MAX_TRAVEL = maxTravel;
        currentTravel = 0f;
    }

    @Override
    public void update(float deltaTime) {
        currentTravel += (deltaTime * MOVE_DURATION);

        if(currentTravel >= MAX_TRAVEL){
            getOwnerArea().unregisterActor(this);
        }

        move((int)MOVE_DURATION);

        if(!isDisplacementOccurs()){
            getOwnerArea().unregisterActor(this);
        }

        super.update(deltaTime);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public boolean wantsCellInteraction() {
        return (currentTravel < MAX_TRAVEL);
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }
}
