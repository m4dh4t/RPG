package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.arpg.Vulnerability;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LogMonster extends Monster {
    private final static float MAXHP = 3.f;

    private LogMonsterState[] states;
    private LogMonsterState currentState;

    /**
     * LogMonster constructor
     *
     * @param area            (Area): Owner area. Not null
     * @param orientation     (Orientation): Initial orientation of the entity. Not null
     * @param position        (Coordinate): Initial position of the entity. Not null
     */
    public LogMonster(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, MAXHP, new ArrayList<>(Arrays.asList(Vulnerability.PHYSICAL, Vulnerability.FIRE)), "zelda/logMonster", 4, new Orientation[] {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
        states[0] = new LogMonsterIdle(orientation);
        states[1] = new LogMonsterAttacking(orientation);
        states[2] = new LogMonsterFallingAsleep(orientation);
        states[3] = new LogMonsterSleeping();
        states[4] = new LogMonsterWakingUp();
        currentState = states[0];
    }

    @Override
    public void draw(Canvas canvas) {
        currentState.animation.draw(canvas);
    }

    @Override
    void spawnCollectables() {
        getOwnerArea().registerActor(new Coin(getOwnerArea(), new DiscreteCoordinates((int)getPosition().x, (int)getPosition().y)));
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return currentState.getFieldOfViewCells();
    }

    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    @Override
    public boolean wantsViewInteraction() {
        return currentState.getClass() == LogMonsterIdle.class || currentState.getClass() == LogMonsterAttacking.class;
    }

    @Override
    public void interactWith(Interactable other) {

    }

    private class LogMonsterState {
        private final static int ANIMATION_DURATION = 8;
        private final static int FIELD_OF_VIEW_DISTANCE = 8;
        private Animation[] animations;
        private Animation animation;

        LogMonsterState(String spriteName, Orientation orientation) {
            Sprite[][] sprites = RPGSprite.extractSprites(spriteName, 4, 2.f, 2.f, LogMonster.this, 32, 32, new Vector(-0.5f, 0.f), new Orientation[] {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
            animations = RPGSprite.createAnimations(ANIMATION_DURATION, sprites);
            animation = animations[orientation.ordinal()];
        }

        LogMonsterState(String spriteName) {
            Sprite[][] sprites = RPGSprite.extractSprites(spriteName, 1, 2.f, 2.f, LogMonster.this, 32, 32, new Vector(-0.5f,0.f), new Orientation[] {Orientation.UP, Orientation.RIGHT, Orientation.DOWN, Orientation.LEFT});

            animations = RPGSprite.createAnimations(ANIMATION_DURATION, sprites);
            animation = animations[0];
        }

        public List<DiscreteCoordinates> getFieldOfViewCells() {
            ArrayList<DiscreteCoordinates> list = new ArrayList<>();
            Vector orientationVector = getOrientation().toVector();

            for (int i = 0; i < FIELD_OF_VIEW_DISTANCE; ++i) {
                 Vector vector = getCurrentMainCellCoordinates().toVector().add(orientationVector.mul(i));
                 list.add(new DiscreteCoordinates((int) vector.x, (int) vector.y));
            }

            return list;
        }
    }

    private class LogMonsterIdle extends LogMonsterState {
        LogMonsterIdle(Orientation orientation) {
            super("zelda/logMonster", orientation);
        }
    }

    private class LogMonsterAttacking extends LogMonsterState {
        LogMonsterAttacking(Orientation orientation) {
            super("zelda/logMonster", orientation);
        }

        @Override
        public List<DiscreteCoordinates> getFieldOfViewCells() {
            return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
        }
    }

    private class LogMonsterFallingAsleep extends LogMonsterState {
        LogMonsterFallingAsleep(Orientation orientation) {
            super("zelda/logMonster", orientation);
        }
    }

    private class LogMonsterSleeping extends LogMonsterState {
        LogMonsterSleeping() {
            super("zelda/logMonster.sleeping");
        }
    }

    private class LogMonsterWakingUp extends LogMonsterState {
        LogMonsterWakingUp() {
            super("zelda/logMonster.wakingUp");
        }
    }
}
