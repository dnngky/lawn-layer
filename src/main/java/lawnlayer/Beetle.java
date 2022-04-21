package lawnlayer;

import processing.core.PImage;

public class Beetle extends Enemy {

    public Beetle(PImage sprite) {

        super(sprite);
        name = "Beetle";
    }

    public Beetle(PImage sprite, int x, int y) {

        super(sprite, x, y);
        name = "Beetle";
    }

    @Override
    public void move() {
        /*
         * If the beetle collides with concrete tiles,
         * bounces the beetle off in a reflected angle.
         * 
         * If the beetle collides with a grass tile,
         * remove the collided grass tile and bounces
         * the beetle of in a reflected angle.
         */
        switch (collidedAt) {
            case UP:
                bouncesOffTop();
                break;
            case DOWN:
                bouncesOffBottom();
                break;
            case LEFT:
                bouncesOffLeft();
                break;
            case RIGHT:
                bouncesOffRight();
                break;
            case NONE:
                break;
        }
        switch (movement) {
            case UPLEFT:
                y--;
                x--;
                break;
            case UPRIGHT:
                y--;
                x++;
                break;
            case DOWNLEFT:
                y++;
                x--;
                break;
            case DOWNRIGHT:
                y++;
                x++;
                break;
            case STATIONARY:
                break;
        }
        checkOffMapMovement();
    }

}
