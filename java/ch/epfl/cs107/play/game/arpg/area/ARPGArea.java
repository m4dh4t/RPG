package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.arpg.ARPG;
import ch.epfl.cs107.play.game.arpg.ARPGBehavior;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
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

    public boolean isDoor(DiscreteCoordinates coord) {
        return (behavior.isDoor(coord));
    }

    @Override
    public float getCameraScaleFactor() {
        return ARPG.CAMERA_SCALE_FACTOR;
    }
}
