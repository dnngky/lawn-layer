package lawnlayer;

import processing.core.PImage;

public class Worm extends Entity {

    public Worm(PImage sprite, int x, int y) {

        super(sprite, x, y);
    }

    @Override
    public void move() {
        /*
         * If the worm collides with concrete tiles,
         * bounces the worm off in a reflected angle.
         */
        if (collidedAtTop)
            bouncesOffTop();

        if (collidedAtBottom)
            bouncesOffBottom();

        if (collidedAtLeft)
            bouncesOffLeft();

        if (collidedAtRight)
            bouncesOffRight();

        if (upLeft) {
            y -= speed;
            x -= speed;
        }
        if (upRight) {
            y -= speed;
            x += speed;
        }
        if (downLeft) {
            y += speed;
            x -= speed;
        }
        if (downRight) {
            y += speed;
            x += speed;
        }
    }

    // PRIVATE METHODS

    private void bouncesOffTop() {

        if (upLeft) {
            downLeft = true;
            upLeft = false;
        } else if (upRight) {
            downRight = true;
            upRight = false;
        }
        collidedAtTop = false;
    }

    private void bouncesOffBottom() {

        if (downLeft) {
            upLeft = true;
            downLeft = false;
        } else if (downRight) {
            upRight = true;
            downRight = false;
        }
        collidedAtBottom = false;
    }

    private void bouncesOffLeft() {

        if (upLeft) {
            upRight = true;
            upLeft = false;
        } else if (downLeft) {
            downRight = true;
            downLeft = false;
        }
        collidedAtLeft = false;
    }

    private void bouncesOffRight() {

        if (upRight) {
            upLeft = true;
            upRight = false;
        } else if (downRight) {
            downLeft = true;
            downRight = false;
        }
        collidedAtRight = false;
    }

}