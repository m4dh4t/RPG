package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Monster;
import ch.epfl.cs107.play.game.rpg.actor.Projectile;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class MagicWaterProjectile extends Projectile {
    private static int ANIMATION_DURATION = 4;
    private MagicWaterProjectileHandler handler;
    private Animation animation;

    /**
     * MagicWaterProjectile constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     * @param moveDuration (float): move speed of the arrow
     * @param maxTravel (float): max distance before the magic water projectile disappears
     */
    public MagicWaterProjectile(Area area, Orientation orientation, DiscreteCoordinates position, float moveDuration, float maxTravel) {
        super(area, orientation, position, moveDuration, maxTravel);
        handler = new MagicWaterProjectileHandler();

        Sprite[] sprites = RPGSprite.extractSprites("zelda/magicWaterProjectile",4,1.f,1.f,this,16,16);
        animation = new Animation(ANIMATION_DURATION/2, sprites, true);
    }

    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        animation.update(deltaTime);
        super.update(deltaTime);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {

    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return null;
    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    private class MagicWaterProjectileHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(Monster monster) {
            monster.weaken(1.f, Monster.Vulnerability.MAGIC);
            setStop(true);
        }

        @Override
        public void interactWith(FireSpell fireSpell) {
            fireSpell.extinguish();
        }
    }
}
