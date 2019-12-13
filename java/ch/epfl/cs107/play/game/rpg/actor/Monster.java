package ch.epfl.cs107.play.game.rpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public abstract class Monster extends MovableAreaEntity implements Interactor {
    private final static int DEAD_ANIMATION_DURATION = 6;
    private final static int ALIVE_ANIMATION_DURATION = 8;
    private final static float MAX_INACTIVE_DURATION = 2.f;
    private float hp;
    private boolean dead;
    private boolean inactive;
    private float inactiveTimeLeft;

    private List<Vulnerability> vulnerabilities;

    private Animation[] animationsAlive;
    private Animation currentAnimationAlive;
    private Animation deadAnimation;
    private boolean forceAnimation;

    /**
     * Monster constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     */
    public Monster(Area area, Orientation orientation, DiscreteCoordinates position, float hpmax, List<Vulnerability> vulnerabilities, String spriteName, int nbFrames, Orientation[] orientations) {
        super(area, orientation, position);
        hp = hpmax;
        dead = false;
        inactive = false;
        inactiveTimeLeft = 0;
        this.vulnerabilities = vulnerabilities;
        forceAnimation = false;

        Sprite[][] aliveSprites = RPGSprite.extractSprites(spriteName, nbFrames, 2.f,2.f, this, 32, 32, new Vector(-0.5f, 0.f),orientations);
        animationsAlive = RPGSprite.createAnimations(ALIVE_ANIMATION_DURATION/2, aliveSprites);
        currentAnimationAlive = animationsAlive[orientation.ordinal()];

        Sprite[] deadSprites = RPGSprite.extractSprites("zelda/vanish", 7, 2.f, 2.f, this, 32, 32, new Vector(-0.5f,0.f));
        deadAnimation = new Animation(DEAD_ANIMATION_DURATION/2, deadSprites, false);
    }

    public static int getAnimationDuration() {return ALIVE_ANIMATION_DURATION;}

    public boolean isAnimationCompleted() {
        return currentAnimationAlive.isCompleted();
    }

    public void setForceAnimation(boolean bool) { forceAnimation = bool; }

    public List<Vulnerability> getVulnerabilities() {
        return vulnerabilities;
    }

    public void setAnimations(Animation[] animations, Orientation orientation) {
        animationsAlive = animations;
        currentAnimationAlive = animationsAlive[orientation.ordinal()];
    }

    public void weaken(float damage, Vulnerability vulnerability) {
        if (vulnerabilities.contains(vulnerability)) {
            hp -= damage;
        }
    }

    protected boolean isDead() {
        return dead;
    }

    protected boolean isInactive() { return inactive; }
    protected void setInactive(boolean bool) { inactive = bool; }
    protected void setInactiveTimeLeft(float time) { inactiveTimeLeft = time; }

    protected void die() {
        dead = true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!dead) {
            currentAnimationAlive.draw(canvas);
        } else {
            deadAnimation.draw(canvas);
        }
    }

    protected void animate(Orientation orientation) {
        if(orientation == Orientation.UP){
            currentAnimationAlive = animationsAlive[0];
        } else if(orientation == Orientation.RIGHT){
            currentAnimationAlive = animationsAlive[1];
        } else if(orientation == Orientation.DOWN){
            currentAnimationAlive = animationsAlive[2];
        } else if(orientation == Orientation.LEFT){
            currentAnimationAlive = animationsAlive[3];
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() {
        return !dead;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor)v).interactWith(this);
    }

    protected void randomMove(boolean wantsInactionPossibility) {
        final double PROBABILITY_TO_CHANGE_DIRECTION = 0.25;
        final double PROBABILITY_TO_GO_INACTIVE = 0.05;

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
            move(ALIVE_ANIMATION_DURATION);
            animate(getOrientation());
        } else if (wantsInactionPossibility) { //The flameSkull doesn't want an inaction time but the darkLord and the logMonster want it
            inactive = true;
            inactiveTimeLeft = RandomGenerator.getInstance().nextFloat() * MAX_INACTIVE_DURATION;
        }
    }

    @Override
    public void update(float deltaTime) {
        inactiveTimeLeft -= deltaTime;

        if (hp <= 0) {
            dead = true;
        }

        if (inactiveTimeLeft <= 0) {
            inactive = false;
        }

        if (!dead) {
            if (forceAnimation || isDisplacementOccurs()) { //forceAnimation is a way to force the current animation to update even if the monster does not move. Useful in LogMonster.java and DarkLord.java
                currentAnimationAlive.update(deltaTime);
            } else {
                currentAnimationAlive.reset();
            }
        } else {
            if (deadAnimation.isCompleted()) {
                getOwnerArea().unregisterActor(this);
                spawnCollectables();
            } else {
                deadAnimation.update(deltaTime);
            }
        }

        super.update(deltaTime);
    }

    abstract protected void spawnCollectables();

    public enum Vulnerability {
        PHYSICAL,
        FIRE,
        MAGIC;
    }
}
