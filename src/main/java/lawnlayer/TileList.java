package lawnlayer;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PImage;

public class TileList {

    private List<Tile> tiles;
    private String tileName;
    private int tileSize;
    private PImage tileSprite;

    public TileList() {

        tiles = new ArrayList<>();
        
        tileName = "UnnamedTileList";
        tileSize = Info.SPRITESIZE;
        tileSprite = null;
    }
    
    public TileList(PImage tileSprite, String tileName) {

        tiles = new ArrayList<>();
        
        this.tileName = tileName;
        tileSize = Info.SPRITESIZE;
        this.tileSprite = tileSprite;
    }

    public TileList(Tile[] tiles) {

        this.tiles = new ArrayList<>();

        for (Tile tile : tiles)
            this.tiles.add(tile);

        tileName = "UnnamedTileList";
        tileSize = Info.SPRITESIZE;
        tileSprite = null;
    }

    public void add(Tile tile) {
        
        if (!tiles.contains(tile))
            tiles.add(tile);
    }

    public void add(int index, Tile tile) {
        
        if (!tiles.contains(tile))
            tiles.add(index, tile);
    }

    public void addAll(TileList otherTiles) {

        for (Tile tile : otherTiles.toArray())
            this.add(tile);
    }

    public void clear() {

        tiles.clear();
    }

    public boolean contains(Tile positionTile) {

        return tiles.contains(positionTile);
    }

    public void convertToFillTiles(TileList otherTiles) {

        for (Tile tile : otherTiles.toArray()) {

            Tile borderTile = new Tile(tileSprite, tile.getX(),
                tile.getY(), tileName);
            borderTile.setOrientation(tile.getOrientation());

            add(borderTile);
        }
        otherTiles.clear();
    }

    public void drawTiles(PApplet app) {

        for (Tile tile : tiles)
            tile.draw(app);
    }

    public void fill(TileList borderTiles, TileList fillTiles,
        TileList concreteTiles, List<Enemy> enemies) {

        Tile headTile = tiles.get(0);
        Tile tailTile = tiles.get(tiles.size() - 1);

        if (headTile.isParallelTo(tailTile))
            fillForParallelHeadAndTail(borderTiles, fillTiles,
                concreteTiles, headTile, tailTile, enemies);
        
        else if (headTile.isNormalTo(tailTile))
            fillForNormalHeadAndTail(borderTiles, fillTiles,
                concreteTiles, headTile, tailTile, enemies);
        
        else if (headTile.isOppositeTo(tailTile))
            fillForOppositeHeadAndTail(borderTiles, fillTiles,
                concreteTiles, headTile, tailTile, enemies);

        borderTiles.convertToFillTiles(this);
    }

    public Tile get(int index) {

        return tiles.get(index);
    }

    public Tile get(Tile positionTile) {

        for (Tile tile : tiles) {

            if (tile.getX() == positionTile.getX() &&
                    tile.getY() == positionTile.getY())
                return tile;
        }
        return null;
    }

    public String getTileName() {

        return tileName;
    }

    public PImage getTileSprite() {

        return tileSprite;
    }

    public boolean isClosedOffBy(TileList borderTiles,
        TileList concreteTiles) {

        Tile headTile =
            new Tile(borderTiles.getTileSprite(), -1, -1, Info.PATH);
        Tile tailTile =
            new Tile(borderTiles.getTileSprite(), -1, -1, Info.PATH);

        if (!isEmpty() &&
            !isAllAdjacentTo(concreteTiles) &&
            !isAllAdjacentTo(borderTiles) &&
            headAndTailAreAdjacentTo(borderTiles, concreteTiles))

            return true;
        
        if (tiles.size() == 2) {
            headTile = tiles.get(0);
            tailTile = tiles.get(tiles.size() - 1);
        }
        if (tiles.size() == 2 &&
            headTile.isAdjacentTo(borderTiles, concreteTiles) &&
            tailTile.isAdjacentTo(borderTiles, concreteTiles)) {

            if ((headTile.getOrientation() == Direction.UP ||
                headTile.getOrientation() == Direction.DOWN) &&
                headTile.isAdjacentVerticallyTo(borderTiles)) {

                return true;
            }
            if ((headTile.getOrientation() == Direction.LEFT ||
                headTile.getOrientation() == Direction.RIGHT) &&
                headTile.isAdjacentHorizontallyTo(borderTiles)) {

                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {

        return (tiles.isEmpty());
    }

    public void propagate(PImage redPathSprite, int frameCount) {

        for (Tile tile : tiles) {

            if (tile.isCollided() &&
                tile.getFrameOfCollision() != frameCount &&
                (frameCount - tile.getFrameOfCollision())
                % Info.FPP == 0) {
                
                TileList adjacentTiles = tile.getAdjacentTiles();
                
                for (Tile adjacentTile : adjacentTiles.toArray()) {

                    if (!tiles.contains(adjacentTile))
                        continue;

                    Tile adjacentPathTile = this.get(adjacentTile);

                    if (!adjacentPathTile.isCollided())
                        adjacentPathTile.turnRed(redPathSprite, frameCount);
                }
            }
        }
    }

    public Tile remove(int index) {

        return tiles.remove(index);
    }

    public boolean remove(Tile tile) {

        return tiles.remove(tile);
    }

    public void removeFloatingTiles() {

        int i = 0;

        while (i < tiles.size()) {

            Tile tile = tiles.get(i);

            if (tile.isFloatingAround(this))
                tiles.remove(tile);
            else
                i++;
        }
    }

    public int size() {

        return tiles.size();
    }

    public List<Tile> toArray() {

        return tiles;
    }

    private void ascendingSort(TileList bounds, String xy) {

        int i = 0;
        while (i < bounds.size()) {

            Tile largest = bounds.get(i);

            for (int j = i; j < bounds.size(); j++) {

                Tile current = bounds.get(j);

                if ((xy.equals("X") &&
                    current.getX() > largest.getX()) ||
                    (xy.equals("Y") &&
                    current.getY() > largest.getY()))

                    largest = current;
            }
            bounds.remove(largest);
            bounds.add(0, largest);
            i++;
        }
    }

    private void descendingSort(TileList bounds, String xy) {

        int i = 0;
        while (i < bounds.size()) {

            Tile largest = bounds.get(0);

            for (int j = 0; j < (bounds.size() - i); j++) {

                Tile current = bounds.get(j);

                if ((xy.equals("X") &&
                    current.getX() > largest.getX()) ||
                    (xy.equals("Y") &&
                    current.getY() > largest.getY()))

                    largest = current;
            }
            bounds.remove(largest);
            bounds.add(bounds.size(), largest);
            i++;
        }
    }

    private void fillForOppositeHeadAndTail(TileList borderTiles,
        TileList fillTiles, TileList concreteTiles, Tile headTile,
        Tile tailTile, List<Enemy> enemies) {
        
        Direction direction = Direction.NONE;
        
        if (headTile.getOrientation() == Direction.LEFT ||
            headTile.getOrientation() == Direction.RIGHT) {
            
            int distY = headTile.getY() - tailTile.getY();

            if (distY > 0)
                direction = Direction.UP;
            else
                direction = Direction.DOWN;
        }
        if (headTile.getOrientation() == Direction.UP ||
            headTile.getOrientation() == Direction.DOWN) {
            
            int distX = headTile.getX() - tailTile.getX();

            if (distX > 0)
                direction = Direction.LEFT;
            else
                direction = Direction.RIGHT;
        }
        TileList bounds = new TileList();
        int[][] limits;

        bounds.addAll(this);
        getBounds(bounds, borderTiles, concreteTiles, headTile, tailTile,
            direction);
        getBounds(bounds, borderTiles, concreteTiles, tailTile, headTile,
            direction.flip());

        limits = bounds.getLimits();

        TileList firstScan =
            fillInsideRegion(bounds, borderTiles, concreteTiles, limits,
                tailTile.getOrientation());

        bounds.clear();
        bounds.addAll(this);
        getBounds(bounds, borderTiles, concreteTiles, headTile, tailTile,
            direction.flip());
        getBounds(bounds, borderTiles, concreteTiles, tailTile, headTile,
            direction);

        limits = bounds.getLimits();

        TileList secondScan =
            fillInsideRegion(bounds, borderTiles, concreteTiles, limits,
                headTile.getOrientation());
        
        firstScan.fillMissingStrips(borderTiles, fillTiles, concreteTiles,
            bounds, secondScan);
        secondScan.fillMissingStrips(borderTiles, fillTiles, concreteTiles,
            bounds, firstScan);
        
        TileList[] regions = 
            getRegions(firstScan, secondScan, borderTiles, concreteTiles,
                bounds, enemies);
        
        fillTiles.addAll(regions[0]);
        fillTiles.addAll(regions[1]);
    }

    private void fillForNormalHeadAndTail(TileList borderTiles,
    TileList fillTiles, TileList concreteTiles, Tile headTile,
    Tile tailTile, List<Enemy> enemies) {
        
        Direction firstDirection = tailTile.getOrientation();
        Direction secondDirection = headTile.getOppositeOrientation();

        TileList bounds = new TileList();
        int[][] limits;

        bounds.addAll(this);
        getBounds(bounds, borderTiles, concreteTiles, headTile, tailTile,
            firstDirection);
        getBounds(bounds, borderTiles, concreteTiles, tailTile, headTile,
            secondDirection);
        
        limits = bounds.getLimits();

        TileList firstScan =
            fillInsideRegion(bounds, borderTiles, concreteTiles, limits,
                firstDirection);

        if (!firstScan.isEnclosed(borderTiles, concreteTiles, bounds))
            firstScan =
                fillInsideRegion(bounds, borderTiles, concreteTiles, limits,
                    secondDirection);

        bounds.clear();
        bounds.addAll(this);
        getBounds(bounds, borderTiles, concreteTiles, headTile, tailTile,
            firstDirection.flip());
        getBounds(bounds, borderTiles, concreteTiles, tailTile, headTile,
            secondDirection.flip());

        limits = bounds.getLimits();

        TileList secondScan =
            fillInsideRegion(bounds, borderTiles, concreteTiles, limits,
                firstDirection.flip());
        
        firstScan.fillMissingStrips(borderTiles, fillTiles, concreteTiles,
            bounds, secondScan);
        secondScan.fillMissingStrips(borderTiles, fillTiles, concreteTiles,
            bounds, firstScan);
        
        TileList[] regions = 
            getRegions(firstScan, secondScan, borderTiles, concreteTiles,
                bounds, enemies);
        
        fillTiles.addAll(regions[0]);
        fillTiles.addAll(regions[1]);
    }

    private void fillForParallelHeadAndTail(TileList borderTiles,
        TileList fillTiles, TileList concreteTiles, Tile headTile,
        Tile tailTile, List<Enemy> enemies) {

        Direction direction = headTile.getOrientation().normal();

        TileList bounds = new TileList(fillTiles.getTileSprite(), Info.GRASS);
        int[][] limits = new int[2][2];
        
        bounds.addAll(this);
        getBounds(bounds, borderTiles, concreteTiles, headTile, tailTile,
            direction);
        getBounds(bounds, borderTiles, concreteTiles, tailTile, headTile,
            direction);

        if (direction == Direction.UP ||
            direction == Direction.DOWN)

            limits =
                new int[][] {{0, 0}, {Info.TOPBAR, Info.HEIGHT - tileSize}};

        if (direction == Direction.LEFT ||
            direction == Direction.RIGHT)

            limits =
                new int[][] {{0, Info.WIDTH - tileSize}, {0, 0}};

        TileList firstScan =
            fillInsideRegion(bounds, borderTiles, concreteTiles, limits,
                direction);
        
        bounds.clear();
        bounds.addAll(this);
        getBounds(bounds, borderTiles, concreteTiles, headTile, tailTile,
            direction.flip());
        getBounds(bounds, borderTiles, concreteTiles, tailTile, headTile,
            direction.flip());

        TileList secondScan =
            fillInsideRegion(bounds, borderTiles, concreteTiles, limits,
                direction.flip());

        firstScan.fillMissingStrips(borderTiles, fillTiles, concreteTiles,
            bounds, secondScan);
        secondScan.fillMissingStrips(borderTiles, fillTiles, concreteTiles,
            bounds, firstScan);
        
        TileList[] regions = 
            getRegions(firstScan, secondScan, borderTiles, concreteTiles,
                bounds, enemies);
        
        fillTiles.addAll(regions[0]);
        fillTiles.addAll(regions[1]);
    }

    private TileList fillInsideRegion(TileList bounds, TileList borderTiles,
        TileList concreteTiles, int[][] limits, Direction direction) {
        
        TileList regionTiles =
            new TileList(borderTiles.getTileSprite(), Info.GRASS);

        switch (direction) {

            case LEFT:
                descendingSort(bounds, "X");
                break;
            case RIGHT:
                ascendingSort(bounds, "X");
                break;
            case UP:
                descendingSort(bounds, "Y");
                break;
            case DOWN:
                ascendingSort(bounds, "Y");
                break;
            default:
                break;
        }
        for (Tile bound : bounds.toArray()) {

            TileList strip;

            switch (direction) {

                case LEFT:
                    strip =
                        fillStripLeftwards(bound, regionTiles, borderTiles,
                            concreteTiles, limits[0][0]);
                    break;
                case RIGHT:
                    strip =
                        fillStripRightwards(bound, regionTiles, borderTiles,
                            concreteTiles, limits[0][1]);
                    break;
                case UP:
                    strip =
                        fillStripUpwards(bound, regionTiles, borderTiles,
                            concreteTiles, limits[1][0]);
                    break;
                case DOWN:
                    strip =
                        fillStripDownwards(bound, regionTiles, borderTiles,
                            concreteTiles, limits[1][1]);
                    break;
                default:
                    strip = new TileList();
                    break;
            }
            regionTiles.addAll(strip);
        }
        return regionTiles;
    }

    private TileList fillOutsideRegion(TileList firstRegion,
        TileList borderTiles, TileList concreteTiles) {

        TileList regionTiles =
            new TileList(borderTiles.getTileSprite(), Info.GRASS);
        
        for (int y = Info.TOPBAR; y < (Info.HEIGHT - tileSize); y += tileSize) {

            for (int x = 0; x < (Info.WIDTH - tileSize); x += tileSize) {

                Tile positionTile =
                    new Tile(borderTiles.getTileSprite(), x, y, Info.GRASS);

                if (!firstRegion.contains(positionTile) &&
                    !borderTiles.contains(positionTile) &&
                    !concreteTiles.contains(positionTile))

                    regionTiles.add(positionTile);
            }
        }
        return regionTiles;
    }

    private void fillMissingStrips(TileList borderTiles, TileList fillTiles,
        TileList concreteTiles, TileList bounds, TileList otherScan) {
        
        for (int y = Info.TOPBAR; y < (Info.HEIGHT - tileSize); y += tileSize) {

            for (int x = 0; x < (Info.WIDTH - tileSize); x += tileSize) {

                Tile positionTile =
                    new Tile(tileSprite, x, y, Info.GRASS);

                if (borderTiles.contains(positionTile) ||
                    concreteTiles.contains(positionTile) ||
                    bounds.contains(positionTile) ||
                    otherScan.contains(positionTile) ||
                    this.contains(positionTile))
                    continue;

                TileList adjacentTiles = positionTile.getAdjacentTiles();

                for (Tile tile : adjacentTiles.toArray()) {

                    if (this.contains(tile) ||
                        fillTiles.contains(tile)) {
                        
                        this.add(positionTile);
                        break;
                    }
                }
            }
        }
    }

    private TileList fillStripDownwards(Tile bound, TileList regionTiles,
        TileList borderTiles, TileList concreteTiles, int limit) {
        
        TileList strip = new TileList();
        Tile up = bound.getAdjacentTile(Direction.UP);

        if (this.contains(up) ||
            regionTiles.contains(up) ||
            borderTiles.contains(up))
            return strip;
        
        int x = bound.getX();
        
        for (int y = (bound.getY() + tileSize); y < limit; y += tileSize) {

            Tile newTile =
                new Tile(borderTiles.getTileSprite(), x, y, Info.GRASS);

            if (this.contains(newTile) ||
                concreteTiles.contains(newTile) ||
                borderTiles.contains(newTile))
                break;

            strip.add(newTile);
        }
        return strip;
    }

    private TileList fillStripLeftwards(Tile bound, TileList regionTiles,
        TileList borderTiles, TileList concreteTiles, int limit) {
        
        TileList strip = new TileList();
        Tile right = bound.getAdjacentTile(Direction.RIGHT);

        if (this.contains(right) ||
            regionTiles.contains(right) ||
            borderTiles.contains(right))
            return strip;
        
        int y = bound.getY();
        
        for (int x = (bound.getX() - tileSize); x > limit; x -= tileSize) {

            Tile newTile =
                new Tile(borderTiles.getTileSprite(), x, y, Info.GRASS);

            if (this.contains(newTile) ||
                concreteTiles.contains(newTile) ||
                borderTiles.contains(newTile))
                break;

            strip.add(newTile);
        }
        return strip;
    }

    private TileList fillStripRightwards(Tile bound, TileList regionTiles,
        TileList borderTiles, TileList concreteTiles, int limit) {
        
        TileList strip = new TileList();
        Tile left = bound.getAdjacentTile(Direction.LEFT);

        if (this.contains(left) ||
            regionTiles.contains(left) ||
            borderTiles.contains(left))
            return strip;
        
        int y = bound.getY();
        
        for (int x = (bound.getX() + tileSize); x < limit; x += tileSize) {

            Tile newTile =
                new Tile(borderTiles.getTileSprite(), x, y, Info.GRASS);

            if (this.contains(newTile) ||
                concreteTiles.contains(newTile) ||
                borderTiles.contains(newTile))
                break;
            
            strip.add(newTile);
        }
        return strip;
    }

    private TileList fillStripUpwards(Tile bound, TileList regionTiles,
        TileList borderTiles, TileList concreteTiles, int limit) {
        
        TileList strip = new TileList();
        Tile down = bound.getAdjacentTile(Direction.DOWN);

        if (this.contains(down) ||
            regionTiles.contains(down) ||
            borderTiles.contains(down))
            return strip;
        
        int x = bound.getX();
        
        for (int y = (bound.getY() - tileSize); y > limit; y -= tileSize) {

            Tile newTile =
                new Tile(borderTiles.getTileSprite(), x, y, Info.GRASS);

            if (this.contains(newTile) ||
                concreteTiles.contains(newTile) ||
                borderTiles.contains(newTile))
                break;

            strip.add(newTile);
        }
        return strip;
    }

    public void getBounds(TileList bounds, TileList borderTiles,
        TileList concreteTiles, Tile start, Tile end, Direction direction) {

        Tile current = start;
        Direction previousDirection = Direction.NONE;
        boolean collidedWithConcrete = false;

        while (true) {

            boolean hasUpdated = false;
            TileList directionTiles = getDirectionTiles(current, direction);
            
            for (Tile tile : directionTiles.toArray()) {

                if (borderTiles.get(tile) != null &&
                    previousDirection != tile.getOppositeOrientation()) {
                
                    current = tile;
                    bounds.add(current);
                    previousDirection = tile.getOrientation();
                    hasUpdated = true;
                    break;
                }
            }
            for (Tile tile : directionTiles.toArray()) {

                if (concreteTiles.contains(tile)) {
                    
                    collidedWithConcrete = true;
                    hasUpdated = true;
                    break;
                }
            }
            if (!hasUpdated ||
                collidedWithConcrete ||
                directionTiles.contains(end))
                break;
        }
    }

    private TileList getDirectionTiles(Tile currentTile, Direction direction) {

        Tile first = currentTile.getAdjacentTile(direction);
        Tile second = currentTile.getAdjacentTile(direction.normal());
        Tile third = currentTile.getAdjacentTile(direction.normal().flip());
        Tile fourth = currentTile.getAdjacentTile(direction.normal());

        return new TileList(new Tile[] {first, second, third, fourth});
    }

    private int[][] getLimits() {

        int xUpper = tiles.get(0).getX();
        int xLower = tiles.get(0).getX();
        int yUpper = tiles.get(0).getY();
        int yLower = tiles.get(0).getY();

        for (Tile tile : tiles) {

            if (tile.getX() < xUpper)
                xUpper = tile.getX();
            else if (tile.getX() > xLower)
                xLower = tile.getX();

            if (tile.getY() < yUpper)
                yUpper = tile.getY();
            else if (tile.getY() > yLower)
                yLower = tile.getY();
        }
        return new int[][] {{xUpper - tileSize, xLower + tileSize},
                            {yUpper - tileSize, yLower + tileSize}};
    }

    private TileList[] getRegions(TileList firstScan, TileList secondScan,
        TileList borderTiles, TileList concreteTiles, TileList bounds,
        List<Enemy> enemies) {

        TileList firstRegion;
        TileList secondRegion;

        if (firstScan.isEnclosed(borderTiles, concreteTiles, bounds) &&
            secondScan.isEnclosed(borderTiles, concreteTiles, bounds)) {
            
            firstRegion = firstScan;
            secondRegion = secondScan;
        }
        else if (firstScan.isEnclosed(borderTiles, concreteTiles, bounds) ||
            secondScan.equals(firstScan)) {

            firstRegion = firstScan;
            secondRegion = fillOutsideRegion(firstRegion, borderTiles,
                concreteTiles);
        }
        else if (secondScan.isEnclosed(borderTiles, concreteTiles, bounds)) {

            secondRegion = secondScan;
            firstRegion = fillOutsideRegion(secondRegion, borderTiles,
                concreteTiles);
        }
        else {

            firstRegion = new TileList();
            secondRegion = new TileList();
        }
        for (Enemy enemy : enemies) {

            if (enemy.isInsideRegion(firstRegion))
                firstRegion.clear();
            
            if (enemy.isInsideRegion(secondRegion))
                secondRegion.clear();
        }
        return new TileList[] { firstRegion, secondRegion };
    }

    private boolean headAndTailAreAdjacentTo(TileList borderTiles,
        TileList otherTiles) {

        Tile headTile = tiles.get(0);
        Tile tailTile = tiles.get(tiles.size() - 1);

        boolean tailIsAdjacent = false;
        boolean headIsAdjacent = false;

        for (Tile tile : otherTiles.toArray()) {

            if (headTile.isAdjacentTo(tile))
                headIsAdjacent = true;
        }
        for (Tile tile : borderTiles.toArray()) {

            if (headTile.isAdjacentTo(tile))
                headIsAdjacent = true;
        }
        for (Tile tile : otherTiles.toArray()) {

            if (tailTile.isAdjacentTo(tile))
                tailIsAdjacent = true;
        }
        for (Tile tile : borderTiles.toArray()) {

            if (tailTile.isAdjacentTo(tile) &&
                !headTile.equals(tailTile))
                tailIsAdjacent = true;
        }
        return (tailIsAdjacent && headIsAdjacent);
    }

    private boolean isAllAdjacentTo(TileList otherTiles) {

        for (Tile tile : tiles) {

            if (!tile.isAdjacentTo(otherTiles))
                return false;
        }
        return true;
    }

    private boolean isEnclosed(TileList borderTiles, TileList concreteTiles,
        TileList bounds) {

        if (tiles.isEmpty())
            return false;
        
        for (Tile tile : tiles) {

            TileList adjacentTiles = tile.getAdjacentTiles();

            for (Tile adjacentTile : adjacentTiles.toArray()) {

                if (!this.contains(adjacentTile) &&
                    !borderTiles.contains(adjacentTile) &&
                    !concreteTiles.contains(adjacentTile) &&
                    !bounds.contains(adjacentTile))
                    
                    return false;
            }
        }
        return true;
    }

}