package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Grass extends AreaEntity {
    private final static int ANIMATION_DURATION = 8;
    private RPGSprite sprite;
    private boolean sliced;
    private Animation animation;

    public Grass(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);
        sprite = new RPGSprite("zelda/grass", 1.f,1.f,this,null, Vector.ZERO,1.f,1);
        Sprite[][] sprites = RPGSprite.extractSprites("zelda/grass.sliced",4,2.f,2.f,this,32,32,new Orientation[] {Orientation.UP,Orientation.RIGHT,Orientation.DOWN,Orientation.LEFT});
        Animation[] animations = RPGSprite.createAnimations(ANIMATION_DURATION, sprites, false);
        animation = animations[0];
    }

    @Override
    public void draw(Canvas canvas) {
        if (!sliced) {
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
        return !sliced;
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

    void slice() {
        sliced = true;
    }

    @Override
    public void update(float deltaTime) {
        if (sliced) {
            animation.update(deltaTime);
        }
    }
}
