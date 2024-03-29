package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
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

public class God extends AreaEntity implements Interactor {
    private final static int IDLE_ANIMATION_DURATION = 8;
    private final static int SPEECH_ANIMATION_DURATION = 8;
    private final static int SPELL_ANIMATION_DURATION = 8;
    private final static int SELECTED_ANIMATION_DURATION = 16;

    private GodHandler handler;
    private Animation idleAnimation;
    private Animation speechAnimation;
    private Animation spellAnimation;
    private Animation currentAnimation;

    private int selectedSlot;
    private Animation selectedAnimation;
    private Vector selectedAnchor;
    private boolean showChoices;

    private Dialog[] dialogs;
    private int skipCount;
    private ARPGPlayer player;
    private boolean restartGame;
    private boolean quitGame;
    private boolean hasInteracted;
    DiscreteCoordinates frontCell;


    /**
     * God constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public God(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);

        Sprite[] idleSprites = RPGSprite.extractSprites("wizard.idle", 4, 2, 4, this, 32, 64);
        idleAnimation = new Animation(IDLE_ANIMATION_DURATION, idleSprites, true);

        Sprite[] speechSprites = RPGSprite.extractSprites("wizard.speech", 4, 2, 4, this, 32, 64);
        speechAnimation = new Animation(SPEECH_ANIMATION_DURATION, speechSprites, true);

        Sprite[] spellSprites = RPGSprite.extractSprites("wizard.spell", 3, 4, 4, this, 64, 64, new Vector(-1f, 0));
        spellAnimation = new Animation(SPELL_ANIMATION_DURATION, spellSprites, false);

        Sprite[] selectedSprites = new Sprite[2];
        selectedAnchor = new Vector(6.125f,8.f);
        selectedSprites[0] = new Sprite("zelda/inventory.selector",3.75f,3.75f, null, new RegionOfInterest(0,0,64,64), selectedAnchor,1.f,3001);
        selectedSprites[1] = new Sprite("zelda/inventory.selector",3.75f,3.75f, null, new RegionOfInterest(64,0,64,64), selectedAnchor,1.f,3001);
        //Did not use RPGSprite.extractSprites(...) here because we need the depth argument
        selectedAnimation = new Animation(SELECTED_ANIMATION_DURATION, selectedSprites);

        frontCell = position.jump(getOrientation().toVector());
        hasInteracted = false;
        showChoices = false;
        restartGame = false;
        quitGame = false;
        handler = new GodHandler();
        currentAnimation = idleAnimation;
        selectedSlot = 0;
        skipCount = 0;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!currentAnimation.isCompleted()) {
            currentAnimation.draw(canvas);
        }

        //Prevent IndexOutOfBound by stopping the dialog if it's empty or skipContent is out of the array
        if(dialogs != null && skipCount <= 1){
            dialogs[skipCount].draw(canvas);
        }

        //We want to show choices to the player only if he skipped the dialogs first
        //and he his not moving (in the cell in front of God)
        if(skipCount >= 2 && showChoices && player.getCurrentCells().contains(frontCell)){
            drawChoice(canvas);
        }
    }

    private void drawChoice(Canvas canvas) {
        Vector anchor = canvas.getTransform().getOrigin();
        Vector backgroundAnchor = anchor.sub(new Vector(canvas.getScaledWidth()/2, canvas.getScaledHeight()/2 - 2));
        //BACKGROUND
        ImageGraphics background = new ImageGraphics(ResourcePath.getSprite("zelda/inventory.background"), canvas.getScaledWidth(),canvas.getScaledHeight() - 2, null, backgroundAnchor, 1.f, 3000);
        background.draw(canvas);
        //CHOICES
        Vector yesAnchor = anchor.add(-3.f, 1.f);
        Vector noAnchor = anchor.add(3.f, 1.f);
        TextGraphics yes = new TextGraphics("YES", 1.25f, Color.BLACK, null, 1, false, false, yesAnchor, TextAlign.Horizontal.CENTER, TextAlign.Vertical.MIDDLE, 1.f, 3001);
        TextGraphics no = new TextGraphics("NO", 1.25f, Color.BLACK, null, 1, false, false, noAnchor, TextAlign.Horizontal.CENTER, TextAlign.Vertical.MIDDLE, 1.f, 3001);
        yes.draw(canvas);
        no.draw(canvas);
        //SELECTOR
        selectedAnimation.draw(canvas);
    }

    private void controls() {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        Button RIGHT = keyboard.get(Keyboard.RIGHT);
        Button LEFT = keyboard.get(Keyboard.LEFT);
        Button ENTER = keyboard.get(Keyboard.ENTER);

        //If the player presses enter, the dialog array index is updated
        if(ENTER.isPressed()){
            ++skipCount;
        }

        //Special behavior if the player must make a choice
        if (showChoices) {
            if (RIGHT.isPressed() || LEFT.isPressed()) {
                if (selectedSlot == 0) {
                    selectedAnchor = selectedAnchor.add(6.25f, 0f);
                    selectedSlot = 1;
                } else {
                    selectedAnchor = selectedAnchor.sub(6.25f, 0f);
                    selectedSlot = 0;
                }
                selectedAnimation.setAnchor(selectedAnchor);
            } else if (ENTER.isPressed() && skipCount >= 3) {
                showChoices = false;
                currentAnimation = spellAnimation;

                if (selectedSlot == 0) {
                    restart();
                } else {
                    quit();
                }
            }
        //If he doesn't have to make a choice, he just quit after reading the dialogs
        } else if (ENTER.isPressed() && skipCount >= 2){
            currentAnimation = spellAnimation;
            quit();
        }
    }

    public void restart(){
        restartGame = true;
    }

    public void quit(){
        quitGame = true;
    }

    @Override
    public void update(float deltaTime) {
        currentAnimation.update(deltaTime);
        selectedAnimation.update(deltaTime);

        //Gives Area-specific controls to the player
        controls();

        //Leave the area only after God completed his animation
        if(currentAnimation.isCompleted()) {
            if (restartGame) {
                player.restartGame();
            } else if (quitGame) {
                player.quitGame();
            }
        }
    }

    public void speak(ARPGPlayer player){
        hasInteracted = true; //Prevent lopping interaction with the player
        this.player = player;
        currentAnimation = speechAnimation; //Update God's animation
        dialogs = new Dialog[2];
        //Generate a dialog depending on why the player is here
        if(player.isGameOver()){
            dialogs[0] = new Dialog("Welcome in paradise brave hero. Sadly, you failed your quest.", "zelda/dialog", getOwnerArea());
            dialogs[1] = new Dialog("But there's still hope, do you want to try to save the kingdom again ?", "zelda/dialog", getOwnerArea());
            showChoices = true; //If gameOver, the player must choose if he wants to respawn
        } else {
            dialogs[0] = new Dialog("Welcome in paradise brave hero. You succeeded in your quest.", "zelda/dialog", getOwnerArea());
            dialogs[1] = new Dialog("It's time for you to get some rest, you earned it. See you soon...", "zelda/dialog", getOwnerArea());
            showChoices = false; //If not gameOver, it will just leave the game after the dialogs
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        //God takes two cells
        return new ArrayList<>(Arrays.asList(getCurrentMainCellCoordinates(), getCurrentMainCellCoordinates().jump(1,0)));
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    @Override
    public boolean wantsViewInteraction() {
        //If he already interacted, then don't allow any more
        return !hasInteracted;
    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
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
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
    }

    private class GodHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(ARPGPlayer player) {
            speak(player);
        }
    }
}
