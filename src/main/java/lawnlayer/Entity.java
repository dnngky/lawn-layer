package lawnlayer;

import processing.core.PImage;

public abstract class Entity extends GameObject {

    protected int speed = Info.SPEED; // pixels per frame

    protected Entity(PImage sprite) {
        
        super(sprite);
    }

    protected Entity(PImage sprite, int x, int y) {

        super(sprite, x, y);
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

    protected abstract void checkOffMapMovement();
    
}
