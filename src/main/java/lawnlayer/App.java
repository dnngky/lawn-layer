package lawnlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import lawnlayer.Info.Name;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.data.JSONArray;
import processing.core.PFont;

public class App extends PApplet {
    
    /**
     * Duration of the display screen (seconds), which includes the
     * start, end, and level screen.
     */
    public static final int DISPLAYSCREENDURATION = 3;

    public final String configPath;
    
    // Font and sprite images

    private PFont font;
    private PImage soil;
    private PImage concreteSprite;
    private PImage greenPathSprite;
    private PImage redPathSprite;
    private PImage wormSprite;
    private PImage beetleSprite;

    private PImage freezeSprite;
    private PImage boostSprite;
    private PImage heart;

    // Information for all levels

    private Map<Integer,String> outlays;
    private Map<Integer,List<Enemy>> enemies;
    private Map<Integer,Float> goals;
    private int lives;

    // Level-specific information

    private int currentLevel;
    private int displayScreenTime;

    private int numOfFillables;
    private double fillGoal;

    private TileList borderTiles;
    private TileList concreteTiles;
    private TileList dirtTiles;
    private TileList grassTiles;
    private TileList pathTiles;

    private List<Enemy> levelEnemies;
    private Player player;

    private Boost boost;
    private Freeze freeze;

    public App() {

        this.configPath = "config.json";
    }

    /**
     * Initialises the setting of the window size.
     */
    @Override
    public void settings() {

        size(Info.WIDTH, Info.HEIGHT);
    }

    /**
     * Loads all resources such as images. Initialise the elements such as the
     * player, enemies and map elements.
     */
    @Override
    public void setup() {

        frameRate(Info.FPS);

        // Load images and fonts during setup

        font = createFont(this.getClass().getResource("upheavtt.ttf").getPath(), 50, false);

        soil = loadImage(this.getClass().getResource("background4.png").getPath());
        concreteSprite = loadImage(this.getClass().getResource("concrete_tile.png").getPath());
        greenPathSprite = loadImage(this.getClass().getResource("green_path.png").getPath());
        redPathSprite = loadImage(this.getClass().getResource("red_path.png").getPath());

        wormSprite = loadImage(this.getClass().getResource("worm.png").getPath());
        beetleSprite = loadImage(this.getClass().getResource("beetle.png").getPath());

        heart = loadImage(this.getClass().getResource("heart_smaller.png").getPath());
        freezeSprite = loadImage(this.getClass().getResource("freeze_small.png").getPath());
        boostSprite = loadImage(this.getClass().getResource("boost_small.png").getPath());

        PImage grassSprite = loadImage(this.getClass().getResource("grass_tile1.png").getPath());
        PImage ballSprite = loadImage(this.getClass().getResource("ball.png").getPath());

        // Initialise config information containers

        outlays = new HashMap<>();
        enemies = new HashMap<>();
        goals = new HashMap<>();

        // Initialise tile containers

        borderTiles = new TileList(grassSprite, Name.GRASS);
        concreteTiles = new TileList(concreteSprite, Name.CONCRETE);
        dirtTiles = new TileList();
        grassTiles = new TileList(grassSprite, Name.GRASS);
        pathTiles = new TileList(greenPathSprite, Name.PATH);

        // Load JSON file

        loadConfigFile();

        // Initialise player and power ups
        
        player = Player.createPlayer(ballSprite);

        currentLevel = 0;
        fillGoal = 0.0;
    }

    /**
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {

        // long start = System.currentTimeMillis();

        background(soil);
        textFont(font);

        double percentageFilled = getFilledPercentage();
        
        if (fillGoal != 0.0) {

            displayTopBarInfo(percentageFilled);

            updatePlayer();
            updateEnemies();
            updatePathTiles();
            updatePowerUps();

            concreteTiles.drawTiles(this);
            borderTiles.drawTiles(this);
            grassTiles.drawTiles(this);
            pathTiles.drawTiles(this);

            player.draw(this);
            for (Enemy enemy : levelEnemies)
                enemy.draw(this);
            boost.draw(this);
            freeze.draw(this);
        }
        if (fillGoal == 0 ||
            percentageFilled >= fillGoal) {

            if (displayScreenTime == 0)
                currentLevel++;
            displayScreen("LEVEL");
        }
        if (lives == 0)
            displayScreen("LOSE");
        
        if (currentLevel > goals.size())
            displayScreen("WIN");
        
        if (displayScreenTime > 0 &&
            frameCount - displayScreenTime ==
                Info.FPS * DISPLAYSCREENDURATION) {
            
            clearAllTiles();

            if (lives == 0 || currentLevel > goals.size()) {
                exit();
            }
            else {
                initialiseLevel(currentLevel);
                player.respawn();
                displayScreenTime = 0;
            }
        }
        // long end = System.currentTimeMillis();

        // System.out.printf("Drawing took %d ms%n", (end - start));
    }

    /**
     * Changes ball's direction when player presses a keyboard key
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

    private void clearAllTiles() {

        concreteTiles.clear();
        borderTiles.clear();
        dirtTiles.clear();
        grassTiles.clear();
        pathTiles.clear();
    }

    private void displayScreen(String result) {

        if (result.equals("WIN")) {

            background(107, 142, 35);
            fill(255);
            textAlign(CENTER, CENTER);
            text("You win :D", 640, 360);
        }
        if (result.equals("LOSE")) {

            background(0);
            fill(255);
            textAlign(CENTER, CENTER);
            text("You lose D:", 640, 360);
        }
        if (result.equals("LEVEL")) {

            background(205,133,63);
            fill(255);
            textAlign(CENTER, CENTER);
            text("Level " + currentLevel, 640, 300);
            
            image(heart, 570, 340);
            text("x " + lives, 665, 351);
        }
        if (displayScreenTime == 0)
            displayScreenTime = frameCount;
    }

    private void displayTopBarInfo(double percentageFilled) {

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
    }

    private TileList fillFrom(String filename, char marker) {

        File outlayFile = new File(filename);
        TileList tiles = new TileList(concreteSprite, Name.CONCRETE);

        try {
            Scanner scan = new Scanner(outlayFile);
            int y = 80;

            while (scan.hasNextLine()) {

                String row = scan.nextLine();

                for (int j = 0; j < row.length(); j++) {

                    char c = row.charAt(j);
                    int x = j * Info.SPRITESIZE;

                    if (c == marker) {
                        Tile concreteTile =
                            new Tile(concreteSprite, x, y, Name.CONCRETE);
                        tiles.add(concreteTile);
                    }
                }
                y += Info.SPRITESIZE;
            }
            scan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tiles;
    }

    private double getFilledPercentage() {

        return ((double) (borderTiles.size() + grassTiles.size()) /
                numOfFillables);
    }

    private void initialiseLevel(int currentLevel) {

        try {

            String currentOutlay = outlays.get(currentLevel);
            concreteTiles = fillFrom(currentOutlay, 'X');
            dirtTiles = fillFrom(currentOutlay, ' ');

            numOfFillables = dirtTiles.size();
            fillGoal = goals.get(currentLevel);

            levelEnemies = enemies.get(currentLevel);
            for (Enemy enemy : levelEnemies)
                enemy.checkIfIsStuckInside(concreteTiles);

            Boost.removeSpeedBoost();
            boost = Boost.createSpeedBoost(boostSprite, Name.BOOST);
            boost.setStartingFrameCount(frameCount);

            Freeze.removeSpeedBoost();
            freeze = Freeze.createFreeze(freezeSprite, Name.FREEZE);
            freeze.setStartingFrameCount(frameCount);
        }
        catch (NullPointerException e) {

            concreteTiles = new TileList();
            dirtTiles = new TileList();

            numOfFillables = 0;
            fillGoal = 0;

            levelEnemies = new ArrayList<>();
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
                Name type;

                if (enemy.getInt("type") == 0) {
                    sprite = wormSprite;
                    type = Name.WORM;
                } else {
                    sprite = beetleSprite;
                    type = Name.BEETLE;
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

    private void updateEnemies() {

        int enemySpeed = levelEnemies.get(0).getSpeed();

        for (int i = 0; i < enemySpeed; i++) {
        
            for (Enemy enemy : levelEnemies) {

                enemy.checkForCollisionWith(concreteTiles, grassTiles, false);
                enemy.checkForCollisionWith(pathTiles, grassTiles, false);
                enemy.checkForCollisionWith(borderTiles, grassTiles, false);

                if (enemy.hasCollidedWith(pathTiles)) {

                    Tile collidedTile = enemy.getCollidedTile();
                    collidedTile.turnRed(redPathSprite, frameCount);
                }
                if (player.isOverlapping(enemy)) {
                    
                    player.respawn();
                    boost.deactivateOn(player);
                    freeze.deactivateOn(levelEnemies);
                    pathTiles.clear();
                    lives--;
                    break;
                }
                enemy.move();
            }
        }
    }

    private void updatePathTiles() {

        if (player.isOnSafeTile() &&
            !pathTiles.isEmpty()) {

            player.stop();
            pathTiles.fill(borderTiles, dirtTiles, grassTiles,
                    concreteTiles, levelEnemies, false);
        }
        grassTiles.removeFloatingTiles(borderTiles);
        pathTiles.propagate(redPathSprite, frameCount);
    }

    private void updatePlayer() {

        for (int i = 0; i < player.getSpeed(); i++) {

            Tile newPath = player.createPath(greenPathSprite);

            if (newPath != null &&
                !player.isOverlapping(concreteTiles) &&
                !player.isOverlapping(borderTiles) &&
                !player.isOverlapping(grassTiles))

                pathTiles.add(newPath);

            Tile overlappedTile = player.getOverlappedTileFrom(
                borderTiles, concreteTiles, grassTiles, pathTiles);

            player.updateStatus(overlappedTile);

            if (player.isOverlapping(pathTiles) &&
                (overlappedTile != pathTiles.get(pathTiles.size() - 1) ||
                overlappedTile.isRed())) {

                player.respawn();
                boost.deactivateOn(player);
                freeze.deactivateOn(levelEnemies);
                pathTiles.clear();
                lives--;
            }
            if (player.isOverlapping(boost))
                boost.activateOn(player, overlappedTile, frameCount);
            
            if (player.isOverlapping(freeze))
                freeze.activateOn(levelEnemies, overlappedTile, frameCount);
            
            player.move();
        }
    }

    private void updatePowerUps() {

        if (boost.isTimeToSpawn(frameCount))
            boost.spawn(dirtTiles, frameCount);
        
        if (boost.isTimeToDespawn(frameCount))
            boost.despawn(frameCount);
        
        if (boost.isTimeToDeactivate(frameCount))
            boost.deactivateOn(player);

        if (freeze.isTimeToSpawn(frameCount))
            freeze.spawn(dirtTiles, frameCount);
        
        if (freeze.isTimeToDespawn(frameCount))
            freeze.despawn(frameCount);

        if (freeze.isTimeToDeactivate(frameCount))
            freeze.deactivateOn(levelEnemies);
    }

    public static void main(String[] args) {
        PApplet.main("lawnlayer.App");
    }

}