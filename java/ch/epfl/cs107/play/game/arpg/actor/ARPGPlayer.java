package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.ARPGItem;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.*;
import ch.epfl.cs107.play.game.rpg.InventoryItem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.Collections;
import java.util.List;

public class ARPGPlayer extends Player implements Inventory.Holder, FlyableEntity, InvincibleEntity {
    private final static float MAX_HP = 10.f;
    private final static int DEFAULT_ANIMATION_DURATION = 8;
    private final static float BLINK_DURATION = 0.1f;
    private final static float COOLDOWN = 0.75f;

    private int animation_duration;
    private Sprite[][] idleSprites;
    private Animation[] idleAnimations;
    private Animation[] swordAnimations;
    private Animation[] bowAnimations;
    private Animation[] staffAnimations;
    private Animation currentAnimation;
    private boolean animateAction;

    private ARPGPlayerHandler handler;
    private ARPGPlayerStatusGUI statusGUI;

    private float hp;

    private ARPGInventory inventory;
    private ARPGItem currentItem;
    private float actionTimer;

    private boolean canFly;
    private boolean invincible;
    private float invincibleTimeLeft;
    private boolean showAnimations; //used to make the sprite blink when the player is invincible
    private float blinkTimeLeft;

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
        inventory.add(ARPGItem.BOW, 1);
        inventory.add(ARPGItem.SWORD, 10);
        inventory.add(ARPGItem.STAFF,1);
        inventory.add(ARPGItem.ARROW,10);
        inventory.add(ARPGItem.WINGS,1);
        currentItem = ARPGItem.BOMB;

        hp = MAX_HP;

        canFly = false;
        invincible = false;
        invincibleTimeLeft = INVICIBILITY_DURATION;
        blinkTimeLeft = BLINK_DURATION;
        showAnimations = true;

        animation_duration = DEFAULT_ANIMATION_DURATION;

        handler = new ARPGPlayerHandler();
        statusGUI = new ARPGPlayerStatusGUI();

        idleSprites = RPGSprite.extractSprites("zelda/player", 4, 1, 2, this, 16, 32, new Orientation[] {Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT});
        idleAnimations = RPGSprite.createAnimations(DEFAULT_ANIMATION_DURATION/2, idleSprites);

        Sprite[][] swordSprites = RPGSprite.extractSprites("zelda/player.sword", 4, 2, 2, this, 32, 32, new Vector(-0.5f,0.f), new Orientation[] {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
        swordAnimations = RPGSprite.createAnimations(DEFAULT_ANIMATION_DURATION/2, swordSprites, false);

        Sprite[][] bowSprites = RPGSprite.extractSprites("zelda/player.bow", 4, 2, 2, this, 32, 32, new Vector(-0.5f,0.f), new Orientation[] {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
        bowAnimations = RPGSprite.createAnimations(DEFAULT_ANIMATION_DURATION/2, bowSprites, false);

        Sprite[][] staffSprites = RPGSprite.extractSprites("zelda/player.staff_water", 4, 2, 2, this, 32, 32, new Vector(-0.5f,0.f), new Orientation[] {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
        staffAnimations = RPGSprite.createAnimations(DEFAULT_ANIMATION_DURATION/2, staffSprites, false);

        currentAnimation = idleAnimations[getOrientation().ordinal()];
        animateAction = false;
        actionTimer = 2.f;

        resetMotion();
    }

    private void moveOrientate(Orientation orientation, Button button) {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        Button orientationKey = keyboard.get(Orientation.getCode(getOrientation()));

        if (button.isDown() && !animateAction) {
            if (getOrientation() == orientation) {
                move(animation_duration);
            } else if (!isDisplacementOccurs() && !orientationKey.isDown()) { //Prevents the player from orientating if the key which corresponds to its orientation is down
                orientate(orientation);
                currentAnimation = idleAnimations[orientation.ordinal()];
            }
        }
    }

    @Override
    public boolean canFly() {
        return canFly;
    }

    @Override
    public boolean isInvincible() {
        return invincible;
    }

    private void inventoryHandler(){
        final Button TAB = getOwnerArea().getKeyboard().get(Keyboard.TAB);
        final Button SPACE = getOwnerArea().getKeyboard().get(Keyboard.SPACE);

        if(TAB.isPressed()){
            currentItem = (ARPGItem) inventory.switchItem(currentItem);
            if(currentItem == ARPGItem.ARROW){
                currentItem = (ARPGItem) inventory.switchItem(currentItem);
            }
        }

        if(SPACE.isPressed() && !isDisplacementOccurs()){
            if(currentItem.use(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates())){
                switch(currentItem){
                    case BOMB:
                        inventory.remove(currentItem, 1);
                        break;
                    case BOW:
                        if(actionTimer >= COOLDOWN) {
                            animateAction = true;
                            currentAnimation = bowAnimations[getOrientation().ordinal()];
                            if (possess(ARPGItem.ARROW)) {
                                if (ARPGItem.ARROW.use(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates())) {
                                    inventory.remove(ARPGItem.ARROW, 1);
                                }
                            }
                            actionTimer = 0.f;
                        }
                        break;
                    case SWORD:
                        animateAction = true;
                        currentAnimation = swordAnimations[getOrientation().ordinal()];
                        break;
                    case STAFF:
                        animateAction = true;
                        currentAnimation = staffAnimations[getOrientation().ordinal()];
                        break;
                }

                if(!possess(currentItem)) {
                    currentItem = (ARPGItem) inventory.switchItem(currentItem);
                }
            }
        }

        if (currentItem == ARPGItem.WINGS && SPACE.isDown()) {
            canFly = true;
            currentAnimation = new Animation(animation_duration, idleSprites[getOrientation().ordinal()]); //Calling this method at each update will stuck the animation
            currentAnimation.setFrame(1); //Setting the frame to 1 will make the player look like he's flying (with an arm in front of him and not standing still, which we would have with position 0 by default)
        } else {
            if (canFly) {
                currentAnimation.reset(); //This handles the case where the player stops pressing SPACE but doesn't move. Without this condition he would still have his arm in front of him.
            }
            canFly = false;
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

    public void strengthen(float hp) {
        this.hp += hp;
        if (this.hp > MAX_HP) {
            this.hp = MAX_HP;
        }
    }

    public void weaken(float hit) {
        if (!isInvincible()) {
            hp -= hit;
            invincible = true;
            if (hp < 0) {
                hp = 0;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (showAnimations) { //Used for blink
            currentAnimation.draw(canvas);
        }
        statusGUI.drawGUI(canvas, hp, currentItem, inventory.getMoney());
    }

    @Override
    public void update(float deltaTime) {
        if (isInvincible()) {
            invincibleTimeLeft -= deltaTime;
            blinkTimeLeft -= deltaTime;

            if (blinkTimeLeft <= 0) {
                showAnimations = !showAnimations; //Blink
                blinkTimeLeft = BLINK_DURATION; //resets blinkTimeLeft
            }

            if (invincibleTimeLeft <= 0) {
                invincible = false; //not invincible anymore
                invincibleTimeLeft = INVICIBILITY_DURATION; //resets time left
                showAnimations = true; //Make sure the player is not invisible at the end of the blink
            }
        }

        Keyboard keyboard = getOwnerArea().getKeyboard();
        if (keyboard.get(Keyboard.Q).isDown()) { //Checks if Q is pressed to allow the player to sprint
            animation_duration = DEFAULT_ANIMATION_DURATION/2;
            for (int i = 0; i < Orientation.values().length; ++i) {
                idleAnimations[i].setSpeedFactor(2);
            }
        } else {
            animation_duration = DEFAULT_ANIMATION_DURATION;
            for (int i = 0; i < Orientation.values().length; ++i) {
                idleAnimations[i].setSpeedFactor(1);
            }
        }

        moveOrientate(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
        moveOrientate(Orientation.UP, keyboard.get(Keyboard.UP));
        moveOrientate(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
        moveOrientate(Orientation.DOWN, keyboard.get(Keyboard.DOWN));

        inventoryHandler();
        actionTimer += deltaTime;

        if (isDisplacementOccurs()) {
            idleAnimations[getOrientation().ordinal()].update(deltaTime);
        } else {
            for (int i = 0; i < idleAnimations.length; i++) {
                idleAnimations[i].reset();
            }

            if (animateAction) {
                if (!currentAnimation.isCompleted()) {
                    currentAnimation.update(deltaTime);
                } else {
                    animateAction = false;
                    currentAnimation.reset();
                    currentAnimation = idleAnimations[getOrientation().ordinal()];
                }
            }
        }


        super.update(deltaTime);
    }

    @Override
    public boolean possess(InventoryItem item) {
        return inventory.isInInventory(item);
    }

    private class ARPGPlayerHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(Door door) {
            if(door instanceof CastleDoor){
                if(door.isOpen()){
                    setIsPassingADoor(door);
                    ((CastleDoor) door).close();
                } else {
                    if(possess(ARPGItem.CASTLEKEY)){
                        ((CastleDoor) door).open();
                    }
                }
            } else {
                setIsPassingADoor(door);
            }
        }

        @Override
        public void interactWith(Coin coin) {
            coin.collect();
            inventory.addMoney(50);
        }

        @Override
        public void interactWith(Heart heart) {
            heart.collect();
            strengthen(2.f);
        }

        @Override
        public void interactWith(CastleKey castleKey) {
            castleKey.collect();
            inventory.add(ARPGItem.CASTLEKEY, 1);
        }

        @Override
        public void interactWith(Chest chest) {
            chest.open(inventory);
        }
    }
}
