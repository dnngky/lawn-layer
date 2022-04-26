package lawnlayer;

import processing.core.PImage;

public abstract class Entity extends GameObject {

    protected int speed = Info.SPEED; // pixels per frame

    protected Entity(PImage sprite, String name) {
        
        super(sprite, name);
    }

    protected Entity(PImage sprite, int x, int y) {

        super(sprite, x, y);
    }

    protected Entity(PImage sprite, int x, int y, String name) {

        super(sprite, x, y, name);
    }

    protected Entity(PImage sprite, String location, String name) {

        super(sprite, location, name);
    }
    
    protected boolean isOverlapping(GameObject other) {

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

    protected abstract void move();

    protected abstract void checkForOffMapMovement();
    
}
