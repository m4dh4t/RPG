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

import java.util.Arrays;

public class CastleDoor extends Door {
    private Sprite sprite;

    public CastleDoor(String destination, DiscreteCoordinates otherSideCoordinates, Logic signal, Area area, Orientation orientation, DiscreteCoordinates position, DiscreteCoordinates... otherCells) {
        super(destination, otherSideCoordinates, signal, area, orientation, position, otherCells);

        sprite = new RPGSprite("zelda/castleDoor.close", 2.f,2.f,this, new RegionOfInterest(0,0,32,32));
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    @Override
    public boolean isViewInteractable(){
        return !isOpen();
    }

    @Override
    public boolean takeCellSpace() {
        return !isOpen();
    }

    public void close(){
        setSignal(Logic.FALSE);
        sprite = new RPGSprite("zelda/castleDoor.close", 2.f,2.f,this, new RegionOfInterest(0,0,32,32));
    }

    public void open(){
        setSignal(Logic.TRUE);
        sprite = new RPGSprite("zelda/castleDoor.open", 2.f,2.f,this, new RegionOfInterest(0,0,32,32));
    }
}
