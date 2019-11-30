package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bomb extends AreaEntity implements Interactor {
    private final static int ANIMATION_DURATION = 4;
    private int timer;
    private boolean exploded;
    private BombHandler handler;
    private Sprite sprite;
    private Animation animation;

    public Bomb(Area area, Orientation orientation, DiscreteCoordinates position, int timer) {
        super(area, orientation, position);
        this.timer = timer;
        handler = new BombHandler();
        sprite = new Sprite("zelda/bomb", 1.f, 1.f, this);
        Sprite[][] sprites = RPGSprite.extractSprites("zelda/explosion",7,1.f,1.f,this,32,32,new Orientation[] {Orientation.UP,Orientation.RIGHT,Orientation.DOWN,Orientation.LEFT});
        Animation[] animations = RPGSprite.createAnimations(ANIMATION_DURATION, sprites, false);
        animation = animations[0];
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
        List<DiscreteCoordinates> list = new ArrayList<>() {
        };

        for (int i = getCurrentMainCellCoordinates().x - 1; i <= getCurrentMainCellCoordinates().x + 1; ++i) {
            for (int j = getCurrentMainCellCoordinates().y - 1; j <= getCurrentMainCellCoordinates().y + 1; ++j) {
                list.add(new DiscreteCoordinates(i,j));
            }
        }

        return list;
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
        --timer;
        if (timer <= 0) {
            exploded = true;
            animation.update(deltaTime);
        }
    }

    private class BombHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(Grass grass) {
            grass.slice();
        }
    }
}
