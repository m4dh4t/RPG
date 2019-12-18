package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.ARPGItem;
import ch.epfl.cs107.play.game.arpg.area.Paradise;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.*;
import ch.epfl.cs107.play.game.rpg.InventoryItem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.Collections;
import java.util.List;

public class ARPGPlayer extends Player implements Inventory.Holder, FlyableEntity, InvincibleEntity {
    //CONSTANTS
    private final static float MAX_HP = 10.f;
    private final static int DEFAULT_ANIMATION_DURATION = 8;
    private final static int BOW_ANIMATION_DURATION = 3;
    private final static int SWORD_ANIMATION_DURATION = 1;
    private final static int STAFF_ANIMATION_DURATION = 2;
    private final static int DEATH_ANIMATION_DURATION = 2;
    private final static float BLINK_DURATION = 0.1f;
    private final static float COOLDOWN = 0.75f;

    //ANIMATIONS
    private int animation_duration;
    private Animation[] idleAnimations;
    private Animation[] swordAnimations;
    private Animation[] bowAnimations;
    private Animation[] staffAnimations;
    private Animation[] flyAnimations;
    private Animation deathAnimation;
    private Animation currentAnimation;
    private boolean animateAction;

    //MISCELLANEOUS
    private ARPGPlayerHandler handler;
    private ARPGPlayerStatusGUI statusGUI;

    //LIFE
    private float hp;

    //INVENTORY
    private ARPGInventory inventory;
    private ARPGItem currentItem;
    private float actionTimer;
    private boolean shootArrow;

    //EXTENSIONS
    private boolean canFly;
    private boolean invincible;
    private float invincibleTimeLeft;
    private boolean showAnimations; //used to make the sprite blink when the player is invincible
    private float blinkTimeLeft;
    private boolean gameOver;
    private boolean quitGame;

    /**
     * ARPGPlayer constructor
     *
     * @param area        (Area): Owner Area, not null
     * @param coordinates (Coordinates): Initial position, not null
     */
    public ARPGPlayer(Area area, DiscreteCoordinates coordinates) {
        super(area, Orientation.DOWN, coordinates);

        inventory = new ARPGInventory(50, 100);
        inventory.add(ARPGItem.BOMB, 3);
        inventory.add(ARPGItem.SWORD, 1);
        currentItem = ARPGItem.SWORD;

        hp = MAX_HP;

        canFly = false;
        invincible = false;
        invincibleTimeLeft = INVICIBILITY_DURATION;
        blinkTimeLeft = BLINK_DURATION;
        showAnimations = true;

        animation_duration = DEFAULT_ANIMATION_DURATION;

        handler = new ARPGPlayerHandler();
        statusGUI = new ARPGPlayerStatusGUI();

        Sprite[][] idleSprites = RPGSprite.extractSprites("zelda/player", 4, 1, 2, this, 16, 32, new Orientation[]{Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT});
        idleAnimations = RPGSprite.createAnimations(DEFAULT_ANIMATION_DURATION/2, idleSprites);

        Sprite[][] swordSprites = RPGSprite.extractSprites("zelda/player.sword", 4, 2, 2, this, 32, 32, new Vector(-0.5f,0.f), new Orientation[] {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
        swordAnimations = RPGSprite.createAnimations(SWORD_ANIMATION_DURATION, swordSprites, false);

        Sprite[][] bowSprites = RPGSprite.extractSprites("zelda/player.bow", 4, 2, 2, this, 32, 32, new Vector(-0.5f,0.f), new Orientation[] {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
        bowAnimations = RPGSprite.createAnimations(BOW_ANIMATION_DURATION, bowSprites, false);

        Sprite[][] staffSprites = RPGSprite.extractSprites("zelda/player.staff_water", 4, 2, 2, this, 32, 32, new Vector(-0.5f,0.f), new Orientation[] {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
        staffAnimations = RPGSprite.createAnimations(STAFF_ANIMATION_DURATION, staffSprites, false);

        Sprite[][] flySprites = RPGSprite.extractSprites("zelda/player.fly", 4, 1,2,this,16,32, new Orientation[] {Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT});
        flyAnimations = RPGSprite.createAnimations(DEFAULT_ANIMATION_DURATION, flySprites);

        Sprite[] deathSprites = new Sprite[20];
        for(int i = 0; i < deathSprites.length; i++){
            deathSprites[i] = new RPGSprite("zelda/player.death", 1, 2, this, new RegionOfInterest(0, 0, 16, 32), new Vector(0, i*0.25f), 1-(i*0.05f), 3001);
        }
        deathAnimation = new Animation(DEATH_ANIMATION_DURATION, deathSprites, false);

        currentAnimation = idleAnimations[getOrientation().ordinal()];
        animateAction = false;
        actionTimer = 2.f;
        shootArrow = false;
        gameOver = false;
        quitGame = false;
    }

    /**
     * This method is used to move or orientate the player
     * in a specific orientation.
     * @param orientation (Orientation): The given orientation
     *                    to move or orientate to.
     * @param button (Button): Checks if the button is down.
     */
    private void moveOrientate(Orientation orientation, Button button) {
        if(!isWeak()) {
            Keyboard keyboard = getOwnerArea().getKeyboard();
            Button orientationKey = keyboard.get(Orientation.getCode(getOrientation()));

            if (button.isDown() && !animateAction) {
                if (getOrientation() == orientation) {
                    move(animation_duration);
                } else if (!isDisplacementOccurs() && !orientationKey.isDown()) { //Prevents the player from orientating if the key which corresponds to its orientation is down
                    orientate(orientation);
                    currentAnimation = canFly ? flyAnimations[orientation.ordinal()] : idleAnimations[orientation.ordinal()];
                }
            }
        }
    }

    /**
     * Implements FlyableEntity.
     * @return (boolean): If the player can fly.
     */
    @Override
    public boolean canFly() {
        return canFly;
    }

    /**
     * Implements InvincibleEntity.
     * @return (boolean): If the player is invincible.
     */
    @Override
    public boolean isInvincible() {
        return invincible;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void restartGame() {
        orientate(Orientation.DOWN);
        currentAnimation = idleAnimations[getOrientation().ordinal()];
        gameOver = false;
        strengthen(MAX_HP);
    }

    public void quitGame() {
        quitGame = true;
    }

    public boolean getQuitGame() {
        return quitGame;
    }

    /**
     * This method is called from update(float deltaTime)
     * to manage the inventory.
     */
    private void inventoryHandler(){
        final Button TAB = getOwnerArea().getKeyboard().get(Keyboard.TAB);
        final Button SPACE = getOwnerArea().getKeyboard().get(Keyboard.SPACE);
        final Button R = getOwnerArea().getKeyboard().get(Keyboard.R);
        final Button T = getOwnerArea().getKeyboard().get(Keyboard.T);
        final Button Y = getOwnerArea().getKeyboard().get(Keyboard.Y);
        final Button U = getOwnerArea().getKeyboard().get(Keyboard.U);
        final Button I = getOwnerArea().getKeyboard().get(Keyboard.I);
        final Button O = getOwnerArea().getKeyboard().get(Keyboard.O);
        final Button P = getOwnerArea().getKeyboard().get(Keyboard.P);
        final Button K = getOwnerArea().getKeyboard().get(Keyboard.K);

        //Used for tests, as asked in the instructions
        if (R.isPressed()) {
            inventory.add(ARPGItem.ARROW, 1);
        } else if (T.isPressed()) {
            inventory.add(ARPGItem.SWORD, 1);
        } else if (Y.isPressed()) {
            inventory.add(ARPGItem.STAFF, 1);
        } else if (U.isPressed()) {
            inventory.add(ARPGItem.BOW, 1);
        } else if (I.isPressed()) {
            inventory.add(ARPGItem.BOMB, 1);
        } else if (O.isPressed()) {
            inventory.add(ARPGItem.CASTLEKEY, 1);
        } else if (P.isPressed()) {
            inventory.add(ARPGItem.WINGS, 1);
        } else if (K.isPressed()) {
            inventory.add(ARPGItem.CHESTKEY, 1);
        }

        /*If the player does not have any currentItem anymore we switch it (f. ex. if a player
        uses his last bomb or if the constructor assigns as the player's current item an item
        he does not possess by mistake)
         */
        if(!possess(currentItem)) {
            currentItem = (ARPGItem) inventory.switchItem(currentItem);
        }

        //Switches item.
        if(TAB.isPressed()){
            currentItem = (ARPGItem) inventory.switchItem(currentItem);
            if(currentItem == ARPGItem.ARROW){
                currentItem = (ARPGItem) inventory.switchItem(currentItem);
            }
        }

        //Uses the item and adapts the animations
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
                            shootArrow = true;
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

            }
        }

        //Wings have a special treatment because SPACE needs to be down and not pressed
        if (currentItem == ARPGItem.WINGS && SPACE.isDown()) {
            //Sets the animation for the current orientation. Make sure it doesn't fly yet
            // so that the animation is not stuck.
            if (!canFly) {
                currentAnimation = flyAnimations[getOrientation().ordinal()];
            }
            canFly = true;
        } else {
            //Resets to idleAnimations
            if (canFly) {
                currentAnimation = idleAnimations[getOrientation().ordinal()];
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

    public boolean isWeak() {
        return (hp <= 0.f);
    }

    /**
     * This method is used to give health points to the player
     * (when he picks up a heart for example).
     * @param hp (float): health points to give.
     */
    private void strengthen(float hp) {
        this.hp += hp;
        if (this.hp > MAX_HP) {
            this.hp = MAX_HP;
        }
    }

    /**
     * This method is used to remove health points from the player.
     * @param hit (float): health points to remove.
     */
    public void weaken(float hit) {
        if (!isInvincible()) {
            hp -= hit;
            invincible = true;
            if (hp < 0) {
                hp = 0;
            }

            if(isWeak()){
                currentAnimation = deathAnimation;
            }
        }
    }

    public void death(){
        if (!getCurrentMainCellCoordinates().equals(new DiscreteCoordinates(11, 9))) {
            move(30);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (showAnimations && currentAnimation != deathAnimation) { //Used for blink
            currentAnimation.draw(canvas);
        }
        //Prevents from seeing the first frame of the death animation when it is actually completed
        if (currentAnimation == deathAnimation && !currentAnimation.isCompleted()) {
            currentAnimation.draw(canvas);
        }

        statusGUI.drawGUI(canvas, hp, currentItem, inventory.getMoney());
    }

    @Override
    public void update(float deltaTime) {
        if (isInvincible() && !isWeak()) {
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
                if (canFly) {
                    if (isDisplacementOccurs()) {
                        flyAnimations[i].setSpeedFactor(2); //We make sure the player is moving to speed up its animation speed so that it does not speed up when not moving
                    } else {
                        flyAnimations[i].setSpeedFactor(1);
                    }
                } else {
                    idleAnimations[i].setSpeedFactor(2);
                }
            }
        } else {
            animation_duration = DEFAULT_ANIMATION_DURATION;
            for (int i = 0; i < Orientation.values().length; ++i) {
                if (canFly) {
                    flyAnimations[i].setSpeedFactor(1);
                } else {
                    idleAnimations[i].setSpeedFactor(1);
                }
            }
        }

        moveOrientate(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
        moveOrientate(Orientation.UP, keyboard.get(Keyboard.UP));
        moveOrientate(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
        moveOrientate(Orientation.DOWN, keyboard.get(Keyboard.DOWN));

        if(isWeak()){
            deathAnimation.update(deltaTime);
            if(currentAnimation.isCompleted()){
                gameOver = true;
                orientate(Orientation.UP);
            }
        }

        if(gameOver){
            death();
            if(currentAnimation == deathAnimation && getOwnerArea() instanceof Paradise) {
                deathAnimation.reset();
                currentAnimation = idleAnimations[getOrientation().ordinal()];
            }
        }

        inventoryHandler();
        actionTimer += deltaTime;

        if (isDisplacementOccurs()) {
            if (canFly) {
                flyAnimations[getOrientation().ordinal()].update(deltaTime);
            } else {
                idleAnimations[getOrientation().ordinal()].update(deltaTime);
            }
        } else {
            if (canFly) {
                //Still want to update the animation if the player is flying even if he does not move
                flyAnimations[getOrientation().ordinal()].update(deltaTime);
            }

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

            if(shootArrow && currentAnimation.isCompleted()){
                if (possess(ARPGItem.ARROW)) {
                    if (ARPGItem.ARROW.use(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates())) {
                        inventory.remove(ARPGItem.ARROW, 1);
                    }
                }
                shootArrow = false;
            }
        }


        super.update(deltaTime);
    }

    /**
     * Implements Inventory.Holder.
     * @param item (InventoryItem): The item we want to know if
     *             it is possessed by the player.
     * @return (boolean): If the player actually possesses the item.
     */
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

        @Override
        public void interactWith(Shop shop, Orientation orientation) {
            //We want that the shopper and the player face at each other so they
            // need to have opposite orientations
            if (getOrientation().opposite().equals(orientation)) {
                shop.shop(inventory);
            }
        }

        @Override
        public void interactWith(King king) {
            king.save();
        }
    }
}
