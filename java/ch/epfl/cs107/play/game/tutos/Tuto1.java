package ch.epfl.cs107.play.game.tutos;

import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.tutos.actor.SimpleGhost;
import ch.epfl.cs107.play.game.tutos.area.tuto1.Farm;
import ch.epfl.cs107.play.game.tutos.area.tuto1.Village;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.*;

public class Tuto1 extends AreaGame {


    private SimpleGhost player;

    private void createAreas(){
        addArea(new Farm());
        addArea(new Village());
    }

    private void switchArea(){
        getCurrentArea().unregisterActor(player);

        if(getCurrentArea() instanceof Farm){
            setCurrentArea("zelda/Village", true);
        } else if(getCurrentArea() instanceof Village){
            setCurrentArea("zelda/Farm", true);
        }

        player.strenghten();

        getCurrentArea().registerActor(player);
        getCurrentArea().setViewCandidate(player);
        getCurrentArea().registerActor(new Background(getCurrentArea()));
    }

    @Override
    public void end() {
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if(player.isWeak()){
            switchArea();
        }

        Keyboard keyboard = getWindow().getKeyboard();
        Button upArrow = keyboard.get(Keyboard.UP);
        Button downArrow = keyboard.get(Keyboard.DOWN);
        Button leftArrow = keyboard.get(Keyboard.LEFT);
        Button rightArrow = keyboard.get(Keyboard.RIGHT);

        if(upArrow.isDown()){
            player.moveUp();
        } else if (downArrow.isDown()){
            player.moveDown();
        } else if (leftArrow.isDown()){
            player.moveLeft();
        } else if (rightArrow.isDown()){
            player.moveRight();
        }
    }

    @Override
    public String getTitle() {
        return "Tuto1";
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)){
            player = new SimpleGhost(new Vector(18,7), "ghost.1");
            createAreas();
            setCurrentArea("zelda/Village", true);
            getCurrentArea().registerActor(player);
            getCurrentArea().setViewCandidate(player);
            getCurrentArea().registerActor(new Background(getCurrentArea()));

            return true;
        } else {
            return false;
        }
    }
}
