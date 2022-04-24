package lawnlayer;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PImage;
//import processing.data.JSONObject;
//import processing.data.JSONArray;
//import processing.core.PFont;

public class App extends PApplet {

    public final String configPath;

    private PImage soil;
    private PImage greenPathSprite;
    private PImage redPathSprite;

    private TileList concreteTiles;
    private TileList pathTiles;
    private TileList grassTiles;

    private Player player;
    private List<Enemy> enemies;

    public App() {
        this.configPath = "config.json";
    }

    /*
     * Initialises the setting of the window size.
     */
    @Override
    public void settings() {
        size(Info.WIDTH, Info.HEIGHT);
    }

    /*
     * Loads all resources such as images. Initialise the elements such as the
     * player, enemies and map elements.
     */
    @Override
    public void setup() {

        frameRate(Info.FPS);

        // Load images during setup

        soil = loadImage(this.getClass().getResource("background4.png").getPath());
        greenPathSprite = loadImage(this.getClass().getResource("green_path.png").getPath());
        redPathSprite = loadImage(this.getClass().getResource("red_path.png").getPath());

        PImage grassSprite = loadImage(this.getClass().getResource("grass_tile1.png").getPath());
        PImage concreteSprite = loadImage(this.getClass().getResource("concrete_tile.png").getPath());
        PImage ballSprite = loadImage(this.getClass().getResource("ball.png").getPath());
        PImage wormSprite = loadImage(this.getClass().getResource("worm.png").getPath());
        PImage beetleSprite = loadImage(this.getClass().getResource("beetle.png").getPath());

        concreteTiles = new TileList(concreteSprite, Info.CONCRETE);
        pathTiles = new TileList(greenPathSprite, Info.PATH);
        grassTiles = new TileList(grassSprite, Info.GRASS);

        for (int i = 0; i < (Info.WIDTH / Info.SPRITESIZE); i++) {
            concreteTiles.add(new Tile(concreteSprite, i * Info.SPRITESIZE, 0, Info.CONCRETE), false);
            concreteTiles.add(new Tile(concreteSprite, i * Info.SPRITESIZE, Info.HEIGHT - Info.SPRITESIZE, Info.CONCRETE), false);
        }
        for (int i = 0; i < (Info.HEIGHT / Info.SPRITESIZE); i++) {
            concreteTiles.add(new Tile(concreteSprite, 0, i * Info.SPRITESIZE, Info.CONCRETE), false);
            concreteTiles.add(new Tile(concreteSprite, Info.WIDTH - Info.SPRITESIZE, i * Info.SPRITESIZE, Info.CONCRETE), false);
        }
        player = new Player(ballSprite);

        enemies = new ArrayList<>();
        enemies.add(new Enemy(wormSprite, Info.WORM));
        enemies.add(new Enemy(beetleSprite, Info.BEETLE));
    }

    /*
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {

        background(soil);

        Tile overlappedTile = player.getOverlappedTile(concreteTiles,
            grassTiles, pathTiles);
        
        Tile newPath = player.createPath(greenPathSprite);

        updatePlayer(enemies, pathTiles, overlappedTile);
        updateEnemies(concreteTiles, grassTiles, pathTiles);
        updatePathTiles(overlappedTile, newPath);
        
        concreteTiles.drawTiles(this);
        grassTiles.drawTiles(this);
        pathTiles.drawTiles(this);

        player.draw(this);

        for (Enemy enemy : enemies)
            enemy.draw(this);
    }

    private void updatePlayer(List<Enemy> enemies, TileList pathTiles,
        Tile overlappedTile) {
        
        player.update(overlappedTile);

        if (player.isOverlapping(pathTiles) &&
            (overlappedTile != pathTiles.get(pathTiles.size() - 1) ||
            overlappedTile.isCollided())) {

            player.respawn();
            pathTiles.clear();
        }
        for (Enemy enemy : enemies) {

            if (player.isOverlapping(enemy)) {

                player.respawn();
                pathTiles.clear();
                break;
            }
        }
        player.move();
    }

    private void updateEnemies(TileList concreteTiles, TileList grassTiles,
        TileList pathTiles) {
        
        for (int i = 0; i < Info.SPEED; i++) {

            for (Enemy enemy : enemies) {

                enemy.checkForCollisionWith(concreteTiles, true);
                enemy.checkForCollisionWith(pathTiles, false);
                enemy.checkForCollisionWith(grassTiles, false);

                if (enemy.hasCollidedWith(pathTiles)) {

                    Tile collidedTile = enemy.getCollidedTile();
                    collidedTile.turnRed(redPathSprite, frameCount);
                }
                enemy.move();
            }
        }
    }

    private void updatePathTiles(Tile overlappedTile, Tile newPath) {
        
        if (newPath != null && !player.isOverlapping(grassTiles))
            pathTiles.add(newPath, false);
        
        if (newPath != null && overlappedTile != null &&
            overlappedTile.isHidden()) {
            
            grassTiles.remove(overlappedTile);
            pathTiles.add(newPath, false);
            pathTiles.enableOverriding();
        }
        if (pathTiles.isEnclosed(grassTiles, concreteTiles)) {
            
            System.out.println("Enclosed");
            player.stop();
            pathTiles.fill(grassTiles, concreteTiles, enemies);
        }
        if (player.isOverlapping(concreteTiles) ||
            (player.isOverlapping(grassTiles) &&
            overlappedTile != null &&
            !overlappedTile.isHidden()))
            
            grassTiles.convertToFillTiles(pathTiles);

        pathTiles.propagateRedPaths(redPathSprite, frameCount);
    }

    /*
     * Runs when player presses a keyboard key.
     */
    @Override
    public void keyPressed() {
        switch (keyCode) {
            case 37:
                player.pressLeft();
                break;
            case 38:
                player.pressUp();
                break;
            case 39:
                player.pressRight();
                break;
            case 40:
                player.pressDown();
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        PApplet.main("lawnlayer.App");
    }

}