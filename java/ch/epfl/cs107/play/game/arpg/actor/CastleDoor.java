package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

public class CastleDoor extends Door {
    private Sprite spriteClosed;
    private Sprite spriteOpen;

    public CastleDoor(String destination, DiscreteCoordinates otherSideCoordinates, Area area, Orientation orientation, DiscreteCoordinates position) {
        super(destination, otherSideCoordinates, Logic.FALSE, area, orientation, position);
        spriteClosed = new RPGSprite("zelda/castleDoor.close",2.f,2.f,this);
        spriteOpen = new RPGSprite("zelda/castleDoor.open",2.f,2.f,this);
    }

    public CastleDoor(String destination, DiscreteCoordinates otherSideCoordinates, Area area, Orientation orientation, DiscreteCoordinates position, DiscreteCoordinates... otherCells) {
        super(destination, otherSideCoordinates, Logic.FALSE, area, orientation, position, otherCells);
        spriteClosed = new RPGSprite("zelda/castleDoor.close",2.f,2.f,this);
        spriteOpen = new RPGSprite("zelda/castleDoor.open",2.f,2.f,this);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (isOpen()) {
            spriteOpen.draw(canvas);
        } else {
            spriteClosed.draw(canvas);
        }
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

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor)v).interactWith(this); //Need to overwrite this method to replace RPGInteractionVisitor with ARPGInteractionVisitor
    }
}
