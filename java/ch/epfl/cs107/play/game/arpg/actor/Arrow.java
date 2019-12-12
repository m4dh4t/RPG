package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Projectile;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class Arrow extends Projectile implements FlyableEntity{
    private ArrowHandler handler;
    private boolean hit;
    private Sprite sprite;

    /**
     * Arrow constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param moveSpeed
     * @param maxTravel
     */
    public Arrow(Area area, Orientation orientation, DiscreteCoordinates position, float moveSpeed, float maxTravel) {
        super(area, orientation, position, moveSpeed, maxTravel);
        handler = new ArrowHandler();
        hit = false;
        sprite = new RPGSprite("zelda/arrow", 1.f,1.f,this, new RegionOfInterest(orientation.ordinal()*32,0,32,32), Vector.ZERO, 1.f, 5);
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        if(hit){
            getOwnerArea().unregisterActor(this);
        }
        super.update(deltaTime);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {

    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return null;
    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    private class ArrowHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(Grass grass) {
            grass.cut();
            hit = true;
        }

        @Override
        public void interactWith(Bomb bomb) {
            bomb.explode();
            hit = true;
        }

        @Override
        public void interactWith(FireSpell fireSpell) {
            fireSpell.extinguish();
        }
    }
}
