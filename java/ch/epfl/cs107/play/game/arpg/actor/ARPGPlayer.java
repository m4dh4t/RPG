package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Door;
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

public class ARPGPlayer extends Player {
    private final static int ANIMATION_DURATION = 5; //DEFAULT: 8
    private Sprite[][] sprites;
    private Animation[] animations;
    private Animation currentAnimation;
    private TextGraphics message;
    private float hp;
    private ARPGPlayerHandler handler;

    public ARPGPlayer(Area area, DiscreteCoordinates coordinates) {
        super(area, Orientation.DOWN, coordinates);
        this.hp = 10;
        message = new TextGraphics(Integer.toString((int)hp), 0.4f, Color.BLUE);
        message.setParent(this);
        message.setAnchor(new Vector(-0.3f, 0.1f));
        sprites = RPGSprite.extractSprites("zelda/player", 4, 1, 2, this, 16, 32, new Orientation[] {Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT});
        animations = RPGSprite.createAnimations(ANIMATION_DURATION/2, sprites);
        currentAnimation = animations[2];
        handler = new ARPGPlayerHandler();

        resetMotion();
    }

    private void moveOrientate(Orientation orientation, Button button) {
        if (button.isDown()) {
            if (getOrientation() == orientation) {
                move(ANIMATION_DURATION);
            } else {
                orientate(orientation);
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
        if (getOwnerArea().getKeyboard().get(Keyboard.E).isDown()) {
            return true;
        }

        return false;
    }

    public boolean isWeak() {
        return (hp <= 0.f);
    }

    public void strengthen() {
        hp = 10;
    }

    @Override
    public void draw(Canvas canvas) {
        currentAnimation.draw(canvas);
        message.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        if(!isWeak()){
            hp -= deltaTime;
            message.setText(Integer.toString((int)hp));
        } else {
            hp = 0.f;
        }

        Keyboard keyboard= getOwnerArea().getKeyboard();
        moveOrientate(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
        moveOrientate(Orientation.UP, keyboard.get(Keyboard.UP));
        moveOrientate(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
        moveOrientate(Orientation.DOWN, keyboard.get(Keyboard.DOWN));

        if (isDisplacementOccurs()) {
            if (getOrientation() == Orientation.UP) {
                currentAnimation = animations[0];
                currentAnimation.update(deltaTime);
            } else if (getOrientation() == Orientation.RIGHT) {
                currentAnimation = animations[1];
                currentAnimation.update(deltaTime);
            } else if (getOrientation() == Orientation.DOWN) {
                currentAnimation = animations[2];
                currentAnimation.update(deltaTime);
            } else if (getOrientation() == Orientation.LEFT) {
                currentAnimation = animations[3];
                currentAnimation.update(deltaTime);
            }
        } else {
            currentAnimation.reset();
        }

        super.update(deltaTime);
    }

    private class ARPGPlayerHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(Door door) {
            setIsPassingADoor(door);
        }

        public void interactWith(Grass grass) {
            grass.slice();
        }
    }
}
