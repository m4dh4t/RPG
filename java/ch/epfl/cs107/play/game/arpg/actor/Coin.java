package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class Coin extends ARPGCollectableAreaEntity {
    private final static int SPIN_DURATION = 4;
    private Animation animation;

    /**
     * Default AreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public Coin(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);

        Sprite[] sprites = RPGSprite.extractSprites("zelda/coin",4,1,1,this,16,16);
        animation = new Animation(SPIN_DURATION, sprites);
    }

    @Override
    public void draw(Canvas canvas) {
        if (!pickedUp()) {
            animation.draw(canvas);
        } else {
            getOwnerArea().unregisterActor(this);
        }
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor)v).interactWith(this);
    }

    @Override
    public void update(float deltaTime) {
        animation.update(deltaTime);
    }
}
