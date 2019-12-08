package ch.epfl.cs107.play.game.rpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public abstract class CollectableAreaEntity extends AreaEntity {
    boolean collected;

    /**
     * Default AreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity in the Area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public CollectableAreaEntity(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        collected = false;
    }

    @Override
    abstract public void draw(Canvas canvas);

    @Override
    abstract public List<DiscreteCoordinates> getCurrentCells();

    @Override
    abstract public boolean takeCellSpace();

    @Override
    abstract public boolean isCellInteractable();

    @Override
    abstract public boolean isViewInteractable();

    @Override
    abstract public void acceptInteraction(AreaInteractionVisitor v);

    public boolean isCollected() {
        return collected;
    }

    private void setCollected(boolean collected) {
        this.collected = collected;
    }

    public void collect(){
        setCollected(true);
        getOwnerArea().unregisterActor(this);
    }
}
