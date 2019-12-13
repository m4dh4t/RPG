package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Monster;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import static ch.epfl.cs107.play.game.rpg.actor.Monster.Vulnerability;

import java.util.Collections;
import java.util.List;

public class Bomb extends AreaEntity implements Interactor {
    private final static int EXPLOSION_DURATION = 3;
    private Sprite sprite;
    private Sprite redSprite;
    private Animation animation;

    private BombHandler handler;

    private float timer;
    private boolean exploded;

    /**
     * Default AreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public Bomb(Area area, DiscreteCoordinates position, float timer) {
        super(area, Orientation.DOWN, position);

        this.timer = timer;
        exploded = false;
        handler = new BombHandler();

        sprite = new RPGSprite("zelda/bomb", 1, 1, this, new RegionOfInterest(0, 0, 16, 16));
        redSprite = new RPGSprite("zelda/bomb", 1, 1, this, new RegionOfInterest(16, 0, 16, 16));

        Sprite[] sprites = RPGSprite.extractSprites("zelda/explosion", 7, 3, 3, this, 32, 32, new Vector(-1.f,-1.f));
        animation = new Animation(EXPLOSION_DURATION, sprites, false);
    }

    public void explode() {
        exploded = true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!exploded) {
            if(timer <= 1){
                redSprite.draw(canvas);
            } else {
                sprite.draw(canvas);
            }
        } else {
            if (!animation.isCompleted()) {
                animation.draw(canvas);
            } else {
                getOwnerArea().unregisterActor(this);
            }
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() {
        return !exploded;
    }

    @Override
    public boolean isCellInteractable() {
        return !exploded;
    }

    @Override
    public boolean isViewInteractable() {
        return !exploded;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor)v).interactWith(this);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return getCurrentMainCellCoordinates().getNeighbours();
    }

    @Override
    public boolean wantsCellInteraction() {
        return exploded;
    }

    @Override
    public boolean wantsViewInteraction() {
        return exploded;
    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    @Override
    public void update(float deltaTime) {
        if(!exploded){
            timer -= deltaTime;
            if(timer <= 0){
                explode();
            }
        } else {
            animation.update(deltaTime);
        }
    }

    private class BombHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(Grass grass) {
            grass.burn();
        }

        @Override
        public void interactWith(ARPGPlayer player) {
            player.weaken(3);
        }

        @Override
        public void interactWith(Monster monster) {
            monster.weaken(1.f, Vulnerability.PHYSICAL);
        }
    }
}
