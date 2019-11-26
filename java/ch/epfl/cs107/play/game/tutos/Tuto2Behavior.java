package ch.epfl.cs107.play.game.tutos;

import ch.epfl.cs107.play.game.areagame.AreaBehavior;
import ch.epfl.cs107.play.game.areagame.Cell;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public class Tuto2Behavior extends AreaBehavior {
    public Tuto2Behavior(Window window, String name){
        super(window, name);

        for(int y = 0; y < getHeight(); y++){
            for(int x = 0; x < getWidth(); x++){
                Tuto2CellType color = Tuto2CellType.toType(getRGB(getHeight()-1-y, x));
                setCell(x, y, new Tuto2Cell(x, y, color));
            }
        }
    }

    public boolean isDoor(DiscreteCoordinates coord) {
        return (((Tuto2Cell)getCell(coord.x, coord.y)).isDoor());
    }

    public enum Tuto2CellType {
        NULL(0, false),
        WALL(-16777216, false),         // #000000, RGB code of black
        IMPASSABLE(-8750470, false),    // #7A7A7A, RGB code of gray
        INTERACT(-256, true),           // #FFFF00, RGB code of yellow
        DOOR(-195580, true),            // #FD0404, RGB code of red
        WALKABLE(-1, true);             // #FFFFFF, RGB code of white

        final int type;
        final boolean isWalkable;

        Tuto2CellType(int type, boolean isWalkable){
            this.type = type;
            this.isWalkable = isWalkable;
        }

        public static Tuto2CellType toType(int type){
            for(Tuto2CellType ict : Tuto2CellType.values()){
                if(ict.type == type)
                    return ict;
            }
            // When you add a new color, you can print the int value here before assign it to a type
            System.out.println(type);
            return NULL;
        }
    }
}

class Tuto2Cell extends Cell {
    /// Type of the cell following the enum
    private final Tuto2Behavior.Tuto2CellType type;

    /**
     * Default Tuto2Cell Constructor
     * @param x (int): x coordinate of the cell
     * @param y (int): y coordinate of the cell
     * @param type (EnigmeCellType), not null
     */
    public Tuto2Cell(int x, int y, Tuto2Behavior.Tuto2CellType type){
        super(x, y);
        this.type = type;
    }

    public boolean isDoor() {
        return type == Tuto2Behavior.Tuto2CellType.DOOR;
    }

    @Override
    protected boolean canLeave(Interactable entity) {
        return true;
    }

    @Override
    protected boolean canEnter(Interactable entity) {
        return type.isWalkable;
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