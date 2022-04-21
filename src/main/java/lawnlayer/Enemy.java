package lawnlayer;

import processing.core.PImage;

public abstract class Enemy extends Entity {

    protected Movement movement;
    protected Direction collidedAt;

    protected Enemy(PImage sprite) {
        /*
         * Randomises spawn location and initialise movement if
         * information is not given when object is instantiated.
         */
        super(sprite);

        movement = initialiseMovement();
        collidedAt = Direction.NONE;
    }

    protected Enemy(PImage sprite, int x, int y) {

        super(sprite, x, y);

        movement = initialiseMovement();
        collidedAt = Direction.NONE;
    }

    private Movement initialiseMovement() {
        /*
         * Randomises and initialises movement for enemy.
         */
        int randomiser = rand.nextInt(4);

        switch (randomiser) {
            case 0:
                return Movement.UPLEFT;
            case 1:
                return Movement.UPRIGHT;
            case 2:
                return Movement.DOWNLEFT;
            case 3:
                return Movement.DOWNRIGHT;
            default:
                return Movement.STATIONARY;
        }
    }

    public void checkForCollisionWith(TileList tiles, boolean printMsg) {

        Tile collidedTile = null;

        for (Tile tile : tiles.toArray()) {

            if (collidesAtTopWith(tile)) {
                collidedTile = tile;
                collidedAt = Direction.UP;
            }
            else if (collidesAtBottomWith(tile)) {
                collidedTile = tile;
                collidedAt = Direction.DOWN;
            }
            else if (collidesAtLeftWith(tile)) {
                collidedTile = tile;
                collidedAt = Direction.LEFT;
            }
            else if (collidesAtRightWith(tile)) {
                collidedTile = tile;
                collidedAt = Direction.RIGHT;
            }
        }
        if (printMsg && collidedTile != null)
            System.out.printf("%s (%d, %d) collided with %s (%d, %d)%n",
                              name, x, y, collidedTile.name, collidedTile.getX(), collidedTile.getY());
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

    protected void bouncesOffTop() {

        if (movement == Movement.UPLEFT)
            movement = Movement.DOWNLEFT;
        else if (movement == Movement.UPRIGHT)
            movement = Movement.DOWNRIGHT;

        collidedAt = Direction.NONE;
    }

    protected void bouncesOffBottom() {

        if (movement == Movement.DOWNLEFT)
            movement = Movement.UPLEFT;
        else if (movement == Movement.DOWNRIGHT)
            movement = Movement.UPRIGHT;

        collidedAt = Direction.NONE;
    }

    protected void bouncesOffLeft() {

        if (movement == Movement.UPLEFT)
            movement = Movement.UPRIGHT;
        else if (movement == Movement.DOWNLEFT)
            movement = Movement.DOWNRIGHT;

        collidedAt = Direction.NONE;
    }

    protected void bouncesOffRight() {

        if (movement == Movement.UPRIGHT)
            movement = Movement.UPLEFT;
        else if (movement == Movement.DOWNRIGHT)
            movement = Movement.DOWNLEFT;

        collidedAt = Direction.NONE;
    }

    @Override
    protected void checkOffMapMovement() {

        int maxWidth = (Info.WIDTH - size);
        int maxHeight = (Info.HEIGHT - size);

        if (x < 0)
            x = size;
        else if (x > maxWidth)
            x = maxWidth - size;
        
        if (y < 0)
            y = size;
        else if (y > maxHeight)
            y = maxHeight - size;
    }

}