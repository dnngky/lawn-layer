package lawnlayer;

import java.util.ArrayList;
import java.util.List;

import lawnlayer.Info.Name;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * A class acting as a container for the Tile objects. Its data structure
 * and behaviours are essentially a combination of a List and a Set: an
 * ordered collection of unique objects that supports random access and
 * indexing. In addition, this class provides methods to perform game
 * mechanics on these tiles such such as filling regions, propagating
 * red paths, removing collided tiles, etc.
 */
public class TileList {

    /**
     * The number of frames per red path propagation.
     */
    private static final int FPP = 3;

    private List<Tile> tiles;
    private Name tileName;
    private PImage tileSprite;

    /**
     * Initialises an empty TileList with no specified name nor sprite
     * of the tiles to be contained inside it.
     */
    public TileList() {

        tiles = new ArrayList<>();
        
        tileName = Name.UNNAMED;
        tileSprite = null;
    }
    
    /**
     * Initialises an empty TileList with the specified tileSprite and
     * tileName of the tiles to be contained inside it.
     * 
     * @param tileSprite the PImage sprite of the tiles to be contained
     * @param tileName the name of the tiles to be contained
     */
    public TileList(PImage tileSprite, Name tileName) {

        tiles = new ArrayList<>();
        
        this.tileName = tileName;
        this.tileSprite = tileSprite;
    }
    
    /**
     * Initialises a TileList containing the tiles inside the specified
     * Tile array.
     * 
     * @param tiles the Tile array of tiles to be added
     */
    public TileList(Tile[] tiles) {

        this.tiles = new ArrayList<>();

        for (Tile tile : tiles)
            this.tiles.add(tile);

        tileName = Name.UNNAMED;
        tileSprite = null;
    }

    /**
     * Adds the specified tile into this TileList, if such tile does not
     * already exist.
     * 
     * @param tile the tile to be added
     * @return true if the tile has been successfully added
     * 
     * @see List#add(Object)
     */
    public boolean add(Tile tile) {
        
        if (!tiles.contains(tile)) {
            tiles.add(tile);
            return true;
        }
        return false;
    }

    /**
     * Adds all of the tiles inside the specified otherTiles into
     * this TileList. For each tile, if it already exists in this
     * TileList, skips and does not add it.
     * 
     * @param otherTiles the TileList of tiles to be added
     * @return true if all tiles have been successfully added
     * 
     * @see #add(Tile)
     */
    public boolean addAll(TileList otherTiles) {

        boolean allTilesAreAdded = true;

        for (Tile tile : otherTiles.toList()) {
            
            if (!this.add(tile))
                allTilesAreAdded = false;
        }
        return allTilesAreAdded;
    }

    /**
     * Clears this TileList.
     * 
     * @see List#clear()
     */
    public void clear() {

        tiles.clear();
    }

    /**
     * Checks if the specified positionTile is contained in this TileList.
     * 
     * @param positionTile the Tile to be checked against for containment
     * @return true if this TileList contains the specified positionTile
     * 
     * @see List#contains(Object)
     */
    public boolean contains(Tile positionTile) {

        return tiles.contains(positionTile);
    }

    /**
     * Draws the tiles in this TileList.
     * 
     * @param app the PApplet instance to be drawn in
     * 
     * @see GameObject#draw(PApplet)
     */
    public void drawTiles(PApplet app) {

        for (Tile tile : tiles)
            tile.draw(app);
    }

    /**
     * Fills the regions enclosed on two sides of this TileList,
     * if there are no enemies within them.
     * <p>
     * Retrieves the starting tiles for the first and second region
     * respectively. Then, fills the first and second region into two
     * TileLists. For each region, checks if there are enemies within
     * it, and clears the corresponding TileList if there is at least
     * one. Finally, updates the grass fill tiles by adding in the
     * region TileLists, and updates the unfilled tiles by removing
     * tiles that are contained in the region TileLists.
     * 
     * @param borderTiles the TileList of grass border tiles
     * @param unfilledTiles the TileList of unfilled tiles
     * @param fillTiles the TileList of grass fill tiles
     * @param concreteTiles the TileList of concrete tiles
     * @param enemies the List of enemies
     * @param printMsg toggles fill messages printed in the terminal
     * 
     * @see #convertToFillTiles(TileList)
     * @see #getStartTiles(TileList, TileList)
     * @see #fillRegion(Tile, TileList, TileList, PImage)
     * @see Enemy#isOverlapping(GameObject)
     */
    public void fill(TileList borderTiles, TileList unfilledTiles,
        TileList fillTiles, TileList concreteTiles, List<Enemy> enemies,
        boolean printMsg) {

        long before = System.currentTimeMillis();

        Tile firstStartTile =
            getStartTiles(borderTiles, concreteTiles).get(0);
        Tile secondStartTile =
            getStartTiles(borderTiles, concreteTiles).get(1);
        
        TileList firstRegion =
            fillRegion(firstStartTile, borderTiles, concreteTiles,
                fillTiles.getTileSprite());
        TileList secondRegion =
            fillRegion(secondStartTile, borderTiles, concreteTiles,
                fillTiles.getTileSprite());

        for (Enemy enemy : enemies) {

            if (enemy.isOverlapping(firstRegion))
                firstRegion.clear();
            
            if (enemy.isOverlapping(secondRegion))
                secondRegion.clear();
        }
        fillTiles.addAll(firstRegion);
        fillTiles.addAll(secondRegion);

        unfilledTiles.removeAll(firstRegion);
        unfilledTiles.removeAll(secondRegion);
        unfilledTiles.removeAll(this);

        borderTiles.convertToFillTiles(this);

        long after = System.currentTimeMillis();

        if (printMsg) {
            int totalFilled =
                this.size() + firstRegion.size() + secondRegion.size();
            System.out.printf("Filling took %d ms, filled %d tiles, " +
                "%d unfilled tiles remaining%n", (after - before),
                totalFilled, unfilledTiles.size());
        }
    }

    /**
     * Retrieves the tile of the specified index in this TileList.
     * 
     * @param index the index of the tile to be retrieved
     * @return the retrieved tile
     * 
     * @see List#get(int)
     */
    public Tile get(int index) {

        return tiles.get(index);
    }

    /**
     * Attempts to retrieve the tile in the TileList equals to specified
     * positionTile.
     * 
     * @param positionTile the tile to be retrieved
     * @return the specified tile inside this TileList if such tile exists,
     * null otherwise
     * 
     * @see Tile#equals(Object)
     */
    public Tile get(Tile positionTile) {

        for (Tile tile : tiles) {

            if (tile.equals(positionTile))
                return tile;
        }
        return null;
    }

    /**
     * Retrieves the name of the tiles in this TileList.
     * 
     * @return the name of the tiles
     */
    public Name getTileName() {

        return tileName;
    }

    /**
     * Retrieves the sprite of the tiles in this TileList.
     * 
     * @return the PImage sprite of the tiles
     */
    public PImage getTileSprite() {

        return tileSprite;
    }

    /**
     * Checks whether this TileList is empty.
     * 
     * @return true if this TileList is empty
     * 
     * @see List#isEmpty()
     */
    public boolean isEmpty() {

        return (tiles.isEmpty());
    }

    /**
     * Propagates red path as per the pre-defined FPP (frames per
     * propagation).
     * <p>
     * Loops through this TileList and locates the red path tile.
     * If a red path is found and n frames has passed since the
     * last propagation (where n is the FPP), retrieves its
     * adjacent path tiles and turn them red.
     * 
     * @param redPathSprite the PImage sprite of the red path
     * @param frameCount the current frame
     * 
     * @see Tile#getAdjacentTiles()
     * @see Tile#isRed()
     * @see Tile#turnRed(PImage, int)
     */
    public void propagate(PImage redPathSprite, int frameCount) {

        for (Tile tile : tiles) {

            if (tile.isRed() &&
                tile.getFrameOfCollision() != frameCount &&
                (frameCount - tile.getFrameOfCollision())
                % FPP == 0) {
                
                TileList adjacentTiles = tile.getAdjacentTiles();
                
                for (Tile adjacentTile : adjacentTiles.toList()) {

                    Tile adjacentPathTile = this.get(adjacentTile);

                    if (adjacentPathTile != null &&
                        !adjacentPathTile.isRed())
                        adjacentPathTile.turnRed(redPathSprite, frameCount);
                }
            }
        }
    }

    /**
     * Removes the specified tile from this TileList if such tile exists.
     * 
     * @param tile the tile to be removed
     * @return true if the tile has been successfully removed
     * 
     * @see List#remove(Object)
     */
    public boolean remove(Tile tile) {

        return tiles.remove(tile);
    }

    /**
     * Removes all of the tiles in the specified otherTiles
     * from this TileList.
     * 
     * @param otherTiles the TileList of tiles to be removed
     * 
     * @see List#removeAll(java.util.Collection)
     */
    public void removeAll(TileList otherTiles) {

        tiles.removeAll(otherTiles.toList());
    }

    /**
     * Removes any floating tiles in this TileList.
     * <p>
     * A tile is removed if it is floating around
     * this TileList and the specified otherTiles.
     * 
     * @param otherTiles the TileList of tiles to be checked against
     * for floating tiles
     * 
     * @see List#removeIf(java.util.function.Predicate)
     * @see Tile#isFloatingAround(TileList)
     */
    public void removeFloatingTiles(TileList otherTiles) {

        tiles.removeIf(tile -> 
            (tile.isFloatingAround(this) &&
            tile.isFloatingAround(otherTiles)));
    }

    /**
     * Retrieves the size of this TileList.
     * 
     * @return the size of this TileList
     * 
     * @see List#size()
     */
    public int size() {

        return tiles.size();
    }

    /**
     * Retrieves this TileList as a List. This method is
     * useful for iterating through a TileList.
     * 
     * @return the Java List container of this TileList
     */
    public List<Tile> toList() {

        return tiles;
    }

    /**
     * Retrieves the string representation of this TileList.
     * 
     * @return the string representation of this TileList
     */
    public String toString() {

        return tiles.toString();
    }

    /**
     * Updates the border tiles contained in this TileList whenever a
     * tile has been collided by an Enemy of type Beetle.
     * <p>
     * Loops through the specified fillTiles and locates the tile(s) that
     * are adjacent to the specified collidedTile. Marks those tile(s) for
     * removal from fillTiles and and adds them to this TileList, turning
     * them into new border tiles. Finally, removes the collidedTile from
     * this TileList.
     * 
     * @param collidedTile the border tile which has been collided
     * @param fillTiles the TileList of grass fill tiles
     * 
     * @see Tile#isAdjacentTo(Tile)
     */
    public void updateBorder(Tile collidedTile, TileList fillTiles) {

        TileList toBeRemoved = new TileList();
        Direction orientation = collidedTile.getOrientation();

        for (Tile tile : fillTiles.toList()) {

            if (tile.isAdjacentTo(collidedTile)) {
                
                tile.setOrientation(orientation);
                add(tile);
                toBeRemoved.add(tile);
            }
        }
        fillTiles.removeAll(toBeRemoved);
        remove(collidedTile);
    }

    /**
     * Converts the specified TileList (otherTiles) into this
     * TileList's tiles.
     * <p>
     * Loops through the TileList to be converted, and for each tile
     * creates a new tile of the same XY-coordinate but with the
     * sprite and name of this TileList's tiles. Then, adds
     * the new tile to this TileList.
     * 
     * @param otherTiles
     */
    private void convertToFillTiles(TileList otherTiles) {

        for (Tile tile : otherTiles.toList()) {

            Tile newTile = new Tile(tileSprite, tile.getX(),
                tile.getY(), tileName);
            newTile.setOrientation(tile.getOrientation());

            add(newTile);
        }
        otherTiles.clear();
    }

    /**
     * Fills the enclosed region specified by the starting tile.
     * <p>
     * Adds the starting tile as the first tile of the region tiles.
     * Then, loops through the region tiles, and for each tile in
     * the region retrieves the adjacent tiles in all four directions.
     * For each adjacent tile, checks if it is fillable (i.e., it is
     * not a path tile, grass border tile, nor concrete tiles), and
     * adds it to the current region tiles if it is. The region
     * tiles should grow progressively until there are no more
     * adjacent tiles which are fillable, indicating the entire region
     * has been filled.
     * <p>
     * If the starting tile is unnamed (i.e., there was no starting
     * tile found), then there is no region to be filled. Thus,
     * returns an empty TileList.
     * 
     * @param startTile the starting tile for filling the region
     * @param borderTiles the TileList of grass border tiles
     * @param concreteTiles the TileList of concrete tiles
     * @param fillSprite the PImage sprite for the filled region tiles
     * @return a TileList of the filled region tiles
     * 
     * @see Tile#getAdjacentTiles()
     */
    private TileList fillRegion(Tile startTile, TileList borderTiles,
        TileList concreteTiles, PImage fillSprite) {

        if (startTile.getName() == Name.UNNAMED)
            return new TileList();
    
        TileList regionTiles = new TileList(fillSprite, Name.GRASS);
        startTile.setSprite(fillSprite);
        regionTiles.add(startTile);
        
        for (int i = 0; i < regionTiles.size(); i++) {

            Tile tile = regionTiles.get(i);
            TileList adjacentTiles = tile.getAdjacentTiles();

            for (Tile adjacentTile : adjacentTiles.toList()) {

                if (!this.contains(adjacentTile) &&
                    !borderTiles.contains(adjacentTile) &&
                    !concreteTiles.contains(adjacentTile)) {
                    
                    regionTiles.add(adjacentTile);
                }
            }
        }
        return regionTiles;
    }

    /**
     * Locates the starting tile for filling each of the two regions.
     * <p>
     * Loops through the path tiles, and for each path tile retrieves the
     * adjacent tiles in the directions normal to the path's orientation.
     * If the adjacent tile is clear (i.e., it is not a concrete, path,
     * nor grass border tile), proceeds to set it as the starting tile.
     * 
     * @param borderTiles the TileList of grass border tiles
     * @param concreteTiles the TileList of concrete tiles
     * @return a TileList of starting tiles for each region
     * 
     * @see Direction#normal()
     * @see Direction#flip()
     * @see Tile#getAdjacentTile(Direction)
     */
    private TileList getStartTiles(TileList borderTiles,
        TileList concreteTiles) {

        Tile firstStartTile = new Tile();
        Tile secondStartTile = new Tile();

        for (Tile tile : tiles) {

            Direction direction = tile.getOrientation();

            Tile firstAdjacentTile =
                tile.getAdjacentTile(direction.normal());
            
            if (!borderTiles.contains(firstAdjacentTile) &&
                !concreteTiles.contains(firstAdjacentTile) &&
                !this.contains(firstAdjacentTile)) {

                firstStartTile = firstAdjacentTile;
                firstStartTile.setName(Name.GRASS);
                break;
            }
        }
        for (Tile tile : tiles) {

            Direction direction = tile.getOrientation();

            Tile secondAdjacentTile =
                tile.getAdjacentTile(direction.normal().flip());
            
            if (!borderTiles.contains(secondAdjacentTile) &&
                !concreteTiles.contains(secondAdjacentTile) &&
                !this.contains(secondAdjacentTile)) {

                secondStartTile = secondAdjacentTile;
                secondStartTile.setName(Name.GRASS);
                break;
            }
        }
        return new TileList(new Tile[] {firstStartTile, secondStartTile});
    }
    
}