package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.actor.CastleDoor;
import ch.epfl.cs107.play.game.arpg.actor.FlameSkull;
import ch.epfl.cs107.play.game.arpg.actor.LogMonster;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Keyboard;

public class RoadCastle extends ARPGArea {
    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door("zelda/Road", new DiscreteCoordinates(9,18), Logic.TRUE, this, Orientation.DOWN, new DiscreteCoordinates(9,0), new DiscreteCoordinates(10,0)));
        registerActor(new CastleDoor("zelda/Castle", new DiscreteCoordinates(7,1), this, Orientation.UP, new DiscreteCoordinates(9,13), new DiscreteCoordinates(10,13)));
    }

    @Override
    public void update(float deltaTime) {
        if (getKeyboard().get(Keyboard.S).isPressed()) {
            registerActor(new FlameSkull(this, Orientation.DOWN, new DiscreteCoordinates(8,10)));
        }

        if (getKeyboard().get(Keyboard.B).isPressed()) {
            registerActor(new Bomb(this, new DiscreteCoordinates(8,11), 10));
        }

        if (getKeyboard().get(Keyboard.L).isPressed()) {
            registerActor(new LogMonster(this, Orientation.DOWN, new DiscreteCoordinates(9,9)));
        }
        super.update(deltaTime);
    }

    @Override
    public String getTitle() {
        return "zelda/RoadCastle";
    }
}
