package lawnlayer;

import processing.core.PImage;

public class Player extends GameObject {

    private int speed; // pixels per frame

    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;
    private boolean movingTowardsTile;
    private boolean onSoil;

    public Player(PImage sprite, int x, int y) {

        super(sprite, x, y);
        speed = Info.SPEED;

        up = false;
        down = false;
        left = false;
        right = false;
        movingTowardsTile = false;
        onSoil = false;
    }

    public boolean isOverlapping(GameObject other) {

        return (other.getX() <= getMidX() && getMidX() < (other.getX() + size) &&
                other.getY() <= getMidY() && getMidY() < (other.getY() + size));
    }

    public boolean isOnSoil() {

        return onSoil;
    }

    public void movingOnConcrete() {

        onSoil = false;
    }

    public void pressUp() {

        if (!down) {
            up = true;
            left = false;
            right = false;
        }
    }

    public void pressDown() {

        if (!up) {
            down = true;
            left = false;
            right = false;
        }
    }

    public void pressLeft() {

        if (!right) {
            left = true;
            up = false;
            down = false;
        }
    }

    public void pressRight() {

        if (!left) {
            right = true;
            up = false;
            down = false;
        }
    }

    public void move(GameObject other) {
        /*
         * If the ball is on soil or not being moved towards a
         * tile, continuously move the ball in the direction
         * per user's keyboard input.
         * 
         * If the ball is not on soil, automatically move the
         * ball towards the nearest tile.
         */
        if (up)
            moveUp(other);

        if (down)
            moveDown(other);

        if (left)
            moveLeft(other);

        if (right)
            moveRight(other);

        checkMoveOffMap();
        onSoil = true;
    }

    // PRIVATE METHODS

    private void moveUp(GameObject other) {

        if (onSoil || !movingTowardsTile)
            y -= speed;
        else
            moveUpToNearestTile(other);

        if (!onSoil)
            movingTowardsTile = true;
    }

    private void moveDown(GameObject other) {

        if (onSoil || !movingTowardsTile)
            y += speed;
        else
            moveDownToNearestTile(other);

        if (!onSoil)
            movingTowardsTile = true;
    }

    private void moveLeft(GameObject other) {

        if (onSoil || !movingTowardsTile)
            x -= speed;
        else
            moveLeftToNearestTile(other);

        if (!onSoil)
            movingTowardsTile = true;
    }

    private void moveRight(GameObject other) {

        if (onSoil || !movingTowardsTile)
            x += speed;
        else
            moveRightToNearestTile(other);

        if (!onSoil)
            movingTowardsTile = true;
    }

    private void stopMoving() {

        up = false;
        down = false;
        left = false;
        right = false;
        movingTowardsTile = false;
    }

    private void moveUpToNearestTile(GameObject tile) {

        if (Math.abs(x - tile.getX()) == speed - 1)
            x = tile.getX();
        else if (x < tile.getX())
            x += speed;
        else if (x > tile.getX())
            x -= speed;

        if (y - tile.getY() == speed - 1)
            y = tile.getY();
        else
            y -= speed;

        if (x == tile.getX() && y == tile.getY())
            stopMoving();
    }

    private void moveDownToNearestTile(GameObject tile) {

        if (Math.abs(x - tile.getX()) == speed - 1)
            x = tile.getX();
        else if (x < tile.getX())
            x += speed;
        else if (x > tile.getX())
            x -= speed;

        if (tile.getY() - y == speed - 1)
            y = tile.getY();
        else
            y += speed;

        if (x == tile.getX() && y == tile.getY())
            stopMoving();
    }

    private void moveLeftToNearestTile(GameObject tile) {

        if (x - tile.getX() == speed - 1)
            x = tile.getX();
        else
            x -= speed;

        if (y - tile.getY() == speed - 1)
            y = tile.getY();
        else if (y < tile.getY())
            y += speed;
        else if (y > tile.getY())
            y -= speed;

        if (x == tile.getX() && y == tile.getY())
            stopMoving();
    }

    private void moveRightToNearestTile(GameObject tile) {

        if (tile.getX() - x == speed - 1)
            x = tile.getX();
        else
            x += speed;

        if (y - tile.getY() == speed - 1)
            y = tile.getY();
        else if (y < tile.getY())
            y += speed;
        else if (y > tile.getY())
            y -= speed;

        if (x == tile.getX() && y == tile.getY())
            stopMoving();
    }

    private void checkMoveOffMap() {

        int maxWidth = (Info.WIDTH - Info.SPRITESIZE);
        int maxHeight = (Info.HEIGHT - Info.SPRITESIZE);

        if (x < 0)
            x = 0;
        else if (x > maxWidth)
            x = maxWidth;
        if (y < 0)
            y = 0;
        else if (y > maxHeight)
            y = maxHeight;
    }

}