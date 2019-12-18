package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.arpg.ARPG;
import ch.epfl.cs107.play.game.arpg.ARPGBehavior;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.window.Window;

abstract public class ARPGArea extends Area {

    private ARPGBehavior behavior;

    /**
     * Create the area by adding all its actors
     * called by the begin method, when the area starts to play
     */
    protected abstract void createArea();

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            // Set the behavior map
            behavior = new ARPGBehavior(window, getTitle());
            setBehavior(behavior);
            createArea();
            return true;
        }
        return false;
    }

    @Override
    public float getCameraScaleFactor() {
        return ARPG.CAMERA_SCALE_FACTOR;
    }

    /**
     * Method used in Shop.java to prevent the entities from moving (or to allow him back to move)
     * (We want that nobody moves while the player is shopping).
     * @param b (boolean): True to allow the entities to move. False to restrict them to move.
     */
    public void setCanEnter(boolean b) {
        behavior.setCanEnter(b);
    }
}
