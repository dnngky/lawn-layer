package lawnlayer;

import processing.core.PImage;

public class Tile extends GameObject {
    
    private Direction orientation;
    private boolean isCollided;
    private int frameOfCollision;

    public Tile(int x, int y) {

        super(x, y);
        this.name = "UnnamedTile";

        orientation = Direction.NONE;
        isCollided = false;
        frameOfCollision = 0;
    }

    public Tile(PImage sprite, int x, int y) {

        super(sprite, x, y);
        this.name = "UnnamedTile";

        orientation = Direction.NONE;
        isCollided = false;
        frameOfCollision = 0;
    }

    public Tile(PImage sprite, int x, int y, String name) {

        super(sprite, x, y, name);

        orientation = Direction.NONE;
        isCollided = false;
        frameOfCollision = 0;
    }

    public boolean isCollided() {

        return isCollided;
    }

    public void setOrientation(Direction orientation) {

        this.orientation = orientation;
    }

    public int getFrameOfCollision() {

        return frameOfCollision;
    }

    public Direction getOrientation() {

        return orientation;
    }

    public Direction getOppositeOrientation() {

        return orientation.flip();
    }

    public Direction getPerpendicularOrientation() {

        return orientation.normal();
    }

    public Tile getAdjacentTile(Direction direction) {

        Tile adjacentTile;

        switch (direction) {

            case UP:
                adjacentTile = new Tile(sprite, x, y - size, name);
                adjacentTile.setOrientation(Direction.UP);
                break;
            
            case DOWN:
                adjacentTile = new Tile(sprite, x, y + size, name);
                adjacentTile.setOrientation(Direction.DOWN);
                break;
            
            case LEFT:
                adjacentTile = new Tile(sprite, x - size, y, name);
                adjacentTile.setOrientation(Direction.LEFT);
                break;
            
            case RIGHT:
                adjacentTile = new Tile(sprite, x + size, y, name);
                adjacentTile.setOrientation(Direction.RIGHT);
                break;

            default:
                adjacentTile = new Tile(sprite, x, y, name);
                break;
        }
        return adjacentTile;
    }

    public TileList getAdjacentTiles() {

        Tile top = getAdjacentTile(Direction.UP);
        Tile bottom = getAdjacentTile(Direction.DOWN);
        Tile left = getAdjacentTile(Direction.LEFT);
        Tile right = getAdjacentTile(Direction.RIGHT);

        return new TileList(new Tile[] {top, bottom, left, right});
    }
    
    public boolean isVerticallyAdjacentTo(Tile other) {

        return (Math.abs(y - other.getY()) == size && x == other.getX());
    }

    public boolean isHorizontallyAdjacentTo(Tile other) {

        return (Math.abs(x - other.getX()) == size && y == other.getY());
    }

    public boolean isVerticallyAdjacentTo(TileList otherTiles) {

        for (Tile other : otherTiles.toArray()) {

            if (isVerticallyAdjacentTo(other))
                return true;
        }
        return false;
    }

    public boolean isHorizontallyAdjacentTo(TileList otherTiles) {

        for (Tile other : otherTiles.toArray()) {

            if (isHorizontallyAdjacentTo(other))
                return true;
        }
        return false;
    }

    public boolean isAdjacentTo(Tile other) {

        return (isVerticallyAdjacentTo(other) ||
                isHorizontallyAdjacentTo(other));
    }

    public boolean isAdjacentTo(TileList otherTiles) {

        for (Tile other : otherTiles.toArray()) {

            if (isAdjacentTo(other))
                return true;
        }
        return false;
    }

    public boolean isAdjacentTo(TileList otherTiles1, TileList otherTiles2) {

        for (Tile other : otherTiles1.toArray()) {

            if (isAdjacentTo(other))
                return true;
        }
        for (Tile other : otherTiles2.toArray()) {

            if (isAdjacentTo(other))
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
    
    public boolean isNormalTo(Tile other) {

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

    public boolean isSurroundedBy(TileList otherTiles) {

        Tile top = getAdjacentTile(Direction.UP);
        Tile bottom = getAdjacentTile(Direction.DOWN);
        Tile left = getAdjacentTile(Direction.LEFT);
        Tile right = getAdjacentTile(Direction.RIGHT);

        return (otherTiles.contains(top) &&
                otherTiles.contains(bottom) &&
                otherTiles.contains(left) &&
                otherTiles.contains(right));
    }

    public boolean isFloating(TileList otherTiles) {

        Tile top = getAdjacentTile(Direction.UP);
        Tile bottom = getAdjacentTile(Direction.DOWN);
        Tile left = getAdjacentTile(Direction.LEFT);
        Tile right = getAdjacentTile(Direction.RIGHT);

        return (!(otherTiles.contains(top) ||
                otherTiles.contains(bottom) ||
                otherTiles.contains(left) ||
                otherTiles.contains(right)));
    }

    public boolean isInsideRegion(TileList borderTiles, TileList fillTiles,
        Direction direction) {

        int x = this.x;
        int y = this.y;
        boolean condition;
        int incr;

        switch (direction) {

            case UP:
                condition = y > Info.TOPBAR;
                incr = -size;
                break;

            case DOWN:
                condition = y < (Info.HEIGHT - size);
                incr = size;
                break;

            case LEFT:
                condition = x > Info.TOPBAR;
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

    public boolean isOutOfBounds() {

        return (x < 0 || x > (Info.WIDTH - size) ||
                y < Info.TOPBAR || y > (Info.HEIGHT - size));
    }

    public void turnRed(PImage redPathSprite, int frameCount) {

        sprite = redPathSprite;
        isCollided = true;
        frameOfCollision = frameCount;
    }
    
    private boolean updateCondition(int n, Direction direction) {

        switch (direction) {

            case UP:
                return n > Info.TOPBAR;
            case DOWN:
                return n < (Info.HEIGHT - size);
            case LEFT:
                return n > Info.TOPBAR;
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
                this.getY() == otherTile.getY());
    }

}