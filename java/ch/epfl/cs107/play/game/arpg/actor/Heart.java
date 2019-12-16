package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;


public class Heart extends CollectableAreaEntity {
    private final static int SPIN_DURATION = 2;
    private Animation animation;

    /**
     * Heart constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public Heart(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);

        Sprite[] sprites = RPGSprite.extractSprites("zelda/heart",4,1.f,1.f,this,16,16);
        animation = new Animation(SPIN_DURATION, sprites);
    }

    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
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
