package lawnlayer;

public enum Direction {

    UP, DOWN, LEFT, RIGHT, NONE;

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