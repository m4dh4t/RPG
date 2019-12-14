package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.ARPGItem;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Dialog;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.Collections;
import java.util.List;

public class Chest extends AreaEntity {
    private final static int ANIMATION_DURATION = 2;
    private Sprite spriteClosed;
    private Sprite spriteOpen;
    private Animation animation;
    private Dialog dialog;

    private boolean skip;
    private boolean open;
    private Logic signal;

    private ARPGInventory inventory;
    /**
     * Chest constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public Chest(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);
        skip = false;
        open = false;
        signal = Logic.FALSE;

        inventory = new ARPGInventory(0);
        inventory.add(ARPGItem.STAFF, 1);
        inventory.add(ARPGItem.ARROW, 1);
        inventory.add(ARPGItem.CASTLEKEY, 3);
        inventory.add(ARPGItem.BOW,10);

        Sprite[] sprites = new Sprite[4];

        sprites[0] = new Sprite("chest", 2.f, 2.f, this, new RegionOfInterest(0, 192, 48, 48), new Vector(-0.5f, 0.f), 1.f, -15);;
        sprites[1] = new Sprite("chest", 2.f, 2.f, this, new RegionOfInterest(0, 240, 48, 48), new Vector(-0.5f, 0.f), 1.f, -15);
        sprites[2] = new Sprite("chest", 2.f, 2.f, this, new RegionOfInterest(0, 288, 48, 48), new Vector(-0.5f, 0.f), 1.f, -15);
        sprites[3] = new Sprite("chest", 2.f, 2.f, this, new RegionOfInterest(0, 336, 48, 48), new Vector(-0.5f, 0.f), 1.f, -15);

        spriteClosed = sprites[0];
        spriteOpen = sprites[3];

        animation = new Animation(ANIMATION_DURATION, sprites, false);
    }

    public void open(ARPGInventory otherInventory) {
        skip = false; //If someone wants to open the chest, it will automatically show something (even if the chest is already open)
        checkKey(otherInventory); //Checks if the player has a chest key

        if (signal.isOn()) {
            if (!open) {
                String message = "You just got : ";
                for (int i = 0; i < inventory.getItems().length; ++i) {
                    ARPGItem item = (ARPGItem) inventory.getItems()[i];

                    if (inventory.getQuantity(item) == 1) {

                        message += "a";

                        if (isVowel(item.getName().charAt(0))) { //Checks if the first letter of the item is a vowel (to choose between "a" and "an")
                            message += "n ";
                        } else {
                            message += " ";
                        }

                    } else {
                        message += inventory.getQuantity(item) + " ";
                    }

                    message += item.getName();

                    message += inventory.getQuantity(item) > 1 ? "s" : ""; //Plural

                    if (i == inventory.getItems().length - 1) { //Checks if we are at the last item or not to know if we need to put a final stop
                        message += ".";
                    } else {
                        if (i == inventory.getItems().length - 2) { //Checks if we are at the next-to-last to know if we need to put a comma or "and" before the last item
                            message += " and ";
                        } else {
                            message += ", ";
                        }
                    }

                    otherInventory.add(item, inventory.getQuantity(item));
                    inventory.remove(item, inventory.getQuantity(item));

                    --i; /*Needed because inventory.getItems().length will decrease as the loops goes (because we remove items from inventory)
                So, in fact, we are not incrementing i ever (it always stays at 0) but the number of items decreases by 1
                at each iteration of the loop*/
                }

                dialog = new Dialog(message, "zelda/dialog", getOwnerArea());
                open = true;
            } else {
                dialog = new Dialog("This chest is already open !", "zelda/dialog", getOwnerArea());
            }
        } else {
            dialog = new Dialog("This chest is locked !", "zelda/dialog", getOwnerArea());
        }
    }

    public void checkKey(ARPGInventory inventory) {
        if (inventory.isInInventory(ARPGItem.CHESTKEY)) {
            signal = Logic.TRUE;
        }
    }

    public boolean isVowel(char c) {
        switch (Character.toLowerCase(c)) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
            case 'y': return true;
            default: return false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!skip && dialog != null) { //Checks if dialog is not null to prevent a NullPointerException when we enter in the area because dialog is not initialized in the constructor
            dialog.draw(canvas);
        }

        if (open) {
            if (animation.isCompleted()) {
                spriteOpen.draw(canvas);
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
        if (open) {
            animation.update(deltaTime);
        }

        Button enterButton = getOwnerArea().getKeyboard().get(Keyboard.ENTER);
        if (enterButton.isPressed()) { //if Enter is pressed, the message disappears
            skip = true;
        }
    }
}
