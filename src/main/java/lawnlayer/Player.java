package lawnlayer;

import java.util.ArrayList;

// import org.checkerframework.checker.units.qual.A;
import processing.core.PImage;

public class Player extends Entity {

    private Direction currentDirection;
    private ArrayList<Direction> directionQueue;

    private Tile overlappedTile;
    private boolean stopQueue;
    private boolean movingTowardsTile;
    private boolean onSoil;

    public Player(PImage sprite, int x, int y) {

        super(sprite, x, y);
        name = Info.PLAYER;

        currentDirection = Direction.NONE;
        directionQueue = new ArrayList<>();
        directionQueue.add(currentDirection);

        overlappedTile = null;
        stopQueue = false;
        movingTowardsTile = false;
        onSoil = false;
    }

    public Player(PImage sprite) {

        super(sprite, Info.SPAWNPOINT.get(0), Info.SPAWNPOINT.get(1));
        name = Info.PLAYER;

        currentDirection = Direction.NONE;
        directionQueue = new ArrayList<>();
        directionQueue.add(currentDirection);

        overlappedTile = null;
        stopQueue = false;
        movingTowardsTile = false;
        onSoil = false;
    }

    public Tile getOverlappedTile(TileList otherTiles) {

        for (Tile tile : otherTiles.toArray()) {
            
            if (this.isOverlapping(tile)) {

                overlappedTile = tile;
                return overlappedTile;
            }
        }
        return null;
    }

    public Tile getOverlappedTile(TileList otherTiles1, TileList otherTiles2,
        TileList otherTiles3) {

        if (isOverlapping(otherTiles1))
            overlappedTile = getOverlappedTile(otherTiles1);
        
        else if (isOverlapping(otherTiles2))
            overlappedTile = getOverlappedTile(otherTiles2);
        
        else if (isOverlapping(otherTiles3))
            overlappedTile = getOverlappedTile(otherTiles3);
        
        else
            overlappedTile = null;
        
        return overlappedTile;
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

    public void respawn() {

        x = Info.SPAWNPOINT.get(0);
        y = Info.SPAWNPOINT.get(1);

        currentDirection = Direction.NONE;
        directionQueue.clear();
        directionQueue.add(currentDirection);

        overlappedTile = null;
        movingTowardsTile = false;
        onSoil = false;
    }

    public void stop() {

        onSoil = false;
        movingTowardsTile = true;
        stopQueue = true;
    }

    public void update(Tile overlappedTile) {

        if (overlappedTile == null ||
            (overlappedTile.name.equals(Info.GRASS) &&
            !stopQueue)) {

            onSoil = true;
            movingTowardsTile = false;
        }
        else if (overlappedTile.name.equals(Info.CONCRETE)) {

            onSoil = false;
            movingTowardsTile = true;
        }
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
    }

    public Tile createPath(PImage greenPathSprite) {

        if (onSoil && isOnTileSpace()) {
            
            Tile newPath = new Tile(greenPathSprite, x, y, Info.PATH);
            newPath.setOrientation(currentDirection);
            return newPath;
        }
        return null;
    }

    private boolean isOnTileSpace() {

        return (x % size == 0 && y % size == 0);
    }

    private void moveUp(Tile targetTile) {

        if (onSoil && !movingTowardsTile)
            y -= speed;
        else
            moveUpToNearestTile(targetTile);
    }

    private void moveDown(Tile targetTile) {

        if (onSoil && !movingTowardsTile)
            y += speed;
        else
            moveDownToNearestTile(targetTile);
    }

    private void moveLeft(Tile targetTile) {

        if (onSoil && !movingTowardsTile)
            x -= speed;
        else
            moveLeftToNearestTile(targetTile);
    }

    private void moveRight(Tile targetTile) {

        if (onSoil && !movingTowardsTile)
            x += speed;
        else
            moveRightToNearestTile(targetTile);
    }

    private void stopMoving() {

        currentDirection = Direction.NONE;
        movingTowardsTile = false;
        stopQueue = false;
    }

    private void moveUpToNearestTile(Tile tile) {

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

    private void moveDownToNearestTile(Tile tile) {

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

    private void moveLeftToNearestTile(Tile tile) {

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

    private void moveRightToNearestTile(Tile tile) {

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