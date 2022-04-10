package lawnlayer;

import java.util.List;
import java.util.ArrayList;
// import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
//import processing.data.JSONObject;
//import processing.data.JSONArray;
//import processing.core.PFont;

public class App extends PApplet {

    public final String configPath;

    private PImage soil;

    private List<Tile> concreteTiles = new ArrayList<>();
    // private List<Tile> grassTiles = new ArrayList<>();

    private Player player;
    private Worm worm;
    // private Beetle beetle;

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
    @Override
    public void settings() {
        size(Info.WIDTH, Info.HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the
     * player, enemies and map elements.
     */
    @Override
    public void setup() {
        frameRate(Info.FPS);

        // Load images during setup

        this.soil = loadImage(this.getClass().getResource("bg3.png").getPath());

        PImage concreteSprite = loadImage(this.getClass().getResource("concrete_tile.png").getPath());
        // PImage grassSprite =
        // loadImage(this.getClass().getResource("gr2.png").getPath());

        PImage ballSprite = loadImage(this.getClass().getResource("ball.png").getPath());
        PImage wormSprite = loadImage(this.getClass().getResource("worm.png").getPath());
        // PImage beetleSprite =
        // loadImage(this.getClass().getResource("beetle.png").getPath());

        for (int i = 0; i < (Info.WIDTH / Info.SPRITESIZE); i++) {
            concreteTiles.add(new Tile(concreteSprite, i * Info.SPRITESIZE, 0));
            concreteTiles.add(new Tile(concreteSprite, i * Info.SPRITESIZE, Info.HEIGHT - Info.SPRITESIZE));
        }
        for (int i = 0; i < (Info.HEIGHT / Info.SPRITESIZE); i++) {
            concreteTiles.add(new Tile(concreteSprite, 0, i * Info.SPRITESIZE));
            concreteTiles.add(new Tile(concreteSprite, Info.WIDTH - Info.SPRITESIZE, i * Info.SPRITESIZE));
        }
        player = new Player(ballSprite, 0, 0);
        worm = new Worm(wormSprite, 200, 200);
    }

    /*
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {

        background(soil);
        Tile overlappedTile = concreteTiles.get(0);

        for (Tile tile : concreteTiles) {

            tile.draw(this);

            if (player.isOverlapping(tile)) {
                player.movingOnConcrete();
                overlappedTile = tile;
            }
            if (worm.collidesAtTopWith(tile)) {
                worm.collidesAt(Side.TOP);
            } else if (worm.collidesAtBottomWith(tile)) {
                worm.collidesAt(Side.BOTTOM);
            } else if (worm.collidesAtLeftWith(tile)) {
                worm.collidesAt(Side.LEFT);
            } else if (worm.collidesAtRightWith(tile)) {
                worm.collidesAt(Side.RIGHT);
            }
        }
        player.draw(this);
        worm.draw(this);
        player.move(overlappedTile);
        worm.move();

        // System.out.println(collidedTile.getX()+" "+collidedTile.getY());
        // System.out.println("Ball "+ball.getMidX()+" "+ball.getMidY()+" is on tile
        // "+collidedTile.getMidX()+" "+collidedTile.getMidY());
        // System.out.println("Ball is on tile: "+ball.isOn(collidedTile));
    }

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
