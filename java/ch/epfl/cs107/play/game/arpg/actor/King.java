package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Dialog;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.Collections;
import java.util.List;

public class King extends AreaEntity {
    private Sprite sprite;
    private Dialog dialog;
    private boolean showDialog;

    /**
     * King constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity in the Area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public King(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        sprite = new Sprite("zelda/king", 1.f, 2.f, this, new RegionOfInterest(0, 64, 16, 32), Vector.ZERO, 1.f, -11);
        dialog = new Dialog("Thank you for your help ! Our kingdom is now in safety !", "zelda/dialog", getOwnerArea());
        showDialog = false;
    }

    public void save() {
        showDialog = true;
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
        if (showDialog) {
            dialog.draw(canvas);
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
        if (getOwnerArea().getKeyboard().get(Keyboard.ENTER).isPressed()) {
            showDialog = false;
        }
    }
}
