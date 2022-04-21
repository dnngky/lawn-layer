package lawnlayer;

import processing.core.PImage;

public class Tile extends GameObject {

    Direction orientation;

    protected Tile(PImage sprite) {

        super(sprite);
        orientation = Direction.NONE;
    }

    protected Tile(PImage sprite, int x, int y) {

        super(sprite, x, y);
        orientation = Direction.NONE;
    }

    protected Tile(PImage sprite, int x, int y, String name) {

        super(sprite, x, y);
        this.name = name;
        orientation = Direction.NONE;
    }

    public void setOrientation(Direction orientation) {

        this.orientation = orientation;
    }

    public Direction getOrientation() {

        return orientation;
    }

    public boolean isAdjacentTo(Tile other) {

        return ((Math.abs(x - other.getX()) == size && y == other.getY()) ||
                (Math.abs(y - other.getY()) == size && x == other.getX()));
    }

}

// public static void checkMissingCorner(List<Path> path, Player player) {

// Path currentBlock = path.get(path.size() - 1);
// PImage sprite = currentBlock.getSprite();
// int x = currentBlock.getX();
// int y = currentBlock.getY();
// String name = "greenPath";

// int deviationX = Math.abs(player.getX() - x);
// int deviationY = Math.abs(player.getY() - y);

// if ((currentBlock.getOrientation() == Direction.UP) &&
// (player.getDirection() == Direction.LEFT ||
// player.getDirection() == Direction.RIGHT) &&
// deviationY < size) {

// Path cornerBlock = new Path(sprite, x, y - size, name);
// if (!Path.contains(path, cornerBlock))
// path.add(cornerBlock);
// }
// if ((currentBlock.getOrientation() == Direction.DOWN) &&
// (player.getDirection() == Direction.LEFT ||
// player.getDirection() == Direction.RIGHT) &&
// deviationY < size) {

// Path cornerBlock = new Path(sprite, x, y + size, name);
// if (!Path.contains(path, cornerBlock))
// path.add(cornerBlock);
// }
// if ((currentBlock.getOrientation() == Direction.LEFT) &&
// (player.getDirection() == Direction.UP ||
// player.getDirection() == Direction.DOWN) &&
// deviationX < size) {

// Path cornerBlock = new Path(sprite, x - size, y, name);
// if (!(Path.contains(path, cornerBlock)))
// path.add(cornerBlock);
// }
// if ((currentBlock.getOrientation() == Direction.RIGHT) &&
// (player.getDirection() == Direction.UP ||
// player.getDirection() == Direction.DOWN) &&
// deviationX < size) {

// Path cornerBlock = new Path(sprite, x + size, y - size, name);
// if (!(Path.contains(path, cornerBlock)))
// path.add(cornerBlock);
// }
// }