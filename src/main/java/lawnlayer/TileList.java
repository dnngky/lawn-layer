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

        if (!tiles.contains(tile))
            tiles.add(index, tile);
    }

    public void addAll(Collection<? extends Tile> tileList) {

        tiles.addAll(tileList);
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

    public boolean isEmpty() {

        return (tiles.size() == 0);
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

        for (Tile tile : tiles) {

            if (tile.getX() == targetTile.getX() &&
                tile.getY() == targetTile.getY())
                
                return true;
        }
        return false;
    }

    public void drawTiles(PApplet app) {

        for (Tile tile : tiles)
            tile.draw(app);
    }

    public void addCornerTile(Player player) {

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
        }
        else {

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

    public boolean isEnclosedWith(TileList otherTiles, TileList fillTiles) {

        Tile headTile = tiles.get(0);
        Tile tailTile = tiles.get(tiles.size() - 1);

        boolean tailIsAdjacent = false;
        boolean headIsAdjacent = false;

        for (Tile tile : otherTiles.toArray()) {

            if (headTile.isAdjacentTo(tile))

                headIsAdjacent = true;
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

    public boolean areAllAdjacentTo(TileList otherTiles) {

        for (Tile tile : tiles) {

            if (!tile.isAdjacentTo(otherTiles))
                return false;
        }
        return true;
    }

    public void fill(TileList fillTiles) {

        Tile headTile = tiles.get(0);
        Tile tailTile = tiles.get(tiles.size() - 1);

        if (headTile.isParallelTo(tailTile))
            fillForParallelOrientiation(fillTiles, headTile, tailTile);
        
        if (headTile.isOppositeTo(tailTile))
            fillForOppositeOrientiation(fillTiles, headTile, tailTile);
        
        if (headTile.isPerpendicularTo(tailTile))
            fillForPerpendicularOrientiation(fillTiles, headTile, tailTile);

        convertToFillTiles(fillTiles);
    }

    public void convertToFillTiles(TileList fillTiles) {

        for (Tile tile : tiles) {

            Tile borderTile = new Tile(fillTiles.getTileSprite(), tile.getX(),
                tile.getY(), Info.GRASS);
            fillTiles.add(borderTile);
        }
        tiles.clear();
    }

    private void fillForParallelOrientiation(TileList fillTiles,
        Tile headTile, Tile tailTile) {

        TileList firstScan = new TileList(fillTiles.getTileSprite());
        TileList secondScan = new TileList(fillTiles.getTileSprite());
        
        parallelFill(fillTiles, firstScan, headTile, tailTile);

        if (headTile.isAdjacentTo(fillTiles) ||
            tailTile.isAdjacentTo(fillTiles))
            
            parallelFillForAdjacentToFillTiles(fillTiles, secondScan,
                headTile, tailTile);
        
        if (secondScan.size() == 0)
            parallelFill(fillTiles, secondScan, headTile, tailTile);
        
        for (Tile tile : firstScan.toArray()) {

            if (secondScan.contains(tile))
                fillTiles.add(tile);
        }
    }

    private void parallelFill(TileList fillTiles,
        TileList firstScan, Tile headTile, Tile tailTile) {
        
        if (headTile.getOrientation() == Direction.UP ||
            headTile.getOrientation() == Direction.DOWN) {

            int xMean = Math.min(headTile.getX(), tailTile.getX()) +
                        (Math.abs(headTile.getX() - tailTile.getX()) / 2);
            int xMid = Info.WIDTH / 2;

            if (xMean < xMid)
                fillInDirection(firstScan, fillTiles, Direction.RIGHT);
            else
                fillInDirection(firstScan, fillTiles, Direction.LEFT);

        }
        if (headTile.getOrientation() == Direction.LEFT ||
            headTile.getOrientation() == Direction.RIGHT) {

            int yMean = Math.min(headTile.getY(), tailTile.getY()) +
                        (Math.abs(headTile.getY() - tailTile.getY()) / 2);
            int yMid = Info.HEIGHT / 2;

            if (yMean < yMid)
                fillInDirection(firstScan, fillTiles, Direction.DOWN);
            else
                fillInDirection(firstScan, fillTiles, Direction.UP);
        }
    }

    private void parallelFillForAdjacentToFillTiles(TileList fillTiles,
        TileList secondScan, Tile headTile, Tile tailTile) {

        if (headTile.getY() == tileSize ||
            tailTile.getY() == tileSize)

            fillInDirection(secondScan, fillTiles, Direction.DOWN);
        
        if (headTile.getY() == Info.HEIGHT - 2*tileSize ||
            tailTile.getY() == Info.HEIGHT - 2*tileSize)

            fillInDirection(secondScan, fillTiles, Direction.UP);

        if (headTile.getX() == tileSize ||
            tailTile.getX() == tileSize)

            fillInDirection(secondScan, fillTiles, Direction.RIGHT);
        
        if (headTile.getX() == Info.WIDTH - 2*tileSize ||
            tailTile.getX() == Info.WIDTH - 2*tileSize)

            fillInDirection(secondScan, fillTiles, Direction.LEFT);
    }

    private void fillForOppositeOrientiation(TileList fillTiles,
        Tile headTile, Tile tailTile) {

        TileList firstScan = new TileList(fillTiles.getTileSprite());
        TileList secondScan = new TileList(fillTiles.getTileSprite());
        TileList thirdScan = new TileList(fillTiles.getTileSprite());

        this.fillInDirection(firstScan, fillTiles, headTile.getOrientation());
        this.fillInDirection(secondScan, fillTiles, headTile.getPerpendicularOrientation());
        this.fillInDirection(thirdScan, fillTiles, tailTile.getPerpendicularOrientation());
        
        for (Tile tile : firstScan.toArray()) {

            if (secondScan.contains(tile) && thirdScan.contains(tile))
                fillTiles.add(tile);
        }
    }

    private void fillForPerpendicularOrientiation(TileList fillTiles,
        Tile headTile, Tile tailTile) {

        TileList firstScan = new TileList(fillTiles.getTileSprite());
        TileList secondScan = new TileList(fillTiles.getTileSprite());

        System.out.println(headTile.getOrientation());
        System.out.println(tailTile.getOppositeOrientation());

        this.fillInDirection(firstScan, fillTiles, headTile.getOrientation());
        this.fillInDirection(secondScan, fillTiles, tailTile.getOppositeOrientation());

        for (Tile tile : firstScan.toArray()) {

            if (secondScan.contains(tile))
                fillTiles.add(tile);
        }
    }

    private void fillInDirection(TileList scanTiles, TileList fillTiles,
        Direction direction) {

        for (int y = tileSize; y < (Info.HEIGHT - tileSize); y += tileSize) {

            for (int x = tileSize; x < (Info.WIDTH - tileSize); x += tileSize) {

                Tile tile = new Tile(fillTiles.getTileSprite(), x, y, Info.GRASS);

                if (this.contains(tile) || fillTiles.contains(tile))
                    continue;

                if (tile.isInsideRegion(this, fillTiles, direction))
                    scanTiles.add(tile);
            }
        }
    }

}