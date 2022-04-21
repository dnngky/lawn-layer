package lawnlayer;

// import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
//import processing.data.JSONObject;
//import processing.data.JSONArray;
//import processing.core.PFont;

public class App extends PApplet {

    public final String configPath;

    private PImage soil;
    private PImage greenPathSprite;
    // private PImage redPathSprite;

    private TileList concreteTiles;
    private TileList grassTiles;
    private TileList pathTiles;

    private Player player;
    private Enemy worm;
    private Enemy beetle;

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

        soil = loadImage(this.getClass().getResource("bg3.png").getPath());
        greenPathSprite = loadImage(this.getClass().getResource("green_path.png").getPath());

        PImage grassSprite = loadImage(this.getClass().getResource("grass.png").getPath());
        PImage concreteSprite = loadImage(this.getClass().getResource("concrete_tile.png").getPath());
        PImage ballSprite = loadImage(this.getClass().getResource("ball.png").getPath());
        PImage wormSprite = loadImage(this.getClass().getResource("worm.png").getPath());
        PImage beetleSprite = loadImage(this.getClass().getResource("beetle.png").getPath());

        concreteTiles = new TileList(concreteSprite);
        grassTiles = new TileList(grassSprite);
        pathTiles = new TileList(greenPathSprite);

        for (int i = 0; i < (Info.WIDTH / Info.SPRITESIZE); i++) {
            concreteTiles.add(new Tile(concreteSprite, i * Info.SPRITESIZE, 0, Info.CONCRETE));
            concreteTiles.add(new Tile(concreteSprite, i * Info.SPRITESIZE, Info.HEIGHT - Info.SPRITESIZE, Info.CONCRETE));
        }
        for (int i = 0; i < (Info.HEIGHT / Info.SPRITESIZE); i++) {
            concreteTiles.add(new Tile(concreteSprite, 0, i * Info.SPRITESIZE, Info.CONCRETE));
            concreteTiles.add(new Tile(concreteSprite, Info.WIDTH - Info.SPRITESIZE, i * Info.SPRITESIZE, Info.CONCRETE));
        }
        player = new Player(ballSprite, 0, 0);
        worm = new Worm(wormSprite);
        beetle = new Beetle(beetleSprite);
    }

    /*
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {

        background(soil);

        player.checkForOverlapWith(concreteTiles);

        Tile newPath = player.addPath(greenPathSprite);

        if (newPath != null && !player.isOnGrass(grassTiles))

            pathTiles.add(newPath);

        if (pathTiles.size() > 1)

            pathTiles.checkMissingCorner(player);

        if (pathTiles.size() > 0 &&
            pathTiles.isEnclosedWith(concreteTiles, grassTiles))

            pathTiles.fill(grassTiles);
        
        player.move();

        for (int i = 0; i < Info.SPEED; i++) {

            worm.checkForCollisionWith(concreteTiles, false);
            beetle.checkForCollisionWith(concreteTiles, false);

            worm.checkForCollisionWith(pathTiles, false);
            beetle.checkForCollisionWith(pathTiles, false);

            worm.move();
            beetle.move();
        }
        concreteTiles.drawTiles(this);
        grassTiles.drawTiles(this);
        pathTiles.drawTiles(this);

        player.draw(this);
        worm.draw(this);
        beetle.draw(this);
        // System.out.println("Ball "+ball.getMidX()+" "+ball.getMidY()+" is on tile
        // "+collidedTile.getMidX()+" "+collidedTile.getMidY());
        // System.out.println("Ball is on tile: "+ball.isOn(collidedTile));
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