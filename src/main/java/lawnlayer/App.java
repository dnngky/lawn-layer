package lawnlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.data.JSONArray;
import processing.core.PFont;

public class App extends PApplet {

    public final String configPath;
    private PFont font;

    private PImage soil;
    private PImage concreteSprite;
    private PImage greenPathSprite;
    private PImage redPathSprite;
    private PImage wormSprite;
    private PImage beetleSprite;
    private PImage heart;

    private Map<Integer, String> outlays;
    private Map<Integer, TileList> concretes;
    private Map<Integer, List<Enemy>> enemies;
    private Map<Integer, Integer> fillables;
    private Map<Integer, Float> goals;
    private int lives;

    private TileList pathTiles;
    private TileList grassTiles;
    private TileList borderTiles;

    private Player player;
    private int currentLevel;
    private int gameOverTime;

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

        // Load images and fonts during setup

        soil = loadImage(this.getClass().getResource("background4.png").getPath());
        concreteSprite = loadImage(this.getClass().getResource("concrete_tile.png").getPath());
        greenPathSprite = loadImage(this.getClass().getResource("green_path.png").getPath());
        redPathSprite = loadImage(this.getClass().getResource("red_path.png").getPath());
        wormSprite = loadImage(this.getClass().getResource("worm.png").getPath());
        beetleSprite = loadImage(this.getClass().getResource("beetle.png").getPath());
        heart = loadImage(this.getClass().getResource("heart_smaller.png").getPath());

        PImage grassSprite = loadImage(this.getClass().getResource("grass_tile1.png").getPath());
        PImage ballSprite = loadImage(this.getClass().getResource("ball.png").getPath());

        font = createFont(this.getClass().getResource("upheavtt.ttf").getPath(), 50, false);

        // Initialise config information containers

        outlays = new HashMap<>();
        concretes = new HashMap<>();
        fillables = new HashMap<>();
        enemies = new HashMap<>();
        goals = new HashMap<>();

        // Initialise tile containers

        pathTiles = new TileList(greenPathSprite, Info.PATH);
        grassTiles = new TileList(grassSprite, Info.GRASS);
        borderTiles = new TileList(grassSprite, Info.GRASS);

        // Load JSON file

        loadConfigFile();

        // Draw concrete tiles and calculate fillable tiles for each level

        outlays.forEach((level, filename) ->
            concretes.put(level, drawConcretes(filename)));
        outlays.forEach((level, filename) ->
            fillables.put(level, calculateFillableTiles(filename)));

        // Initialise player and level

        player = new Player(ballSprite);
        currentLevel = 1;
    }

    /*
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {

        background(soil);
        textFont(font);

        int fillableTiles;
        TileList concreteTiles;
        List<Enemy> enemiesOnThisLevel;

        double percentageFilled;
        double fillGoal;

        try {

            fillableTiles = fillables.get(currentLevel);
            concreteTiles = concretes.get(currentLevel);
            enemiesOnThisLevel = enemies.get(currentLevel);

            percentageFilled = getFilledPercentage(borderTiles, grassTiles,
                fillableTiles);
            fillGoal = goals.get(currentLevel);
        } catch (NullPointerException e) {

            concreteTiles = new TileList();
            enemiesOnThisLevel = new ArrayList<>();

            percentageFilled = -1;
            fillGoal = -1;
        }
        if (gameOverTime == 0) {

            fill(0);
            textAlign(RIGHT, CENTER);
            text((int) (percentageFilled * 100) + "% | " +
                    (int) (fillGoal * 100) + "%", 1250, 35);

            textAlign(CENTER, CENTER);
            text("Level " + currentLevel, 640, 35);

            int x = 30;
            for (int n = 0; n < lives; n++) {
                image(heart, x, 25);
                x += 50;
            }
            Tile overlappedTile = player.getOverlappedTile(borderTiles,
                    concreteTiles, grassTiles, pathTiles);

            Tile newPath = player.createPath(greenPathSprite);

            updatePlayer(enemiesOnThisLevel, pathTiles, overlappedTile);
            updateEnemies(enemiesOnThisLevel, concreteTiles, grassTiles,
                    pathTiles);
            updatePathTiles(enemiesOnThisLevel, concreteTiles, overlappedTile,
                    newPath);

            concreteTiles.drawTiles(this);
            borderTiles.drawTiles(this);
            grassTiles.drawTiles(this);
            pathTiles.drawTiles(this);

            player.draw(this);

            for (Enemy enemy : enemiesOnThisLevel)
                enemy.draw(this);
        }
        if (percentageFilled >= fillGoal) {

            borderTiles.clear();
            grassTiles.clear();
            pathTiles.clear();
            player.respawn();

            currentLevel++;
        }
        if (lives == 0) {

            concreteTiles.clear();
            borderTiles.clear();
            grassTiles.clear();
            pathTiles.clear();

            background(0);
            fill(255);
            textAlign(CENTER, CENTER);
            text("You lose D:", 640, 360);

            if (gameOverTime == 0)
                gameOverTime = frameCount;
        }
        if (currentLevel > goals.size()) {

            concreteTiles.clear();
            borderTiles.clear();
            grassTiles.clear();
            pathTiles.clear();

            background(107, 142, 35);
            fill(255);
            textAlign(CENTER, CENTER);
            text("You win :D", 640, 360);

            if (gameOverTime == 0)
                gameOverTime = frameCount;
        }
        if (gameOverTime > 0 &&
                frameCount - gameOverTime == Info.FPS * Info.ENDGAMESCREENDELAY)
            exit();
    }

    /**
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

    private void loadConfigFile() {

        JSONObject values = loadJSONObject(configPath);
        JSONArray levels = values.getJSONArray("levels");

        for (int i = 0; i < levels.size(); i++) {

            JSONObject level = levels.getJSONObject(i);

            outlays.put(i + 1, level.getString("outlay"));

            JSONArray enemyArray = level.getJSONArray("enemies");
            List<Enemy> enemyList = new ArrayList<>();

            for (int j = 0; j < enemyArray.size(); j++) {

                JSONObject enemy = enemyArray.getJSONObject(j);

                PImage sprite;
                String type;

                if (enemy.getInt("type") == 0) {
                    sprite = wormSprite;
                    type = Info.WORM;
                } else {
                    sprite = beetleSprite;
                    type = Info.BEETLE;
                }
                String spawn = enemy.getString("spawn");

                if (spawn.equals("random"))
                    enemyList.add(new Enemy(sprite, type));
                else
                    enemyList.add(new Enemy(sprite, spawn, type));
            }
            enemies.put(i + 1, enemyList);

            goals.put(i + 1, level.getFloat("goal"));
        }
        lives = values.getInt("lives");
    }

    private TileList drawConcretes(String filename) {

        File outlayFile = new File(filename);
        TileList concreteTiles = new TileList();

        try {
            Scanner scan = new Scanner(outlayFile);
            int y = 80;

            while (scan.hasNextLine()) {

                String row = scan.nextLine();

                for (int j = 0; j < row.length(); j++) {

                    char c = row.charAt(j);
                    int x = j * Info.SPRITESIZE;

                    if (c == 'X') {
                        Tile concreteTile = new Tile(concreteSprite, x, y, Info.CONCRETE);
                        concreteTiles.add(concreteTile);
                    }
                }
                y += Info.SPRITESIZE;
            }
            scan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return concreteTiles;
    }

    private int calculateFillableTiles(String filename) {

        File outlayFile = new File(filename);
        int fillableTiles = 0;

        try {
            Scanner scan = new Scanner(outlayFile);

            while (scan.hasNextLine()) {

                String row = scan.nextLine();

                for (int j = 0; j < row.length(); j++) {

                    char c = row.charAt(j);
                    if (c != 'X') {
                        fillableTiles++;
                    }
                }
            }
            scan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fillableTiles;
    }

    public double getFilledPercentage(TileList borderTiles,
            TileList fillTiles, int fillableTiles) {

        return ((double) (borderTiles.size() + fillTiles.size()) /
                fillableTiles);
    }

    private void updatePlayer(List<Enemy> enemiesOnThisLevel,
            TileList pathTiles, Tile overlappedTile) {

        player.update(overlappedTile);
        boolean playerHasDied = false;

        if (player.isOverlapping(pathTiles) &&
                (overlappedTile != pathTiles.get(pathTiles.size() - 1) ||
                        overlappedTile.isCollided())) {

            playerHasDied = true;
        }
        for (Enemy enemy : enemiesOnThisLevel) {

            if (player.isOverlapping(enemy)) {
                playerHasDied = true;
                break;
            }
        }
        if (playerHasDied) {

            player.respawn();
            pathTiles.clear();
            lives--;
        }
        player.move();
    }

    private void updateEnemies(List<Enemy> enemiesOnThisLevel,
            TileList concreteTiles, TileList grassTiles, TileList pathTiles) {

        for (Enemy enemy : enemiesOnThisLevel) {

            enemy.unstuckIfIsStuckInside(concreteTiles);
            enemy.unstuckIfIsStuckInside(pathTiles);
            enemy.unstuckIfIsStuckInside(grassTiles);
            enemy.unstuckIfIsStuckInside(borderTiles);

            enemy.checkForCollisionWith(concreteTiles, false);
            enemy.checkForCollisionWith(pathTiles, false);
            enemy.checkForCollisionWith(borderTiles, false);

            if (enemy.hasCollidedWith(pathTiles)) {

                Tile collidedTile = enemy.getCollidedTile();
                collidedTile.turnRed(redPathSprite, frameCount);
            }
            enemy.move();
        }
    }

    private void updatePathTiles(List<Enemy> enemiesOnThisLevel,
            TileList concreteTiles, Tile overlappedTile, Tile newPath) {

        if (newPath != null &&
                !player.isOverlapping(concreteTiles) &&
                !player.isOverlapping(borderTiles) &&
                !player.isOverlapping(grassTiles))

            pathTiles.add(newPath);

        if (pathTiles.isClosedOffBy(borderTiles, concreteTiles)) {

            player.stop();
            pathTiles.fill(borderTiles, grassTiles, concreteTiles,
                    enemiesOnThisLevel);
        }
        if (player.isOverlapping(concreteTiles) ||
                (player.isOverlapping(borderTiles) &&
                        overlappedTile != null))

            borderTiles.convertToFillTiles(pathTiles);

        grassTiles.removeFloatingTiles();
        pathTiles.propagate(redPathSprite, frameCount);
    }

    public static void main(String[] args) {
        PApplet.main("lawnlayer.App");
    }

}