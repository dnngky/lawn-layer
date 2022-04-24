package lawnlayer;

import processing.core.PImage;

public class Enemy extends Entity {

    private Movement movement;
    private Collision collidedAt;
    private Tile collidedTile;
    private TileList removedTiles;

    public Enemy(PImage sprite, String name) {
        
        super(sprite);
        this.name = name;

        movement = initialiseMovement();
        collidedAt = Collision.NONE;
        collidedTile = null;
        removedTiles = new TileList();
    }

    public Enemy(PImage sprite, int x, int y, String name) {

        super(sprite, x, y);
        this.name = name;

        movement = initialiseMovement();
        collidedAt = Collision.NONE;
        removedTiles = new TileList();
    }

    public Tile getCollidedTile() {

        return collidedTile;
    }

    public TileList getRemovedTiles() {

        return removedTiles;
    }

    public void checkForCollisionWith(TileList otherTiles, boolean printMsg) {

        for (Tile tile : otherTiles.toArray()) {
            
            if (collidesDiagonallyWith(tile, otherTiles) &&
                collidedAt == Collision.NONE) {
                
                collidedTile = tile;
                collidedAt = Collision.DIAGONAL;
            }
        }
        for (Tile tile : otherTiles.toArray()) {

            if (collidesVerticallyWith(tile) &&
                collidedAt != Collision.DIAGONAL) {

                collidedTile = tile;
                collidedAt = Collision.VERTICAL;
            }
            else if (collidesHorizontallyWith(tile) &&
                collidedAt != Collision.DIAGONAL) {

                collidedTile = tile;
                collidedAt = Collision.HORIZONTAL;
            }
        }
        if (printMsg && collidedTile != null &&
            collidedTile.getName().equals(otherTiles.getTileName()))

            System.out.printf("%s collided %sLY with %s%n",
                              this, collidedAt, collidedTile);
        
        if (collidedTile != null &&
            collidedTile.getName().equals(Info.GRASS) &&
            name.equals(Info.BEETLE)) {

            collidedTile.hide();
            removedTiles.add(collidedTile, false);
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

    private boolean collidesVerticallyWith(Tile tile) {

        return (tile.getX() <= getMidX() && getMidX() < (tile.getX() + size) &&
                Math.abs(tile.getMidY() - getMidY()) == size &&
                !tile.isHidden());
    }

    private boolean collidesHorizontallyWith(Tile tile) {

        return (Math.abs(tile.getMidX() - getMidX()) == size &&
                tile.getY() <= getMidY() && getMidY() < (tile.getY() + size) &&
                !tile.isHidden());
    }

    private boolean collidesDiagonallyWith(Tile tile, TileList otherTiles) {

        TileList adjacentTiles = tile.getAdjacentTiles();
        Tile top = adjacentTiles.get(0);
        Tile bottom = adjacentTiles.get(1);
        Tile left = adjacentTiles.get(2);
        Tile right = adjacentTiles.get(3);

        if (otherTiles.contains(top) && otherTiles.contains(bottom) ||
            otherTiles.contains(left) && otherTiles.contains(right))
            
            return false;

        boolean collidedTopLeft =
            ((tile.getX() - size) <= x && x <= (tile.getX() - size/2) &&
            (tile.getY() - size) <= y && y <= (tile.getY() - size/2) &&
            !tile.isHidden());

        boolean collidedBottomLeft =
            ((tile.getX() - size) <= x && x <= (tile.getX() - size/2) &&
            (tile.getY() + size/2) <= y && y <= (tile.getY() + size) &&
            !tile.isHidden());

        boolean collidedTopRight =
            ((tile.getX() + size/2) <= x && x <= (tile.getX() + size) &&
            (tile.getY() - size) <= y && y <= (tile.getY() - size/2) &&
            !tile.isHidden());

        boolean collidedBottomRight =
            ((tile.getX() + size/2) <= x && x <= (tile.getX() + size) &&
            (tile.getY() + size/2) <= y && y <= (tile.getY() + size) &&
            !tile.isHidden());

        return (collidedTopLeft || collidedBottomLeft ||
                collidedTopRight || collidedBottomRight);
    }

    @Override
    protected void move() {

        switch (collidedAt) {
            
            case VERTICAL:
                movement = movement.flipVertically();
                break;
            case HORIZONTAL:
                movement = movement.flipHorizontally();
                break;
            case DIAGONAL:
                movement = movement.flipDiagonally();
                break;
            default:
                break;
        }
        collidedAt = Collision.NONE;
        collidedTile = null;

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

    @Override
    protected void checkOffMapMovement() {

        int maxWidth = (Info.WIDTH - 2*size);
        int maxHeight = (Info.HEIGHT - 2*size);

        if (x < size)
            x = size;
        else if (x > maxWidth)
            x = maxWidth;
        
        if (y < size)
            y = size;
        else if (y > maxHeight)
            y = maxHeight;
    }

}