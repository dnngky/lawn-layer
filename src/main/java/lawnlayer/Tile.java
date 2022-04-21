package lawnlayer;

import processing.core.PImage;

public class Tile extends GameObject {

    Direction orientation;

    protected Tile(PImage sprite) {

        super(sprite);
        orientation = Direction.NONE;
    }

    protected Tile(PImage sprite, int x, int y) {

        super(sprite, x, y);
        orientation = Direction.NONE;
    }

    protected Tile(PImage sprite, int x, int y, String name) {

        super(sprite, x, y);
        this.name = name;
        orientation = Direction.NONE;
    }

    public void setOrientation(Direction orientation) {

        this.orientation = orientation;
    }

    public Direction getOrientation() {

        return orientation;
    }

    public Direction getOppositeOrientation() {

        switch (orientation) {

            case UP:
                return Direction.DOWN;
            case DOWN:
                return Direction.UP;
            case LEFT:
                return Direction.RIGHT;
            case RIGHT:
                return Direction.LEFT;
            default:
                return Direction.NONE;
        }
    }

    public Direction getPerpendicularOrientation() {

        switch (orientation) {

            case UP:
                return Direction.LEFT;
            case DOWN:
                return Direction.RIGHT;
            case LEFT:
                return Direction.UP;
            case RIGHT:
                return Direction.DOWN;
            default:
                return Direction.NONE;
        }
    }

    public boolean isAdjacentTo(Tile other) {

        return ((Math.abs(x - other.getX()) == size && y == other.getY()) ||
                (Math.abs(y - other.getY()) == size && x == other.getX()));
    }

    public boolean isAdjacentTo(TileList otherTiles) {

        for (Tile other : otherTiles.toArray()) {

            if (this.isAdjacentTo(other))
                return true;
        }
        return false;
    }

    public boolean isOppositeTo(Tile other) {

        return (orientation == other.getOppositeOrientation());
    }

    public boolean isParallelTo(Tile other) {

        return (orientation == other.getOrientation());
    }
    
    public boolean isPerpendicularTo(Tile other) {

        if (orientation == Direction.UP ||
            orientation == Direction.DOWN)

            return (other.getOrientation() == Direction.LEFT ||
                    other.getOrientation() == Direction.RIGHT);

        if (orientation == Direction.LEFT ||
            orientation == Direction.RIGHT)

            return (other.getOrientation() == Direction.UP ||
                    other.getOrientation() == Direction.DOWN);
        
        return false;
    }

    public boolean isInsideRegion(TileList borderTiles, TileList fillTiles,
        Direction direction) {

        int x = this.x;
        int y = this.y;
        boolean condition;
        int incr;

        switch (direction) {

            case UP:
                condition = y > 0;
                incr = -size;
                break;

            case DOWN:
                condition = y < (Info.HEIGHT - size);
                incr = size;
                break;

            case LEFT:
                condition = x > 0;
                incr = -size;
                break;

            case RIGHT:
                condition = x < (Info.WIDTH - size);
                incr = size;
                break;

            default:
                condition = false;
                incr = 0;
                break;
        }
        while (condition) {

            Tile positionTile = new Tile(sprite, x, y);

            if (borderTiles.contains(positionTile) ||
                fillTiles.contains(positionTile))
                return true;

            if (direction == Direction.UP || direction == Direction.DOWN) {
                y += incr;
                condition = updateCondition(y, direction);
            }
            else {
                x += incr;
                condition = updateCondition(x, direction);
            }
        }
        return false;
    }

    private boolean updateCondition(int n, Direction direction) {

        switch (direction) {

            case UP:
                return n > 0;
            case DOWN:
                return n < (Info.HEIGHT - size);
            case LEFT:
                return n > 0;
            case RIGHT:
                return n < (Info.WIDTH - size);
            default:
                return false;
        }
    }

    public boolean equals(Object other) {

        if (this == other)
            return true;

        if (!(other instanceof Tile))
            return false;

        Tile otherTile = (Tile) other;

        return (this.getX() == otherTile.getX() &&
                this.getY() == otherTile.getY() &&
                this.getName() == otherTile.getName());
    }

}