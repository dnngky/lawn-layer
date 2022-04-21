package lawnlayer;

import org.checkerframework.checker.units.qual.A;

import processing.core.PImage;

public class Player extends Entity {

    private Direction direction;

    private Tile overlappedTile;
    private boolean movingTowardsTile;
    private boolean onSoil;
    private boolean adjustingMovement;

    public Player(PImage sprite, int x, int y) {

        super(sprite, x, y);
        name = "Player";

        direction = Direction.NONE;

        overlappedTile = null;
        movingTowardsTile = false;
        onSoil = false;
        adjustingMovement = false;
    }

    public void checkForOverlapWith(TileList tiles) {

        for (Tile tile : tiles.toArray()) {
            
            if (isOverlapping(tile)) {
                overlappedTile = tile;
                onSoil = false;
            }
        }
    }

    public boolean isOverlapping(GameObject other) {

        return (other.getX() <= getMidX() && getMidX() < (other.getX() + size) &&
                other.getY() <= getMidY() && getMidY() < (other.getY() + size));
    }

    public boolean isOnGrass(TileList grassTiles) {

        for (Tile grass : grassTiles.toArray()) {

            if (isOverlapping(grass))
                return true;
        }
        return false;
    }

    public Direction getDirection() {

        return direction;
    }

    public void pressUp() {

        if (direction != Direction.DOWN && !adjustingMovement)
            direction = Direction.UP;
    }

    public void pressDown() {

        if (direction != Direction.UP && !adjustingMovement)
            direction = Direction.DOWN;
    }

    public void pressLeft() {

        if (direction != Direction.RIGHT && !adjustingMovement)
            direction = Direction.LEFT;
    }

    public void pressRight() {

        if (direction != Direction.LEFT && !adjustingMovement)
            direction = Direction.RIGHT;
    }

    @Override
    public void move() {
        /*
         * If the ball is on soil or not being moved towards a
         * tile, continuously move the ball in the direction
         * per user's keyboard input.
         * 
         * If the ball is not on soil, automatically move the
         * ball towards the nearest tile.
         */
        switch (direction) {
            case UP:
                moveUp(overlappedTile);
                break;
            case DOWN:
                moveDown(overlappedTile);
                break;
            case LEFT:
                moveLeft(overlappedTile);
                break;
            case RIGHT:
                moveRight(overlappedTile);
                break;
            case NONE:
                break;
        }
        if (onSoil)
            adjustMovementOnSoil();
        
        checkOffMapMovement();
        onSoil = true;
    }

    public Tile addPath(PImage greenPathSprite) {

        if (onSoil && x % size == 0 && y % size == 0) {
            
            Tile newPath = new Tile(greenPathSprite, x, y, Info.GREENPATH);
            newPath.setOrientation(direction);
            return newPath;
        }
        return null;
    }

    private void adjustMovementOnSoil() {

        int xDeviation = x % size;
        int yDeviation = y % size;

        if (direction == Direction.UP || direction == Direction.DOWN) {

            if (xDeviation < speed)
                x -= xDeviation;
            else if (size - xDeviation < speed)
                x += (size - xDeviation);
            else if (xDeviation < (size / 2))
                x -= speed;
            else
                x += speed;
        }
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            
            if (yDeviation < speed)
                y -= yDeviation;
            else if (size - yDeviation < speed)
                y += (size - yDeviation);
            else if (yDeviation < (size / 2))
                y -= speed;
            else
                y += speed;
        }
    }

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

        direction = Direction.NONE;
        movingTowardsTile = false;
    }

    private void moveUpToNearestTile(GameObject tile) {

        int xDistFromTile = Math.abs(x - tile.getX());
        int yDistFromTile = y - tile.getY();

        if (0 < xDistFromTile && xDistFromTile < speed)
            x = tile.getX();
        else if (x < tile.getX())
            x += speed;
        else if (x > tile.getX())
            x -= speed;
        
        if (0 < yDistFromTile && yDistFromTile < speed)
            y = tile.getY();
        else
            y -= speed;

        if (x == tile.getX() && y == tile.getY())
            stopMoving();
    }

    private void moveDownToNearestTile(GameObject tile) {

        int xDistFromTile = Math.abs(x - tile.getX());
        int yDistFromTile = tile.getY() - y;

        if (0 < xDistFromTile && xDistFromTile < speed)
            x = tile.getX();
        else if (x < tile.getX())
            x += speed;
        else if (x > tile.getX())
            x -= speed;

        if (0 < yDistFromTile && yDistFromTile < speed)
            y = tile.getY();
        else
            y += speed;

        if (x == tile.getX() && y == tile.getY())
            stopMoving();
    }

    private void moveLeftToNearestTile(GameObject tile) {

        int xDistFromTile = x - tile.getX();
        int yDistFromTile = Math.abs(y - tile.getY());

        if (0 < xDistFromTile && xDistFromTile < speed)
            x = tile.getX();
        else
            x -= speed;

        if (0 < yDistFromTile && yDistFromTile < speed)
            y = tile.getY();
        else if (y < tile.getY())
            y += speed;
        else if (y > tile.getY())
            y -= speed;

        if (x == tile.getX() && y == tile.getY())
            stopMoving();
    }

    private void moveRightToNearestTile(GameObject tile) {

        int xDistFromTile = tile.getX() - x;
        int yDistFromTile = Math.abs(y - tile.getY());

        if (0 < xDistFromTile && xDistFromTile < speed)
            x = tile.getX();
        else
            x += speed;

        if (0 < yDistFromTile && yDistFromTile < speed)
            y = tile.getY();
        else if (y < tile.getY())
            y += speed;
        else if (y > tile.getY())
            y -= speed;

        if (x == tile.getX() && y == tile.getY())
            stopMoving();
    }
    
    @Override
    protected void checkOffMapMovement() {

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