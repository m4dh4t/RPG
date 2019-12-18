package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Monster;
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
    private final static float MIN_SLEEPING_DURATION = 1.f; //in seconds
    private final static float MAX_SLEEPING_DURATION = 3.f; //in seconds
    private final static float MAXHP = 3.f;

    private LogMonsterState currentState;

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
        currentState = new LogMonsterIdle();
        handler = new LogMonsterHandler();
    }

    /**
     * Extends from Monster.java
     */
    @Override
    protected void spawnCollectables() {
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
        //If it is Idle : wants to switch to Attacking mode; if attacking, wants to damage the player
        return (currentState instanceof LogMonsterIdle) || (currentState instanceof LogMonsterAttacking);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (!isDead() && !isInactive()) { //If inactive, does not do anything
            currentState.update(deltaTime);
        }
    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    private class LogMonsterHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(ARPGPlayer player) {
            if (currentState instanceof LogMonsterAttacking) {
                player.weaken(3);
            } else {
                move(ANIMATION_DURATION/2); //Starts to move to prevent the logMonster to fall asleep
                // if he wakes up seeing the player (he would not move so he would instantly fall asleep)
                currentState = new LogMonsterAttacking(); //If not attacking, switches to mode "attack"
            }
        }
    }

    private abstract class LogMonsterState {
        private final static int FIELD_OF_VIEW_DISTANCE = 8;

        LogMonsterState(String spriteName, int duration) { //Constructor for Attacking
            Sprite[][] sprites = RPGSprite.extractSprites(spriteName, 4, 2.f, 2.f, LogMonster.this, 32, 32, new Vector(-0.5f, 0.f), new Orientation[] {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
            Animation[] animations = RPGSprite.createAnimations(duration, sprites);
            setAnimations(animations, getOrientation());
        }

        LogMonsterState(String spriteName) { //Constructor for Idle and Falling Asleep
            this(spriteName, ANIMATION_DURATION);
        }

        LogMonsterState(String spriteName, boolean repeat, boolean sleeping) { //Constructor for Sleeping and Waking Up
            Sprite[] sprites = RPGSprite.extractVerticalSprites(spriteName, sleeping ? 4 : 3, 2.f, 2.f, LogMonster.this, 32, 32, new Vector(-0.5f,0.f));
            Sprite[][] spritesUsed = {sprites, sprites, sprites, sprites}; //Same sprites for the 4 different orientations possible

            Animation[] animations = RPGSprite.createAnimations(ANIMATION_DURATION, spritesUsed, repeat);
            setAnimations(animations, Orientation.UP);
        }

        public List<DiscreteCoordinates> getFieldOfViewCells() {
            ArrayList<DiscreteCoordinates> list = new ArrayList<>();
            Vector orientationVector = getOrientation().toVector();

            for (int i = 1; i <= FIELD_OF_VIEW_DISTANCE; ++i) {
                 Vector vector = getCurrentMainCellCoordinates().toVector().add(orientationVector.mul(i));
                 list.add(new DiscreteCoordinates((int) vector.x, (int) vector.y));
            }

            return list;
        }

        public abstract void update(float deltaTime);
    }

    private class LogMonsterIdle extends LogMonsterState {

        LogMonsterIdle() {
            super("zelda/logMonster");
            setForceAnimation(false);
        }

        @Override
        public void update(float deltaTime) {
            randomMove(true);
        }
    }

    private class LogMonsterAttacking extends LogMonsterState {
        LogMonsterAttacking() {
            super("zelda/logMonster", ANIMATION_DURATION/4);
        }

        @Override
        public List<DiscreteCoordinates> getFieldOfViewCells() {
            return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
        }

        @Override
        public void update(float deltaTime) {
            if (isDisplacementOccurs()) {
                move(ANIMATION_DURATION/2);
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
            setInactive(true);
            setInactiveTimeLeft(MIN_SLEEPING_DURATION + RandomGenerator.getInstance().nextFloat() * (MAX_SLEEPING_DURATION - MIN_SLEEPING_DURATION));
            currentState = new LogMonsterSleeping();
        }
    }

    private class LogMonsterSleeping extends LogMonsterState {
        LogMonsterSleeping() {
            super("zelda/logMonster.sleeping", true, true);
            setForceAnimation(true);
        }

        @Override
        public void update(float deltaTime) {
            currentState = new LogMonsterWakingUp();
        }
    }

    private class LogMonsterWakingUp extends LogMonsterState {
        LogMonsterWakingUp() {
            super("zelda/logMonster.wakingUp", false, false);
            setForceAnimation(true);
        }

        @Override
        public void update(float deltaTime) {
            if (isAnimationCompleted()) {
                currentState = new LogMonsterIdle();
            }
        }
    }
}
