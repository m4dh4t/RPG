package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

public class CastleDoor extends Door {
    private Sprite sprite;

    /**
     * CastleDoor constructor
     * @param destination        (String): Name of the destination area, not null
     * @param otherSideCoordinates (DiscreteCoordinate):Coordinates of the other side, not null
     * @param area        (Area): Owner area, not null
     * @param orientation (Orientation): Initial orientation of the entity, not null
     * @param position    (DiscreteCoordinates): Initial position of the entity, not null
     * @param otherCell (DiscreteCoordinates): Second initial position of the entity, not null (We want a second position
     *                  because the sprite is two-cases large.)
     */
    public CastleDoor(String destination, DiscreteCoordinates otherSideCoordinates, Area area, Orientation orientation, DiscreteCoordinates position, DiscreteCoordinates otherCell) {
        super(destination, otherSideCoordinates, Logic.FALSE, area, orientation, position, otherCell);
        sprite = new RPGSprite("zelda/castleDoor.close",2.f,2.f,this);
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    public void close(){
        setSignal(Logic.FALSE);
        sprite = new RPGSprite("zelda/castleDoor.close", 2.f,2.f,this, new RegionOfInterest(0,0,32,32));
    }

    public void open(){
        setSignal(Logic.TRUE);
        sprite = new RPGSprite("zelda/castleDoor.open", 2.f,2.f,this, new RegionOfInterest(0,0,32,32));
    }

    @Override
    public boolean isViewInteractable() {
        return !isOpen(); //Only accepts view interactions when it is closed to prevent the player to enter in the castle just by pressing E (and not moving into it)
    }

    @Override
    protected void setSignal(Logic signal) {
        super.setSignal(signal); //Override of this method because it has protected access in Door (rpg package) and we want to use it in ARPGPlayer in arpg package
    }

    @Override
    public boolean takeCellSpace() {
        return !isOpen(); //Take cell space when it is closed to keep the player stuck in front of it
    }
}
