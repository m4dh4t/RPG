package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.arpg.actor.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.area.Farm;
import ch.epfl.cs107.play.game.arpg.area.Road;
import ch.epfl.cs107.play.game.arpg.area.Village;
import ch.epfl.cs107.play.game.rpg.RPG;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public class ARPG extends RPG {
    public final static float CAMERA_SCALE_FACTOR = 13.f;

    private final String startingArea = "zelda/Farm";
    private final DiscreteCoordinates startingPosition = new DiscreteCoordinates(6,10);

    private void createAreas(){
        addArea(new Farm());
        addArea(new Village());
        addArea(new Road());
    }

    @Override
    public void end() {
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public String getTitle() {
        return "ZeldIC";
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)){
            createAreas();
            Area area = setCurrentArea(startingArea, true);
            initPlayer(new ARPGPlayer(area, startingPosition));
            return true;
        } else {
            return false;
        }
    }
}
