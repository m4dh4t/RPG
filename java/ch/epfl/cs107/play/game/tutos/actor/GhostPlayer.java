package ch.epfl.cs107.play.game.tutos.actor;

import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.tutos.area.Tuto2Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class GhostPlayer extends MovableAreaEntity {
    /// Animation duration in frame number
    private final static int ANIMATION_DURATION = 2; //DEFAULT: 8
    private boolean isPassingADoor;
    private Sprite sprite;
    private TextGraphics message;
    private float hp;

    /**
     * Default MovableAreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param coordinates    (Coordinate): Initial position of the entity. Not null
     */
    public GhostPlayer(Area area, Orientation orientation, DiscreteCoordinates coordinates, String spriteName) {
        super(area, orientation, coordinates);
        this.hp = 10;
        message = new TextGraphics(Integer.toString((int)hp), 0.4f, Color.BLUE);
        message.setParent(this);
        message.setAnchor(new Vector(-0.3f, 0.1f));
        sprite = new Sprite(spriteName, 1.f, 1.f,this);

        resetMotion();
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

        super.update(deltaTime);

        List<DiscreteCoordinates> coords = getCurrentCells();
        if(coords != null){
            for(DiscreteCoordinates c : coords){
                if(((Tuto2Area)getOwnerArea()).isDoor(c)){
                    setIsPassingADoor();
                }
            }
        }
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
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
        message.draw(canvas);
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
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {

    }

    public void enterArea(Area area, DiscreteCoordinates position){
        area.registerActor(this);
        area.setViewCandidate(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());

        resetMotion();
    }

    public void leaveArea(){
        getOwnerArea().unregisterActor(this);
    }

    /**
     * Indicate the player just passed a door
     */
    private void setIsPassingADoor(){ //
        isPassingADoor = true;
    }

    /**@return (boolean): true if the player is passing a door*/
    public boolean isPassingADoor(){
        return isPassingADoor;
    }

    public void resetDoorState() {
        isPassingADoor = false;
    }

    public boolean isWeak() {
        return (hp <= 0.f);
    }

    public void strengthen() {
        hp = 10;
    }
}
