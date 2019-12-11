package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.ARPGItem;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Inventory;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.game.rpg.actor.Player;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.Collections;
import java.util.List;

public class ARPGPlayer extends Player implements Inventory.Holder {
    private final static int ANIMATION_DURATION = 3; //DEFAULT: 8
    private final static int MAXHP = 5;
    private Animation[] animations;
    private Animation currentAnimation;

    private ARPGPlayerHandler handler;
    private ARPGPlayerStatusGUI statusGUI;

    private float hp;
    private ARPGInventory inventory;
    private ARPGItem currentItem;

    /**
     * Default Player constructor
     *
     * @param area        (Area): Owner Area, not null
     * @param coordinates (Coordinates): Initial position, not null
     */
    public ARPGPlayer(Area area, DiscreteCoordinates coordinates) {
        super(area, Orientation.DOWN, coordinates);


        inventory = new ARPGInventory(50);
        inventory.add(ARPGItem.BOMB, 3);
        inventory.add(ARPGItem.BOW, 2);
        inventory.add(ARPGItem.SWORD, 10);
        inventory.add(ARPGItem.STAFF,1);
        currentItem = ARPGItem.BOMB;

        hp = MAXHP;

        handler = new ARPGPlayerHandler();
        statusGUI = new ARPGPlayerStatusGUI(inventory.getMoney(), MAXHP, currentItem);

        Sprite[][] sprites = RPGSprite.extractSprites("zelda/player", 4, 1, 2, this, 16, 32, new Orientation[] {Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT});
        animations = RPGSprite.createAnimations(ANIMATION_DURATION/2, sprites);
        currentAnimation = animations[2];

        resetMotion();
    }

    private void moveOrientate(Orientation orientation, Button button) {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        Button orientationKey = keyboard.get(Orientation.getCode(getOrientation()));

        if (button.isDown()) {
            if (getOrientation() == orientation) {
                move(ANIMATION_DURATION);
                animate(orientation);
            } else if (!isDisplacementOccurs() && !orientationKey.isDown()) { //Prevents the player from orientating if the key which corresponds to its orientation is down
                orientate(orientation);
                animate(orientation);
            }
        }
    }

    private void animate(Orientation orientation) {
        if(orientation == Orientation.UP){
            currentAnimation = animations[0];
        } else if(orientation == Orientation.RIGHT){
            currentAnimation = animations[1];
        } else if(orientation == Orientation.DOWN){
            currentAnimation = animations[2];
        } else if(orientation == Orientation.LEFT){
            currentAnimation = animations[3];
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
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
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return getOwnerArea().getKeyboard().get(Keyboard.E).isPressed();
    }

    private boolean isWeak() {
        return (hp <= 0.f);
    }

    public void strengthen(int hp) {
        this.hp += hp;
        if (this.hp > MAXHP) {
            this.hp = MAXHP;
        }
    }

    public void weaken(float hit) {
        hp -= hit;
        if (hp < 0) {
            hp = 0;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        currentAnimation.draw(canvas);
        statusGUI.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        statusGUI.update(inventory.getMoney(),hp,currentItem);

        Keyboard keyboard = getOwnerArea().getKeyboard();
        moveOrientate(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
        moveOrientate(Orientation.UP, keyboard.get(Keyboard.UP));
        moveOrientate(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
        moveOrientate(Orientation.DOWN, keyboard.get(Keyboard.DOWN));

        for(int i = 0; i < 4; i++) {
            if (isDisplacementOccurs()) {
                animations[i].update(deltaTime);
            } else {
                animations[i].reset();
            }
        }

        if (keyboard.get(Keyboard.SPACE).isPressed()) {
            currentItemInteraction();
        }

        if (keyboard.get(Keyboard.TAB).isPressed() || !inventory.isInInventory(currentItem)) {
            switchItem();
        }

        super.update(deltaTime);
    }

    private void switchItem() {
        currentItem = (ARPGItem)inventory.switchItem(currentItem);
    }

    private void currentItemInteraction() {
        if (inventory.remove(currentItem, 1)) {
            if (!currentItem.interaction(getOwnerArea(), getFieldOfViewCells().get(0))) {
                inventory.add(currentItem,1);
            }
        }
    }

    private class ARPGPlayerHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(Door door) {
            setIsPassingADoor(door);
        }

        @Override
        public void interactWith(Grass grass) {
            grass.cut();
        }

        @Override
        public void interactWith(Coin coin) {
            inventory.addMoney(50);
            coin.pickUp();
        }

        @Override
        public void interactWith(Heart heart) {
            strengthen(1);
            heart.pickUp();
        }

        @Override
        public void interactWith(CastleDoor door) {
            if (door.isOpen()) {
                setIsPassingADoor(door);
                door.setSignal(Logic.FALSE);
            } else if (inventory.isInInventory(ARPGItem.CASTLEKEY)) {
                door.setSignal(Logic.TRUE);
            }
        }

        @Override
        public void interactWith(CastleKey key) {
            inventory.add(ARPGItem.CASTLEKEY, 1);
            key.pickUp();
        }
    }
}
