package lawnlayer;

import java.util.ArrayList;

// import org.checkerframework.checker.units.qual.A;
import processing.core.PImage;

public class Player extends Entity {

    private Direction currentDirection;
    private ArrayList<Direction> directionQueue;

    private Tile overlappedTile;
    private boolean movingTowardsTile;
    private boolean onSoil;

    public Player(PImage sprite, int x, int y) {

        super(sprite, x, y);
        name = "Player";

        currentDirection = Direction.NONE;
        directionQueue = new ArrayList<>();
        directionQueue.add(currentDirection);

        overlappedTile = null;
        movingTowardsTile = false;
        onSoil = false;
    }

    public void checkForOverlapWith(TileList tiles) {

        for (Tile tile : tiles.toArray()) {
            
            if (isOverlapping(tile)) {
                overlappedTile = tile;
                onSoil = false;
            }
        }
    }

    public boolean isOnGrass(TileList grassTiles) {

        for (Tile grass : grassTiles.toArray()) {

            if (this.isOverlapping(grass))
                return true;
        }
        return false;
    }

    public boolean isOnConcrete(TileList concreteTiles) {

        for (Tile concrete : concreteTiles.toArray()) {

            if (this.isOverlapping(concrete))
                return true;
        }
        return false;
    }

    public Direction getDirection() {

        return currentDirection;
    }

    public void pressUp() {

        if (currentDirection != Direction.DOWN)
            directionQueue.add(Direction.UP);
    }

    public void pressDown() {

        if (currentDirection != Direction.UP)
            directionQueue.add(Direction.DOWN);
    }

    public void pressLeft() {

        if (currentDirection != Direction.RIGHT)
            directionQueue.add(Direction.LEFT);
    }

    public void pressRight() {

        if (currentDirection != Direction.LEFT)
            directionQueue.add(Direction.RIGHT);
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

        // Limits maximum number of delayed movements
        if (directionQueue.size() > Info.MAXQUEUESIZE)
            directionQueue.remove(Info.MAXQUEUESIZE);
        
        if (this.isOnTileSpace() && !directionQueue.isEmpty()) {
            
            currentDirection = directionQueue.get(0);
            directionQueue.remove(0);
        }
        switch (currentDirection) {
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
        checkOffMapMovement();
        onSoil = true;
    }

    public Tile addPath(PImage greenPathSprite) {

        if (onSoil && isOnTileSpace()) {
            
            Tile newPath = new Tile(greenPathSprite, x, y, Info.GREENPATH);
            newPath.setOrientation(currentDirection);
            return newPath;
        }
        return null;
    }

    private boolean isOverlapping(GameObject other) {

        return (other.getX() <= getMidX() && getMidX() < (other.getX() + size) &&
                other.getY() <= getMidY() && getMidY() < (other.getY() + size));
    }

    private boolean isOnTileSpace() {

        return (x % size == 0 && y % size == 0);
    }

    private void moveUp(Tile targetTile) {

        if (onSoil || !movingTowardsTile)
            y -= speed;
        else
            moveUpToNearestTile(targetTile);

        if (!onSoil)
            movingTowardsTile = true;
    }

    private void moveDown(Tile targeTile) {

        if (onSoil || !movingTowardsTile)
            y += speed;
        else
            moveDownToNearestTile(targeTile);
        if (!onSoil)
            movingTowardsTile = true;
    }

    private void moveLeft(Tile targeTile) {

        if (onSoil || !movingTowardsTile)
            x -= speed;
        else
            moveLeftToNearestTile(targeTile);

        if (!onSoil)
            movingTowardsTile = true;
    }

    private void moveRight(Tile targeTile) {

        if (onSoil || !movingTowardsTile)
            x += speed;
        else
            moveRightToNearestTile(targeTile);

        if (!onSoil)
            movingTowardsTile = true;
    }

    private void stopMoving() {

        currentDirection = Direction.NONE;
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