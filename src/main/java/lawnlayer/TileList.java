package lawnlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import processing.core.PApplet;
import processing.core.PImage;

public class TileList {

    private List<Tile> tiles;

    private PImage tileSprite;
    private static int tileSize = Info.SPRITESIZE;

    public TileList(PImage tileSprite) {

        tiles = new ArrayList<>();
        this.tileSprite = tileSprite;
    }

    public TileList(Tile[] tiles) {

        this.tiles = new ArrayList<>();
        for (Tile tile : tiles)
            this.tiles.add(tile);
    }

    public PImage getTileSprite() {

        return tileSprite;
    }

    public void add(Tile tile) {

        if (!tiles.contains(tile))
            tiles.add(tile);
    }

    public void add(int index, Tile tile) {

        tiles.add(index, tile);
    }

    public void addAll(Collection<? extends Tile> tileList) {

        tiles.addAll(tileList);
    }

    public Tile getTile(Tile positionTile) {

        for (Tile tile : tiles) {

            if (tile.getX() == positionTile.getX() &&
                    tile.getY() == positionTile.getY())
                return tile;
        }
        return null;
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

    public void checkMissingCorner(Player player) {

        Tile tile = tiles.get(tiles.size() - 1);
        Direction tileOrientation = tile.getOrientation();
        Direction playerDirection = player.getDirection();

        if (playerDirection == Direction.UP ||
            playerDirection == Direction.DOWN) {

            int xDeviation = Math.abs(player.getX() - tile.getX());

            if (tileOrientation == Direction.LEFT &&
                xDeviation >= tileSize / 2) {

                Tile cornerTile = new Tile(tileSprite, tile.getX() - tileSize, tile.getY(), Info.GREENPATH);
                tiles.add(cornerTile);
                cornerTile.setOrientation(tileOrientation);
            }
            if (tileOrientation == Direction.RIGHT &&
                xDeviation >= tileSize / 2) {

                Tile cornerTile = new Tile(tileSprite, tile.getX() + tileSize, tile.getY(), Info.GREENPATH);
                tiles.add(cornerTile);
                cornerTile.setOrientation(tileOrientation);
            }
        } else {

            int yDeviation = Math.abs(player.getY() - tile.getY());

            if (tileOrientation == Direction.UP &&
                yDeviation >= tileSize / 2) {

                Tile cornerTile = new Tile(tileSprite, tile.getX(), tile.getY() - tileSize, Info.GREENPATH);
                tiles.add(cornerTile);
                cornerTile.setOrientation(tileOrientation);
            }
            if (tileOrientation == Direction.DOWN &&
                yDeviation >= tileSize / 2) {

                Tile cornerTile = new Tile(tileSprite, tile.getX(), tile.getY() + tileSize, Info.GREENPATH);
                tiles.add(cornerTile);
                cornerTile.setOrientation(tileOrientation);
            }
        }
    }

    public boolean isEnclosedWith(TileList concreteTiles, TileList grassTiles) {

        Tile tailPath = tiles.get(0);
        Tile headPath = tiles.get(tiles.size() - 1);

        boolean tailIsAdjacent = false;
        boolean headIsAdjacent = false;

        for (Tile concrete : concreteTiles.toArray()) {

            if (tailPath.isAdjacentTo(concrete))
                tailIsAdjacent = true;
        }
        for (Tile concrete : concreteTiles.toArray()) {

            if (headPath.isAdjacentTo(concrete))
                headIsAdjacent = true;
        }
        for (Tile grass : grassTiles.toArray()) {

            if (tailPath.isAdjacentTo(grass))
                tailIsAdjacent = true;
        }
        for (Tile grass : grassTiles.toArray()) {

            if (headPath.isAdjacentTo(grass))
                headIsAdjacent = true;
        }
        return (tailIsAdjacent && headIsAdjacent);
    }

    public void drawTiles(PApplet app) {

        for (Tile tile : tiles)
            tile.draw(app);
    }

    public void fill(TileList fillTiles) {

        Tile firstTile = tiles.get(0);

        if (tiles.size() == 1 && movingAdjacentToConcrete(firstTile)) {

            fillTiles.add(new Tile(fillTiles.getTileSprite(), firstTile.getX(), firstTile.getY(), Info.GRASS));
            tiles.clear();
        }
        else if (tiles.size() == 1 && !movingAdjacentToConcrete(firstTile)) {
            // Empty
        }
        else {

            if (firstTile.getY() == tileSize)
                fillVertically(fillTiles, tileSize, Info.HEIGHT - tileSize);

            else if (firstTile.getX() == tileSize)
                fillHorizontally(fillTiles, tileSize, Info.WIDTH - tileSize);

            if (firstTile.getY() == Info.HEIGHT - 2 * tileSize)
                fillVertically(fillTiles, Info.HEIGHT - 2 * tileSize, 0);

            else if (firstTile.getX() == Info.WIDTH - 2 * tileSize)
                fillHorizontally(fillTiles, Info.WIDTH - 2 * tileSize, 0);

            for (Tile tile : tiles)
                fillTiles.add(new Tile(fillTiles.getTileSprite(), tile.getX(), tile.getY(), Info.GRASS));
            
            tiles.clear();
        }
    }

    private boolean movingAdjacentToConcrete(Tile firstTile) {

        return (((firstTile.getY() == tileSize ||
                    firstTile.getY() == Info.HEIGHT - 2*tileSize) &&
                (firstTile.getOrientation() == Direction.LEFT ||
                    firstTile.getOrientation() == Direction.RIGHT)) ||
                ((firstTile.getX() == tileSize ||
                    firstTile.getX() == Info.WIDTH - 2*tileSize) &&
                (firstTile.getOrientation() == Direction.UP ||
                    firstTile.getOrientation() == Direction.DOWN)));
    }

    public boolean contains(Tile targetTile) {

        for (Tile tile : tiles) {

            if (tile.getX() == targetTile.getX() &&
                    tile.getY() == targetTile.getY())
                return true;
        }
        return false;
    }

    private void fillHorizontally(TileList fillTiles, int start, int end) {

        boolean inEnclosedRegion = false;
        Tile firstTile = tiles.get(0);
        Tile lastTile = tiles.get(tiles.size() - 1);

        if (lastTile.getY() == tileSize)
            
            inEnclosedRegion = true;

        int yMean = Math.abs(firstTile.getY() - lastTile.getY()) / 2;
        int right = Info.WIDTH - 2 * tileSize;

        if ((firstTile.getY() == tileSize && lastTile.getY() == right ||
            firstTile.getY() == right && lastTile.getY() == tileSize) &&
            firstTile.getY() + yMean < Info.HEIGHT / 2)
            
            inEnclosedRegion = true;

        for (int y = tileSize; y < Info.HEIGHT - tileSize; y += tileSize) {

            Tile first = new Tile(fillTiles.getTileSprite(), start, y);
            boolean fill = inEnclosedRegion;

            if (this.contains(first) && !inEnclosedRegion) {
                
                inEnclosedRegion = !inEnclosedRegion;
                fill = inEnclosedRegion;
            }
            else if (this.contains(first) && inEnclosedRegion) {
                
                inEnclosedRegion = !inEnclosedRegion;
            }
            TileList strip = fillHorizontalStrip(fillTiles, y, start, end, fill);
            fillTiles.addAll(strip.toArray());
        }
    }

    private void fillVertically(TileList fillTiles, int start, int end) {

        boolean inEnclosedRegion = false;
        Tile firstTile = tiles.get(0);
        Tile lastTile = tiles.get(tiles.size() - 1);

        if (lastTile.getX() == tileSize)
            
            inEnclosedRegion = true;

        int xMean = Math.abs(firstTile.getX() - lastTile.getX()) / 2;
        int bottom = Info.HEIGHT - 2 * tileSize;

        if ((firstTile.getY() == tileSize && lastTile.getY() == bottom ||
            firstTile.getY() == bottom && lastTile.getY() == tileSize) &&
            firstTile.getX() + xMean < Info.WIDTH / 2)

            inEnclosedRegion = true;

        for (int x = tileSize; x < Info.WIDTH - tileSize; x += tileSize) {

            Tile first = new Tile(fillTiles.getTileSprite(), x, start);
            boolean fill = inEnclosedRegion;

            if (this.contains(first) && !inEnclosedRegion) {
                
                inEnclosedRegion = !inEnclosedRegion;
                fill = inEnclosedRegion;
            } 
            else if (this.contains(first) && inEnclosedRegion) {
                
                inEnclosedRegion = !inEnclosedRegion;
            }
            TileList strip = fillVerticalStrip(fillTiles, x, start, end, fill);
            fillTiles.addAll(strip.toArray());
        }
    }

    private TileList fillHorizontalStrip(TileList fillTiles, int y, int start, int end, boolean fill) {

        TileList strip = new TileList(fillTiles.getTileSprite());
        int x = start;

        boolean condition = x < end;
        int incr = tileSize;
        Direction d = Direction.RIGHT;

        if (start > end) {

            condition = x > end;
            incr = -tileSize;
            d = Direction.LEFT;
        }
        while (condition) {

            Tile current = new Tile(fillTiles.getTileSprite(), x, y);
            Tile next = new Tile(fillTiles.getTileSprite(), x + incr, y);
            Tile aboveNext = new Tile(fillTiles.getTileSprite(), x + incr, y - tileSize);
            Tile belowNext = new Tile(fillTiles.getTileSprite(), x + incr, y + tileSize);

            if (isInsideRegion(current, fill))

                strip.add(current);

            if (isPassingThickBorder(current, next, aboveNext, belowNext, d)) {

                int thickness = countHorizontalThickness(next, d);

                if (thickness % 2 != 0)
                    fill = !fill;
                
                x += thickness * incr;
            }
            else if (isPassingThinBorder(current, next)) {

                fill = updateFill(fillTiles, next, aboveNext, d);
            }
            x += incr;
            
            if (start < end)
                condition = x < end;
            else
                condition = x > end;
        }
        return strip;
    }

    private TileList fillVerticalStrip(TileList fillTiles, int x, int start, int end, boolean fill) {

        TileList strip = new TileList(fillTiles.getTileSprite());
        int y = start;

        boolean condition = y < end;
        int incr = tileSize;
        Direction d = Direction.DOWN;
        
        if (start > end) {
            
            condition = y > end;
            incr = -tileSize;
            d = Direction.UP;
        }
        while (condition) {

            Tile current = new Tile(fillTiles.getTileSprite(), x, y);
            Tile next = new Tile(fillTiles.getTileSprite(), x, y + incr);
            Tile aboveNext = new Tile(fillTiles.getTileSprite(), x - tileSize, y + incr);
            Tile belowNext = new Tile(fillTiles.getTileSprite(), x + tileSize, y + incr);

            if (isInsideRegion(current, fill))

                strip.add(current);

            if (isPassingThickBorder(current, next, aboveNext, belowNext, d)) {
                
                int thickness = countVerticalThickness(next, d);

                if (thickness % 2 != 0)
                    fill = !fill;
                
                y += thickness * incr;
            }
            else if (isPassingThinBorder(current, next)) {

                fill = updateFill(fillTiles, next, aboveNext, d);
            }
            y += incr;

            if (start < end)
                condition = y < end;
            else
                condition = y > end;
        }
        return strip;
    }

    private int countHorizontalThickness(Tile tile, Direction d) {

        int thickness = 0;
        int x = tile.getX();
        int y = tile.getY();

        while (true) {

            Tile below = new Tile(getTileSprite(), x, y + tileSize);
            Tile current = new Tile(getTileSprite(), x, y);
            Tile above = new Tile(getTileSprite(), x, y - tileSize);            

            if (this.contains(below) &&
                this.contains(current) &&
                this.contains(above))

                thickness += 1;
            
            else break;

            if (d == Direction.RIGHT)
                x += tileSize;
            else
                x -= tileSize;
        }
        return thickness;
    }

    private int countVerticalThickness(Tile tile, Direction d) {

        int thickness = 0;
        int x = tile.getX();
        int y = tile.getY();

        while (true) {

            Tile below = new Tile(getTileSprite(), x + tileSize, y);
            Tile current = new Tile(getTileSprite(), x, y);
            Tile above = new Tile(getTileSprite(), x - tileSize, y);

            if (this.contains(below) &&
                this.contains(current) &&
                this.contains(above))
                
                thickness += 1;

            else break;

            if (d == Direction.DOWN)
                y += tileSize;
            else
                y -= tileSize;
        }
        return thickness;
    }

    private boolean isInsideRegion(Tile current, boolean fill) {

        return (!this.contains(current) && fill);
    }

    private boolean isPassingThickBorder(Tile current, Tile next,
            Tile aboveNext, Tile belowNext, Direction d) {

        Tile nextTile = getTile(next);

        if (d == Direction.LEFT || d == Direction.RIGHT)

            return (!this.contains(current) &&
                    this.contains(next) &&
                    this.contains(aboveNext) &&
                    this.contains(belowNext) &&
                    (nextTile.getOrientation() == Direction.UP ||
                    nextTile.getOrientation() == Direction.DOWN));

        if (d == Direction.UP || d == Direction.DOWN)

            return (!this.contains(current) &&
                    this.contains(next) &&
                    this.contains(aboveNext) &&
                    this.contains(belowNext) &&
                    (nextTile.getOrientation() == Direction.LEFT ||
                    nextTile.getOrientation() == Direction.RIGHT));

        return false;
    }

    private boolean isPassingThinBorder(Tile current, Tile next) {
        
        return (this.contains(current) && !this.contains(next));
    }

    private boolean updateFill(TileList fillTiles, Tile next, Tile aboveNext, Direction d) {

        if (this.contains(aboveNext)) {

            int x = next.getX();
            int y = next.getY();

            boolean condition = y < Info.HEIGHT - tileSize;;

            if (d == Direction.UP || d == Direction.DOWN)
                condition = x < Info.WIDTH - tileSize;

            while (condition) {

                Tile positionTile = new Tile(fillTiles.getTileSprite(), x, y);

                if (this.contains(positionTile))
                    return true;

                if (d == Direction.UP || d == Direction.DOWN) {
                    x += tileSize;
                    condition = x < Info.WIDTH - tileSize;
                }
                else {
                    y += tileSize;
                    condition = y < Info.HEIGHT - tileSize;
                }
            }
            return false;
        }
        else {

            return (fillTiles.contains(aboveNext));
        }
    }

}