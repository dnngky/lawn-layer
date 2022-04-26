package lawnlayer;

import processing.core.PImage;

public class Enemy extends Entity {

    private Movement movement;
    private Collision collidedAt;
    private Tile collidedTile;
    private Tile previouslyCollidedTile;
    private TileList removedTiles;

    public Enemy(PImage sprite, String name) {
        
        super(sprite, name);

        movement = initialiseMovement();
        collidedAt = Collision.NONE;
        collidedTile = null;
        previouslyCollidedTile = null;
        removedTiles = new TileList();
    }

    public Enemy(PImage sprite, int x, int y, String name) {
        
        super(sprite, x, y, name);

        movement = initialiseMovement();
        collidedAt = Collision.NONE;
        collidedTile = null;
        previouslyCollidedTile = null;
        removedTiles = new TileList();
    }

    public Enemy(PImage sprite, String location, String name) {
        
        super(sprite, location, name);

        movement = initialiseMovement();
        collidedAt = Collision.NONE;
        collidedTile = null;
        previouslyCollidedTile = null;
        removedTiles = new TileList();
    }

    public Tile getCollidedTile() {

        return collidedTile;
    }

    public TileList getRemovedTiles() {

        return removedTiles;
    }

    public void checkForCollisionWith(TileList otherTiles, boolean printMsg) {

        checkForDiagonalCollisionWith(otherTiles);
        checkForStraightCollisionWith(otherTiles);
        
        if (printMsg && collidedAt != Collision.NONE &&
            collidedTile.getName().equals(otherTiles.getTileName()))

            System.out.printf("%s collided %sLY with %s%n",
                              this, collidedAt, collidedTile);
        
        if (collidedAt != Collision.NONE &&
            collidedTile.getName().equals(Info.GRASS) &&
            name.equals(Info.BEETLE)) {

            otherTiles.remove(collidedTile);
            removedTiles.add(collidedTile);
        }
    }

    public boolean hasCollidedWith(TileList otherTiles) {

        return (collidedTile != null &&
                collidedTile.getName().equals(otherTiles.getTileName()));
    }
    
    public boolean isInsideRegion(TileList fillTiles) {

        for (Tile tile : fillTiles.toArray()) {

            if (this.isOverlapping(tile))

                return true;
        }
        return false;
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

    private void checkForDiagonalCollisionWith(TileList otherTiles) {

        for (Tile tile : otherTiles.toArray()) {
            
            if (collidesTopLeftWith(tile, otherTiles) &&
                collidedAt == Collision.NONE &&
                previouslyCollidedTile != tile) {
                
                collidedTile = tile;
                collidedAt = Collision.TOPLEFT;
            }
            else if (collidesTopRightWith(tile, otherTiles) &&
                collidedAt == Collision.NONE &&
                previouslyCollidedTile != tile) {
                
                collidedTile = tile;
                collidedAt = Collision.BOTTOMLEFT;
            }
            else if (collidesBottomLeftWith(tile, otherTiles) &&
                collidedAt == Collision.NONE &&
                previouslyCollidedTile != tile) {
                
                collidedTile = tile;
                collidedAt = Collision.TOPRIGHT;
            }
            else if (collidesBottomRightWith(tile, otherTiles) &&
                collidedAt == Collision.NONE &&
                previouslyCollidedTile != tile) {
                
                collidedTile = tile;
                collidedAt = Collision.BOTTOMRIGHT;
            }
        }
    }

    private void checkForStraightCollisionWith(TileList otherTiles) {

        for (Tile tile : otherTiles.toArray()) {

            if (collidesVerticallyWith(tile) &&
                collidedAt != Collision.TOPLEFT &&
                collidedAt != Collision.BOTTOMLEFT &&
                collidedAt != Collision.TOPRIGHT &&
                collidedAt != Collision.BOTTOMRIGHT &&
                previouslyCollidedTile != tile) {

                collidedTile = tile;
                collidedAt = Collision.VERTICAL;
            }
            else if (collidesHorizontallyWith(tile) &&
                collidedAt != Collision.TOPLEFT &&
                collidedAt != Collision.BOTTOMLEFT &&
                collidedAt != Collision.TOPRIGHT &&
                collidedAt != Collision.BOTTOMRIGHT &&
                previouslyCollidedTile != tile) {

                collidedTile = tile;
                collidedAt = Collision.HORIZONTAL;
            }
        }
    }

    private boolean collidesVerticallyWith(Tile tile) {

        return (tile.getX() <= getMidX() && getMidX() < (tile.getX() + size) &&
                Math.abs(tile.getMidY() - getMidY()) == size);
    }

    private boolean collidesHorizontallyWith(Tile tile) {

        return (Math.abs(tile.getMidX() - getMidX()) == size &&
                tile.getY() <= getMidY() && getMidY() < (tile.getY() + size));
    }

    private boolean collidesTopLeftWith(Tile tile, TileList otherTiles) {

        Tile top = tile.getAdjacentTile(Direction.UP);
        Tile left = tile.getAdjacentTile(Direction.LEFT);

        return (!otherTiles.contains(top) && !otherTiles.contains(left) &&
                (tile.getX() - size) <= x && x <= (tile.getX() - size/2) &&
                (tile.getY() - size) <= y && y <= (tile.getY() - size/2));
    }

    private boolean collidesBottomLeftWith(Tile tile, TileList otherTiles) {

        Tile bottom = tile.getAdjacentTile(Direction.DOWN);
        Tile left = tile.getAdjacentTile(Direction.LEFT);

        return (!otherTiles.contains(bottom) && !otherTiles.contains(left) &&
                (tile.getX() - size) <= x && x <= (tile.getX() - size/2) &&
                (tile.getY() + size/2) <= y && y <= (tile.getY() + size));
    }

    private boolean collidesTopRightWith(Tile tile, TileList otherTiles) {

        Tile top = tile.getAdjacentTile(Direction.UP);
        Tile right = tile.getAdjacentTile(Direction.RIGHT);

        return (!otherTiles.contains(top) && !otherTiles.contains(right) &&
                (tile.getX() + size/2) <= x && x <= (tile.getX() + size) &&
                (tile.getY() - size) <= y && y <= (tile.getY() - size/2));
    }

    private boolean collidesBottomRightWith(Tile tile, TileList otherTiles) {

        Tile bottom = tile.getAdjacentTile(Direction.DOWN);
        Tile right = tile.getAdjacentTile(Direction.RIGHT);

        return (!otherTiles.contains(bottom) && !otherTiles.contains(right) &&
                (tile.getX() + size/2) <= x && x <= (tile.getX() + size) &&
                (tile.getY() + size/2) <= y && y <= (tile.getY() + size));
    }

    public void unstuckIfIsStuckInside(TileList otherTiles) {

        for (Tile tile : otherTiles.toArray()) {

            int xDist = Math.abs(x - tile.getX());
            int yDist = Math.abs(y - tile.getY());

            if (xDist < size && yDist < size) {

                boolean isUnstuck = false;
                TileList adjacentTiles = tile.getAdjacentTiles();

                for (Tile adjacentTile : adjacentTiles.toArray()) {

                    if (!otherTiles.contains(adjacentTile) &&
                        !adjacentTile.isOutOfBounds()) {

                        x = adjacentTile.getX();
                        y = adjacentTile.getY();
                        isUnstuck = true;
                        collidedAt = Collision.NONE;
                        break;
                    }
                }
                if (!isUnstuck) {
                    randomiseXY();
                    initialiseMovement();
                }
            }
        }
    }

    @Override
    protected void move() {

        checkForOffMapMovement();

        switch (collidedAt) {
            
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
            case VERTICAL:
                movement = movement.flipVertically();
                break;
            case HORIZONTAL:
                movement = movement.flipHorizontally();
                break;
            default:
                break;
        }
        collidedAt = Collision.NONE;
        previouslyCollidedTile = collidedTile;
        collidedTile = null;

        switch (movement) {

            case UPLEFT:
                y -= Info.SPEED;
                x -= Info.SPEED;
                break;
            case UPRIGHT:
                y -= Info.SPEED;
                x += Info.SPEED;
                break;
            case DOWNLEFT:
                y += Info.SPEED;
                x -= Info.SPEED;
                break;
            case DOWNRIGHT:
                y += Info.SPEED;
                x += Info.SPEED;
                break;
            case STATIONARY:
                break;
        }
    }

    @Override
    protected void checkForOffMapMovement() {

        int maxWidth = (Info.WIDTH - 2*size);
        int maxHeight = (Info.HEIGHT - 2*size);

        if (x < size)
            x = size;
        else if (x > maxWidth)
            x = maxWidth;
        
        if (y < Info.TOPBAR + size)
            y = Info.TOPBAR + size;
        else if (y > maxHeight)
            y = maxHeight;
    }

}