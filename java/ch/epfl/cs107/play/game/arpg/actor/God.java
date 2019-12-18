package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.arpg.ARPG;
import ch.epfl.cs107.play.game.arpg.ARPGItem;
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
        selectedAnchor = new Vector(-3.8f,-1.65f);
        selectedSprites[0] = new Sprite("zelda/inventory.selector",3f,3f,this, new RegionOfInterest(0,0,64,64), selectedAnchor,1.f,3001);
        selectedSprites[1] = new Sprite("zelda/inventory.selector",3f,3f,this, new RegionOfInterest(64,0,64,64), selectedAnchor,1.f,3001);
        //Did not use RPGSprite.extractSprites(...) here because we need the depth argument
        selectedAnimation = new Animation(SELECTED_ANIMATION_DURATION, selectedSprites);

        showChoices = true;
        restartGame = false;
        quitGame = false;
        handler = new GodHandler();
        currentAnimation = idleAnimation;
        selectedSlot = 0;
        skipCount = 0;
    }

    @Override
    public void draw(Canvas canvas) {
        currentAnimation.draw(canvas);

        if(dialogs != null && skipCount <= 1){
            dialogs[skipCount].draw(canvas);
        }

        if(skipCount >= 2 && showChoices){
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
        Vector yesAnchor = new Vector(-1.5f, 0.f);
        Vector noAnchor = new Vector(2.5f, 0.f);
        TextGraphics yes = new TextGraphics("YES", 1.25f, Color.BLACK, null, 1, false, false, yesAnchor, TextAlign.Horizontal.CENTER, TextAlign.Vertical.MIDDLE, 1.f, 3001);
        TextGraphics no = new TextGraphics("NO", 1.25f, Color.BLACK, null, 1, false, false, noAnchor, TextAlign.Horizontal.CENTER, TextAlign.Vertical.MIDDLE, 1.f, 3001);
        yes.setParent(this);
        no.setParent(this);
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

        if (RIGHT.isPressed() || LEFT.isPressed()) {
            if (selectedSlot == 0) {
                selectedAnchor = selectedAnchor.add(4.5f, 0f);
                selectedSlot = 1;
            } else {
                selectedAnchor = selectedAnchor.add(-4.5f, 0f);
                selectedSlot = 0;
            }
            selectedAnimation.setAnchor(selectedAnchor);
        } else if (ENTER.isPressed() && skipCount >= 3){
            showChoices = false;
            currentAnimation = spellAnimation;
            if(selectedSlot == 0){
                restart();
            } else {
                quit();
            }
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

        Button enterButton = getOwnerArea().getKeyboard().get(Keyboard.ENTER);
        if (enterButton.isPressed()) { //if Enter is pressed, the message disappears
            ++skipCount;
        }

        if(skipCount >= 2){
            controls();
        }

        if(currentAnimation.isCompleted()) {
            if (restartGame) {
                player.restartGame();
            } else if (quitGame) {
                player.quitGame();
            }
        }
    }

    public void speak(ARPGPlayer player){
        this.player = player;
        currentAnimation = speechAnimation;
        dialogs = new Dialog[2];
        dialogs[0] = new Dialog("Welcome in paradise brave hero. Sadly, you failed your quest.", "zelda/dialog", getOwnerArea());
        dialogs[1] = new Dialog("But there's still hope, do you want to try to save the kingdom again ?", "zelda/dialog", getOwnerArea());
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
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
        return showChoices;
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
        ((ARPGInteractionVisitor)v).interactWith(this);
    }

    private class GodHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(ARPGPlayer player) {
            speak(player);
        }
    }
}
