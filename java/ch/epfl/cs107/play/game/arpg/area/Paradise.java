package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.arpg.actor.God;
import ch.epfl.cs107.play.game.arpg.actor.WhiteHalo;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Paradise extends ARPGArea {
    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new God(this, new DiscreteCoordinates(11, 10)));
        registerActor(new WhiteHalo(this, new DiscreteCoordinates(11, 9)));
    }

    @Override
    public String getTitle() {
        return "Paradise";
    }
}
