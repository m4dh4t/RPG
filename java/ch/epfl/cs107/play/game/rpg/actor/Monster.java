package ch.epfl.cs107.play.game.rpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public abstract class Monster extends MovableAreaEntity implements Interactor, InvincibleEntity {
    private final static int DEAD_ANIMATION_DURATION = 6;
    private final static int ALIVE_ANIMATION_DURATION = 8;
    private final static float MAX_INACTIVE_DURATION = 2.f;
    private final static float BLINK_DURATION = 0.1f;

    //"STATES"
    private float hp;
    private boolean dead;
    private boolean inactive;
    private float inactiveTimeLeft;

    private List<Vulnerability> vulnerabilities;

    //ANIMATIONS
    private Animation[] animationsAlive;
    private Animation currentAnimationAlive;
    private Animation deadAnimation;

    /*
    forceAnimation has been created because by default, if a monster does not
    move we do not want its animations to update because we want it to stay
    still if he does not move. But in certain cases (f. ex. when the LogMonster
    is sleeping or waking up) we want the animation to update even if the
    monster is standing still. That is why we created this attribute and
    the method associated setForceAnimation(boolean bool) to set it as we want.
     */
    private boolean forceAnimation;

    //EXTENSIONS
    private boolean invincible;
    private float invincibleTimeLeft;
    private boolean showAnimations; //used to make the sprite blink when the player is invincible
    private float blinkTimeLeft;

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

        invincible = false;
        invincibleTimeLeft = INVICIBILITY_DURATION;
        blinkTimeLeft = BLINK_DURATION;
        showAnimations = true;

        this.vulnerabilities = vulnerabilities;
        forceAnimation = false;

        Sprite[][] aliveSprites = RPGSprite.extractSprites(spriteName, nbFrames, 2.f,2.f, this, 32, 32, new Vector(-0.5f, 0.f),orientations);
        animationsAlive = RPGSprite.createAnimations(ALIVE_ANIMATION_DURATION/2, aliveSprites);
        currentAnimationAlive = animationsAlive[orientation.ordinal()];

        Sprite[] deadSprites = RPGSprite.extractSprites("zelda/vanish", 7, 2.f, 2.f, this, 32, 32, new Vector(-0.5f,0.f));
        deadAnimation = new Animation(DEAD_ANIMATION_DURATION/2, deadSprites, false);
    }

    /*Methods to manage the animations. We wanted that the animations were managed here in Monster.java to prevent code
    duplication but monsters like LogMonster and DarkLord need to have their animations changed and this is why there
    are these methods
    */
    protected static int getAnimationDuration() { return ALIVE_ANIMATION_DURATION; }
    protected void setAnimations(Animation[] animations, Orientation orientation) {
        animationsAlive = animations;
        currentAnimationAlive = animationsAlive[orientation.ordinal()];
    }
    protected boolean isAnimationCompleted() { return currentAnimationAlive.isCompleted(); }
    protected void setForceAnimation(boolean bool) { forceAnimation = bool; }

    public List<Vulnerability> getVulnerabilities() {
        return vulnerabilities;
    }

    /**
     * Method used to weaken the monster with a certain vulnerability. If the monster is vulnerable to the vulnerability,
     * it takes damage and does not otherwise.
     * @param damage (float): The damage we want to do to the monster.
     * @param vulnerability (Vulnerability): The type of the attack.
     */
    public void weaken(float damage, Vulnerability vulnerability) {
        if (!isInvincible() && vulnerabilities.contains(vulnerability)) {
            hp -= damage;
            invincible = true;
        }
    }

    //Implements InvincibleEntity.java
    @Override
    public boolean isInvincible() {
        return invincible;
    }

    //Methods to make the monster die and to check if it is actually dead or not
    protected void die() {
        dead = true;
    }
    protected boolean isDead() {
        return dead;
    }

    /*To avoid code duplication we wanted to put the methods related to inaction time here in Monster.java
    To manage these inaction times, these methods need to be implemented
    */
    protected boolean isInactive() { return inactive; }
    protected void setInactive(boolean bool) { inactive = bool; }
    protected void setInactiveTimeLeft(float time) { inactiveTimeLeft = time; }

    @Override
    public void draw(Canvas canvas) {
        if (!dead) {
            if (showAnimations) { //Used for blink
                currentAnimationAlive.draw(canvas);
            }
        } else {
            deadAnimation.draw(canvas); // We don't want to check if we need to show the animations here because we don't want the death animation to blink
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

    /**
     * This method is useful because it is needed in LogMonster.java, DarkLord.java and FlameSkull.java. It makes the
     * monster move randomly with 2 constants defined locally.
     * @param wantsInactionPossibility (boolean): The log monster and the dark lord want to have a possibility to have
     *                                 an inaction time whereas flame skulls do not. We make this distinction here, in
     *                                 the argument of the method.
     */
    protected void randomMove(boolean wantsInactionPossibility) {
        final double PROBABILITY_TO_CHANGE_DIRECTION = 0.2;
        final double PROBABILITY_TO_GO_INACTIVE = 0.05;

        if (RandomGenerator.getInstance().nextDouble() >= PROBABILITY_TO_GO_INACTIVE) {
            if (RandomGenerator.getInstance().nextDouble() < PROBABILITY_TO_CHANGE_DIRECTION) {
                switch (RandomGenerator.getInstance().nextInt(4)) {
                    case 0:
                        orientate(Orientation.LEFT);
                        break;
                    case 1:
                        orientate(Orientation.UP);
                        break;
                    case 2:
                        orientate(Orientation.RIGHT);
                        break;
                    case 3:
                        orientate(Orientation.DOWN);
                        break;
                }
            }
            currentAnimationAlive = animationsAlive[getOrientation().ordinal()];
            move(ALIVE_ANIMATION_DURATION);
        } else if (wantsInactionPossibility) {
            inactive = true;
            inactiveTimeLeft = RandomGenerator.getInstance().nextFloat() * MAX_INACTIVE_DURATION;
        }
    }

    @Override
    public void update(float deltaTime) {
        if (isInvincible()) {
            invincibleTimeLeft -= deltaTime;
            blinkTimeLeft -= deltaTime;

            if (blinkTimeLeft <= 0) {
                showAnimations = !showAnimations; //Blink
                blinkTimeLeft = BLINK_DURATION; //resets blinkTimeLeft
            }

            if (invincibleTimeLeft <= 0) {
                invincible = false; //not invincible anymore
                invincibleTimeLeft = INVICIBILITY_DURATION; //resets time left
                showAnimations = true; //Make sure the player is not invisible at the end of the blink
            }
        }

        inactiveTimeLeft -= deltaTime;

        if (hp <= 0) {
            dead = true;
        }

        if (inactiveTimeLeft <= 0) {
            inactive = false;
        }

        if (!dead) {
            //forceAnimation is a way to force the current animation to update even if the monster does not move.
            // Useful in LogMonster.java and DarkLord.java
            if (forceAnimation || isDisplacementOccurs()) {
                currentAnimationAlive.update(deltaTime);
            } else {
                for (int i = 0; i < Orientation.values().length; ++i) {
                    animationsAlive[i].reset();
                }
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

    //This method is needed to make sure the monster spawn a collectable when he dies (See line 244). If the monster
    //does not spawn a collectable (like the flame skull), it is simply overridden but has no body
    abstract protected void spawnCollectables();

    public enum Vulnerability {
        PHYSICAL,
        FIRE,
        MAGIC;
    }
}
