package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.game.rpg.actor.TriggerableEntity;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Orb extends MovableAreaEntity {
    private final static int ANIMATION_DURATION = 3;
    private TriggerableEntity triggerableEntity;
    private Animation animation;
    /**
     * Orb constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     */
    public Orb(Area area, DiscreteCoordinates position, TriggerableEntity triggerableEntity) {
        super(area, Orientation.DOWN, position);

        Sprite[] sprites = RPGSprite.extractSprites("zelda/orb", 6, 1.f, 1.f, this, 64, 32, 32);
        animation = new Animation(ANIMATION_DURATION, sprites);
        this.triggerableEntity = triggerableEntity;
        getOwnerArea().registerActor(triggerableEntity);
    }

    public void hit() {
        Sprite[] sprites = RPGSprite.extractSprites("zelda/orb", 6, 1.f, 1.f, this, 32, 32, 32);
        animation = new Animation(ANIMATION_DURATION, sprites);
        triggerableEntity.trigger();
    }

    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() {
        return true;
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

    @Override
    public void update(float deltaTime) {
        animation.update(deltaTime);
        super.update(deltaTime);
    }
}
