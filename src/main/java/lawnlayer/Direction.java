package lawnlayer;

/**
 * An enum indicating the direction the Player is moving in,
 * as well as the orientation of Tiles.
 */
public enum Direction {

    UP, DOWN, LEFT, RIGHT, NONE;

    /**
     * Flips the current direction by 180 degrees.
     * 
     * @return the opposite direction.
     */
    public Direction flip() {

        switch (this) {

            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            default:
                return null;
        }
    }

    /**
     * Flips the current direction by 90 degrees.
     * 
     * @return the normal direction
     */
    public Direction normal() {

        switch (this) {

            case UP:
                return Direction.RIGHT;
            case DOWN:
                return Direction.LEFT;
            case LEFT:
                return Direction.UP;
            case RIGHT:
                return Direction.DOWN;
            default:
                return Direction.NONE;
        }
    }
}