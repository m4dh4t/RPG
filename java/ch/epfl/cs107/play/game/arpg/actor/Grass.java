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
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Grass extends AreaEntity {
    private final static int CUT_DURATION = 4;
    private final static int BURN_DURATION = 7;
    private final static double PROBABILITY_TO_DROP_ITEM = 0.5;
    private final static double PROBABILITY_TO_DROP_HEART = 0.5;

    private Sprite sprite;
    private Animation cutAnimation;
    private Animation burnAnimation;

    private boolean cut;
    private boolean burnt;


    public Grass(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);

        cut = false;
        burnt = false;

        sprite = new RPGSprite("zelda/grass", 1.f,1.f,this, new RegionOfInterest(0,0,16,16), Vector.ZERO, 1.f, -1);


        Sprite[] cutSprites = RPGSprite.extractSprites("zelda/grass.sliced",4,2.f,2.f,this,32,32);
        cutAnimation = new Animation(CUT_DURATION, cutSprites, false);

        Sprite[] burnSprites = RPGSprite.extractSprites("zelda/fire",7,1.f,1.f,this,16,16);
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
        if (!cut && !burnt) {
            sprite.draw(canvas);
        } else {
            if (cut && !cutAnimation.isCompleted()) {
                cutAnimation.draw(canvas);
            } else if (burnt && !burnAnimation.isCompleted()) {
                burnAnimation.draw(canvas);
            } else if (burnAnimation.isCompleted() || cutAnimation.isCompleted()){
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
        return !cut && !burnt;
    }

    @Override
    public boolean isCellInteractable() {
        return !cut && !burnt;
    }

    @Override
    public boolean isViewInteractable() {
        return !cut && !burnt;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor)v).interactWith(this);
    }

    public void cut() {
        cut = true;
        dropItem();
    }

    private void dropItem(){
        double randomDouble = RandomGenerator.getInstance().nextDouble();

        if(randomDouble < PROBABILITY_TO_DROP_ITEM){
            randomDouble = RandomGenerator.getInstance().nextDouble();
            if(randomDouble < PROBABILITY_TO_DROP_HEART){
                getOwnerArea().registerActor(new Heart(getOwnerArea(), getCurrentMainCellCoordinates()));
            } else {
                getOwnerArea().registerActor(new Coin(getOwnerArea(), getCurrentMainCellCoordinates()));
            }
        }
    }

    public void burn() {
        burnt = true;
    }

    @Override
    public void update(float deltaTime) {
        if (cut) {
            cutAnimation.update(deltaTime);
        } else if (burnt) {
            burnAnimation.update(deltaTime);
        }
    }
}
