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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DarkLord extends Monster {
    private final static int ANIMATION_DURATION = getAnimationDuration();
    private final static float MAX_HP = 6;
    private final static int FIELD_OF_VIEW_RADIUS = 3;
    private final static float MIN_SPELL_WAIT_DURATION = 4.f; //in seconds
    private final static float MAX_SPELL_WAIT_DURATION = 5.f; //in seconds
    private final static double CHOOSE_SPELL_NUMBER = 0.3; //A random number between 0 and 1 will be chosen.
    // If it is above this number, darkLord will attack and will cast a spell if below
    private final static int FIRE_SPELL_FORCE = 6;
    private final static float TELEPORTATION_COOLDOWN = 1.f;

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
        super(area, orientation, position, MAX_HP, Collections.singletonList(Vulnerability.MAGIC), "zelda/darkLord", 3, new Orientation[] {Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT});
        handler = new DarkLordHandler();
        remainingTPCooldown = TELEPORTATION_COOLDOWN;
        cycle = MIN_SPELL_WAIT_DURATION + RandomGenerator.getInstance().nextFloat() * (MAX_SPELL_WAIT_DURATION - MIN_SPELL_WAIT_DURATION);
        currentState = new DarkLordIdle();
    }

    /**
     * Extends from Monster.java
     */
    @Override
    protected void spawnCollectables() {
        CastleKey key = new CastleKey(getOwnerArea(), new DiscreteCoordinates((int) getPosition().x, (int) getPosition().y));
        getOwnerArea().registerActor(key);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        ArrayList<DiscreteCoordinates> list = new ArrayList<>();

        //Square of length FIELD_OF_VIEW_RADIUS centered on the dark lord.
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
        //We do not want the dark lord to teleport when he is dead
        //(during its death animation).
        return !isDead();
    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    @Override
    public void update(float deltaTime) {
        if (!isDead()) {
            if (!(currentState instanceof DarkLordCastingTP) && !(currentState instanceof DarkLordTP)) {
                cycle -= deltaTime;
                remainingTPCooldown -= deltaTime;

                if (remainingTPCooldown <= 0) {
                    remainingTPCooldown = 0;
                }

                if (cycle <= 0) {
                    if (RandomGenerator.getInstance().nextDouble() > CHOOSE_SPELL_NUMBER) {
                        //FIRE SPELL
                        boolean success = false;

                        ArrayList<Orientation> orientationsToTest = new ArrayList<>(Arrays.asList(Orientation.values()));

                        do {
                            //Chooses a random orientation between 0 and the number of remaining orientations to test
                            int randomNumber = RandomGenerator.getInstance().nextInt(orientationsToTest.size());
                            Orientation orientation = orientationsToTest.get(randomNumber);


                            DiscreteCoordinates frontCell = getCurrentMainCellCoordinates().jump(orientation.toVector());
                            FireSpell spell = new FireSpell(getOwnerArea(), orientation, frontCell, FIRE_SPELL_FORCE);

                            if (getOwnerArea().canEnterAreaCells(spell, Collections.singletonList(frontCell))) {
                                orientate(orientation);
                                currentState = new DarkLordAttacking();
                                success = true;
                            } else {
                                orientationsToTest.remove(orientation);
                            }

                        } while (!success && !orientationsToTest.isEmpty());
                    } else {
                        //FIRE SKULL
                        currentState = new DarkLordCastingSpell();
                    }

                    //Resets cycle
                    cycle = MIN_SPELL_WAIT_DURATION + RandomGenerator.getInstance().nextFloat() * (MAX_SPELL_WAIT_DURATION - MIN_SPELL_WAIT_DURATION);
                }
            }
            //Updates its current state
            currentState.update(deltaTime);
        }

        super.update(deltaTime);
    }

    private class DarkLordHandler implements ARPGInteractionVisitor {
        @Override
        public void interactWith(ARPGPlayer player) {
            //Checks if its cooldown is 0
            if (!(currentState instanceof DarkLordCastingTP) && !(currentState instanceof DarkLordTP) && remainingTPCooldown == 0) {
                currentState = new DarkLordCastingTP();
            }
        }
    }

    private abstract class DarkLordState {

        DarkLordState(String spriteName, boolean repeat) {
            Sprite[][] sprites = RPGSprite.extractSprites(spriteName, 3, 2.f, 2.f, DarkLord.this, 32, 32, new Vector(-0.5f, 0), new Orientation[] {Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT});
            Animation[] animations = RPGSprite.createAnimations(ANIMATION_DURATION/2, sprites, repeat);
            setAnimations(animations, getOrientation());
        }

        DarkLordState(String spriteName) {
            this(spriteName, true);
        }

        public abstract void update(float deltaTime); //Every subclass needs to override this method
    }

    private class DarkLordIdle extends DarkLordState {
        private final static float MAX_INACTIVE_DURATION = 1.5f; //in seconds

        private boolean inactive;
        private float inactiveTimeLeft;

        DarkLordIdle() {
            super("zelda/darkLord");
            inactive = true;
            inactiveTimeLeft = RandomGenerator.getInstance().nextFloat() * MAX_INACTIVE_DURATION;
            setForceAnimation(false); //See Monster.java to understand this call
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
        private boolean attacked;

        DarkLordAttacking() {
            super("zelda/darkLord.spell", false);
            setForceAnimation(true);
            attacked = false;
            //Need an attacked attribute to know if the dark lord already attacked
            //to prevent it to attack every frame until its animation is completed
        }

        @Override
        public void update(float deltaTime) {
            if (!attacked) {
                DiscreteCoordinates frontCell = getCurrentMainCellCoordinates().jump(getOrientation().toVector());
                FireSpell spell = new FireSpell(getOwnerArea(), getOrientation(), frontCell, FIRE_SPELL_FORCE);
                getOwnerArea().registerActor(spell);
            }

            if (isAnimationCompleted()) {
                currentState = new DarkLordIdle();
            }
        }
    }

    private class DarkLordCastingSpell extends DarkLordState {
        private boolean casted;

        DarkLordCastingSpell() {
            super("zelda/darkLord.spell", false);
            setForceAnimation(true);
            casted = false;
            //The casted attribute has the same function as the attacked
            //attribute in DarkLordAttacking
        }

        @Override
        public void update(float deltaTime) {
            if (!casted) {
                FlameSkull skull = new FlameSkull(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates());
                getOwnerArea().registerActor(skull);
                casted = true;
            }

            if (isAnimationCompleted()) {
                currentState = new DarkLordIdle();
            }
        }
    }

    private class DarkLordCastingTP extends DarkLordState {
        DarkLordCastingTP() {
            super("zelda/darkLord.spell", false);
            setForceAnimation(true);
        }

        @Override
        public void update(float deltaTime) {
            if (!isDisplacementOccurs() && isAnimationCompleted()) {
                currentState = new DarkLordTP();
            }
        }
    }

    private class DarkLordTP extends DarkLordState {
        private final static int TELEPORTATION_RADIUS = 5;

        DarkLordTP() {
            super("zelda/darkLord");
            setForceAnimation(false);
        }

        @Override
        public void update(float deltaTime) {
            final int MAX_TRIES = 4;
            int tries = 0;
            boolean success = false;

            while (!success && tries < MAX_TRIES) {
                int x = getCurrentMainCellCoordinates().x - TELEPORTATION_RADIUS + RandomGenerator.getInstance().nextInt(2 * TELEPORTATION_RADIUS + 1);
                int y = getCurrentMainCellCoordinates().y - TELEPORTATION_RADIUS + RandomGenerator.getInstance().nextInt(2 * TELEPORTATION_RADIUS + 1);
                DiscreteCoordinates coordinates = new DiscreteCoordinates(x, y);

                success = getOwnerArea().canEnterAreaCells(DarkLord.this, Collections.singletonList(coordinates));

                if (success) {
                    //Need to leave and enter the old and new cells to prevent creating invisible walls when teleporting
                    getOwnerArea().leaveAreaCells(DarkLord.this, getCurrentCells());
                    getOwnerArea().enterAreaCells(DarkLord.this, Collections.singletonList(coordinates));
                    setCurrentPosition(coordinates.toVector());
                }

                ++tries;
            }

            //Resets cooldown
            remainingTPCooldown = TELEPORTATION_COOLDOWN;

            currentState = new DarkLordIdle();
        }
    }
}
