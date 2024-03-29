package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
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

public class Grass extends AreaEntity implements Interactor {
    private final static int CUT_DURATION = 4;
    private final static int BURN_DURATION = 7;
    private final static double PROBABILITY_TO_DROP_ITEM = 0.5;
    private final static double PROBABILITY_TO_DROP_HEART = 0.5;

    private Sprite sprite;
    private Animation cutAnimation;
    private Animation burnAnimation;
    private Animation currentAnimation;

    private GrassHandler handler;

    private boolean cut;
    private boolean burnt;


    public Grass(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);

        handler = new GrassHandler();
        cut = false;
        burnt = false;

        sprite = new RPGSprite("zelda/grass", 1.f,1.f,this, new RegionOfInterest(0,0,16,16), Vector.ZERO, 1.f, -1);


        Sprite[] cutSprites = RPGSprite.extractSprites("zelda/grass.sliced",4,2.f,2.f,this,32,32);
        cutAnimation = new Animation(CUT_DURATION, cutSprites, false);

        Sprite[] burnSprites = RPGSprite.extractSprites("zelda/fire",7,1.f,1.f,this,16,16);
        burnAnimation = new Animation(BURN_DURATION, burnSprites, false);

        currentAnimation = cutAnimation;
    }

    /**
     * This method is useful if we want to create a zone of grass, like in Road.
     * @param area (Area): The area in which the grassZone will be implemented.
     * @param xBegin (int): The x-coordinate where the zone begins.
     * @param xEnd (int): The x-coordinate where the zone ends.
     * @param yBegin (int): The y-coordinate where the zone begins.
     * @param yEnd (int): The y-coordinate where the zone ends.
     * @return (Grass[]): An array of grass to be registered.
     */
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
        } else if (!currentAnimation.isCompleted()){
            //Prevents from seeing the first frame of the animation when it is actually completed
            currentAnimation.draw(canvas);
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return null;
    }

    //The grass will burn the player if he steps on it
    @Override
    public boolean wantsCellInteraction() {
        return burnt;
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    @Override
    public boolean takeCellSpace() {
        return !cut && !burnt;
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
     * cut() can be called to cut the grass and possibly drop an item.
     */
    public void cut() {
        currentAnimation = cutAnimation;
        //Checks if the grass is cut to prevent the player to spam cutting the grass while it is in the cut
        //animation to maximize his chances to drop an item.
        if (!cut) {
            dropItem();
        }
        cut = true;
    }

    /**
     * Returns if the grass is cut.
     * @return (boolean): If the grass is cut.
     */
    public boolean isCut() {
        return cut;
    }

    /**
     * Method to drop an item.
     */
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

    /**
     * burn() can be called to burn the grass.
     */
    public void burn() {
        currentAnimation = burnAnimation;
        burnt = true;
    }

    /**
     * Returns if the grass is burnt.
     * @return (boolean): If the grass is burnt.
     */
    public boolean isBurnt() {
        return burnt;
    }

    /**
     * Method to extinguish the grass if burning.
     */
    public void extinguish() {
        if (isBurnt()) { //Cannot be extinguished if not burnt
            getOwnerArea().unregisterActor(this);
        }
    }

    @Override
    public void update(float deltaTime) {
        if(cut || burnt){
            currentAnimation.update(deltaTime);
        }

        if (currentAnimation.isCompleted()) {
            getOwnerArea().unregisterActor(this);
        }
    }

    private class GrassHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(ARPGPlayer player) {
            player.weaken(1.f);
        }
    }
}
