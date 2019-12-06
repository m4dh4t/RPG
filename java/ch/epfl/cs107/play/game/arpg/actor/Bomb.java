package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.ARPGItem;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Bomb extends AreaEntity implements Interactor {
    private final static int EXPLOSION_DURATION = 2;
    private Sprite sprite;
    private Animation animation;

    private BombHandler handler;

    private float timer;
    private boolean exploded;
    private boolean didDamage;

    /**
     * Default AreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public Bomb(Area area,  DiscreteCoordinates position, int timer) {
        super(area, Orientation.DOWN, position);

        this.timer = timer;
        exploded = false;
        handler = new BombHandler();

        sprite = new RPGSprite("zelda/bomb", 1, 1, this, new RegionOfInterest(0, 0, 16, 16));

        Sprite[] sprites = RPGSprite.extractSprites("zelda/explosion", 7, 1, 1, this, 32, 32);
        animation = new Animation(EXPLOSION_DURATION, sprites, false);
        didDamage = false;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!exploded) {
            sprite.draw(canvas);
        } else {
            if (!animation.isCompleted()) {
                animation.draw(canvas);
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
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {

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
                exploded = true;
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
            if(!didDamage) {
                player.damage(ARPGItem.BOMB);
                didDamage = true;
            }
        }
    }
}
