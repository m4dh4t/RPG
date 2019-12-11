package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LogMonster extends Monster {
    private static final int ANIMATION_DURATION = getAnimationDuration();
    private final static float MAX_INACTIVE_DURATION = 2.f; //in seconds
    private final static float MIN_SLEEPING_DURATION = 1.f; //in seconds
    private final static float MAX_SLEEPING_DURATION = 3.f; //in seconds
    private final static float MAXHP = 3.f;

    private float inactiveTimeLeft;
    private LogMonsterState currentState;
    private boolean inactive;

    private LogMonsterHandler handler;

    /**
     * LogMonster constructor
     *
     * @param area            (Area): Owner area. Not null
     * @param orientation     (Orientation): Initial orientation of the entity. Not null
     * @param position        (Coordinate): Initial position of the entity. Not null
     */
    public LogMonster(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, MAXHP, new ArrayList<>(Arrays.asList(Vulnerability.PHYSICAL, Vulnerability.FIRE)), "zelda/logMonster", 4, new Orientation[] {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
        currentState = new LogMonsterIdle();;
        inactive = false;
        handler = new LogMonsterHandler();
        inactiveTimeLeft = 0;
    }

    @Override
    void spawnCollectables() {
        Coin coin = new Coin(getOwnerArea(), new DiscreteCoordinates((int)getPosition().x, (int)getPosition().y));
        getOwnerArea().registerActor(coin);
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
    public void update(float deltaTime) {
        if (!isDead()) {
            inactiveTimeLeft -= deltaTime;

            if (inactiveTimeLeft <= 0) {
                inactive = false;
            }

            if (!inactive) {
                currentState.update(deltaTime);
            }
        }
        super.update(deltaTime);
    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    private class LogMonsterHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(ARPGPlayer player) {
            if (currentState instanceof LogMonsterAttacking) {
                player.weaken(2);
            } else {
                currentState = new LogMonsterAttacking(); //If not attacking, switches to mode "attack"
            }
        }
    }

    private abstract class LogMonsterState {
        private final static int FIELD_OF_VIEW_DISTANCE = 8;

        LogMonsterState(String spriteName) { //Constructor for Idle, Attacking and Falling Asleep
            Sprite[][] sprites = RPGSprite.extractSprites(spriteName, 4, 2.f, 2.f, LogMonster.this, 32, 32, new Vector(-0.5f, 0.f), new Orientation[] {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
            Animation[] animations = RPGSprite.createAnimations(ANIMATION_DURATION/2, sprites);
            setAnimations(animations, getOrientation());
        }

        LogMonsterState(String spriteName, boolean repeat) { //Constructor for Sleeping and Waking Up
            Sprite[][] sprites = RPGSprite.extractSprites(spriteName, 1, 2.f, 2.f, LogMonster.this, 32, 32, new Vector(-0.5f,0.f), new Orientation[] {Orientation.UP, Orientation.RIGHT, Orientation.DOWN, Orientation.LEFT});

            Animation[] animations = RPGSprite.createAnimations(ANIMATION_DURATION, sprites, repeat);
            setAnimations(animations, Orientation.UP);
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

        public abstract void update(float deltaTime);
    }

    private class LogMonsterIdle extends LogMonsterState {
        private static final double PROBABILITY_TO_CHANGE_DIRECTION = 0.1;
        private static final double PROBABILITY_TO_GO_INACTIVE = 0.05;

        LogMonsterIdle() {
            super("zelda/logMonster");
        }

        @Override
        public void update(float deltaTime) {
            if (RandomGenerator.getInstance().nextDouble() >= PROBABILITY_TO_GO_INACTIVE) {
                if (RandomGenerator.getInstance().nextDouble() < PROBABILITY_TO_CHANGE_DIRECTION) {
                    switch (RandomGenerator.getInstance().nextInt(4)) {
                        case 0:
                            orientate(Orientation.LEFT);
                            animate(Orientation.LEFT);
                            break;
                        case 1:
                            orientate(Orientation.UP);
                            animate(Orientation.UP);
                            break;
                        case 2:
                            orientate(Orientation.RIGHT);
                            animate(Orientation.RIGHT);
                            break;
                        case 3:
                            orientate(Orientation.DOWN);
                            animate(Orientation.DOWN);
                            break;
                    }
                }
                move(ANIMATION_DURATION);
                animate(getOrientation());
            } else {
                inactive = true;
                inactiveTimeLeft = RandomGenerator.getInstance().nextFloat() * MAX_INACTIVE_DURATION;
            }
        }
    }

    private class LogMonsterAttacking extends LogMonsterState {
        LogMonsterAttacking() {
            super("zelda/logMonster");
        }

        @Override
        public List<DiscreteCoordinates> getFieldOfViewCells() {
            return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
        }

        @Override
        public void update(float deltaTime) {
            if (isDisplacementOccurs() || getOwnerArea().canEnterAreaCells(LogMonster.this, getFieldOfViewCells())) {
                move(ANIMATION_DURATION);
            } else {
                currentState = new LogMonsterFallingAsleep();
            }
        }
    }

    private class LogMonsterFallingAsleep extends LogMonsterState {
        LogMonsterFallingAsleep() {
            super("zelda/logMonster");
        }

        @Override
        public void update(float deltaTime) {
            inactive = true;
            inactiveTimeLeft = MIN_SLEEPING_DURATION + RandomGenerator.getInstance().nextFloat() * (MAX_SLEEPING_DURATION - MIN_SLEEPING_DURATION);
            currentState = new LogMonsterSleeping();
        }
    }

    private class LogMonsterSleeping extends LogMonsterState {
        LogMonsterSleeping() {
            super("zelda/logMonster.sleeping", true);
        }

        @Override
        public void update(float deltaTime) {
            currentState = new LogMonsterWakingUp();
        }
    }

    private class LogMonsterWakingUp extends LogMonsterState {
        LogMonsterWakingUp() {
            super("zelda/logMonster.wakingUp", false);
        }

        @Override
        public void update(float deltaTime) {
            if (isAnimationCompleted()) {
                currentState = new LogMonsterIdle();
            }
        }
    }
}
