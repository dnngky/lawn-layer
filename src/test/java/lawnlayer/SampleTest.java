package lawnlayer;

import org.junit.jupiter.api.Test;

import lawnlayer.Info.Name;
import processing.core.PApplet;
import processing.core.PImage;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class SampleTest extends App {

    private PImage concreteSprite;
    private PImage greenPathSprite;
    private PImage wormSprite;
    private PImage beetleSprite;

    private PImage freezeSprite;
    private PImage boostSprite;
    private PImage invincibleSprite;
    private PImage shieldSprite;

    private PImage grassSprite;
    private PImage playerSprite;

    public void setup() {

        concreteSprite = loadImage(this.getClass().getResource("concrete_tile.png").getPath());
        greenPathSprite = loadImage(this.getClass().getResource("green_path.png").getPath());

        wormSprite = loadImage(this.getClass().getResource("worm.png").getPath());
        beetleSprite = loadImage(this.getClass().getResource("beetle.png").getPath());

        freezeSprite = loadImage(this.getClass().getResource("freeze.png").getPath());
        boostSprite = loadImage(this.getClass().getResource("boost.png").getPath());
        invincibleSprite = loadImage(this.getClass().getResource("invincible.png").getPath());
        shieldSprite = loadImage(this.getClass().getResource("shield.png").getPath());

        grassSprite = loadImage(this.getClass().getResource("grass_tile.png").getPath());
        playerSprite = loadImage(this.getClass().getResource("ball.png").getPath());
    }

    @Test
    public void testTile() {

        // Testing the basic attributes of a tile

        Tile tile = new Tile(concreteSprite, 640, 350, Name.CONCRETE);

        assertEquals(640, tile.getX());
        assertEquals(350, tile.getY());

        assertEquals(650, tile.getMidX());
        assertEquals(360, tile.getMidY());

        tile.setName(Name.GRASS);
        assertEquals(Name.GRASS, tile.getName());

        tile.setOrientation(Direction.UP);
        assertEquals(Direction.UP, tile.getOrientation());

        // Testing detection for adjacency
        
        Tile adjacentTile = tile.getAdjacentTile(Direction.DOWN);
        assertTrue(tile.isAdjacentTo(adjacentTile));

        TileList adjacentTiles = tile.getAdjacentTiles();

        for (Tile adjTile : adjacentTiles.toList())
            assertTrue(tile.isAdjacentTo(adjTile));

        // Testing toString()

        assertEquals("GRASS@[640,350]", tile.toString());

        // Testing equals()

        Tile equalTile = new Tile(concreteSprite, 640, 350, Name.CONCRETE);
        assertEquals(equalTile, tile);

        Tile nonEqualTile = new Tile(concreteSprite, 640, 360, Name.CONCRETE);
        assertNotEquals(nonEqualTile, tile);

        // Testing out-of-bound detection

        Tile outOfBoundsTile = new Tile(greenPathSprite, 2000, 2000, Name.PATH);
        assertTrue(outOfBoundsTile.isOutOfBounds());

        // Testing detection for floating tiles

        TileList floatingTiles =
            new TileList(new Tile[] {
                new Tile(grassSprite, 600, 350, Name.GRASS),
                new Tile(grassSprite, 680, 350, Name.GRASS),
                new Tile(grassSprite, 640, 310, Name.GRASS),
                new Tile(grassSprite, 640, 390, Name.GRASS)});

        assertTrue(tile.isFloatingAround(floatingTiles));
    }

    @Test
    public void testTileListBasic() {

        // Testing instantiation of a blank TileList

        TileList tileList = new TileList();

        assertEquals(Name.UNNAMED, tileList.getTileName());
        assertTrue(tileList.isEmpty());

        // Testing adding and removing, including detection for
        // duplicate Tiles

        Tile tile1 = new Tile(grassSprite, 10, 10, Name.DIRT);
        
        tileList.add(tile1);
        assertEquals(1, tileList.size());
        assertEquals(tile1, tileList.get(0));
        assertEquals(tile1, tileList.get(tile1));

        tileList.add(tile1);
        assertEquals(1, tileList.size());

        Tile tile2 = new Tile(grassSprite, 20, 20, Name.DIRT);

        tileList.add(tile2);
        tileList.remove(tile1);
        assertEquals(1, tileList.size());

        // Testing addAll() and removeAll()

        TileList tiles =
            new TileList(new Tile[] {
                new Tile(grassSprite, 10, 10, Name.GRASS),
                new Tile(grassSprite, 20, 20, Name.GRASS),
                new Tile(grassSprite, 30, 30, Name.GRASS),
                new Tile(grassSprite, 40, 40, Name.GRASS)});

        tileList.addAll(tiles);
        assertEquals(4, tileList.size());

        tileList.removeAll(tiles);
        assertEquals(0, tileList.size());

        tileList.addAll(tiles);
        tileList.clear();
        assertEquals(0, tileList.size());

        Tile tile3 = new Tile(grassSprite, 640, 350, Name.CONCRETE);
        Tile tile4 = new Tile(grassSprite, 620, 350, Name.CONCRETE);
        tileList.add(tile3);
        tileList.add(tile4);

        // Testing toString()

        assertEquals("[CONCRETE@[640,350], CONCRETE@[620,350]]",
            tileList.toString());

        // Testing floating tiles removal

        TileList floatingTiles =
            new TileList(new Tile[] {
                new Tile(grassSprite, 600, 350, Name.GRASS),
                new Tile(grassSprite, 680, 350, Name.GRASS),
                new Tile(grassSprite, 640, 310, Name.GRASS),
                new Tile(grassSprite, 640, 390, Name.GRASS)});

        tileList.removeFloatingTiles(floatingTiles);
        assertEquals(tile3, tileList.get(0));

        // Testing toList() conversion

        assertTrue(tileList.toList() instanceof List);

    }

    @Test
    public void testTileListFill() {

        TileList concreteTiles = new TileList();
        
        // Creating a mini 200 x 200 map bounded by concrete tiles

        for (int x = 0; x < 220; x += 20) {

            Tile newTile1 = new Tile(concreteSprite, x, 0, Name.CONCRETE);
            Tile newTile2 = new Tile(concreteSprite, x, 200, Name.CONCRETE);
            concreteTiles.add(newTile1);
            concreteTiles.add(newTile2);
        }
        for (int y = 0; y < 220; y += 20) {

            Tile newTile1 = new Tile(concreteSprite, 0, y, Name.CONCRETE);
            Tile newTile2 = new Tile(concreteSprite, 200, y, Name.CONCRETE);
            concreteTiles.add(newTile1);
            concreteTiles.add(newTile2);
        }

        // Creating unfilled TileList

        TileList unfilledTiles = new TileList();

        for (int x = 20; x < 200; x += 20) {

            for (int y = 20; y < 200; y += 20) {

                Tile newTile = new Tile(concreteSprite, x, y, Name.DIRT);
                unfilledTiles.add(newTile);
            }
        }

        // Simulating a simple path down the centre of the map

        TileList pathTiles = new TileList();

        for (int y = 20; y < 200; y += 20) {

            Tile newTile = new Tile(greenPathSprite, 100, y, Name.PATH);
            newTile.setOrientation(Direction.DOWN);
            pathTiles.add(newTile);
        }

        // Creating a TileList for filling tiles

        TileList fillTiles = new TileList();

        // Creating a TileList for border tiles

        TileList borderTiles = new TileList();

        // Creating some enemies on the right side of the path

        Enemy enemy1 = new Enemy(wormSprite, 150, 100, Name.WORM);
        Enemy enemy2 = new Enemy(beetleSprite, 160, 150, Name.BEETLE);
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(enemy1);
        enemies.add(enemy2);

        // Calculating the total number of fillable spaces and length of path

        int fillableSpaces = unfilledTiles.size();
        int lengthOfPath = pathTiles.size();

        // Calling the fill() method

        pathTiles.fill(borderTiles, unfilledTiles, fillTiles, concreteTiles,
            enemies, false);

        /*
        After filling, the fillTiles should be filled with the left region
        only (since there are enemies on the right), unfilled tiles should
        only contain the right region. All path tiles should be converted
        to border tiles.
        */

        // Filled and unfilled region should be equal
        assertEquals(unfilledTiles.size(), fillTiles.size());

        // Total number of tiles should add up to initial total
        // fillable spaces
        assertEquals(fillableSpaces,
            unfilledTiles.size() + fillTiles.size() + lengthOfPath);

        // Length of border tiles should be equal to initial length of path
        assertEquals(lengthOfPath, borderTiles.size());

        // Path tiles should now be empty
        assertTrue(pathTiles.isEmpty());

        // The starting tile in the filled region should be (80, 20), as that
        // is the tile space adjacent to the first path tile
        assertEquals(80, fillTiles.get(0).getX());
        assertEquals(20, fillTiles.get(0).getY());
    }

    @Test
    public void testEnemy() {

        Enemy enemy = new Enemy(beetleSprite, 120, 240, Name.BEETLE);
        enemy.setMovement(Movement.UPRIGHT);
        
        // Testing collision detection

        TileList collisionTiles =
            new TileList(new Tile[] {
                new Tile(grassSprite, 120, 220, Name.GRASS)});
        
        Tile overlapTile1 = new Tile(grassSprite, 120, 220, Name.GRASS);
        assertTrue(enemy.isOverlappingHorizontally(overlapTile1));
        
        enemy.checkForCollisionWith(collisionTiles, collisionTiles, false);
        assertEquals(overlapTile1, enemy.getCollidedTile());
        
        // Testing movement

        enemy.move();
        assertEquals(Movement.DOWNRIGHT, enemy.getMovement());
        assertEquals(121, enemy.getX());
        assertEquals(241, enemy.getY());

        // Testing collision after movement

        Tile overlapTile2 = new Tile(grassSprite, 120, 240, Name.CONCRETE);
        assertTrue(enemy.isOverlappingVertically(overlapTile2));

        Tile collisionTile = new Tile(grassSprite, 140, 240, Name.CONCRETE);
        collisionTiles.add(collisionTile);

        enemy.checkForCollisionWith(collisionTiles, collisionTiles, false);
        assertEquals(collisionTile, enemy.getCollidedTile());
    }

    @Test
    public void testPlayer() {

        Player.removePlayer();
        Player player = Player.createPlayer(playerSprite, 20, 120);
        assertThrows(IllegalStateException.class,
            () -> Player.createPlayer(playerSprite));

        // Testing overlapped tile detection

        Tile overlappedTile = null;
        player.updateStatus(overlappedTile);
        
        Tile path = new Tile(greenPathSprite, 20, 120, Name.PATH);
        assertEquals(path, player.createPath(greenPathSprite));

        // Testing movement on soil

        player.pressDown();
        player.move();
        player.move();
        assertEquals(121, player.getY());

        player.pressUp();
        player.move();
        player.move();
        assertEquals(123, player.getY());

        player.pressRight();
        assertEquals(20, player.getX());
        
        for (int i = 0; i < 18; i++)
            player.move();
        assertEquals(140, player.getY());
        assertEquals(21, player.getX());

        player.pressLeft();
        player.move();
        assertEquals(22, player.getX());
        for (int i = 0; i < 9; i++)
            player.move();

        // Testing movement on concrete

        Tile concrete1 = new Tile(concreteSprite, 40, 140, Name.CONCRETE);
        player.getOverlappedTileFrom(
            new TileList(new Tile[] {concrete1}),
            new TileList(),
            new TileList(),
            new TileList());
        player.updateStatus(concrete1);

        assertEquals(31, player.getX());
        for (int i = 0; i < 9; i++)
            player.move();
        
        assertEquals(40, player.getX());
        player.move();
        player.move();
        assertEquals(40, player.getX());

        player.pressUp();
        for (int i = 0; i < 11; i++)
            player.move();
        assertEquals(129, player.getY());
        assertEquals(40, player.getX());

        Tile concrete2 = new Tile(concreteSprite, 40, 120, Name.CONCRETE);
        player.getOverlappedTileFrom(
            new TileList(new Tile[] {concrete2}),
            new TileList(),
            new TileList(),
            new TileList());
        player.updateStatus(concrete2);

        assertEquals(concrete2, player.getOverlappedTile());

        for (int i = 0; i < 19; i++)
            player.move();

        assertEquals(120, player.getY());
    }

    @Test
    public void testEnums() {

        Direction direction = Direction.UP;
        assertEquals(Direction.DOWN, direction.flip());
        assertEquals(Direction.RIGHT, direction.normal());

        Movement movement = Movement.DOWNRIGHT;
        assertEquals(Movement.UPRIGHT, movement.flipVertically());
        assertEquals(Movement.DOWNLEFT, movement.flipHorizontally());
    }

    @Test
    public void testBoost() {

        Player.removePlayer();
        Player player = Player.createPlayer(playerSprite);

        // Testing multiple instantiation attempt

        Boost boost = Boost.createBoost(boostSprite, Name.BOOST);
        assertThrows(IllegalStateException.class,
            () -> Boost.createBoost(boostSprite, Name.BOOST));
        
        boost.setStartingFrameCount(0);
        assertFalse(boost.isTimeToSpawn(0));
        assertTrue(boost.isTimeToSpawn(20 * Info.FPS));

        TileList spawnTiles =
            new TileList(new Tile[] {
                new Tile(grassSprite, 200, 350, Name.GRASS),
                new Tile(grassSprite, 640, 330, Name.GRASS),
                new Tile(grassSprite, 610, 310, Name.GRASS),
                new Tile(grassSprite, 840, 500, Name.GRASS)});

        // Testing spawn and activation
        
        boost.spawn(spawnTiles, 10 * Info.FPS);
        boost.activateOn(player, new Tile(concreteSprite, 0, 0, Name.CONCRETE), frameCount);
        assertEquals(5, player.getSpeed());
            
        // Testing deactivation

        boost.despawn(500);
        boost.deactivateOn(player);
        assertEquals(2, player.getSpeed());
    }

    @Test
    public void testShield() {

        Player.removePlayer();
        Player player = Player.createPlayer(playerSprite);

        // Testing multiple instantiation attempt

        Shield shield = Shield.createShield(shieldSprite, Name.SHIELD);
        assertThrows(IllegalStateException.class,
            () -> Shield.createShield(shieldSprite, Name.SHIELD));
        
        shield.setStartingFrameCount(0);
        assertFalse(shield.isTimeToSpawn(0));
        assertTrue(shield.isTimeToSpawn(10 * Info.FPS));

        TileList spawnTiles =
            new TileList(new Tile[] {
                new Tile(grassSprite, 600, 350, Name.GRASS),
                new Tile(grassSprite, 680, 350, Name.GRASS),
                new Tile(grassSprite, 640, 310, Name.GRASS),
                new Tile(grassSprite, 640, 390, Name.GRASS)});

        // Testing spawn and activation

        shield.spawn(spawnTiles, 10 * Info.FPS);
        shield.activateOn(player, new Tile(concreteSprite, 0, 0, Name.CONCRETE), frameCount);
        assertTrue(player.isShielded());

        // Testing deactivation
        
        shield.despawn(500);
        shield.deactivateOn(player);
        assertFalse(shield.isInEffect());
        assertFalse(player.isShielded());
    }

    @Test
    public void testFreeze() {

        Enemy enemy = new Enemy(wormSprite, 20, 20, Name.WORM);

        // Testing multiple instantiation attempt

        Freeze freeze = Freeze.createFreeze(freezeSprite, Name.FREEZE);
        assertThrows(IllegalStateException.class,
            () -> Freeze.createFreeze(freezeSprite, Name.FREEZE));
        
        freeze.setStartingFrameCount(0);
        assertFalse(freeze.isTimeToSpawn(0));
        assertTrue(freeze.isTimeToSpawn(20 * Info.FPS));

        TileList spawnTiles =
            new TileList(new Tile[] {
                new Tile(grassSprite, 200, 350, Name.GRASS),
                new Tile(grassSprite, 640, 330, Name.GRASS),
                new Tile(grassSprite, 610, 310, Name.GRASS),
                new Tile(grassSprite, 840, 500, Name.GRASS)});

        // Testing spawn and activation

        freeze.spawn(spawnTiles, 10 * Info.FPS);
        freeze.activateOn(enemy, new Tile(concreteSprite, 0, 0, Name.CONCRETE), frameCount);
        assertEquals(Movement.STATIONARY, enemy.getMovement());

        // Testing deactivation
        
        freeze.despawn(500);
        freeze.deactivateOn(enemy);
        assertNotEquals(Movement.STATIONARY, enemy.getMovement());

        // Testing spawn on grass

        freeze.spawn(spawnTiles, 20 * Info.FPS);
        freeze.activateOn(enemy, new Tile(grassSprite, 0, 0, Name.GRASS), frameCount);
        assertNotEquals(Movement.STATIONARY, enemy.getMovement());
    }

    @Test
    public void testInvincible() {
        
        Player.removePlayer();
        Player player = Player.createPlayer(playerSprite);
        Enemy enemy = new Enemy(wormSprite, 20, 20, Name.WORM);

        // Testing multiple instantiation attempt

        Invincible invincible = Invincible.createInvincible(invincibleSprite, Name.SHIELD);
        assertThrows(IllegalStateException.class,
            () -> Invincible.createInvincible(invincibleSprite, Name.SHIELD));
        
        invincible.setStartingFrameCount(0);
        assertFalse(invincible.isTimeToSpawn(0));
        assertFalse(invincible.isTimeToSpawn(5 * Info.FPS));
        assertTrue(invincible.isTimeToSpawn(30 * Info.FPS));

        TileList spawnTiles =
            new TileList(new Tile[] {
                new Tile(grassSprite, 600, 350, Name.GRASS),
                new Tile(grassSprite, 680, 350, Name.GRASS),
                new Tile(grassSprite, 640, 310, Name.GRASS),
                new Tile(grassSprite, 640, 390, Name.GRASS)});

        // Testing spawn and activation

        invincible.spawn(spawnTiles, 10 * Info.FPS);
        invincible.activateOn(player, new Tile(concreteSprite, 0, 0, Name.CONCRETE), frameCount);
        invincible.activateOn(enemy, new Tile(concreteSprite, 0, 0, Name.CONCRETE), frameCount);
        assertTrue(player.isShielded());
        assertEquals(5, player.getSpeed());
        assertEquals(Movement.STATIONARY, enemy.getMovement());

        // Testing deactivation
        
        invincible.despawn(500);
        invincible.deactivateOn(player);
        invincible.deactivateOn(enemy);
        assertFalse(player.isShielded());
        assertEquals(2, player.getSpeed());
        assertNotEquals(Movement.STATIONARY, enemy.getMovement());
    }

    @Test
    public void testApp() {

        App app = new App();
        app.noLoop();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup();
        app.delay(1000);
    }

}