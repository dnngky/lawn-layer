package lawnlayer;

import processing.core.PImage;

public abstract class Entity extends GameObject {

    protected int speed;

    protected Entity(PImage sprite, String name) {

        super(sprite, name);
        speed = Info.SPEED;
    }

    protected Entity(PImage sprite, int x, int y, String name) {

        super(sprite, x, y, name);
        speed = Info.SPEED;
    }

    protected Entity(PImage sprite, String location, String name) {

        super(sprite, location, name);
        speed = Info.SPEED;
    }
    
    public boolean isOverlapping(GameObject other) {

        return (other.getX() <= getMidX() && getMidX() < (other.getX() + size) &&
                other.getY() <= getMidY() && getMidY() < (other.getY() + size));
    }

    public boolean isOverlapping(TileList otherTiles) {

        for (Tile tile : otherTiles.toArray()) {

            if (this.isOverlapping(tile))
                return true;
        }
        return false;
    }

    protected abstract void checkForOffMapMovement();
    
    protected abstract void move();
    
}
