package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.ARPGItem;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.InventoryItem;
import ch.epfl.cs107.play.game.rpg.actor.Dialog;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chest extends AreaEntity {
    private final static int ANIMATION_DURATION = 2;
    private Sprite spriteOpen;
    private Sprite spriteClosed;
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
        skip = true;
        open = false;
        signal = Logic.FALSE;

        inventory = new ARPGInventory(0,300);
        inventory.add(ARPGItem.STAFF, 1);
        inventory.add(ARPGItem.ARROW, 20);
        inventory.add(ARPGItem.WINGS, 1);

        Sprite[] sprites = new Sprite[4];

        sprites[0] = new Sprite("chest", 2.f, 2.f, this, new RegionOfInterest(0, 192, 48, 48), new Vector(-0.5f, 0.f), 1.f, -15);
        sprites[1] = new Sprite("chest", 2.f, 2.f, this, new RegionOfInterest(0, 240, 48, 48), new Vector(-0.5f, 0.f), 1.f, -15);
        sprites[2] = new Sprite("chest", 2.f, 2.f, this, new RegionOfInterest(0, 288, 48, 48), new Vector(-0.5f, 0.f), 1.f, -15);
        sprites[3] = new Sprite("chest", 2.f, 2.f, this, new RegionOfInterest(0, 336, 48, 48), new Vector(-0.5f, 0.f), 1.f, -15);

        //Select the sprites when open and closed
        spriteOpen = sprites[3];
        spriteClosed = sprites[0];

        animation = new Animation(ANIMATION_DURATION, sprites, false);
    }

    public void open(ARPGInventory otherInventory) {
        skip = false; //If someone wants to open the chest, it will automatically show something (even if the chest is already open)
        checkKey(otherInventory); //Checks if the player has a chest key

        if (signal.isOn()) {
            if (inventory.getItems().length != 0) {
                ArrayList<InventoryItem> toBeTransferred = new ArrayList<>();
                ArrayList<InventoryItem> toStayInChest = new ArrayList<>();

                //We sort the inventory by weight here to make sure that the heaviest items are taken before the lightest ones
                InventoryItem[] sortedInventory = sortedInventory();

                for (int i = 0; i < sortedInventory.length; ++i) {
                    InventoryItem item = sortedInventory[i];
                    if (otherInventory.add(item, inventory.getQuantity(item))) {
                        toBeTransferred.add(item);
                    } else {
                        toStayInChest.add(item);
                    }
                }

                //Dialog
                String message = "You just got : ";
                message += displayTrade(toBeTransferred.toArray(new InventoryItem[0]));
                message += " There is still : ";
                message += displayTrade(toStayInChest.toArray(new InventoryItem[0]));
                if (toStayInChest.size() != 0) {
                    message += " You are too heavy !";
                }

                dialog = new Dialog(message, "zelda/dialog", getOwnerArea());
                open = true;

                /*Removes everything that needs to be transferred from inventory
                We cannot do this earlier (in the previous for loop) because in that
                case, displayTrade would show that the player got 0 from each item
                because they have all been removed.
                 */
                for (int i = 0; i < toBeTransferred.size(); ++i) {
                    inventory.remove(toBeTransferred.get(i), inventory.getQuantity(toBeTransferred.get(i)));
                }
            } else {
                dialog = new Dialog("This chest is empty !", "zelda/dialog", getOwnerArea());
            }
        } else {
            dialog = new Dialog("This chest is locked ! You need a chest key.", "zelda/dialog", getOwnerArea());
        }
    }

    private void checkKey(ARPGInventory inventory) {
        if (inventory.isInInventory(ARPGItem.CHESTKEY)) {
            signal = Logic.TRUE;
        }
    }

    private InventoryItem[] sortedInventory() {
        //Selection sort on the weight of each item
        InventoryItem[] array = inventory.getItems();
        int min;
        for (int i = 0; i < array.length - 1; ++i) {
            min = i;
            for (int j = i + 1; j < array.length; ++j) {
                if (array[j].getWeight() > array[min].getWeight()) {
                    min = j;
                }
            }
            //Swap array[min] and array[i]
            InventoryItem temp = array[min];
            array[min] = array[i];
            array[i] = temp;
        }
        return array;
    }

    private String displayTrade(InventoryItem[] inventoryToDisplay) {
        String message = "";
        if (inventoryToDisplay.length == 0) {
            message = "nothing !";
        }
        for (int i = 0; i < inventoryToDisplay.length; ++i) {
            ARPGItem item = (ARPGItem) inventoryToDisplay[i];
            if (item != ARPGItem.WINGS && inventory.getQuantity(item) == 1) { //We do not say "a Wings"

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

            if (i == inventoryToDisplay.length - 1) { //Checks if we are at the last item or not to know if we need to put a final stop
                message += ".";
            } else {
                if (i == inventoryToDisplay.length - 2) { //Checks if we are at the next-to-last to know if we need to put a comma or "and" before the last item
                    message += " and ";
                } else {
                    message += ", ";
                }
            }
        }
        return message;
    }

    private boolean isVowel(char c) {
        switch (Character.toLowerCase(c)) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
            case 'y':
                return true;
            default:
                return false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!skip) {
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

        Button ENTER = getOwnerArea().getKeyboard().get(Keyboard.ENTER);
        if (ENTER.isPressed()) { //if Enter is pressed, the message disappears
            skip = true;
        }
    }
}
