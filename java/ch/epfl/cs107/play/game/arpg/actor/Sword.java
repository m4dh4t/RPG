package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Weapon;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Sword extends Weapon {
    private SwordHandler handler;

    /**
     * Sword constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity in the Area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public Sword(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        handler = new SwordHandler();
    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    private class SwordHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(Grass grass) {
            grass.cut();
        }

        @Override
        public void interactWith(Bomb bomb) {
            bomb.explode();
        }

        @Override
        public void interactWith(Monster monster) {
            monster.weaken(1f, Monster.Vulnerability.PHYSICAL);
        }
    }
}