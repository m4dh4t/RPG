package ch.epfl.cs107.play.game.rpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;

import java.util.Collections;
import java.util.List;

public abstract class Projectile extends MovableAreaEntity implements Interactor, FlyableEntity {
    private final float MOVE_DURATION;
    private final DiscreteCoordinates MAX_TRAVEL;

    //Attribute to know if the projectile needs to stop or not
    private boolean stop;

    /**
     * Projectile constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     */
    public Projectile(Area area, Orientation orientation, DiscreteCoordinates position, float moveDuration, float maxTravel) {
        super(area, orientation, position);
        MOVE_DURATION = moveDuration;
        Vector maxVector = orientation.toVector().mul(maxTravel);
        MAX_TRAVEL = new DiscreteCoordinates((int) (position.x + maxVector.x), (int) (position.y + maxVector.y));
        stop = false;
    }

    @Override
    public void update(float deltaTime) {
        DiscreteCoordinates position = new DiscreteCoordinates((int) getPosition().x, (int) getPosition().y);

        //Checks if it hasn't reached its destination, if it can enter the cell in front of it and if it must not stop
        if (!stop && !position.equals(MAX_TRAVEL) && getOwnerArea().canEnterAreaCells(this, Collections.singletonList(position.jump(getOrientation().toVector())))) {
            move((int) MOVE_DURATION);
        } else {
            getOwnerArea().unregisterActor(this);
        }

        super.update(deltaTime);
    }

    /**
     * Sets the variable stop to true to make the projectile disappear (see update).
     */
    protected void stop() {
        stop = true;
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
        return true;
    }
}
