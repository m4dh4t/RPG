package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.ARPGItem;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Chest extends AreaEntity {
    private final static int ANIMATION_DURATION = 4;
    private Sprite spriteClosed;
    private Sprite spriteOpened;
    private Animation animation;
    private boolean opened;

    private ARPGInventory inventory;
    /**
     * Chest constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public Chest(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);
        opened = false;
        inventory = new ARPGInventory(0);
        inventory.add(ARPGItem.STAFF, 1);

        Sprite[] sprites = new Sprite[4];

        sprites[0] = new Sprite("chest", 2.f, 2.f, this, new RegionOfInterest(0, 192, 48, 48), new Vector(-0.5f, 0.f), 1.f, -15);;
        sprites[1] = new Sprite("chest", 2.f, 2.f, this, new RegionOfInterest(0, 240, 48, 48), new Vector(-0.5f, 0.f), 1.f, -15);
        sprites[2] = new Sprite("chest", 2.f, 2.f, this, new RegionOfInterest(0, 288, 48, 48), new Vector(-0.5f, 0.f), 1.f, -15);
        sprites[3] = new Sprite("chest", 2.f, 2.f, this, new RegionOfInterest(0, 336, 48, 48), new Vector(-0.5f, 0.f), 1.f, -15);

        spriteClosed = sprites[0];
        spriteOpened = sprites[3];

        animation = new Animation(ANIMATION_DURATION, sprites, false);
    }

    public void open(ARPGInventory otherInventory) {
        opened = true;

        for (int i = 0; i < ARPGItem.values().length; ++i) {
            ARPGItem item = ARPGItem.values()[i];
            if (inventory.isInInventory(item)) {
                otherInventory.add(item, inventory.howMany(item));
                inventory.remove(item, inventory.howMany(item));
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (opened) {
            if (animation.isCompleted()) {
                spriteOpened.draw(canvas);
            } else {
                animation.draw(canvas);
            }
        } else {
            spriteClosed.draw(canvas);
        }
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
        return false;
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
        if (opened) {
            animation.update(deltaTime);
        }
    }
}
