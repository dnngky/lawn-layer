package lawnlayer;

import lawnlayer.Info.Name;
import processing.core.PImage;

/**
 * A subclass of Entity governing the behaviours of all enemy types in
 * the game.
 */
public class Enemy extends Entity {
    
    private Side collidedAt;
    private Tile collidedTile;
    private Movement movement;

    /**
     * Initialises an Enemy with the specified sprite and name. Its
     * XY-coordinate will be randomised within the map bounds.
     * 
     * @param sprite the PImage sprite of the Enemy
     * @param name the name of the Enemy
     * 
     * @see Entity#Entity(PImage, Name)
     */
    public Enemy(PImage sprite, Name name) {
        
        super(sprite, name);

        collidedAt = Side.NONE;
        collidedTile = null;
        initialiseMovement();
    }

    /**
     * Initialises an Enemy with the specified sprite, XY-coordinate,
     * and name.
     * 
     * @param sprite the PImage sprite of the Enemy
     * @param x the x-coordinate of the Enemy
     * @param y the y-coordinate of the Enemy
     * @param name the name of the Enemy
     * 
     * @see Enemy#Enemy(PImage, int, int, Name)
     */
    public Enemy(PImage sprite, int x, int y, Name name) {
        
        super(sprite, x, y, name);

        collidedAt = Side.NONE;
        collidedTile = null;
        initialiseMovement();
    }

    /**
     * Initialises an Enemy with the specified sprite, tile location, and
     * name. Its XY-coordinate will be randomised within the tile location.
     * 
     * @param sprite the PImage sprite of the Enemy
     * @param location the tile location of the Enemy
     * @param name the name of the Enemy
     * 
     * @see Enemy#Enemy(PImage, String, Name)
     */
    public Enemy(PImage sprite, String location, Name name) {
        
        super(sprite, location, name);

        collidedAt = Side.NONE;
        collidedTile = null;
        initialiseMovement();
    }

    /**
     * Checks for any collision of this Enemy with the tiles inside thef
     * specified TileList 'otherTiles' and updates its direction of
     * movement accordingly.
     * <p>
     * Checks for both diagonal (corner) collision and straight (side)
     * collisions with the tiles in 'otherTiles'. If 'otherTiles' are grass
     * border tiles, and the Enemy type is beetle, then update the border
     * tiles.
     * 
     * @param otherTiles the TileList to be checked against for collision
     * with this Enemy
     * @param fillTiles the TileList which will be updated alongside
     * 'otherTiles' if 'otherTiles' are grass borders tiles and Enemy type
     * is beetle
     * @param printMsg toggles collision messages printed in the terminal
     * 
     * @see #checkForDiagonalCollisionWith(TileList)
     * @see #checkForStraightCollisionWith(TileList)
     * @see TileList#updateBorder(Tile, TileList)
     */
    public void checkForCollisionWith(TileList otherTiles, TileList fillTiles,
        boolean printMsg) {
        
        if (movement != Movement.STATIONARY)
            checkForDiagonalCollisionWith(otherTiles);
        
        if (movement != Movement.STATIONARY &&
            collidedAt == Side.NONE)
            checkForStraightCollisionWith(otherTiles);
        
        if (collidedAt != Side.NONE &&
            collidedTile.getName() == Name.GRASS &&
            name == Name.BEETLE) {
            
            otherTiles.updateBorder(collidedTile, fillTiles);
        }
        if (printMsg && collidedAt != Side.NONE &&
            collidedTile.getName().equals(otherTiles.getTileName()))

            System.out.printf("%s collided at %s with %s%n",
                              this, collidedAt, collidedTile);
    }

    /**
     * Checks if this Enemy has spawned at a tile location already occupied
     * by a tile in the specified TileList 'otherTiles'.
     * <p>
     * Checks if this Enemy is overlapping at least one tile in 'otherTiles'.
     * If it is, randomises its XY-coordinate within the map bounds and
     * rechecks until it is no longer overlapping any tiles.
     * 
     * @param otherTiles the TileList to be checked against for entrapping
     * this Enemy
     * 
     * @see GameObject#randomiseXY()
     * @see Entity#isOverlapping(GameObject)
     */
    public void checkIfIsStuckInside(TileList otherTiles) {

        if (isOverlapping(otherTiles)) {
            randomiseXY();
            checkIfIsStuckInside(otherTiles);
        }
    }

    /**
     * Retrieves the tile collided by this Enemy.
     * 
     * @return the collided tile
     */
    public Tile getCollidedTile() {

        return collidedTile;
    }

    /**
     * Retrieves the Enemy's current movement.
     * 
     * @return the current movement
     */
    public Movement getMovement() {

        return movement;
    }

    /**
     * Checks if this Enemy has collided with one of the tiles specified in
     * the specified TileList 'otherTiles'.
     * <p>
     * This method checks whether a collidedTile has been found (i.e, is not
     * null) and, if it has, whether it shares the same name as the name of
     * the tiles in 'otherTiles'.
     * 
     * @param otherTiles the TileList to be checked against for collision
     * @return true if this Enemy has collided with a tile in 'otherTiles'
     */
    public boolean hasCollidedWith(TileList otherTiles) {

        return (collidedTile != null &&
                collidedTile.getName() == otherTiles.getTileName());
    }

    /**
     * Initialises the direction of movement of this Enemy at spawn.
     * <p>
     * Allocates each direction of movement to an integer from 0 to 3.
     * Then, retrieves a random integer from 0 and to 3 and sets this
     * Enemy's direction of movement accordingly.
     * 
     * @see GameObject#rand
     */
    public void initialiseMovement() {
        
        int randomiser = rand.nextInt(4);

        switch (randomiser) {

            case 0:
                movement = Movement.UPLEFT;
                break;
            case 1:
                movement = Movement.UPRIGHT;
                break;
            case 2:
                movement = Movement.DOWNLEFT;
                break;
            case 3:
                movement = Movement.DOWNRIGHT;
                break;
            default:
                movement = Movement.STATIONARY;
                break;
        }
    }

    /**
     * Processes the movement of this Enemy each frame.
     * <p>
     * Off-bounds movement is first checked. The direction of movement
     * is modified accordingly to the direction of collision. Then, the
     * Enemy's XY-coordinate is modified accordingly to the direction of
     * movement.
     * 
     * @see #checkForOffMapMovement()
     * @see Entity#move()
     * @see Movement#flipHorizontally()
     * @see Movement#flipVertically()
     */
    @Override
    public void move() {

        checkForOffMapMovement();

        switch (collidedAt) {
            
            case TOP:
            case BOTTOM:
                movement = movement.flipVertically();
                break;
            
            case LEFT:
            case RIGHT:
                movement = movement.flipHorizontally();
                break;
            
            case TOPLEFT:
                movement = Movement.UPLEFT;
                break;
            
            case BOTTOMLEFT:
                movement = Movement.DOWNLEFT;
                break;
        
            case TOPRIGHT:
                movement = Movement.UPRIGHT;
                break;
            
            case BOTTOMRIGHT:
                movement = Movement.DOWNRIGHT;
                break;
            
            default:
                break;
        }
        collidedAt = Side.NONE;
        collidedTile = null;

        switch (movement) {

            case UPLEFT:
                y -= 1;
                x -= 1;
                break;
            case UPRIGHT:
                y -= 1;
                x += 1;
                break;
            case DOWNLEFT:
                y += 1;
                x -= 1;
                break;
            case DOWNRIGHT:
                y += 1;
                x += 1;
                break;
            case STATIONARY:
                break;
        }
    }

    /**
     * Sets this Enemy's movement to the specified movement.
     * 
     * @param movement the movement to be set to this Enemy
     */
    public void setMovement(Movement movement) {

        this.movement = movement;
    }

    /**
     * Checks if this Enemy has moved off the map bounds and applies
     * the appropriate actions.
     * 
     * @see Entity#checkForOffMapMovement()
     */
    @Override
    protected void checkForOffMapMovement() {

        int maxWidth = (Info.WIDTH - 2*SIZE);
        int maxHeight = (Info.HEIGHT - 2*SIZE);

        if (x < SIZE)
            x = SIZE;
        else if (x > maxWidth)
            x = maxWidth;
        
        if (y < Info.TOPBAR + SIZE)
            y = Info.TOPBAR + SIZE;
        else if (y > maxHeight)
            y = maxHeight;
    }

    /**
     * Checks whether this Enemy is overlapping on the horizontal axis with
     * the specified 'other' GameObject.
     * 
     * @param other the GameObject to be checked against for overlap
     * @return true if this Enemy overlaps on the horizontal axis with 'other'
     * 
     * @see Entity#isOverlappingHorizontally(GameObject)
     */
    @Override
    protected boolean isOverlappingHorizontally(GameObject other) {

        return ((other.getX() - SIZE) <= x && x <= (other.getX() + SIZE));
    }

    /**
     * Checks whether this Enemy is overlapping on the vertical axis with
     * the specified 'other' GameObject.
     * 
     * @param other the GameObject to be checked against for overlap
     * @return true if this Enemy overlaps on the vertical axis with 'other'
     * 
     * @see Entity#isOverlappingVertically(GameObject)
     */
    @Override
    protected boolean isOverlappingVertically(GameObject other) {

        return ((other.getY() - SIZE) <= y && y <= (other.getY() + SIZE));
    }

    /**
     * Checks whether this Enemy has collided off the corner of a tile
     * in the specified TileList 'otherTiles'.
     * <p>
     * If this Enemy has collided off a given corner of 'tile', also
     * checks if there are adjacent tiles in the given directions. If there
     * are none, updates this Enemy's collidedTile and collidedAt.
     * 
     * @param otherTiles the TileList to be checked against for diagonal
     * collision
     * 
     * @see #collidesAtBottomLeftWith(Tile, TileList)
     * @see #collidesAtBottomRightWith(Tile, TileList)
     * @see #collidesAtTopLeftWith(Tile, TileList)
     * @see #collidesAtTopRightWith(Tile, TileList)
     * @see Tile#getAdjacentTile(Direction)
     */
    private void checkForDiagonalCollisionWith(TileList otherTiles) {

        for (Tile tile : otherTiles.toList()) {

            Tile top = tile.getAdjacentTile(Direction.UP);
            Tile bottom = tile.getAdjacentTile(Direction.DOWN);
            Tile left = tile.getAdjacentTile(Direction.LEFT);
            Tile right = tile.getAdjacentTile(Direction.RIGHT);
            
            if (collidesAtTopLeftWith(tile) &&
                !otherTiles.contains(top) && !otherTiles.contains(left)) {
                
                collidedTile = tile;
                collidedAt = Side.TOPLEFT;
            }
            else if (collidesAtTopRightWith(tile) &&
                !otherTiles.contains(top) && !otherTiles.contains(right)) {
                
                collidedTile = tile;
                collidedAt = Side.BOTTOMLEFT;
            }
            else if (collidesAtBottomLeftWith(tile) &&
                !otherTiles.contains(bottom) && !otherTiles.contains(left)) {
                
                collidedTile = tile;
                collidedAt = Side.TOPRIGHT;
            }
            else if (collidesAtBottomRightWith(tile) &&
                !otherTiles.contains(bottom) && !otherTiles.contains(right)) {
                
                collidedTile = tile;
                collidedAt = Side.BOTTOMRIGHT;
            }
        }
    }

    /**
     * Checks whether this Enemy has collided off the side of a tile
     * in the specified TileList 'otherTiles'.
     * <p>
     * For each tile in 'otherTiles', checks whether this Enemy has
     * collided with a tile in each of the four possible sides. If it
     * has collided with a tile at a given side, also checks whether
     * there is an adjacent tile on the opposite side. If there is
     * none, updates this Enemy's collidedTile and collidedAt.
     * 
     * @param otherTiles the TileList to be checked against for straight
     * collision
     * 
     * @see #collidesAtBottomWith(Tile)
     * @see #collidesAtLeftWith(Tile)
     * @see #collidesAtRightWith(Tile)
     * @see #collidesAtTopWith(Tile)
     * @see Tile#getAdjacentTile(Direction)
     */
    private void checkForStraightCollisionWith(TileList otherTiles) {

        for (Tile tile : otherTiles.toList()) {

            Tile top = tile.getAdjacentTile(Direction.UP);
            Tile bottom = tile.getAdjacentTile(Direction.DOWN);
            Tile left = tile.getAdjacentTile(Direction.LEFT);
            Tile right = tile.getAdjacentTile(Direction.RIGHT);

            if (collidesAtTopWith(tile) &&
                !otherTiles.contains(bottom)) {

                collidedTile = tile;
                collidedAt = Side.TOP;
            }
            else if (collidesAtBottomWith(tile) &&
                !otherTiles.contains(top)) {

                collidedTile = tile;
                collidedAt = Side.BOTTOM;
            }
            else if (collidesAtLeftWith(tile) &&
                !otherTiles.contains(right)) {

                collidedTile = tile;
                collidedAt = Side.LEFT;
            }
            else if (collidesAtRightWith(tile) &&
                !otherTiles.contains(left)) {

                collidedTile = tile;
                collidedAt = Side.RIGHT;
            }
        }
    }

    /**
     * Checks if this Enemy has collided off the top left corner of a
     * tile.
     * <p>
     * For a top-left collision to be possible, this Enemy must firstly
     * be moving in the down-right direction. If it is and it is at the
     * appropriate XY-coordinate in relation to the tile's XY-coordinate,
     * return true.
     * 
     * @param tile the Tile to be checked against for top-left collision
     * @param otherTiles the TileList to be checked against for adjacency
     * @return true if this Enemy has collided off the top-left corner
     * of 'tile'
     */
    private boolean collidesAtTopLeftWith(Tile tile) {

        return (movement == Movement.DOWNRIGHT &&
                getMidX() == (tile.getMidX() - SIZE) &&
                getMidY() == (tile.getMidY() - SIZE));
    }

    /**
     * Checks if this Enemy has collided off the top right corner of a
     * tile.
     * <p>
     * For a top-right collision to be possible, this Enemy must firstly
     * be moving in the down-left direction. If it is and it is at the
     * appropriate XY-coordinate in relation to the tile's XY-coordinate,
     * return true.
     * 
     * @param tile the Tile to be checked against for top-right collision
     * @param otherTiles the TileList to be checked against for adjacency
     * @return true if this Enemy has collided off the top-right corner
     * of 'tile'
     */
    private boolean collidesAtTopRightWith(Tile tile) {

        return (movement == Movement.DOWNLEFT &&
                getMidX() == (tile.getMidX() + SIZE) &&
                getMidY() == (tile.getMidY() - SIZE));
    }

    /**
     * Checks if this Enemy has collided off the bottom left corner of a
     * tile.
     * <p>
     * For a bottom-left collision to be possible, this Enemy must firstly
     * be moving in the up-right direction. If it is and it is at the
     * appropriate XY-coordinate in relation to the tile's XY-coordinate,
     * return true.
     * 
     * @param tile the Tile to be checked against for bottom-left collision
     * @param otherTiles the TileList to be checked against for adjacency
     * @return true if this Enemy has collided off the bottom-left corner
     * of 'tile'
     */
    private boolean collidesAtBottomLeftWith(Tile tile) {

        return (movement == Movement.UPRIGHT &&
                getMidX() == (tile.getMidX() - SIZE) &&
                getMidY() == (tile.getMidY() + SIZE));
    }

    /**
     * Checks if this Enemy has collided off the bottom right corner of a
     * tile.
     * <p>
     * For a bottom-right collision to be possible, this Enemy must firstly
     * be moving in the up-left direction. If it is and it is at the
     * appropriate XY-coordinate in relation to the tile's XY-coordinate,
     * return true.
     * 
     * @param tile the Tile to be checked against for bottom-right collision
     * @param otherTiles the TileList to be checked against for adjacency
     * @return true if this Enemy has collided off the bottom-right corner
     * of 'tile'
     */
    private boolean collidesAtBottomRightWith(Tile tile) {

        return (movement == Movement.UPLEFT &&
                getMidX() == (tile.getMidX() + SIZE) &&
                getMidY() == (tile.getMidY() + SIZE));
    }

    /**
     * Checks if this Enemy off the top side of a tile.
     * <p>
     * The vertical distance between this Enemy and 'tile' is calculated.
     * If this vertical distance lies between 0 and 1, and it is overlapping
     * on the horizontal axis with the same tile, returns true. Recalibrates
     * this Enemy's y-coordinate if needed.
     * 
     * @param tile the Tile to be checked against for top collision
     * @return true if this Enemy has collided off the top side of 'tile'.
     * 
     * @see #isOverlappingHorizontally(GameObject)
     */
    private boolean collidesAtTopWith(Tile tile) {

        int distY = SIZE - (y - tile.getY());

        if (isOverlappingHorizontally(tile) && (0 <= distY && distY <= 1)) {
            y = tile.getY() + SIZE;
            return true;
        }
        return false;
    }

    /**
     * Checks if this Enemy off the bottom side of a tile.
     * <p>
     * The vertical distance between this Enemy and 'tile' is calculated.
     * If this vertical distance lies between 0 and 1, and it is overlapping
     * on the horizontal axis with the same tile, returns true. Recalibrates
     * this Enemy's y-coordinate if needed.
     * 
     * @param tile the Tile to be checked against for bottom collision
     * @return true if this Enemy has collided off the bottom side of 'tile'.
     * 
     * @see #isOverlappingHorizontally(GameObject)
     */
    private boolean collidesAtBottomWith(Tile tile) {
        
        int distY = SIZE - (tile.getY() - y);

        if (isOverlappingHorizontally(tile) && (0 <= distY && distY <= 1)) {
            y = tile.getY() - SIZE;
            return true;
        }
        return false;
    }

    /**
     * Checks if this Enemy off the left side of a tile.
     * <p>
     * The horizontal distance between this Enemy and 'tile' is calculated.
     * If this horizontal distance lies between 0 and 1, and it is overlapping
     * on the vertical axis with the same tile, returns true. Recalibrates
     * this Enemy's x-coordinate if needed.
     * 
     * @param tile the Tile to be checked against for left collision
     * @return true if this Enemy has collided off the left side of 'tile'.
     * 
     * @see #isOverlappingVertically(GameObject)
     */
    private boolean collidesAtLeftWith(Tile tile) {

        int distX = SIZE - (x - tile.getX());

        if ((0 <= distX && distX <= 1) && isOverlappingVertically(tile)) {
            x = tile.getX() + SIZE;
            return true;
        }
        return false;
    }

    /**
     * Checks if this Enemy off the right side of a tile.
     * <p>
     * The horizontal distance between this Enemy and 'tile' is calculated.
     * If this horizontal distance lies between 0 and 1, and it is overlapping
     * on the vertical axis with the same tile, returns true. Recalibrates
     * this Enemy's x-coordinate if needed.
     * 
     * @param tile the Tile to be checked against for right collision
     * @return true if this Enemy has collided off the right side of 'tile'.
     * 
     * @see #isOverlappingVertically(GameObject)
     */
    private boolean collidesAtRightWith(Tile tile) {

        int distX = SIZE - (tile.getX() - x);

        if ((0 <= distX && distX <= 1) && isOverlappingVertically(tile)) {
            x = tile.getX() - SIZE;
            return true;
        }
        return false;
    }

}