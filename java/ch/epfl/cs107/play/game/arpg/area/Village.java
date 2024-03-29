package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.Shop;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Village extends ARPGArea {

    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door("zelda/Farm", new DiscreteCoordinates(4,1), Logic.TRUE, this, Orientation.UP, new DiscreteCoordinates(4,19), new DiscreteCoordinates(5,19)));
        registerActor(new Door("zelda/Farm", new DiscreteCoordinates(14,1), Logic.TRUE, this, Orientation.UP, new DiscreteCoordinates(13,19), new DiscreteCoordinates(14,19), new DiscreteCoordinates(15,19)));
        registerActor(new Door("zelda/Road", new DiscreteCoordinates(9,1), Logic.TRUE, this, Orientation.UP, new DiscreteCoordinates(29,19), new DiscreteCoordinates(30,19)));
        registerActor(new Shop(this, Orientation.DOWN, new DiscreteCoordinates(17, 11)));
    }

    @Override
    public String getTitle() {
        return"zelda/Village";
    }
}
