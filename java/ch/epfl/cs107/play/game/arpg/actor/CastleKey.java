package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class CastleKey extends ARPGCollectableAreaEntity {
    private Sprite sprite;

    public CastleKey(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        sprite = new RPGSprite("zelda/key",1.f,1.f,this);
    }

    @Override
    public void draw(Canvas canvas) {
        if (!pickedUp()) {
            sprite.draw(canvas);
        } else {
            getOwnerArea().unregisterActor(this);
        }
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor)v).interactWith(this);
    }
}
