package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class WhiteHalo extends AreaEntity {
    private final static int ANIMATION_DURATION = 3;
    private Animation animation;

    /**
     * WhiteHalo constructor
     * This actor is just used whenever the player enter or left the Paradise Area.
     *
     * @param area        (Area): Owner area. Not null
     */
    public WhiteHalo(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);

        //We construct an animation where the alpha value slowly decrease to make it fade away
        Sprite[] haloSprites = new Sprite[20];
        for(int i = 0; i < haloSprites.length; i++){
            haloSprites[i] = new RPGSprite("whiteHalo", 100, 100, this, null, new Vector(-10f, -10f), 1-(i*0.05f), 9999);
        }
        animation = new Animation(ANIMATION_DURATION, haloSprites, false);
    }

    @Override
    public void draw(Canvas canvas) {
        if(!animation.isCompleted()){
            animation.draw(canvas);
        }
    }

    @Override
    public void update(float deltaTime) {
        if(!animation.isCompleted()){
            animation.update(deltaTime);
        } else {
            getOwnerArea().unregisterActor(this);
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() {
        return false;
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
}
