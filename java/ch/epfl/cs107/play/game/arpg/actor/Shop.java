package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Dialog;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
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
    private final static int SELECTED_ANIMATION_DURATION = 4;
    private Sprite sellerSprite;

    private boolean openInventory;
    private int[] selectedSlot;
    private Animation selectedAnimation;

    private boolean showDialog;
    private Dialog dialog;

    /**
     * Shop constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity in the Area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public Shop(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        openInventory = false;
        selectedSlot = new int[]{0,0};

        showDialog = false;
        dialog = new Dialog("Welcome to our shop !", "zelda/dialog", area);

        ArrayList<Orientation> orientations = new ArrayList<>(Arrays.asList(Orientation.UP, Orientation.RIGHT, Orientation.DOWN, Orientation.LEFT));
        sellerSprite = new Sprite("zelda/character", 1, 2, this, new RegionOfInterest(0, orientations.indexOf(orientation) * 32, 16,32));
    }

    public void buy() {
        showDialog = true;
    }

    private void drawInventory(Canvas canvas) {
        Sprite background = new Sprite("zelda/inventory.background", canvas.getScaledWidth(),canvas.getScaledHeight() - 2, this, null, new Vector(-canvas.getScaledWidth()/2, -canvas.getScaledHeight()/2), 1.f, 3000);
        background.draw(canvas);

        TextGraphics text = new TextGraphics("SHOP", 1.5f, Color.black, null, 1, false, false, new Vector(0.f, 3.f), TextAlign.Horizontal.CENTER, TextAlign.Vertical.MIDDLE, 1.f, 3001);
        text.setParent(this);
        text.draw(canvas);

        for (int i = 0; i < 2; ++i) { //2 rows
            for (int j = 0; j < 4; ++j) { //4 columns
                if (selectedSlot[0] == i && selectedSlot[1] == j) {
                    Vector anchor = Vector.ZERO;
                    Sprite[] selectedSprites = new Sprite[2];
                    selectedSprites[0] = new Sprite("zelda/inventory.selector",2.f,2.f,this,new RegionOfInterest(0,0,64,64),anchor,1.f,3001);
                    selectedSprites[1] = new Sprite("zelda/inventory.selector",2.f,2.f,this,new RegionOfInterest(64,0,64,64),anchor,1.f,3001);
                    //Did not use RPGSprite.extractSprites here because we need the Depth parameter.
                    selectedAnimation = new Animation(SELECTED_ANIMATION_DURATION, selectedSprites);
                    selectedAnimation.draw(canvas);
                } else {
                }
            }
        }


    }

    @Override
    public void draw(Canvas canvas) {
        sellerSprite.draw(canvas);

        if (showDialog) {
            dialog.draw(canvas);
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

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor)v).interactWith(this, getOrientation());
    }

    @Override
    public void update(float deltaTime) {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        Button ENTER = keyboard.get(Keyboard.ENTER);
        Button DOWN = keyboard.get(Keyboard.DOWN);
        Button RIGHT = keyboard.get(Keyboard.RIGHT);
        Button LEFT = keyboard.get(Keyboard.LEFT);
        Button UP = keyboard.get(Keyboard.UP);

        if (showDialog && ENTER.isPressed()) {
            showDialog = false;
            openInventory = true;
        }

        if (openInventory) {
            if (DOWN.isPressed()) {
                selectedSlot[0] = Math.min(selectedSlot[0] + 1, 2); //Only has two rows
            } else if (RIGHT.isPressed()) {
                selectedSlot[1] = Math.min(selectedSlot[1] + 1, 4); // Only has 4 columns
            } else if (LEFT.isPressed()) {
                selectedSlot[1] = Math.max(selectedSlot[1] - 1, 0);
            } else if (UP.isPressed()) {
                selectedSlot[0] = Math.max(selectedSlot[0] - 1, 0);
            }
        }
    }
}
