package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.Vulnerability;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public abstract class Monster extends MovableAreaEntity implements Interactor {
    private final static int DEAD_ANIMATION_DURATION = 4;
    private final static int ALIVE_ANIMATION_DURATION = 4;
    private final float MAXHP;
    private float hp;
    private boolean dead;

    private List<Vulnerability> vulnerabilities;

    private Animation[] animationsAlive;
    private Animation currentAnimationAlive;
    private Animation deadAnimation;

    /**
     * Monster constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     */
    public Monster(Area area, Orientation orientation, DiscreteCoordinates position, float hpmax, List<Vulnerability> vulnerabilities, String spriteName, int nbFrames, Orientation[] orientations) {
        super(area, orientation, position);
        MAXHP = hpmax;
        hp = hpmax;
        dead = false;
        this.vulnerabilities = vulnerabilities;

        Sprite[][] aliveSprites = RPGSprite.extractSprites(spriteName, nbFrames, 2.f,2.f, this, 32, 32, new Vector(-0.5f, 0.f),orientations);
        animationsAlive = RPGSprite.createAnimations(ALIVE_ANIMATION_DURATION, aliveSprites);
        currentAnimationAlive = animationsAlive[orientation.ordinal()];

        Sprite[] deadSprites = RPGSprite.extractSprites("zelda/vanish", 7, 2.f, 2.f, this, 32, 32, new Vector(-0.5f,0.f));
        deadAnimation = new Animation(DEAD_ANIMATION_DURATION, deadSprites, false);
    }

    public List<Vulnerability> getVulnerabilities() {
        return vulnerabilities;
    }

    public void weaken(float damage, Vulnerability vulnerability) {
        if (vulnerabilities.contains(vulnerability)) {
            hp -= damage;
        }
    }

    public boolean isDead() {
        return dead;
    }

    public void die() {
        dead = true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!dead) {
            currentAnimationAlive.draw(canvas);
        } else {
            if (!deadAnimation.isCompleted()) {
                deadAnimation.draw(canvas);
                spawnCollectables();
            } else {
                getOwnerArea().unregisterActor(this);
            }
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

    @Override
    public void update(float deltaTime) {
        if (hp <= 0) {
            dead = true;
        }

        if (!dead) {
            currentAnimationAlive.update(deltaTime);
        } else {
            deadAnimation.update(deltaTime);
        }

        super.update(deltaTime);
    }

    abstract void spawnCollectables();
}
