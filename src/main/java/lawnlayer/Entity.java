package lawnlayer;

import lawnlayer.Info.Name;
import processing.core.PImage;

/**
 * An abstract subclass of GameObject governing the behaviours of all
 * entities in the game. Entities include the Player and all Enemy types.
 */
public abstract class Entity extends GameObject {

    /**
     * The default speed of an entity (pixels per seconds).
     */
    public static final int DEFAULTSPEED = 2;

    protected int speed;

    /**
     * Initialises an Entity with the specified sprite and name. Its
     * XY-coordinate will be randomised within the map bounds.
     * 
     * @param sprite the PImage sprite of the Entity
     * @param name the name of the Entity
     * 
     * @see GameObject#GameObject(PImage, Name)
     */
    protected Entity(PImage sprite, Name name) {

        super(sprite, name);
        speed = DEFAULTSPEED;
    }

    /**
     * Initialises an Entity with the specified sprite, XY-coordinate,
     * and name.
     * 
     * @param sprite the PImage sprite of the Entity
     * @param x the x-coordinate of the Entity
     * @param y the y-coordinate of the Entity
     * @param name the name of the Entity
     * 
     * @see GameObject#GameObject(PImage, int, int, Name)
     */
    protected Entity(PImage sprite, int x, int y, Name name) {

        super(sprite, x, y, name);
        speed = DEFAULTSPEED;
    }

    /**
     * Initialises an Entity with the specified sprite, tile location, and
     * name. Its XY-coordinate will be randomised within the tile location.
     * 
     * @param sprite the PImage sprite of the Entity
     * @param location the tile location of the Entity
     * @param name the name of the Entity
     * 
     * @see GameObject#GameObject(PImage, String, Name)
     */
    protected Entity(PImage sprite, String location, Name name) {

        super(sprite, location, name);
        speed = DEFAULTSPEED;
    }

    /**
     * An abstract method which, when implemented, checks if this Entity
     * has moved off the map bounds and applies the appropriate actions.
     */
    protected abstract void checkForOffMapMovement();

    /**
     * An abstract method which, when implemented, checks whether this
     * Entity is overlapping on the horizontal axis with the specified
     * 'other' GameObject.
     * 
     * @param other the GameObject to be checked against for overlap
     * @return true if this Entity overlaps on the horizontal axis with 'other'
     */
    protected abstract boolean isOverlappingHorizontally(GameObject other);

    /**
     * An abstract method which, when implemented, checks whether this
     * Entity is overlapping on the vertical axis with the specified
     * 'other' GameObject.
     * 
     * @param other the GameObject to be checked against for overlap
     * @return true if this Entity overlaps on the vertical axis with 'other'
     */
    protected abstract boolean isOverlappingVertically(GameObject other);
    
    /**
     * An abstract method which governs the way this Entity moves each frame.
     */
    protected abstract void move();

    /**
     * Retrieves the current speed of this Entity.
     * 
     * @return the speed of this Entity
     */
    public int getSpeed() {

        return speed;
    }

    /**
     * Checks whether this Entity is overlapping the specified
     * GameObject 'other'.
     * <p>
     * Checking mechanism for overlap depends on this Entity's
     * implementation of the abstract methods which check for vertical and
     * horizontal overlaps. 
     * 
     * @param other the GameObject to be checked against for overlap
     * @return true if this Entity overlaps with 'other'
     * 
     * @see #isOverlappingHorizontally(GameObject)
     * @see #isOverlappingVertically(GameObject)
     */
    public boolean isOverlapping(GameObject other) {

        return (isOverlappingHorizontally(other) &&
                isOverlappingVertically(other));
    }

    /**
     * Checks whether this Entity is overlapping any of the tiles in the
     * specified TileList 'otherTiles'.
     * <p>
     * Checking mechanism for overlap depends on this Entity's
     * implementation of the abstract methods which check for vertical and
     * horizontal overlaps.
     * 
     * @param otherTiles the TileList to be checked against for overlap
     * @return true if this Entity overlaps with at least one tile
     * inside 'otherTiles'
     * 
     * @see #isOverlapping(GameObject)
     */
    public boolean isOverlapping(TileList otherTiles) {

        for (Tile tile : otherTiles.toList()) {

            if (isOverlapping(tile))
                return true;
        }
        return false;
    }

    /**
     * Sets the speed of this Entity to the specified value.
     * 
     * @param speed the speed to be set for this Entity
     */
    public void setSpeed(int speed) {

        this.speed = speed;
    }
    
}
