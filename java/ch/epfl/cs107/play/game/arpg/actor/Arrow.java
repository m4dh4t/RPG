package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Monster;
import ch.epfl.cs107.play.game.rpg.actor.Projectile;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Arrow extends Projectile {
    private ArrowHandler handler;
    private Sprite sprite;

    /**
     * Arrow constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param moveDuration
     * @param maxTravel
     */
    public Arrow(Area area, Orientation orientation, DiscreteCoordinates position, float moveDuration, float maxTravel) {
        super(area, orientation, position, moveDuration, maxTravel);
        handler = new ArrowHandler();
        sprite = new RPGSprite("zelda/arrow", 1.f,1.f,this, new RegionOfInterest(orientation.ordinal()*32,0,32,32), Vector.ZERO, 1.f, 5);
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {

    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    @Override
    public boolean wantsViewInteraction() {
        return true;
    }

    private class ArrowHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(Monster monster) {
            monster.weaken(1.f, Monster.Vulnerability.PHYSICAL);
            setStop(true);
        }

        @Override
        public void interactWith(Grass grass) {
            if (grass.isBurnt()) {
                grass.extinguish();
            } else {
                if (!grass.isCut()) { //Checks if the grass is cut so that the arrow doesn't disappear if the grass is in a cut animation
                    setStop(true);
                }
                grass.cut();
            }
        }

        @Override
        public void interactWith(Bomb bomb) {
            bomb.explode();
            setStop(true);
        }

        @Override
        public void interactWith(FireSpell fireSpell) {
            fireSpell.extinguish();
        }

        @Override
        public void interactWith(Orb orb) {
            orb.hit();
            setStop(true);
        }
    }
}
