package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.TriggerableEntity;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bridge extends TriggerableEntity {
    private Sprite sprite;

    /**
     * Bridge constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public Bridge(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);

        sprite = new Sprite("zelda/bridge", 4.f, 3.f, this, null, new Vector(0.f,-1.f), 1.f, -11);
    }

    @Override
    public void draw(Canvas canvas) {
        if(isTriggered()){
            sprite.draw(canvas);
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return new ArrayList<>(Arrays.asList(getCurrentMainCellCoordinates().jump(1,0), getCurrentMainCellCoordinates().jump(2,0)));
    }

    @Override
    public boolean takeCellSpace() {
        return !isTriggered();
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
    public void acceptInteraction(AreaInteractionVisitor v) {

    }
}
