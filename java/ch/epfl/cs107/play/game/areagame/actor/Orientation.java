package ch.epfl.cs107.play.game.areagame.actor;

import ch.epfl.cs107.play.math.Vector;


public enum Orientation {

    /// Enumeration elements
    UP (new Vector(0.0f, 1.0f)),
    RIGHT ( new Vector(1.0f, 0.0f)),
    DOWN (new Vector(0.0f, -1.0f)),
    LEFT (new Vector(-1.0f, 0.0f));


    /// Direction of the Orientation
    private final Vector direction;

    /**
     * Default Orientation constructor
     * @param direction (Vector). Not null
     */
    Orientation(Vector direction){
        this.direction = direction;
    }

    /**
     * Return the opposite Orientation
     * @return (Orientation): the opposite orientation Down:Up, Right:Left
     */
    public Orientation opposite(){
        return Orientation.values()[(ordinal()+2)%4];
    }

    /** @return (Orientation): the orientation on the left of this*/
    public Orientation hisLeft(){
        // Be careful, % return the reminder and not the modulus i.e. could be negative
        // It is why we do this trick +4)%4
        return Orientation.values()[(((ordinal()-1)%4)+4)%4];
    }

    /** @return (Orientation): the orientation on the right of this*/
    public Orientation hisRight(){
        return Orientation.values()[(ordinal()+1)%4];
    }

    /**
     * Convert an orientation into vector
     * @return (Vector)
     */
    public Vector toVector(){
        return direction;
    }

    /**
     * Convert an int into an orientation
     * @param index the int representing the orientation
     * @return the orientation
     */
    public static Orientation fromInt(int index) {
		switch(index) {
			case 0: return Orientation.UP;
			case 1: return Orientation.RIGHT;
			case 2: return Orientation.DOWN;
			case 3: return Orientation.LEFT;
		}
		return null;
	}
    
    /**
     * Convert a vector into an orientation
     * @param v the vector representing the orientation
     * @return the orientation
     */
    public static Orientation fromVector(Vector v) {
    	if(v.x > 0 && v.y == 0)
    		return Orientation.RIGHT;
    	if(v.x < 0 && v.y == 0)
    		return Orientation.LEFT;
    	if(v.y > 0 && v.x == 0)
    		return Orientation.UP;
    	if(v.y < 0 && v.x == 0)
    		return Orientation.DOWN;
		return null;
	}

    @Override
    public String toString(){
        return super.toString()+direction.toString();
    }

    /**
     * Method used in ARGPlayer to prevent a bug that would allow the player to orientate if a key was pressed while another
     * one was down while facing a wall
     * @param orientation   The orientation we need the code for
     * @return The code of the orientation given
     */
    public static int getCode(Orientation orientation) {
        switch (orientation) {
            case LEFT:
                return 37;
            case UP:
                return 38;
            case RIGHT:
                return 39;
            case DOWN:
                return 40;
            default:
                return -1;
        }
    }
}
