package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.arpg.actor.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.actor.WhiteHalo;
import ch.epfl.cs107.play.game.arpg.area.*;
import ch.epfl.cs107.play.game.rpg.RPG;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public class ARPG extends RPG {
    public final static float CAMERA_SCALE_FACTOR = 13.f;

    private ARPGPlayer player;
    private Area currentArea;
    private final String startingArea = "zelda/Farm";
    private final DiscreteCoordinates startingPosition = new DiscreteCoordinates(6, 10);

    private void createAreas(){
        addArea(new Farm());
        addArea(new Village());
        addArea(new Road());
        addArea(new RoadCastle());
        addArea(new Castle());
        addArea(new RoadTemple());
        addArea(new Temple());
        addArea(new Paradise());
    }

    @Override
    public void update(float deltaTime) {
        //If the player is dead or won the game, teleport him to Paradise
        if((player.isGameOver() || player.hasWon()) && !(currentArea instanceof Paradise)){
            player.leaveArea();
            currentArea = setCurrentArea("Paradise", true);
            player.enterArea(currentArea, new DiscreteCoordinates(11, 7));
        //If the player is in paradise and want to continue, make him respawn
        } else if (!player.isGameOver() && !player.hasWon() && currentArea instanceof Paradise){
            player.leaveArea();
            currentArea = setCurrentArea("zelda/Farm", false);
            player.enterArea(currentArea, startingPosition);
            currentArea.registerActor(new WhiteHalo(currentArea, startingPosition));
        //If he wants to quit, close the window
        } else if (player.getQuitGame()){
            getWindow().setCloseRequested();
        }

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
            currentArea = setCurrentArea(startingArea, true);
            player = new ARPGPlayer(currentArea, startingPosition);
            initPlayer(player);
            return true;
        } else {
            return false;
        }
    }
}
