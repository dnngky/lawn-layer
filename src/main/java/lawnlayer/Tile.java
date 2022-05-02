package lawnlayer;

import lawnlayer.Info.Name;
import processing.core.PImage;

/**
 * A subclass of GameObject for all tile objects in the game. This includes
 * concrete tiles, grass tiles, border tiles, and path tiles.
 */
public class Tile extends GameObject {
    
    private int frameOfCollision;
    private boolean isRed;
    private Direction orientation;

    /**
     * Initiates a basic tile with no sprite nor name.
     * 
     * @see GameObject#GameObject()
     */
    public Tile() {

        sprite = null;
        name = Name.UNNAMED;

        orientation = Direction.NONE;
        isRed = false;
        frameOfCollision = 0;
    }

    /**
     * Initiates a tile with the specified sprite, xy-coordinates, and name.
     * 
     * @param sprite the PImage sprite of the tile
     * @param x the x-coordinate of the tile
     * @param y the y-coordinate of the tile
     * @param name the name of the tile
     * 
     * @see GameObject#GameObject(PImage, int, int, Name)
     */
    public Tile(PImage sprite, int x, int y, Name name) {

        super(sprite, x, y, name);

        orientation = Direction.NONE;
        isRed = false;
        frameOfCollision = 0;
    }

    /**
     * Overrides the default 'equals' method of the Object superclass.
     * Tile objects are considered 'equal' if they share the same
     * x-y coordinates.
     */
    @Override
    public boolean equals(Object other) {

        if (this == other)
            return true;

        if (!(other instanceof Tile))
            return false;

        Tile otherTile = (Tile) other;

        return (this.getX() == otherTile.getX() &&
                this.getY() == otherTile.getY());
    }

    /**
     * Retrieves the frame in which this Tile was collided.
     * 
     * @return the frame of collision
     */
    public int getFrameOfCollision() {

        return frameOfCollision;
    }

    /**
     * Retrieves the orientation of this Tile.
     * 
     * @return this Tile's orientation
     */
    public Direction getOrientation() {

        return orientation;
    }

    /**
     * Retrieves a Tile adjacent to this Tile in the specified direction.
     * The adjacent Tile shares the same sprite and name as this Tile.
     * Its orientation is set to the direction in which it is adjacent
     * to this Tile.
     * 
     * @param direction the direction of the adjacent Tile
     * @return a new adjacent Tile
     */
    public Tile getAdjacentTile(Direction direction) {

        Tile adjacentTile;

        switch (direction) {

            case UP:
                adjacentTile = new Tile(sprite, x, y - SIZE, name);
                adjacentTile.setOrientation(Direction.UP);
                break;
            
            case DOWN:
                adjacentTile = new Tile(sprite, x, y + SIZE, name);
                adjacentTile.setOrientation(Direction.DOWN);
                break;
            
            case LEFT:
                adjacentTile = new Tile(sprite, x - SIZE, y, name);
                adjacentTile.setOrientation(Direction.LEFT);
                break;
            
            case RIGHT:
                adjacentTile = new Tile(sprite, x + SIZE, y, name);
                adjacentTile.setOrientation(Direction.RIGHT);
                break;

            default:
                adjacentTile = new Tile(sprite, x, y, name);
                break;
        }
        return adjacentTile;
    }

    /**
     * Retrieves a list of Tiles adjacent to this Tile in all four
     * directions.
     * 
     * @return a TileList of four adjacent tiles
     * 
     * @see #getAdjacentTile(Direction)
     */
    public TileList getAdjacentTiles() {

        Tile top = getAdjacentTile(Direction.UP);
        Tile bottom = getAdjacentTile(Direction.DOWN);
        Tile left = getAdjacentTile(Direction.LEFT);
        Tile right = getAdjacentTile(Direction.RIGHT);

        return new TileList(new Tile[] {top, bottom, left, right});
    }

    /**
     * Checks whether this Tile is adjacent to the specified Tile 'other'.
     * 
     * @param other the Tile to be checked for adjacency with this Tile
     * @return true if this Tile is adjacent to 'other'
     */
    public boolean isAdjacentTo(Tile other) {

        return (Math.abs(x - other.getX()) == SIZE && y == other.getY() ||
                Math.abs(y - other.getY()) == SIZE && x == other.getX());
    }

    /**
     * Checks whether this (path) Tile is red (i.e., it has been collided). 
     * 
     * @return true if this Tile is red
     */
    public boolean isRed() {

        return isRed;
    }

    /**
     * Checks if this Tile is floating around the specified otherTiles.
     * <p>
     * Retrieves the adjacent tiles of this Tile. Then, returns true
     * if none of the adjacent tiles are contained in the otherTiles.
     * Returns false otherwise.
     * 
     * @param otherTiles the TileList in which this Tile is to be checked
     * against for floating
     * @return true if this Tile is floating
     */
    public boolean isFloatingAround(TileList otherTiles) {

        Tile top = getAdjacentTile(Direction.UP);
        Tile bottom = getAdjacentTile(Direction.DOWN);
        Tile left = getAdjacentTile(Direction.LEFT);
        Tile right = getAdjacentTile(Direction.RIGHT);

        return (!(otherTiles.contains(top) ||
                otherTiles.contains(bottom) ||
                otherTiles.contains(left) ||
                otherTiles.contains(right)));
    }

    /**
     * Checks whether this Tile is out of the map bounds (the map width
     * and height are pre-defined).
     * 
     * @return true if this Tile is out of bounds
     */
    public boolean isOutOfBounds() {

        return (x < 0 || x > (Info.WIDTH - SIZE) ||
                y < Info.TOPBAR || y > (Info.HEIGHT - SIZE));
    }

    /**
     * Sets the orientation of this Tile to the specified orientation.
     * 
     * @param orientation the orientation to be set to for this Tile 
     */
    public void setOrientation(Direction orientation) {

        this.orientation = orientation;
    }

    /**
     * Turns this (green path) Tile into a red one.
     * <p>
     * Sets the sprite of the Tile to the specified red path sprite,
     * sets the frame of collision to the specified frameCount, and
     * updates isRed to true.
     * 
     * @param redPathSprite the PImage sprite this Tile is to be set to
     * @param frameCount the frame this Tile's frame of collision is to
     * be set to
     */
    public void turnRed(PImage redPathSprite, int frameCount) {

        sprite = redPathSprite;
        frameOfCollision = frameCount;
        isRed = true;
    }

}