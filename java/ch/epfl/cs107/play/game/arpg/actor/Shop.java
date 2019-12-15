package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.arpg.ARPGItem;
import ch.epfl.cs107.play.game.arpg.area.ARPGArea;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.InventoryItem;
import ch.epfl.cs107.play.game.rpg.actor.Dialog;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.TextAlign;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Shop extends AreaEntity {
    private final static int SELECTED_ANIMATION_DURATION = 16;
    private final Dialog WELCOME_DIALOG;
    private final Dialog GOODBYE_DIALOG;
    private final InventoryItem[] STARTING_INVENTORY;
    /*STARTING_INVENTORY is not an ARPGInventory because if it was, initializing it to inventory
    (STARTING_INVENTORY = inventory) would have not been useful because the changes made to inventory
    would also be seen from STARTING_INVENTORY
    */
    private Sprite sellerSprite;

    private ARPGInventory inventory;
    private ARPGInventory customerInventory;

    private boolean openInventory;
    private int[] selectedSlot;
    private Animation selectedAnimation;
    private Vector selectedAnchor;
    private ARPGItem selectedItem;

    private boolean showWelcomeDialog;
    private boolean showGoodbyeDialog;

    /**
     * Shop constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity in the Area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public Shop(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        inventory = new ARPGInventory(0,500);
        inventory.add(ARPGItem.ARROW, 15);
        inventory.add(ARPGItem.BOMB, 5);
        inventory.add(ARPGItem.BOW, 1);
        inventory.add(ARPGItem.SWORD, 1);
        inventory.add(ARPGItem.WINGS, 1);
        inventory.add(ARPGItem.CHESTKEY,1);
        STARTING_INVENTORY = inventory.getItems();

        openInventory = false;
        selectedSlot = new int[]{0,0};
        selectedItem = (ARPGItem) inventory.getItems()[0];

        showWelcomeDialog = false;
        showGoodbyeDialog = false;
        WELCOME_DIALOG = new Dialog("Welcome to our shop !", "zelda/dialog", area);
        GOODBYE_DIALOG = new Dialog("Enjoy your day !", "zelda/dialog", area);

        ArrayList<Orientation> orientations = new ArrayList<>(Arrays.asList(Orientation.UP, Orientation.RIGHT, Orientation.DOWN, Orientation.LEFT));
        sellerSprite = new Sprite("zelda/character", 1, 2, this, new RegionOfInterest(0, orientations.indexOf(orientation) * 32, 16,32));

        Sprite[] selectedSprites = new Sprite[2];
        selectedAnchor = new Vector(-6,-1);
        selectedSprites[0] = new Sprite("zelda/inventory.selector",2.5f,2.5f,this, new RegionOfInterest(0,0,64,64), selectedAnchor,1.f,3001);
        selectedSprites[1] = new Sprite("zelda/inventory.selector",2.5f,2.5f,this, new RegionOfInterest(64,0,64,64), selectedAnchor,1.f,3001);
        //Did not use RPGSprite.extractSprites(...) here because we need the depth argument
        selectedAnimation = new Animation(SELECTED_ANIMATION_DURATION, selectedSprites);
    }

    public void shop(ARPGInventory inventory) {
        if (!showWelcomeDialog && !openInventory && !showGoodbyeDialog) { //Prevents the case where the player interacts with the shop but he is already shopping
            showWelcomeDialog = true;
        }

        customerInventory = inventory;
    }

    private void buy(ARPGItem item) {
        if (item != null && customerInventory.removeMoney(item.getPrice())) {
            inventory.remove(item, 1);
            customerInventory.add(item, 1);
        }
    }

    private void drawInventory(Canvas canvas) {
        Vector anchor = canvas.getTransform().getOrigin();
        Vector backgroundAnchor = anchor.sub(new Vector(canvas.getScaledWidth()/2, canvas.getScaledHeight()/2 - 2));
        //BACKGROUND
        ImageGraphics background = new ImageGraphics(ResourcePath.getSprite("zelda/inventory.background"), canvas.getScaledWidth(),canvas.getScaledHeight() - 2, null, backgroundAnchor, 1.f, 3000);
        background.draw(canvas);

        //SHOP TEXT
        TextGraphics shopText = new TextGraphics("SHOP", 1.25f, Color.BLACK, null, 1, false, false, new Vector(0.f, 3.f), TextAlign.Horizontal.CENTER, TextAlign.Vertical.MIDDLE, 1.f, 3001);
        shopText.setParent(this);
        shopText.draw(canvas);

        //SELECTOR
        selectedAnimation.draw(canvas);

        //ITEMS, THEIR SLOTS AND THEIR QUANTITY
        for (int i = 0; i < 2; ++i) { //2 rows
            for (int j = 0; j < 4; ++j) { //4 columns
                Vector itemsAnchor = anchor.add(-6,1);
                //SLOTS
                if (selectedSlot[0] != i || selectedSlot[1] != j) { //Selected item is already drawn
                    ImageGraphics slotSprite = new ImageGraphics(ResourcePath.getSprite("zelda/inventory.slot"), 2.5f, 2.5f, null, itemsAnchor.add(j * 3,i * (-3)), 1.f, 3001);
                    slotSprite.draw(canvas);
                }

                int index = i * 4 + j;
                ARPGItem item = index < STARTING_INVENTORY.length ? (ARPGItem) STARTING_INVENTORY[index] : null;
                if (inventory.isInInventory(item)) {
                    //ITEMS
                    RegionOfInterest roi = item == ARPGItem.BOMB ? new RegionOfInterest(0,0,16,16) : null;
                    ImageGraphics itemSprite = new ImageGraphics(ResourcePath.getSprite(item.getSpriteName()),  1.f, 1.f, roi, itemsAnchor.add(0.75f + j * 3,  1 + i * (-3)), 1.f, 3001);
                    itemSprite.draw(canvas);

                    //QUANTITY
                    TextGraphics itemQuantity = new TextGraphics("x" + inventory.getQuantity(item), 0.5f, Color.BLACK, null, 1, false, false, itemsAnchor.add(1.25f + j * 3, 0.6f + i * (-3)), TextAlign.Horizontal.CENTER, TextAlign.Vertical.MIDDLE, 1, 3001);
                    itemQuantity.draw(canvas);
                }

            }
        }

        //PRICE
        if (selectedItem != null) {
            String number = Integer.toString(selectedItem.getPrice());
            int[] digits = new int[number.length()];

            for (int i = 0; i < number.length(); ++i) {
                digits[i] = Character.getNumericValue(number.charAt(number.length() - i - 1));
            }

            for (int i = 0; i < digits.length; i++) {
                int x;
                int y;

                if (digits[i] == 0) {
                    x = 1;
                    y = 2;
                } else {
                    x = (digits[i] - 1) % 4;
                    y = digits[i] / 4;
                }

                Vector digitsAnchor = anchor.add(3.5f, -3.25f);
                ImageGraphics digit = new ImageGraphics(ResourcePath.getSprite("zelda/digits"), 0.9f, 0.9f, new RegionOfInterest(x * 16, y * 16, 16, 16), digitsAnchor.sub(new Vector(i * 0.7f, 0)), 1, 30001);
                digit.draw(canvas);
            }

            Vector coinAnchor = anchor.add(4.4f, -3.4f);
            ImageGraphics coin = new ImageGraphics(ResourcePath.getSprite("zelda/coin"), 1.1f, 1.1f, new RegionOfInterest(0, 0, 16, 16), coinAnchor, 1, 3001);
            coin.draw(canvas);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        sellerSprite.draw(canvas);

        if (showWelcomeDialog) {
            WELCOME_DIALOG.draw(canvas);
        } else if (showGoodbyeDialog) {
            GOODBYE_DIALOG.draw(canvas);
        }

        if (openInventory) {
            drawInventory(canvas);
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
        /*We put the shop current cells one cell away from him in his direction to be able to interact
        * with it from behind the stall*/
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

    private void controls() {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        Button DOWN = keyboard.get(Keyboard.DOWN);
        Button RIGHT = keyboard.get(Keyboard.RIGHT);
        Button LEFT = keyboard.get(Keyboard.LEFT);
        Button UP = keyboard.get(Keyboard.UP);

        if (DOWN.isPressed()) {
            if (selectedSlot[0] != 1) { //If we are already at the maximum row, we do not want the selectedSlot to move
                selectedAnchor = selectedAnchor.add(0, -3);
                selectedAnimation.setAnchor(selectedAnchor);

            }
            selectedSlot[0] = Math.min(selectedSlot[0] + 1, 1); //Has only two rows
        } else if (RIGHT.isPressed()) { //Max column case
            if (selectedSlot[1] != 3) {
                selectedAnchor = selectedAnchor.add(3, 0);
                selectedAnimation.setAnchor(selectedAnchor);
            }
            selectedSlot[1] = Math.min(selectedSlot[1] + 1, 3); // Has only 4 columns
        } else if (LEFT.isPressed()) {
            if (selectedSlot[1] != 0) {
                selectedAnchor = selectedAnchor.add(-3, 0);
                selectedAnimation.setAnchor(selectedAnchor);
            }
            selectedSlot[1] = Math.max(selectedSlot[1] - 1, 0);
        } else if (UP.isPressed()) {
            if (selectedSlot[0] != 0) {
                selectedAnchor = selectedAnchor.add(0, 3);
                selectedAnimation.setAnchor(selectedAnchor);
            }
            selectedSlot[0] = Math.max(selectedSlot[0] - 1, 0);
        }

        int index = selectedSlot[0] * 4 + selectedSlot[1];
        ARPGItem item = index < STARTING_INVENTORY.length ? (ARPGItem) STARTING_INVENTORY[index] : null;
        if (inventory.isInInventory(item)) { //Prevents ArrayIndexOutOfBoundsException when selecting an empty slot
            selectedItem = (ARPGItem) STARTING_INVENTORY[4 * selectedSlot[0] + selectedSlot[1]];
        } else {
            selectedItem = null;
        }
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor)v).interactWith(this, getOrientation());
    }

    @Override
    public void update(float deltaTime) {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        Button ENTER = keyboard.get(Keyboard.ENTER);
        Button W = keyboard.get(Keyboard.W);

        if (ENTER.isPressed()) {
            if (showWelcomeDialog) {
                showWelcomeDialog = false;
                openInventory = true;
                ((ARPGArea) getOwnerArea()).setCanEnter(false);
            } else if (showGoodbyeDialog) {
                showGoodbyeDialog = false;
                ((ARPGArea) getOwnerArea()).setCanEnter(true);
            } else if (openInventory) {
                buy(selectedItem);
            }
        }

        if (openInventory && W.isPressed()) {
            openInventory = false;
            showGoodbyeDialog = true;
        }

        if (openInventory) {
            controls();
            selectedAnimation.update(deltaTime);
        }
    }
}