package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Monster;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.game.rpg.actor.Monster.Vulnerability;

import java.util.Collections;
import java.util.List;

public class FireSpell extends AreaEntity implements Interactor {
    private final static int ANIMATION_DURATION = 8;
    private final static float MIN_LIFE_TIME = 5.f; //in seconds
    private final static float MAX_LIFE_TIME = 10.f; //in seconds
    private final static float PROPAGATION_TIME_FIRE = 0.5f; //in seconds

    private float lifeTime;
    private float countDown;
    private boolean summoned;
    private int force;

    private Animation animation;
    private FireSpellHandler handler;

    /**
     * FireSpell constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity in the Area. Not null
     * @param position    (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public FireSpell(Area area, Orientation orientation, DiscreteCoordinates position, int force) {
        super(area, orientation, position);
        lifeTime = MIN_LIFE_TIME + RandomGenerator.getInstance().nextFloat() * (MAX_LIFE_TIME - MIN_LIFE_TIME);
        countDown = PROPAGATION_TIME_FIRE;
        summoned = false;
        this.force = force;
        handler = new FireSpellHandler();

        Sprite[] sprites = RPGSprite.extractSprites("zelda/fire", 7, 1.f, 1.f, this, 16, 16);
        animation = new Animation(ANIMATION_DURATION, sprites, true);
    }

    public void summon() {
        if (force > 1) {
            DiscreteCoordinates spellPosition = getCurrentMainCellCoordinates().jump(getOrientation().toVector());
            FireSpell spell = new FireSpell(getOwnerArea(), getOrientation(), spellPosition, force - 1);
            if (getOwnerArea().canEnterAreaCells(spell, Collections.singletonList(spellPosition))) {
                getOwnerArea().registerActor(spell);
            }
        }
    }

    public void extinguish(){
        getOwnerArea().unregisterActor(this);
    }

    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return null;
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor)v).interactWith(this);
    }

    @Override
    public void update(float deltaTime) {
        lifeTime -= deltaTime;
        countDown -= deltaTime;

        if (lifeTime <= 0) {
            getOwnerArea().unregisterActor(this);
        } else {
            animation.update(deltaTime);

            if (!summoned && countDown <= 0) {
                summon();
                summoned = true;
            }
        }

        super.update(deltaTime);
    }

    private class FireSpellHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(ARPGPlayer player) {
            player.weaken(0.5f);
        }

        @Override
        public void interactWith(Monster monster) {
            monster.weaken(0.5f, Vulnerability.FIRE);
        }

        @Override
        public void interactWith(Grass grass) {
            grass.burn();
        }

        @Override
        public void interactWith(Bomb bomb) {
            bomb.explode();
        }
    }
}
