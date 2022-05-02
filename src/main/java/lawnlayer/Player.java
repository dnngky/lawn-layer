package lawnlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lawnlayer.Info.Name;
import processing.core.PImage;

/**
 * A singleton subclass of Entity governing the behaviours of the player.
 * As there should only ever be one player in the game, this has been set
 * up as a singleton class.
 */
public class Player extends Entity {

    /**
     * The single instance of Player.
     */
    private static Player player = null;

    /**
     * The xy-coordinate of the player's default spawn point.
     */
    private static final List<Integer> SPAWNPOINT =
        Collections.unmodifiableList(Arrays.asList(0, Info.TOPBAR));
    /**
     * The number of allowed delayed key presses.
     */
    private static final int MAXQUEUESIZE = 2;
    
    /**
     * Player's current direction of movement.
     */
    private Direction currentDirection;
    /**
     * Stores all movement queues and executes them in order. If the
     * maximum number of queues is reached, all subsequent key presses
     * will be ignored (the maximum number of queues is pre-defined).
     */
    private ArrayList<Direction> movementQueue;
    /**
     * The tile Player is overlapping.
     */
    private Tile overlappedTile;
    /**
     * Indicates whether Player is moving on a soil surfece (this
     * includes grass).
     */
    private boolean isOnSoil;
    /**
     * Indicates whether Player is moving towards a tile space, and
     * ensures Player stops after moving to the nearest tile space,
     * unless a key is continuously pressed.
     */
    private boolean isMovingTowardsTile;
    /**
     * Indicates whether Player is currently shielded.
     */
    private boolean isShielded;
    /**
     * Indicates that the Player is in the process of stopping.
     */
    private boolean stopQueue;

    /**
     * Initialises Player with the specified sprite. The pre-defined
     * XY-coordinate of the Player's spawn point is used.
     * 
     * @param sprite the PImage sprite of the Player
     */
    private Player(PImage sprite) {

        super(sprite, SPAWNPOINT.get(0), SPAWNPOINT.get(1), Name.PLAYER);

        currentDirection = Direction.NONE;
        movementQueue = new ArrayList<>();
        movementQueue.add(currentDirection);
        overlappedTile = null;
        isOnSoil = false;
        isMovingTowardsTile = false;
        isShielded = false;
        stopQueue = false;
    }

    /**
     * Initialises Player with the specified sprite and XY-coordinate.
     * 
     * @param sprite the PImage sprite of the Player
     * @param x the x-coordinate of the Player
     * @param y the y-coordinate of the Player
     */
    private Player(PImage sprite, int x, int y) {

        super(sprite, x, y, Name.PLAYER);

        currentDirection = Direction.NONE;
        movementQueue = new ArrayList<>();
        movementQueue.add(currentDirection);
        overlappedTile = null;
        isOnSoil = false;
        isMovingTowardsTile = false;
        stopQueue = false;
    }

    /**
     * A 'static constructor' for Player. If multiple instantiation of Player
     * is attempted, an exception is thrown.
     * 
     * @param sprite the PImage sprite of the Player
     * @return a single instance of Player
     * 
     * @see Player#Player(PImage)
     * @throws IllegalStateException if an instance already exists
     */
    public static Player createPlayer(PImage sprite) {

        if (player == null) {
            player = new Player(sprite);
            return player;
        } 
        else throw
            new IllegalStateException("Player has already been created");
    }

    /**
     * A 'static constructor' for Player. If multiple instantiation of Player
     * is attempted, an exception is thrown.
     * 
     * @param sprite the PImage sprite of the Player
     * @param x the x-coordinate of the Player
     * @param y the y-coordinate of the Player
     * @return a single instance of Player
     * 
     * @see #Player(PImage, int, int)
     * @throws IllegalStateException if an instance already exists
     */
    public static Player createPlayer(PImage sprite, int x, int y) {

        if (player == null) {
            player = new Player(sprite, x, y);
            return player;
        }
        else throw
            new IllegalStateException("Player has already been created");
    }

    /**
     * Removes the single instance of Player.
     */
    public static void removePlayer() {

        if (player != null)
            player = null;
    }

    /**
     * Creates a new path tile as Player moves on soil.
     * <p>
     * Checks if Player is moving on soil and is exactly on a tile space.
     * If it is, creates and returns a new Path tile at its current
     * XY-coordinate.
     * 
     * @param greenPathSprite the PImage of the path to be created
     * @return a new path Tile
     */
    public Tile createPath(PImage greenPathSprite) {

        if (isOnSoil && isOnTileSpace()) {
            
            Tile newPath = new Tile(greenPathSprite, x, y, Name.PATH);
            newPath.setOrientation(currentDirection);
            return newPath;
        }
        return null;
    }

    /**
     * Enables shield mode on Player.
     * 
     * @see #isShielded
     */
    public void disableShield() {

        isShielded = false;
    }

    /**
     * Disables shield mode on Player.
     * 
     * @see #isShielded
     */
    public void enableShield() {

        isShielded = true;
    }

    /**
     * Retrieves the current tile Player is overlapping.
     * 
     * @return the overlapped tile
     */
    public Tile getOverlappedTile() {

        return overlappedTile;
    }

    /**
     * Retrieves the tile Player is overlapping from the specified
     * TileList 'otherTiles'.
     * <p>
     * Loops through 'otherTiles' and for each tile checks if Player
     * is overlapping it. If true, set overlappedTile to that Tile
     * and returns overlappedTile. If no tiles are found to be
     * overlapped by Player, return null.
     * 
     * @param otherTiles the TileList to be checked against for
     * overlap by Player
     * @return the overlapped Tile
     * 
     * @see #isOverlappingHorizontally(GameObject)
     * @see #isOverlappingVertically(GameObject)
     * @see Entity#isOverlapping(GameObject)
     */
    public Tile getOverlappedTileFrom(TileList otherTiles) {

        for (Tile tile : otherTiles.toList()) {
            
            if (isOverlapping(tile)) {

                overlappedTile = tile;
                return overlappedTile;
            }
        }
        return null;
    }

    /**
     * Retrieves the tile Player is overlapping from the possible specified
     * TileLists.
     * <p>
     * For each TileList, if Player overlaps a tile inside it, then attempts
     * to retrieve that tile. If the Player does not overlap any tile in all
     * of the TileLists, returns null. 
     * 
     * @param otherTiles1 the 1st TileList to be checked against for overlap
     * @param otherTiles2 the 2nd TileList to be checked against for overlap
     * @param otherTiles3 the 3rd TileList to be checked against for overlap
     * @param otherTiles4 the 4th TileList to be checked against for overlap
     * @return the overlapped Tile
     * 
     * @see #getOverlappedTileFrom(TileList)
     * @see #isOverlappingHorizontally(GameObject)
     * @see #isOverlappingVertically(GameObject)
     * @see Entity#isOverlapping(GameObject)
     */
    public Tile getOverlappedTileFrom(TileList otherTiles1,
        TileList otherTiles2, TileList otherTiles3, TileList otherTiles4) {

        if (isOverlapping(otherTiles1))
            overlappedTile = getOverlappedTileFrom(otherTiles1);
        
        else if (isOverlapping(otherTiles2))
            overlappedTile = getOverlappedTileFrom(otherTiles2);
        
        else if (isOverlapping(otherTiles3))
            overlappedTile = getOverlappedTileFrom(otherTiles3);

        else if (isOverlapping(otherTiles4))
            overlappedTile = getOverlappedTileFrom(otherTiles4);
        
        else
            overlappedTile = null;
        
        return overlappedTile;
    }

    /**
     * Checks if Player is currently on a safe tile.
     * <p>
     * Checks if overlappedTile is null. If it is not, returns true if
     * it is a concrete or grass tile.
     * 
     * @return true if Player is on a safe tile
     */
    public boolean isOnSafeTile() {

        return (overlappedTile != null &&
                (overlappedTile.getName() == Name.CONCRETE ||
                overlappedTile.getName() == Name.GRASS));
    }

    /**
     * Checks if Player is current shielded.
     * 
     * @return true if Player is currently in shield mode
     * 
     * @see #isShielded
     */
    public boolean isShielded() {

        return isShielded;
    }

    /**
     * If the ball is on soil or not being moved towards a
     * tile, continuously move the ball in the direction
     * per user's keyboard input. If the ball is not on soil,
     * automatically move the ball towards the nearest tile.
     */
    @Override
    public void move() {
        
        if (movementQueue.size() > MAXQUEUESIZE)
            movementQueue.remove(MAXQUEUESIZE);
        
        if (this.isOnTileSpace() && !movementQueue.isEmpty()) {
            
            currentDirection = movementQueue.get(0);
            movementQueue.remove(0);
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
        checkForOffMapMovement();
    }

    /**
     * Adds moving downwards to the movement queue if Player is
     * not currently moving upwards.
     */
    public void pressDown() {

        if (currentDirection != Direction.UP)
            movementQueue.add(Direction.DOWN);
    }

    /**
     * Adds moving leftwards to the movement queue if Player is
     * not currently moving rightwards.
     */
    public void pressLeft() {

        if (currentDirection != Direction.RIGHT)
            movementQueue.add(Direction.LEFT);
    }

    /**
     * Adds moving rightwards to the movement queue if Player is
     * not currently moving leftwards.
     */
    public void pressRight() {

        if (currentDirection != Direction.LEFT)
            movementQueue.add(Direction.RIGHT);
    }

    /**
     * Adds moving upwards to the movement queue if Player is
     * not currently moving downwards.
     */
    public void pressUp() {

        if (currentDirection != Direction.DOWN)
            movementQueue.add(Direction.UP);
    }

    /**
     * Respawns Player.
     * <p>
     * Sets Player's XY-coordinate to the pre-defined spawn point,
     * clears any queues in the movement queue, sets overlappedTile
     * to null, and disables isMovingTowardsTile and isOnSoil.
     * 
     * @see #isOnSoil
     * @see #isMovingTowardsTile
     */
    public void respawn() {

        x = SPAWNPOINT.get(0);
        y = SPAWNPOINT.get(1);

        currentDirection = Direction.NONE;
        movementQueue.clear();
        movementQueue.add(currentDirection);

        overlappedTile = null;
        isMovingTowardsTile = false;
        isOnSoil = false;
    }

    /**
     * Brings Player's movement to a halt once it has reached the
     * nearest tile space.
     * <p>
     * isOnSoil is disabled, isMovingTowardsTile is enabled, and stopQueue
     * is enabled.
     * 
     * @see #isOnSoil
     * @see #isMovingTowardsTile
     * @see #stopQueue
     */
    public void stop() {

        isOnSoil = false;
        isMovingTowardsTile = true;
        stopQueue = true;
    }

    /**
     * Updates Player's states as it moves from from a concrete to
     * grass or soil surface, and vice versa.
     * <p>
     * If overlappedTile is null, or it is a grass tile and stopQueue is
     * currently disabled, enables isOnSoil and disables isMovingTowardsTile.
     * Else if overlappedTile is a concrete tile, disables isOnSoil and
     * enables isMovingTowardsTile.
     * 
     * @param overlappedTile the Tile which is overlapped by Player
     * 
     * @see #isOnSoil
     * @see #isMovingTowardsTile
     * @see #stopQueue
     */
    public void updateStatus(Tile overlappedTile) {

        if (overlappedTile == null ||
            (overlappedTile.getName() == Name.GRASS &&
            !stopQueue)) {

            isOnSoil = true;
            isMovingTowardsTile = false;
        }
        else if (overlappedTile.getName() == Name.CONCRETE) {

            isOnSoil = false;
            isMovingTowardsTile = true;
        }
    }

    /**
     * Checks if Player has moved off the map bounds and applies
     * the appropriate actions.
     * 
     * @see Entity#checkForOffMapMovement()
     */
    @Override
    protected void checkForOffMapMovement() {

        int maxWidth = (Info.WIDTH - SIZE);
        int maxHeight = (Info.HEIGHT - SIZE);

        if (x < 0) {
            x = 0;
            currentDirection = Direction.NONE;
        }
        else if (x > maxWidth) {
            x = maxWidth;
            currentDirection = Direction.NONE;
        }
        if (y < Info.TOPBAR) {
            y = Info.TOPBAR;
            currentDirection = Direction.NONE;
        }
        else if (y > maxHeight) {
            y = maxHeight;
            currentDirection = Direction.NONE;
        }
    }

    /**
     * Checks whether Player is overlapping on the horizontal axis with
     * the specified 'other' GameObject.
     * 
     * @param other the GameObject to be checked against for overlap
     * @return true if Player overlaps on the horizontal axis with 'other'
     * 
     * @see Entity#isOverlappingHorizontally(GameObject)
     */
    @Override
    protected boolean isOverlappingHorizontally(GameObject other) {

        return (other.getX() <= getMidX() &&
                getMidX() < (other.getX() + SIZE));
    }

    /**
     * Checks whether Player is overlapping on the vertical axis with
     * the specified 'other' GameObject.
     * 
     * @param other the GameObject to be checked against for overlap
     * @return true if Player overlaps on the vertical axis with 'other'
     * 
     * @see Entity#isOverlappingVertically(GameObject)
     */
    @Override
    protected boolean isOverlappingVertically(GameObject other) {

        return (other.getY() <= getMidY() &&
                getMidY() < (other.getY() + SIZE));
    }

    /**
     * Checks if Player is exactly on a tile space. A valid tile space
     * is one whose XY-coordinate is a multiple of the spritesize
     * (which is pre-defined), and is below the top bar (whose height
     * is also pre-defined).
     * 
     * @return true if Player is exactly on a tile space.
     */
    private boolean isOnTileSpace() {

        return (x % SIZE == 0 && y % SIZE == 0 && y >= Info.TOPBAR);
    }

    /**
     * Moves Player downwards.
     * 
     * @param positionTile the Tile space Player will move to if
     * isMovingTowardsTile is true
     * 
     * @see #isMovingTowardsTile
     * @see #moveDownTo(Tile)
     */
    private void moveDown(Tile positionTile) {

        if (isOnSoil && !isMovingTowardsTile)
            y++;
        else
            moveDownTo(positionTile);
    }

    /**
     * Moves Player downwards and stops at the specified 'tile'.
     * 
     * @param tile the Tile space Player moves downwards to
     * 
     * @see #stopMoving()
     */
    private void moveDownTo(Tile tile) {

        int xDistFromTile = Math.abs(x - tile.getX());
        int yDistFromTile = tile.getY() - y;

        if (0 < xDistFromTile && xDistFromTile < 1)
            x = tile.getX();
        else if (x < tile.getX())
            x++;
        else if (x > tile.getX())
            x--;

        if (0 < yDistFromTile && yDistFromTile < 1)
            y = tile.getY();
        else
            y++;

        if (x == tile.getX() && y == tile.getY())
            stopMoving();
    }

    /**
     * Moves Player leftwards.
     * 
     * @param positionTile the Tile space Player will move to if
     * isMovingTowardsTile is true
     * 
     * @see #isMovingTowardsTile
     * @see #moveLeftTo(Tile)
     */
    private void moveLeft(Tile positionTile) {

        if (isOnSoil && !isMovingTowardsTile)
            x--;
        else
            moveLeftTo(positionTile);
    }

    /**
     * Moves Player leftwards and stops at the specified 'tile'.
     * 
     * @param tile the Tile space Player moves leftwards to
     * 
     * @see #stopMoving()
     */
    private void moveLeftTo(Tile tile) {

        int xDistFromTile = x - tile.getX();
        int yDistFromTile = Math.abs(y - tile.getY());

        if (0 < xDistFromTile && xDistFromTile < 1)
            x = tile.getX();
        else
            x--;

        if (0 < yDistFromTile && yDistFromTile < 1)
            y = tile.getY();
        else if (y < tile.getY())
            y++;
        else if (y > tile.getY())
            y--;

        if (x == tile.getX() && y == tile.getY())
            stopMoving();
    }

    /**
     * Moves Player rightwards.
     * 
     * @param positionTile the Tile space Player will move to if
     * isMovingTowardsTile is true
     * 
     * @see #isMovingTowardsTile
     * @see #moveRightTo(Tile)
     */
    private void moveRight(Tile positionTile) {

        if (isOnSoil && !isMovingTowardsTile)
            x++;
        else
            moveRightTo(positionTile);
    }

    /**
     * Moves Player rightwards and stops at the specified 'tile'.
     * 
     * @param tile the Tile space Player moves rightwards to
     * 
     * @see #stopMoving()
     */
    private void moveRightTo(Tile tile) {

        int xDistFromTile = tile.getX() - x;
        int yDistFromTile = Math.abs(y - tile.getY());

        if (0 < xDistFromTile && xDistFromTile < 1)
            x = tile.getX();
        else
            x++;

        if (0 < yDistFromTile && yDistFromTile < 1)
            y = tile.getY();
        else if (y < tile.getY())
            y++;
        else if (y > tile.getY())
            y--;

        if (x == tile.getX() && y == tile.getY())
            stopMoving();
    }

    /**
     * Moves Player upwards.
     * 
     * @param positionTile the Tile space Player will move to if
     * isMovingTowardsTile is true
     * 
     * @see #isMovingTowardsTile
     * @see #moveUpTo(Tile)
     */
    private void moveUp(Tile positionTile) {

        if (isOnSoil && !isMovingTowardsTile)
            y--;
        else
            moveUpTo(positionTile);
    }

    /**
     * Moves Player upwards and stops at the specified 'tile'.
     * 
     * @param tile the Tile space Player moves upwards to
     * 
     * @see #stopMoving()
     */
    private void moveUpTo(Tile tile) {

        int xDistFromTile = Math.abs(x - tile.getX());
        int yDistFromTile = y - tile.getY();

        if (0 < xDistFromTile && xDistFromTile < 1)
            x = tile.getX();
        else if (x < tile.getX())
            x++;
        else if (x > tile.getX())
            x--;
        
        if (0 < yDistFromTile && yDistFromTile < 1)
            y = tile.getY();
        else
            y--;

        if (x == tile.getX() && y == tile.getY())
            stopMoving();
    }

    /**
     * Halts the player's movement on the next immediate frame.
     * <p>
     * Resets currentDirection, and disables isMovingTowardsTile
     * and stopQueue.
     * 
     * @see #isMovingTowardsTile
     * @see #stopQueue
     */
    private void stopMoving() {

        currentDirection = Direction.NONE;
        isMovingTowardsTile = false;
        stopQueue = false;
    }

}