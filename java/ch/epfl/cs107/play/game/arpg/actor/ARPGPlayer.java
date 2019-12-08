package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.ARPGItem;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.InventoryItem;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.game.rpg.actor.Inventory;
import ch.epfl.cs107.play.game.rpg.actor.Player;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import static ch.epfl.cs107.play.game.arpg.ARPGItem.*;

public class ARPGPlayer extends Player implements Inventory.Holder{
    private final static int ANIMATION_DURATION = 4; //DEFAULT: 8
    private Animation[] animations;
    private Animation currentAnimation;

    private ARPGPlayerHandler handler;

    private ARPGPlayerStatusGUI gui;
    private TextGraphics message;
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
        handler = new ARPGPlayerHandler();

        hp = 10;
        message = new TextGraphics(Integer.toString((int)hp), 0.4f, Color.BLUE);
        message.setParent(this);
        message.setAnchor(new Vector(-0.3f, 0.1f));

        Sprite[][] sprites = RPGSprite.extractSprites("zelda/player", 4, 1, 2, this, 16, 32, new Orientation[] {Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT});
        animations = RPGSprite.createAnimations(ANIMATION_DURATION/2, sprites);
        currentAnimation = animations[2];
        gui = new ARPGPlayerStatusGUI();

        inventory = new ARPGInventory(50);
        inventory.addItem(BOMB, 5);
        inventory.addItem(SWORD, 1);
        currentItem = inventory.nextItem();

        resetMotion();
    }

    private void moveOrientate(Orientation orientation, Button button) {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        Button orientationKey = keyboard.get(Orientation.getCode(getOrientation()));

        if (button.isDown()) {
            if (getOrientation() == orientation) {
                move(ANIMATION_DURATION);
                animate(orientation);
            } else if (!isDisplacementOccurs() && !orientationKey.isDown()) {
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

    private void inventoryHandler(){
        final Button TAB = getOwnerArea().getKeyboard().get(Keyboard.TAB);
        final Button SPACE = getOwnerArea().getKeyboard().get(Keyboard.SPACE);

        if(TAB.isPressed()){
            currentItem = inventory.nextItem();
        }

        if(SPACE.isPressed() && possess(currentItem)){
            if(currentItem.use(getOwnerArea(), getCurrentMainCellCoordinates(), getOrientation())){
                if(currentItem == BOMB){
                    inventory.removeItem(currentItem, 1);
                }
            }
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
        return getOwnerArea().getKeyboard().get(Keyboard.E).isDown();
    }

    private boolean isWeak() {
        return (hp <= 0.f);
    }

    public void strengthen() {
        hp = 10;
    }

    public void damage(ARPGItem item){
        if(item == BOMB){
            hp -= 3.f;
            if(isWeak()){
                hp = 0.f;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        currentAnimation.draw(canvas);
        gui.drawGUI(canvas, hp, currentItem, inventory.getMoney());

        //FOR MANUAL HP STATUS CHECK
        //message.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        message.setText(Integer.toString((int)hp));

        Keyboard keyboard = getOwnerArea().getKeyboard();
        moveOrientate(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
        moveOrientate(Orientation.UP, keyboard.get(Keyboard.UP));
        moveOrientate(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
        moveOrientate(Orientation.DOWN, keyboard.get(Keyboard.DOWN));

        inventoryHandler();

        for(int i = 0; i < 4; i++) {
            if (isDisplacementOccurs()) {
                animations[i].update(deltaTime);
            } else {
                animations[i].reset();
            }
        }

        super.update(deltaTime);
    }

    @Override
    public boolean possess(InventoryItem inventoryItem) {
        return inventory.isStocked(inventoryItem);
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
            coin.collect();
            inventory.addMoney(50);
        }

        @Override
        public void interactWith(Heart heart) {
            heart.collect();
            hp += 2;
        }
    }
}
