package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.Vulnerability;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlameSkull extends Monster implements FlyableEntity {
    private final static int ANIMATION_DURATION = 8;
    private final static double PROBABILITY_TO_CHANGE_DIRECTION = 0.1;
    private final static int MIN_LIFE_TIME = 3;
    private final static int MAX_LIFE_TIME = 12;
    private final static float MAXHP = 0.5f;

    private float lifeTime;
    private FlameSkullHandler handler;

    /**
     * FlameSkull constructor
     *
     * @param area            (Area): Owner area. Not null
     * @param orientation     (Orientation): Initial orientation of the entity. Not null
     * @param position        (Coordinate): Initial position of the entity. Not null
     */
    public FlameSkull(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, MAXHP, new ArrayList<>(Arrays.asList(Vulnerability.PHYSICAL, Vulnerability.MAGIC)), "zelda/flameSkull", 3 , new Orientation[] {Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT});
        handler = new FlameSkullHandler();
        lifeTime = MIN_LIFE_TIME + RandomGenerator.getInstance().nextInt(MAX_LIFE_TIME - MIN_LIFE_TIME);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return null;
    }

    @Override
    public boolean wantsCellInteraction() {
        return !isDead();
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    void spawnCollectables() {

    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    private void move(Orientation orientation) {
        orientate(orientation);
        move(ANIMATION_DURATION);
        animate(orientation);

    }

    @Override
    public void update(float deltaTime) {
        if (!isDead()) {
            if (RandomGenerator.getInstance().nextDouble() < PROBABILITY_TO_CHANGE_DIRECTION) {
                switch (RandomGenerator.getInstance().nextInt(4)) {
                    case 0:
                        move(Orientation.LEFT);
                        break;
                    case 1:
                        move(Orientation.UP);
                        break;
                    case 2:
                        move(Orientation.RIGHT);
                        break;
                    case 3:
                        move(Orientation.DOWN);
                        break;
                }
            }
            lifeTime -= deltaTime;

            if (lifeTime <= 0) {
                die();
            }


        }

        super.update(deltaTime);
    }

    class FlameSkullHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(Monster monster) {
            monster.weaken(1.f, Vulnerability.FIRE);
        }

        @Override
        public void interactWith(ARPGPlayer player) {
            player.weaken(1.f);
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
