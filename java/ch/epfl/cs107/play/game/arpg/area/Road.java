package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.*;
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
        registerActor(new Door("zelda/RoadCastle", new DiscreteCoordinates(9,1), Logic.TRUE, this, Orientation.UP, new DiscreteCoordinates(9,19), new DiscreteCoordinates(10,19)));
        registerActors(Grass.grassZone(this, 5,7,6,11));
        registerActor(new Bomb(this,new DiscreteCoordinates(6,10), 2.5f));
        registerActor(new Waterfall(this, new DiscreteCoordinates(15,4)));
    }

    @Override
    public String getTitle() {
        return"zelda/Road";
    }
}
