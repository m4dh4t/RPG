package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Monster;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DarkLord extends Monster {
    private final static int ANIMATION_DURATION = getAnimationDuration();
    private final static float MAXHP = 6;
    private final static int FIELD_OF_VIEW_RADIUS = 3;
    private final static float MIN_SPELL_WAIT_DURATION = 4.f; //in seconds
    private final static float MAX_SPELL_WAIT_DURATION = 5.f; //in seconds
    private final static double CHOOSE_SPELL_NUMBER = 0.3; //A random number between 0 and 1 will be chosen. If it is above this number, darkLord will attack and will cast a spell if below
    private static final int FIRE_SPELL_FORCE = 6;
    private static final float TELEPORTATION_COOLDOWN = 2.f;

    private DarkLordHandler handler;

    private float remainingTPCooldown;
    private float cycle;
    private DarkLordState currentState;
    /**
     * DarkLord constructor
     *
     * @param area            (Area): Owner area. Not null
     * @param orientation     (Orientation): Initial orientation of the entity. Not null
     * @param position        (Coordinate): Initial position of the entity. Not null
     */
    public DarkLord(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, MAXHP, Collections.singletonList(Vulnerability.MAGIC), "zelda/darkLord", 3, new Orientation[] {Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT});
        handler = new DarkLordHandler();
        remainingTPCooldown = TELEPORTATION_COOLDOWN;
        cycle = MIN_SPELL_WAIT_DURATION + RandomGenerator.getInstance().nextFloat() * (MAX_SPELL_WAIT_DURATION - MIN_SPELL_WAIT_DURATION);
        currentState = new DarkLordIdle();
    }

    @Override
    protected void spawnCollectables() {
        CastleKey key = new CastleKey(getOwnerArea(), new DiscreteCoordinates((int) getPosition().x, (int) getPosition().y));
        getOwnerArea().registerActor(key);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        ArrayList<DiscreteCoordinates> list = new ArrayList<>();

        for (int i = - FIELD_OF_VIEW_RADIUS; i <= FIELD_OF_VIEW_RADIUS; ++i) {
            for (int j = - FIELD_OF_VIEW_RADIUS; j <= FIELD_OF_VIEW_RADIUS; ++j) {
                list.add(new DiscreteCoordinates(getCurrentMainCellCoordinates().x + i, getCurrentMainCellCoordinates().y + j));
            }
        }

        return list;
    }

    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    @Override
    public boolean wantsViewInteraction() {
        return !isDead();
    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    @Override
    public void update(float deltaTime) {
        if (!isDead()) {
            cycle -= deltaTime;
            remainingTPCooldown -= deltaTime;

            if (remainingTPCooldown <= 0) {
                remainingTPCooldown = 0;
            }

            if (cycle <= 0) {
                if (RandomGenerator.getInstance().nextDouble() > CHOOSE_SPELL_NUMBER) {
                    //FIRE SPELL
                    for (int i = 0; i < Orientation.values().length; ++i) {
                        Orientation orientation = Orientation.values()[RandomGenerator.getInstance().nextInt(Orientation.values().length)]; //Chooses a random orientation
                        DiscreteCoordinates frontCell = getCurrentMainCellCoordinates().jump(orientation.toVector());
                        FireSpell spell = new FireSpell(getOwnerArea(), orientation, frontCell, FIRE_SPELL_FORCE);

                        if (getOwnerArea().canEnterAreaCells(spell, Collections.singletonList(frontCell))) {
                            orientate(orientation);
                        }
                    }

                    currentState = new DarkLordAttacking();
                } else {
                    //FIRE SKULL
                    currentState = new DarkLordCastingSpell();
                }

                cycle = MIN_SPELL_WAIT_DURATION + RandomGenerator.getInstance().nextFloat() * (MAX_SPELL_WAIT_DURATION - MIN_SPELL_WAIT_DURATION);
            }

            currentState.update(deltaTime);
        }

        super.update(deltaTime);
    }

    private class DarkLordHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(ARPGPlayer player) {
            if (!(currentState instanceof DarkLordTP)) {
                currentState = new DarkLordCastingTP();
            }
        }
    }

    private abstract class DarkLordState {

        DarkLordState(String spriteName) {
            Sprite[][] sprites = RPGSprite.extractSprites(spriteName, 3, 2.f, 2.f, DarkLord.this, 32, 32, new Vector(-0.5f, 0), new Orientation[] {Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT});
            Animation[] animations = RPGSprite.createAnimations(ANIMATION_DURATION/2, sprites);
            setAnimations(animations, getOrientation());
        }

        public abstract void update(float deltaTime);
    }

    private class DarkLordIdle extends DarkLordState {
        private static final double PROBABILITY_TO_CHANGE_DIRECTION = 0.25;
        private static final double PROBABILITY_TO_GO_INACTIVE = 0.05;
        private final static float MAX_INACTIVE_DURATION = 1.5f; //in seconds

        private boolean inactive;
        private float inactiveTimeLeft;

        DarkLordIdle() {
            super("zelda/darkLord");
            inactive = true;
            inactiveTimeLeft = RandomGenerator.getInstance().nextFloat() * MAX_INACTIVE_DURATION;
        }

        @Override
        public void update(float deltaTime) {
            inactiveTimeLeft -= deltaTime;

            if (inactiveTimeLeft <= 0) {
                inactive = false;
            }

            if (!inactive) {
                randomMove(true);
            }
        }
    }

    private class DarkLordAttacking extends DarkLordState {
        DarkLordAttacking() {
            super("zelda/darkLord.spell");
        }

        @Override
        public void update(float deltaTime) {
            DiscreteCoordinates frontCell = getCurrentMainCellCoordinates().jump(getOrientation().toVector());
            FireSpell spell = new FireSpell(getOwnerArea(), getOrientation(), frontCell, FIRE_SPELL_FORCE);
            getOwnerArea().registerActor(spell);
            currentState = new DarkLordIdle();
        }
    }

    private class DarkLordCastingSpell extends DarkLordState {
        DarkLordCastingSpell() {
            super("zelda/darkLord.spell");
        }

        @Override
        public void update(float deltaTime) {
            FlameSkull skull = new FlameSkull(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates());
            getOwnerArea().registerActor(skull);
            currentState = new DarkLordIdle();
        }
    }

    private class DarkLordCastingTP extends DarkLordState {
        DarkLordCastingTP() {
            super("zelda/darkLord.spell");
        }

        @Override
        public void update(float deltaTime) {
            if (!isDisplacementOccurs()) {
                currentState = new DarkLordTP();
            }
        }
    }

    private class DarkLordTP extends DarkLordState {
        private final static int TELEPORTATION_RADIUS = 5;

        DarkLordTP() {
            super("zelda/darkLord");
        }

        @Override
        public void update(float deltaTime) {
            if (remainingTPCooldown == 0) {
                final int MAX_TRIES = 4;
                int tries = 0;
                boolean success = false;

                while (!success && tries < MAX_TRIES) {
                    int x = getCurrentMainCellCoordinates().x - TELEPORTATION_RADIUS + RandomGenerator.getInstance().nextInt(2 * TELEPORTATION_RADIUS + 1);
                    int y = getCurrentMainCellCoordinates().y - TELEPORTATION_RADIUS + RandomGenerator.getInstance().nextInt(2 * TELEPORTATION_RADIUS + 1);
                    DiscreteCoordinates coordinates = new DiscreteCoordinates(x, y);

                    success = getOwnerArea().canEnterAreaCells(DarkLord.this, Collections.singletonList(coordinates));

                    if (success) {
                        getOwnerArea().leaveAreaCells(DarkLord.this, getCurrentCells());
                        setCurrentPosition(coordinates.toVector());
                    }

                    ++tries;
                }
                remainingTPCooldown = TELEPORTATION_COOLDOWN;
            }

            currentState = new DarkLordIdle();
        }
    }
}
