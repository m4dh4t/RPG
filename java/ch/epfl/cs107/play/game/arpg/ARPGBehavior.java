package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.game.areagame.AreaBehavior;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.FlyableEntity;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public class ARPGBehavior extends AreaBehavior {

    /**
     * AreaBehavior Constructor
     *
     * @param window (Window): graphic context, not null
     * @param name   (String): name of the behavior image, not null
     */
    public ARPGBehavior(Window window, String name){
        super(window, name);

        for(int y = 0; y < getHeight(); y++){
            for(int x = 0; x < getWidth(); x++){
                ARPGCellType color = ARPGCellType.toType(getRGB(getHeight()-1-y, x));
                setCell(x, y, new ARPGCell(x, y, color));
            }
        }
    }

    public enum ARPGCellType {
        NULL(0, false, false),
        WALL(-16777216, false, false),         // #000000, RGB code of black
        IMPASSABLE(-8750470, false, true),    // #7A7A7A, RGB code of gray
        INTERACT(-256, true, true),           // #FFFF00, RGB code of yellow
        DOOR(-195580, true, true),            // #FD0404, RGB code of red
        WALKABLE(-1, true, true);             // #FFFFFF, RGB code of white

        final int type;
        final boolean isWalkable;
        final boolean isFlyable;

        ARPGCellType(int type, boolean isWalkable, boolean isFlyable){
            this.type = type;
            this.isWalkable = isWalkable;
            this.isFlyable = isFlyable;
        }

        public static ARPGCellType toType(int type){
            for(ARPGCellType ict : ARPGCellType.values()){
                if(ict.type == type)
                    return ict;
            }
            // When you add a new color, you can print the int value here before assign it to a type
            System.out.println(type);
            return NULL;
        }
    }

    public class ARPGCell extends AreaBehavior.Cell {
        /// Type of the cell following the enum
        private final ARPGCellType type;

        /**
         * Default ARPGCell Constructor
         * @param x (int): x coordinate of the cell
         * @param y (int): y coordinate of the cell
         * @param type (EnigmeCellType), not null
         */
        private ARPGCell(int x, int y, ARPGBehavior.ARPGCellType type){
            super(x, y);
            this.type = type;
        }

        @Override
        protected boolean canLeave(Interactable entity) {
            return true;
        }

        @Override
        protected boolean canEnter(Interactable entity) {
            if (entity instanceof FlyableEntity && ((FlyableEntity) entity).canFly()) { //Doesn't make a ClassCastException if entity isn't a FlyableEntity because it first checks if it is one
                return type.isFlyable;
            } else {
                return type.isWalkable && (!hasNonTraversableContent() || !entity.takeCellSpace());
            }
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
        }

    }
}
