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

    protected abstract void move();

    protected abstract void checkOffMapMovement();
    
}
