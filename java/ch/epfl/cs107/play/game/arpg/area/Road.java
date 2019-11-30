package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Road extends ARPGArea {

    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door("zelda/Farm", new DiscreteCoordinates(18,15), Logic.TRUE, this, Orientation.UP, new DiscreteCoordinates(0,15), new DiscreteCoordinates(0,16)));
        registerActor(new Door("zelda/Village", new DiscreteCoordinates(29,18), Logic.TRUE, this, Orientation.DOWN, new DiscreteCoordinates(9,0), new DiscreteCoordinates(10,0)));
        for (int i = 5; i <= 7; ++i) {
            for (int j = 6; j <= 11; ++j) {
                registerActor(new Grass(this, new DiscreteCoordinates(i,j)));
            }
        }
        registerActor(new Bomb(this,Orientation.DOWN,new DiscreteCoordinates(6,10), 50));
    }

    @Override
    public String getTitle() {
        return"zelda/Road";
    }
}
