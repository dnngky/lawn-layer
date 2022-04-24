package lawnlayer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PImage;

public class TileList {

    private List<Tile> tiles;
    private boolean isOverriding;
    
    private PImage tileSprite;
    private String tileName;
    private static int tileSize = Info.SPRITESIZE;

    public TileList() {

        tiles = new ArrayList<>();
        tileSprite = null;
        isOverriding = false;
    }
    
    public TileList(PImage tileSprite, String tileName) {

        tiles = new ArrayList<>();
        this.tileSprite = tileSprite;
        this.tileName = tileName;
        isOverriding = false;
    }

    public TileList(Tile[] tiles) {

        this.tiles = new ArrayList<>();
        for (Tile tile : tiles)
            this.tiles.add(tile);
    }

    public PImage getTileSprite() {

        return tileSprite;
    }

    public String getTileName() {

        return tileName;
    }

    public void add(Tile tile, boolean overrideMode) {

        if (overrideMode &&
            tiles.contains(tile) &&
            !tile.isHidden() &&
            this.get(tile).isHidden())

            this.get(tile).unhide();
        
        if (!tiles.contains(tile))
            tiles.add(tile);
    }

    public void addAll(TileList otherTiles, boolean overrideMode) {

        for (Tile tile : otherTiles.toArray())
            this.add(tile, overrideMode);
    }

    public void clear() {

        tiles.clear();
    }

    public void clearNonBorderTiles() {

        int n = 0;

        while (n < tiles.size()) {

            Tile tile = tiles.get(n);

            if (tile.getOrientation() == Direction.NONE)
                tiles.remove(tile);
            else
                n++;
        }
    }
    
    public void enableOverriding() {

        isOverriding = true;
    }

    public void disableOverriding() {

        isOverriding = false;
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

    public void hide() {

        for (Tile tile : tiles) {

            if (tile.getOrientation() == Direction.NONE)

                tile.hide();
        }
    }

    public boolean isEmpty() {

        return (tiles.isEmpty());
    }

    public boolean remove(Tile tile) {

        return tiles.remove(tile);
    }

    public Tile remove(int index) {

        return tiles.remove(index);
    }

    public int size() {

        return tiles.size();
    }

    public List<Tile> toArray() {

        return tiles;
    }

    public String toString() {

        return tiles.toString();
    }

    public boolean contains(Tile targetTile) {

        return tiles.contains(targetTile);
    }

    public void drawTiles(PApplet app) {

        for (Tile tile : tiles) {

            if (!tile.isHidden())
                tile.draw(app);
        }
    }

    public boolean isEnclosed(TileList fillTiles, TileList concreteTiles) {
        
        Tile headTile = new Tile(fillTiles.getTileSprite(), -1, -1);
        Tile tailTile = new Tile(fillTiles.getTileSprite(), -1, -1);

        if (!isEmpty() &&
            !areAllAdjacentTo(concreteTiles) &&
            !areAllAdjacentTo(fillTiles) &&
            headAndTailTilesAreAdjacentTo(concreteTiles, fillTiles)) {
            
            return true;
        }
        if (tiles.size() == 2) {
            headTile = tiles.get(0);
            tailTile = tiles.get(tiles.size() - 1);
        }
        if (tiles.size() == 2 &&
            isOverriding &&
            (headTile.isAdjacentTo(fillTiles, concreteTiles) &&
            tailTile.isAdjacentTo(fillTiles, concreteTiles))) {

            if ((headTile.getOrientation() == Direction.UP ||
                headTile.getOrientation() == Direction.DOWN) &&
                headTile.isVerticallyAdjacentTo(fillTiles)) {

                return true;
            }
            if ((headTile.getOrientation() == Direction.LEFT ||
                headTile.getOrientation() == Direction.RIGHT) &&
                headTile.isHorizontallyAdjacentTo(fillTiles)) {

                return true;
            }
        }
        return false;
    }

    public void fill(TileList fillTiles, TileList concreteTiles, List<Enemy> enemies) {

        Tile headTile = tiles.get(0);
        Tile tailTile = tiles.get(tiles.size() - 1);

        if (headTile.isParallelTo(tailTile))
            fillForParallelOrientiation(fillTiles, concreteTiles, headTile,
                tailTile, enemies);
        
        if (headTile.isOppositeTo(tailTile)) {
            fillForOppositeOrientiation(fillTiles, concreteTiles, headTile,
                tailTile, enemies);
        }
        if (headTile.isPerpendicularTo(tailTile)) {
            fillForPerpendicularOrientiation(fillTiles, concreteTiles, headTile,
                tailTile, enemies);
        }
        fillTiles.convertToFillTiles(this);
        fillTiles.fillUpHolesMadeBy(enemies);
        fillTiles.removeFloatingTiles();
        disableOverriding();
    }

    public void propagateRedPaths(PImage redPathSprite, int frameCount) {

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

    private boolean areAllAdjacentTo(TileList otherTiles) {

        for (Tile tile : tiles) {

            if (!tile.isAdjacentTo(otherTiles))
                return false;
        }
        return true;
    }

    private boolean headAndTailTilesAreAdjacentTo(TileList otherTiles,
        TileList fillTiles) {

        Tile headTile = tiles.get(0);
        Tile tailTile = tiles.get(tiles.size() - 1);

        boolean tailIsAdjacent = false;
        boolean headIsAdjacent = false;

        for (Tile tile : otherTiles.toArray()) {

            if (headTile.isAdjacentTo(tile)) {

                headIsAdjacent = true;
            }
        }
        for (Tile tile : fillTiles.toArray()) {

            if (headTile.isAdjacentTo(tile))
                
                headIsAdjacent = true;
        }
        for (Tile tile : otherTiles.toArray()) {

            if (tailTile.isAdjacentTo(tile))

                tailIsAdjacent = true;
        }
        for (Tile tile : fillTiles.toArray()) {

            if (tailTile.isAdjacentTo(tile) &&
                !headTile.equals(tailTile))
                
                tailIsAdjacent = true;
        }
        return (tailIsAdjacent && headIsAdjacent);
    }

    public void convertToFillTiles(TileList otherTiles) {

        for (Tile tile : otherTiles.toArray()) {

            Tile borderTile = new Tile(tileSprite, tile.getX(),
                tile.getY(), tileName);
            borderTile.setOrientation(tile.getOrientation());

            add(borderTile, false);
        }
        otherTiles.clear();
    }

    private void fillUpHolesMadeBy(List<Enemy> enemies) {

        for (Enemy enemy : enemies) {
        
            for (Tile tile : tiles) {

                if (tile.isSurroundedBy(this) &&
                    tile.isHidden() &&
                    enemy.getRemovedTiles().contains(tile))

                    tile.unhide();
            }
        }
    }

    private void removeFloatingTiles() {

        int i = 0;

        while (i < tiles.size()) {

            Tile tile = tiles.get(i);

            if (tile.isFloating(this))
                tiles.remove(tile);
            else
                i++;
        }
    }

    private void fillForParallelOrientiation(TileList fillTiles,
        TileList concreteTiles, Tile headTile, Tile tailTile, List<Enemy> enemies) {

        TileList defaultScan = new TileList(fillTiles.getTileSprite(),
            fillTiles.getTileName());
        TileList overrideScan = new TileList(fillTiles.getTileSprite(),
            fillTiles.getTileName());
        
        if (headTile.getOrientation() == Direction.UP ||
            headTile.getOrientation() == Direction.DOWN)

            scanForVerticalParallel(defaultScan, overrideScan, fillTiles,
                concreteTiles, headTile, tailTile);
        
        else

            scanForHorizontalParallel(defaultScan, overrideScan, fillTiles,
                concreteTiles, headTile, tailTile);
        
        for (Enemy enemy : enemies) {

            if (enemy.isInsideRegion(defaultScan)) {
                defaultScan.hide();
            }
            if (enemy.isInsideRegion(overrideScan)) {
                overrideScan.hide();
            }
        }
        fillTiles.addAll(defaultScan, isOverriding);
        fillTiles.addAll(overrideScan, isOverriding);
    }

    private void scanForVerticalParallel(TileList defaultScan, TileList overrideScan,
        TileList fillTiles, TileList concreteTiles, Tile headTile, Tile tailTile) {

        int[][] bounds;
        Direction direction;

        int xMean = Math.min(headTile.getX(), tailTile.getX()) +
                        (Math.abs(headTile.getX() - tailTile.getX()) / 2);
        int xMid = Info.WIDTH / 2;

        if (xMean < xMid) {

            direction = Direction.RIGHT;
            bounds = getBounds(fillTiles, concreteTiles, direction);

            if (!isOverriding)
                bounds[0][0] = tileSize;
        }
        else {

            direction = Direction.LEFT;
            bounds = getBounds(fillTiles, concreteTiles, direction);

            if (!isOverriding)
                bounds[0][1] = Info.WIDTH - 2*tileSize;
        }
        fillInDirection(defaultScan, fillTiles, bounds, direction);

        if (isOverriding) {

            bounds = getBounds(fillTiles, concreteTiles, direction.flip());
            fillInDirection(overrideScan, fillTiles, bounds, direction.flip());
        }
    }

    private void scanForHorizontalParallel(TileList defaultScan, TileList overrideScan,
        TileList fillTiles, TileList concreteTiles, Tile headTile, Tile tailTile) {

        int[][] bounds;
        Direction direction;

        int yMean = Math.min(headTile.getY(), tailTile.getY()) +
                        (Math.abs(headTile.getY() - tailTile.getY()) / 2);
        int yMid = Info.HEIGHT / 2;

        if (yMean < yMid) {

            direction = Direction.DOWN;
            bounds = getBounds(fillTiles, concreteTiles, direction);

            if (!isOverriding)
                bounds[1][0] = tileSize;
        }
        else {

            direction = Direction.UP;
            bounds = getBounds(fillTiles, concreteTiles, direction);

            if (!isOverriding)
                bounds[1][1] = Info.HEIGHT - 2*tileSize;
        }
        fillInDirection(defaultScan, fillTiles, bounds, direction);

        if (isOverriding) {

            bounds = getBounds(fillTiles, concreteTiles, direction.flip());
            fillInDirection(overrideScan, fillTiles, bounds, direction.flip());
        }
    }

    private void fillForOppositeOrientiation(TileList fillTiles,
        TileList concreteTiles, Tile headTile, Tile tailTile, List<Enemy> enemies) {

        TileList scan1 = new TileList(fillTiles.getTileSprite(),
            fillTiles.getTileName());
        TileList scan2 = new TileList(fillTiles.getTileSprite(),
            fillTiles.getTileName());
        TileList scan3 = new TileList(fillTiles.getTileSprite(),
            fillTiles.getTileName());

        int[][] bounds1 = getBounds(fillTiles, concreteTiles,
            headTile.getOrientation());
        int[][] bounds2 = getBounds(fillTiles, concreteTiles,
            headTile.getPerpendicularOrientation());
        int[][] bounds3 = getBounds(fillTiles, concreteTiles,
            tailTile.getPerpendicularOrientation());

        // System.out.printf("%s: [%d,%d],[%d,%d]%n", headTile.getOrientation(), bounds1[0][0], bounds1[0][1], bounds1[1][0], bounds1[1][1]);
        // System.out.printf("%s: [%d,%d],[%d,%d]%n", headTile.getPerpendicularOrientation(), bounds2[0][0], bounds2[0][1], bounds2[1][0], bounds2[1][1]);
        // System.out.printf("%s: [%d,%d],[%d,%d]%n", tailTile.getPerpendicularOrientation(), bounds3[0][0], bounds3[0][1], bounds3[1][0], bounds3[1][1]);

        fillInDirection(scan1, fillTiles, bounds1,
            headTile.getOrientation());
        fillInDirection(scan2, fillTiles, bounds2,
            headTile.getPerpendicularOrientation());
        fillInDirection(scan3, fillTiles, bounds3,
            tailTile.getPerpendicularOrientation());

        TileList defaultScan = new TileList();
        
        for (Tile tile : scan1.toArray()) {

            if (scan2.contains(tile) && scan3.contains(tile))
                defaultScan.add(tile, false);
        }
        for (Enemy enemy : enemies) {

            if (enemy.isInsideRegion(defaultScan)) {
                defaultScan.hide();
            }
        }
        fillTiles.addAll(defaultScan, isOverriding);
    }

    private void fillForPerpendicularOrientiation(TileList fillTiles,
        TileList concreteTiles, Tile headTile, Tile tailTile, List<Enemy> enemies) {

        TileList firstScan = new TileList(fillTiles.getTileSprite(),
            fillTiles.getTileName());
        TileList secondScan = new TileList(fillTiles.getTileSprite(),
            fillTiles.getTileName());

        int[][] firstBounds = getBounds(fillTiles, concreteTiles,
            headTile.getOrientation());
        int[][] secondBounds = getBounds(fillTiles, concreteTiles,
            tailTile.getOppositeOrientation());

        // System.out.printf("%s: [%d,%d],[%d,%d]%n", headTile.getOrientation(), firstBounds[0][0], firstBounds[0][1], firstBounds[1][0], firstBounds[1][1]);
        // System.out.printf("%s: [%d,%d],[%d,%d]%n", tailTile.getOppositeOrientation(), secondBounds[0][0], secondBounds[0][1], secondBounds[1][0], secondBounds[1][1]);

        fillInDirection(firstScan, fillTiles, firstBounds,
            headTile.getOrientation());
        fillInDirection(secondScan, fillTiles, secondBounds,
            tailTile.getOppositeOrientation());

        TileList scanTiles = new TileList();

        for (Tile tile : firstScan.toArray()) {

            if (secondScan.contains(tile))
                scanTiles.add(tile, false);
        }
        for (Enemy enemy : enemies) {

            if (enemy.isInsideRegion(scanTiles)) {
                scanTiles.hide();
            }
        }
        fillTiles.addAll(scanTiles, isOverriding);
    }

    private void fillInDirection(TileList scanTiles, TileList fillTiles,
        int[][] bounds, Direction direction) {

        for (int y = bounds[1][0]; y < (bounds[1][1] + tileSize); y += tileSize) {

            for (int x = bounds[0][0]; x < (bounds[0][1] + tileSize); x += tileSize) {

                Tile tile = new Tile(scanTiles.getTileSprite(), x, y, Info.GRASS);

                if (!this.contains(tile) &&
                    tile.isInsideRegion(this, fillTiles, direction))

                    scanTiles.add(tile, false);
            }
        }
    }

    public int[][] getBounds(TileList fillTiles,
        TileList concreteTiles, Direction direction) {
        
        int[][] bounds = getPathBounds();
        
        Tile start = tiles.get(0);
        Tile end = tiles.get(tiles.size() - 1);
        
        Tile currentTile = start;

        Map<Direction,Tile> boundTiles = new EnumMap<>(Direction.class);
        boundTiles.put(Direction.UP, currentTile);
        boundTiles.put(Direction.DOWN, currentTile);
        boundTiles.put(Direction.LEFT, currentTile);
        boundTiles.put(Direction.RIGHT, currentTile);

        Direction previousDirection = Direction.NONE;
        boolean collidedWithConcrete = false;
        boolean reachedLimit = false;

        while (true) {

            // System.out.println("Current tile: "+currentTile);
            // System.out.printf("[%d,%d],[%d,%d]%n",
            //     boundTiles.get(Direction.LEFT).getX(),
            //     boundTiles.get(Direction.RIGHT).getX(),
            //     boundTiles.get(Direction.UP).getY(),
            //     boundTiles.get(Direction.DOWN).getY());

            TileList adjacentTiles = getAdjacentTiles(currentTile, direction);

            if (reachedLimit && (collidedWithConcrete ||
                adjacentTiles.contains(end))) {
                
                // System.out.println("Reached the end");
                break;
            }
            currentTile =
                updateCurrentTile(currentTile, fillTiles, adjacentTiles,
                previousDirection);

            previousDirection =
                updatePreviousDirection(fillTiles, adjacentTiles,
                previousDirection);

            updateBounds(boundTiles, currentTile);
            
            collidedWithConcrete =
                updateCollisionWithConcrete(boundTiles, currentTile, concreteTiles,
                adjacentTiles);

            TileList updatedTiles =
                updateParameters(collidedWithConcrete, reachedLimit,
                new TileList(new Tile[] {start, end, currentTile}), adjacentTiles);

            boolean[] updatedBooleans =
                updateParameters(collidedWithConcrete, reachedLimit, end,
                adjacentTiles);

            start = updatedTiles.get(0);
            end = updatedTiles.get(1);
            currentTile = updatedTiles.get(2);

            reachedLimit = updatedBooleans[0];
            collidedWithConcrete = updatedBooleans[1];
        }
        int leftmost = boundTiles.get(Direction.LEFT).getX();
        int rightmost = boundTiles.get(Direction.RIGHT).getX();
        int upmost = boundTiles.get(Direction.UP).getY();
        int downmost = boundTiles.get(Direction.DOWN).getY();

        // System.out.printf("[%d,%d],[%d,%d]%n%n", leftmost, rightmost, upmost, downmost);

        if (leftmost < bounds[0][0])
            bounds[0][0] = leftmost;
        if (rightmost > bounds[0][1])
            bounds[0][1] = rightmost;
        if (upmost < bounds[1][0])
            bounds[1][0] = upmost;
        if (downmost > bounds[1][1])
            bounds[1][1] = downmost;

        return bounds;
    }

    private int[][] getPathBounds() {

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
        return new int[][] {{xUpper, xLower}, {yUpper, yLower}};
    }

    private TileList getAdjacentTiles(Tile currentTile, Direction direction) {

        Tile first = currentTile.getAdjacentTile(direction.flip());
        Tile second = currentTile.getAdjacentTile(first.getOppositeOrientation());
        Tile third = currentTile.getAdjacentTile(second.getPerpendicularOrientation());
        Tile fourth = currentTile.getAdjacentTile(first.getPerpendicularOrientation());

        return new TileList(new Tile[] {first, second, third, fourth});
    }

    private Tile updateCurrentTile(Tile currentTile, TileList fillTiles,
        TileList adjacentTiles, Direction previousDirection) {
        
        Tile first = adjacentTiles.get(0);
        Tile second = adjacentTiles.get(1);
        Tile third = adjacentTiles.get(2);
        Tile fourth = adjacentTiles.get(3);

        if (fillTiles.get(first) != null &&
            fillTiles.get(first).getOrientation() != Direction.NONE &&
            previousDirection != first.getOppositeOrientation()) {
            
            currentTile = fillTiles.get(first);
        }
        else if (fillTiles.get(second) != null &&
            fillTiles.get(second).getOrientation() != Direction.NONE &&
            previousDirection != second.getOppositeOrientation()) {
            
            currentTile = fillTiles.get(second);
        }
        else if (fillTiles.get(third) != null &&
            fillTiles.get(third).getOrientation() != Direction.NONE &&
            previousDirection != third.getOppositeOrientation()) {
            
            currentTile = fillTiles.get(third);
        }
        else if (fillTiles.get(fourth) != null &&
            fillTiles.get(fourth).getOrientation() != Direction.NONE &&
            previousDirection != fourth.getOppositeOrientation()) {
            
            currentTile = fillTiles.get(fourth);
        }
        return currentTile;
    }

    private Direction updatePreviousDirection(TileList fillTiles,
        TileList adjacentTiles, Direction previousDirection) {
        
        Tile first = adjacentTiles.get(0);
        Tile second = adjacentTiles.get(1);
        Tile third = adjacentTiles.get(2);
        Tile fourth = adjacentTiles.get(3);

        if (fillTiles.get(first) != null &&
            fillTiles.get(first).getOrientation() != Direction.NONE &&
            previousDirection != first.getOppositeOrientation()) {
            
            previousDirection = first.getOrientation();
        }
        else if (fillTiles.get(second) != null &&
            fillTiles.get(second).getOrientation() != Direction.NONE &&
            previousDirection != second.getOppositeOrientation()) {
            
            previousDirection = second.getOrientation();
        }
        else if (fillTiles.get(third) != null &&
            fillTiles.get(third).getOrientation() != Direction.NONE &&
            previousDirection != third.getOppositeOrientation()) {
            
            previousDirection = third.getOrientation();;
        }
        else if (fillTiles.get(fourth) != null &&
            fillTiles.get(fourth).getOrientation() != Direction.NONE &&
            previousDirection != fourth.getOppositeOrientation()) {
            
            previousDirection = fourth.getOrientation();
        }
        return previousDirection;
    }

    private void updateBounds(Map<Direction,Tile> boundTiles,
        Tile currentTile) {

        if (currentTile.getY() < boundTiles.get(Direction.UP).getY())
            boundTiles.replace(Direction.UP, currentTile);
        
        if (currentTile.getY() > boundTiles.get(Direction.DOWN).getY())
            boundTiles.replace(Direction.DOWN, currentTile);

        if (currentTile.getX() < boundTiles.get(Direction.LEFT).getX())
            boundTiles.replace(Direction.LEFT, currentTile);

        if (currentTile.getX() > boundTiles.get(Direction.RIGHT).getX())
            boundTiles.replace(Direction.RIGHT, currentTile);
    }

    private boolean updateCollisionWithConcrete(Map<Direction,Tile> boundTiles,
        Tile currentTile, TileList concreteTiles, TileList adjacentTiles) {
        
        Tile first = adjacentTiles.get(0);
        Tile second = adjacentTiles.get(1);
        Tile third = adjacentTiles.get(2);
        Tile fourth = adjacentTiles.get(3);

        boolean collidedWithConcrete = false;

        if (concreteTiles.contains(first)) {

            boundTiles.replace(first.getOrientation(), currentTile);
            collidedWithConcrete = true;
        }
        if (concreteTiles.contains(second)) {

            boundTiles.replace(second.getOrientation(), currentTile);
            collidedWithConcrete = true;
        }
        if (concreteTiles.contains(third)) {

            boundTiles.replace(third.getOrientation(), currentTile);
            collidedWithConcrete = true;
        }
        if (concreteTiles.contains(fourth)) {

            boundTiles.replace(fourth.getOrientation(), currentTile);
            collidedWithConcrete = true;
        }
        return collidedWithConcrete;
    }

    private TileList updateParameters(boolean collidedWithConcrete,
        boolean reachedLimit, TileList parameters, TileList adjacentTiles) {

        Tile start = parameters.get(0);
        Tile end = parameters.get(1);
        Tile currentTile = parameters.get(2);
        
        if (!reachedLimit && (collidedWithConcrete ||
            adjacentTiles.contains(end))) {

            start = tiles.get(tiles.size() - 1);
            end = tiles.get(0);
            currentTile = start;
        }
        return new TileList(new Tile[] {start, end, currentTile});
    }

    private boolean[] updateParameters(boolean collidedWithConcrete,
        boolean reachedLimit, Tile end, TileList adjacentTiles) {
        
        if (!reachedLimit && (collidedWithConcrete ||
            adjacentTiles.contains(end))) {

            reachedLimit = true;
            collidedWithConcrete = false;
        }
        return new boolean[] {reachedLimit, collidedWithConcrete};
    }

}