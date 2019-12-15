package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.LogMonster;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class RoadTemple extends ARPGArea {
    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door("zelda/Road", new DiscreteCoordinates(18,9), Logic.TRUE, this, Orientation.LEFT, new DiscreteCoordinates(0,4), new DiscreteCoordinates(0,5), new DiscreteCoordinates(0,6)));
        registerActor(new Door("zelda/Temple", new DiscreteCoordinates(4,1), Logic.TRUE, this, Orientation.UP, new DiscreteCoordinates(5,6)));
        registerActor(new LogMonster(this, Orientation.LEFT, new DiscreteCoordinates(6,4)));
    }

    @Override
    public String getTitle() {
        return "zelda/RoadTemple";
    }
}
