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

/**
 * The game engine.
 */
public class App extends PApplet {

    public final String configPath;
    /**
     * Duration of the display screen (seconds), which includes the
     * start, end, and level screen.
     */
    private static final int DISPLAYSCREENDURATION = 3;
    
    // Font and sprite images

    private PFont font;

    private PImage soil;
    private PImage concreteSprite;
    private PImage greenPathSprite;
    private PImage redPathSprite;
    private PImage wormSprite;
    private PImage beetleSprite;

    private PImage heartSprite;
    private PImage freezeSprite;
    private PImage boostSprite;
    private PImage invincibleSprite;
    private PImage shieldSprite;

    // Config information

    private Map<Integer,String> outlays;
    private Map<Integer,List<Enemy>> enemies;
    private Map<Integer,Float> goals;
    private int lives;

    // Level-specific information

    private int currentLevel;

    private int displayScreenFrameCount;

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
    private Invincible invincible;
    private Shield shield;

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
     * Loads all resources such as images. Initialises the elements such as the
     * player, enemies and map elements.
     */
    @Override
    public void setup() {

        frameRate(Info.FPS);

        // Load images and fonts during setup

        font = createFont(this.getClass().getResource("upheavtt.ttf").getPath(), 50, false);

        soil = loadImage(this.getClass().getResource("background.png").getPath());
        concreteSprite = loadImage(this.getClass().getResource("concrete_tile.png").getPath());
        greenPathSprite = loadImage(this.getClass().getResource("green_path.png").getPath());
        redPathSprite = loadImage(this.getClass().getResource("red_path.png").getPath());

        wormSprite = loadImage(this.getClass().getResource("worm.png").getPath());
        beetleSprite = loadImage(this.getClass().getResource("beetle.png").getPath());

        heartSprite = loadImage(this.getClass().getResource("heart.png").getPath());
        freezeSprite = loadImage(this.getClass().getResource("freeze.png").getPath());
        boostSprite = loadImage(this.getClass().getResource("boost.png").getPath());
        invincibleSprite = loadImage(this.getClass().getResource("invincible.png").getPath());
        shieldSprite = loadImage(this.getClass().getResource("shield.png").getPath());

        PImage grassSprite = loadImage(this.getClass().getResource("grass_tile.png").getPath());
        PImage playerSprite = loadImage(this.getClass().getResource("ball.png").getPath());

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

        // Initialise player and level
        
        Player.removePlayer();
        player = Player.createPlayer(playerSprite);

        currentLevel = 0;
        fillGoal = 0.0;
    }

    /**
     * Draws all elements in the game by current frame.
     */
    @Override
    public void draw() {

        background(soil);
        textFont(font);

        int percentageFilled = (int) (getFilledPercentage() * 100);
        int percentageGoal = (int) (fillGoal * 100);
        
        if (percentageGoal != 0.0) {

            displayTopBarInfo(percentageFilled, percentageGoal);

            updatePlayer();
            updateEnemies();
            updateTiles();
            updatePowerUps();

            concreteTiles.drawTiles(this);
            borderTiles.drawTiles(this);
            grassTiles.drawTiles(this);
            pathTiles.drawTiles(this);

            player.draw(this);
            levelEnemies.forEach(enemy -> enemy.draw(this));
            
            boost.draw(this);
            freeze.draw(this);
            invincible.draw(this);
            shield.draw(this);
        }
        if (fillGoal == 0 ||
            percentageFilled >= percentageGoal) {

            if (displayScreenFrameCount == 0) {
                deactivateAllPowerUps();
                currentLevel++;
            }
            displayScreen("LEVEL");
        }
        if (lives == 0)
            displayScreen("LOSE");
        
        if (currentLevel > goals.size())
            displayScreen("WIN");
        
        if (displayScreenFrameCount > 0 &&
            frameCount - displayScreenFrameCount ==
                Info.FPS * DISPLAYSCREENDURATION) {
            
            clearAllTiles();

            if (lives == 0 || currentLevel > goals.size()) {
                exit();
            }
            else {
                initialiseLevel(currentLevel);
                player.respawn();
                displayScreenFrameCount = 0;
            }
        }
    }

    /**
     * Changes ball's direction when player presses a keyboard key.
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

    /**
     * Clears all tile containers.
     */
    private void clearAllTiles() {

        concreteTiles.clear();
        borderTiles.clear();
        dirtTiles.clear();
        grassTiles.clear();
        pathTiles.clear();
    }

    /**
     * Deactivates all power ups.
     */
    private void deactivateAllPowerUps() {

        if (boost != null)
            boost.deactivateOn(player);
        
        if (freeze != null && levelEnemies != null)
            levelEnemies.forEach(freeze::deactivateOn);
        
        if (invincible != null && levelEnemies != null) {
            invincible.deactivateOn(player);
            levelEnemies.forEach(invincible::deactivateOn);
        }
        if (shield != null)
            shield.deactivateOn(player);
    }

    /**
     * Displays the win, lose, and level screen.
     * 
     * @param type the type of display screen
     */
    private void displayScreen(String type) {

        if (type.equals("WIN")) {

            background(107, 142, 35);
            fill(255);
            textAlign(CENTER, CENTER);
            text("You Win!", 640, 360);
        }
        if (type.equals("LOSE")) {

            background(0);
            fill(255);
            textAlign(CENTER, CENTER);
            text("Game Over", 640, 360);
        }
        if (type.equals("LEVEL")) {

            background(205,133,63);
            fill(255);
            textAlign(CENTER, CENTER);
            text("Level " + currentLevel, 640, 300);
            
            image(heartSprite, 570, 340);
            text("x " + lives, 665, 351);
        }
        if (displayScreenFrameCount == 0)
            displayScreenFrameCount = frameCount;
    }

    /**
     * Displays the information located at the top bar.
     * 
     * @param percentageFilled the percentage of tiles filled
     * @param percentageGoal the percentage of tiles required to be filled
     */
    private void displayTopBarInfo(int percentageFilled, int percentageGoal) {

        fill(0);
        textAlign(RIGHT, CENTER);
        text(percentageFilled + "% | " + percentageGoal + "%", 1250, 35);

        textAlign(CENTER, CENTER);
        text("Level " + currentLevel, 640, 35);

        int x = 30;
        for (int n = 0; n < lives; n++) {
            image(heartSprite, x, 25);
            x += 50;
        }
    }

    /**
     * Scans a text file and creates a TileList of tiles based on the
     * specified marker.
     * <p>
     * Scans through the text file and looks for the marker. Creates a
     * tile at the same relative XY-coordinate as the marker and adds it
     * into a TileList. After all of the file has been scanned, returns
     * the filled TileList.
     * 
     * @param filename the name of the text file to be scanned
     * @param marker the marker to be used to create tiles
     * @return a TileList of tiles created from the marker in the text file
     */
    private TileList fillFrom(String filename, char marker, Name tileName) {

        File outlayFile = new File(filename);
        TileList tiles = new TileList(concreteSprite, tileName);

        try {
            Scanner scan = new Scanner(outlayFile);
            int y = 80;

            while (scan.hasNextLine()) {

                String row = scan.nextLine();

                for (int j = 0; j < row.length(); j++) {

                    char c = row.charAt(j);
                    int x = j * GameObject.SIZE;

                    if (c == marker) {
                        Tile concreteTile =
                            new Tile(concreteSprite, x, y, tileName);
                        tiles.add(concreteTile);
                    }
                }
                y += GameObject.SIZE;
            }
            scan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tiles;
    }

    /**
     * Calculates the current percentage of tiles filled.
     * 
     * @return the percentage of tiles filled
     */
    private double getFilledPercentage() {

        return ((double) (borderTiles.size() + grassTiles.size()) /
                numOfFillables);
    }

    /**
     * Initialises the elements of the specifed 'currentLevel'.
     * <p>
     * This includes concrete tiles, enemies, and power ups.
     * 
     * @param currentLevel the level to initialise
     */
    private void initialiseLevel(int currentLevel) {

        try {

            String currentOutlay = outlays.get(currentLevel);
            concreteTiles = fillFrom(currentOutlay, 'X', Name.CONCRETE);
            dirtTiles = fillFrom(currentOutlay, ' ', Name.DIRT);

            numOfFillables = dirtTiles.size();
            fillGoal = goals.get(currentLevel);

            levelEnemies = enemies.get(currentLevel);
            levelEnemies.forEach(enemy ->
                enemy.checkIfIsStuckInside(concreteTiles));

            Boost.removeBoost();
            boost = Boost.createBoost(boostSprite, Name.BOOST);
            boost.setStartingFrameCount(frameCount);

            Freeze.removeFreeze();
            freeze = Freeze.createFreeze(freezeSprite, Name.FREEZE);
            freeze.setStartingFrameCount(frameCount);

            Invincible.removeInvincible();
            invincible = Invincible.createInvincible
                (invincibleSprite, Name.INVINCIBLE);
            invincible.setStartingFrameCount(frameCount);

            Shield.removeShield();
            shield = Shield.createShield(shieldSprite, Name.SHIELD);
            shield.setStartingFrameCount(frameCount);
        }
        catch (NullPointerException e) {

            concreteTiles = new TileList();
            dirtTiles = new TileList();

            numOfFillables = 0;
            fillGoal = 0;

            levelEnemies = new ArrayList<>();
        }
    }

    /**
     * Loads in the config information from the config file into
     * their respective containers.
     */
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

    /**
     * Updates all Enemies' behaviours per frame, which includes
     * collisions and movements, and killing off Player.
     */
    private void updateEnemies() {

        int enemySpeed = levelEnemies.get(0).getSpeed();

        for (int i = 0; i < enemySpeed; i++) {
        
            for (Enemy enemy : levelEnemies) {

                enemy.checkForCollisionWith(concreteTiles, grassTiles, false);
                enemy.checkForCollisionWith(pathTiles, grassTiles, false);
                enemy.checkForCollisionWith(borderTiles, grassTiles, false);

                if (enemy.hasCollidedWith(pathTiles) &&
                    !player.isShielded()) {

                    Tile collidedTile = enemy.getCollidedTile();
                    collidedTile.turnRed(redPathSprite, frameCount);
                }
                if (player.isOverlapping(enemy) &&
                    !player.isShielded()) {
                    
                    player.respawn();
                    pathTiles.clear();
                    deactivateAllPowerUps();
                    lives--;
                }
                enemy.move();
            }
        }
    }

    /**
     * Updates all Tiles' behaviours per frame, which includes
     * filling regions and propagating red paths.
     */
    private void updateTiles() {

        if (player.isOnSafeTile() &&
            !pathTiles.isEmpty()) {

            player.stop();
            pathTiles.fill(borderTiles, dirtTiles, grassTiles,
                    concreteTiles, levelEnemies, false);
        }
        grassTiles.removeFloatingTiles(borderTiles);
        pathTiles.propagate(redPathSprite, frameCount);
    }

    /**
     * Updates the Player's behaviours per frame, which includes
     * laying down path, dying, and activating power ups.
     */
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
                pathTiles.clear();
                deactivateAllPowerUps();
                lives--;
            }
            if (player.isOverlapping(boost))
                boost.activateOn(player, overlappedTile, frameCount);
            
            if (player.isOverlapping(freeze))
                levelEnemies.forEach(enemy ->
                    freeze.activateOn(enemy, overlappedTile, frameCount));

            if (player.isOverlapping(invincible)) {
                invincible.activateOn(player, overlappedTile, frameCount);
                levelEnemies.forEach(enemy ->
                    invincible.activateOn(enemy, overlappedTile, frameCount));
            }
            if (player.isOverlapping(shield))
                shield.activateOn(player, overlappedTile, frameCount);
            
            player.move();
        }
    }

    /**
     * Updates all PowerUps' behaviours per frame, which includes
     * spawning and despawning, activating and deactivating, and
     * displaying the progress bar.
     */
    private void updatePowerUps() {

        PowerUp[] powerUps =
            new PowerUp[] {boost, freeze, invincible, shield};

        PowerUp mostRecent = null;

        for (PowerUp powerUp : powerUps) {

            if (powerUp.isTimeToSpawn(frameCount))
                powerUp.spawn(dirtTiles, frameCount);

            if (powerUp.isTimeToDespawn(frameCount))
                powerUp.despawn(frameCount);

            if (powerUp.isInEffect() &&
                (mostRecent == null ||
                powerUp.getStateChangeFrameCount() >
                mostRecent.getStateChangeFrameCount())) {
                
                mostRecent = powerUp;
            }
        }
        if (mostRecent != null) {
            
            int[] rgb = mostRecent.getRGB();
            int width =
                (int) mostRecent.getProgressBarWidth(240, frameCount);
            
            rectMode(CORNER);
            fill(100);
            rect(200, 26, 240, 30, 20);

            rectMode(CORNER);
            fill(rgb[0], rgb[1], rgb[2]);
            rect(200, 26, width, 30, 20);
        }
        if (boost.isTimeToDeactivate(frameCount))
            boost.deactivateOn(player);

        if (freeze.isTimeToDeactivate(frameCount))
            levelEnemies.forEach(freeze::deactivateOn);
        
        if (invincible.isTimeToDeactivate(frameCount)) {
            invincible.deactivateOn(player);
            levelEnemies.forEach(freeze::deactivateOn);
        }
        if (shield.isTimeToDeactivate(frameCount))
            shield.deactivateOn(player);
    }

    public static void main(String[] args) {
        PApplet.main("lawnlayer.App");
    }

}