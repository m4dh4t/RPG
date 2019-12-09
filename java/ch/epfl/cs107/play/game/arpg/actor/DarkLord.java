package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.Collections;
import java.util.List;

public class DarkLord extends Monster {
    private final static float MAXHP = 6;
    /**
     * DarkLord constructor
     *
     * @param area            (Area): Owner area. Not null
     * @param orientation     (Orientation): Initial orientation of the entity. Not null
     * @param position        (Coordinate): Initial position of the entity. Not null
     */
    public DarkLord(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, MAXHP, Collections.singletonList(Vulnerability.MAGIC), "zelda/darkLord", 3, new Orientation[] {Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT});
    }

    @Override
    void spawnCollectables() {

    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return null;
    }

    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    @Override
    public void interactWith(Interactable other) {

    }

    private class DarkLordState {
        private final int ANIMATION_DURATION = getAnimationDuration();

        DarkLordState(String spriteName, Orientation orientation) {
            Sprite[][] sprites = RPGSprite.extractSprites(spriteName, 3, 2.f, 2.f, DarkLord.this, 32, 32, new Orientation[] {Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT});
            Animation[] animations = RPGSprite.createAnimations(ANIMATION_DURATION, sprites, true);
            setAnimations(animations, orientation);
        }
    }
}
