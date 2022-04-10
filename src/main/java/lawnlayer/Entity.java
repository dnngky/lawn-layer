package lawnlayer;

import processing.core.PImage;

public abstract class Entity extends GameObject {

    protected int speed; // pixels per frame

    protected boolean upLeft;
    protected boolean upRight;
    protected boolean downLeft;
    protected boolean downRight;

    protected boolean collidedAtTop;
    protected boolean collidedAtBottom;
    protected boolean collidedAtLeft;
    protected boolean collidedAtRight;

    protected Entity(PImage sprite, int x, int y) {

        super(sprite, x, y);
        speed = Info.SPEED;

        upLeft = true;
        upRight = false;
        downLeft = false;
        downRight = false;
    }

    public void collidesAt(Side collidedSide) {
        /*
         * Records which side of the entity collides with
         */
        switch (collidedSide) {
            case TOP:
                collidedAtTop = true;
                break;
            case BOTTOM:
                collidedAtBottom = true;
                break;
            case LEFT:
                collidedAtLeft = true;
                break;
            case RIGHT:
                collidedAtRight = true;
                break;
        }
    }

    protected boolean collidesAtTopWith(GameObject other) {

        return (other.getX() <= getMidX() && getMidX() < (other.getX() + size) &&
                getMidY() == (other.getMidY() + size));
    }

    protected boolean collidesAtBottomWith(GameObject other) {

        return (other.getX() <= getMidX() && getMidX() < (other.getX() + size) &&
                getMidY() == (other.getMidY() - size));
    }

    protected boolean collidesAtLeftWith(GameObject other) {

        return (getMidX() == (other.getMidX() + size) &&
                other.getY() <= getMidY() && getMidY() < (other.getY() + size));
    }

    protected boolean collidesAtRightWith(GameObject other) {

        return (getMidX() == (other.getMidX() - size) &&
                other.getY() <= getMidY() && getMidY() < (other.getY() + size));
    }

    protected abstract void move();

}