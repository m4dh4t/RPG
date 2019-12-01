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
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Grass extends AreaEntity {
    private final static int SLICE_DURATION = 8;
    private final static int BURN_DURATION = 8;
    private Sprite sprite;
    private Animation sliceAnimation;
    private Animation burnAnimation;


    private boolean sliced;
    private boolean burnt;


    public Grass(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);
        sprite = new RPGSprite("zelda/grass", 1.f,1.f,this,new RegionOfInterest(0,0,16,16));


        Sprite[] sliceSprites = RPGSprite.extractSprites("zelda/grass.sliced",4,2.f,2.f,this,32,32);
        sliceAnimation = new Animation(SLICE_DURATION, sliceSprites, false);

        Sprite[] burnSprites = RPGSprite.extractSprites("zelda/fire",4,1.f,1.f,this,16,16);
        burnAnimation = new Animation(BURN_DURATION, burnSprites, false);
    }

    public static Grass[] grassZone(Area area, int xBegin, int xEnd, int yBegin, int yEnd){
        Grass[] grasses = new Grass[(xEnd-xBegin+1)*(yEnd-yBegin+1)];
        int grassesIndex = 0;

        for(int i = xBegin; i <= xEnd; i++){
            for(int j = yBegin; j <= yEnd; j++){
                grasses[grassesIndex] = new Grass(area, new DiscreteCoordinates(i, j));
                ++grassesIndex;
            }
        }

        return grasses;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!sliced && !burnt) {
            sprite.draw(canvas);
        } else {
            if (sliced && !sliceAnimation.isCompleted()) {
                sliceAnimation.draw(canvas);
            } else if (burnt && !burnAnimation.isCompleted()) {
                burnAnimation.draw(canvas);
            }
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() {
        return !sliced && !burnt;
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

    void burn() {
        burnt = true;
    }

    @Override
    public void update(float deltaTime) {
        if (sliced) {
            sliceAnimation.update(deltaTime);
        } else if (burnt) {
            burnAnimation.update(deltaTime);
        }
    }
}
