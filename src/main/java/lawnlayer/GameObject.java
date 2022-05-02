package lawnlayer;

import java.util.Random;

import lawnlayer.Info.Name;
import processing.core.PImage;
import processing.core.PApplet;

/**
 * An abstract class through which all objects in the game inherit from.
 */
public abstract class GameObject {
    
    public static final int SIZE = 20;
    
    protected Name name;
    protected PImage sprite;
    protected int x;
    protected int y;

    protected Random rand = new Random();

    /**
     * Initialises a basic GameObject with no sprite nor name. Its
     * xy-coordinate will be randomised within the map bounds (the width
     * and height of the map is pre-defined).
     */
    protected GameObject() {

        randomiseXY();
    }

    /**
     * Initialises a GameObject with the specified sprite and name. Its
     * xy-coordinates will be randomised within the map bounds (the width
     * and height of the map is pre-defined).
     * 
     * @param sprite the PImage sprite of this GameObject
     * @param name the name of this GameObject
     */
    protected GameObject(PImage sprite, Name name) {
        
        this.name = name;
        this.sprite = sprite;
        randomiseXY();
    }

    /**
     * Initialises a GameObject with the specified sprite, xy-coordinates,
     * and name.
     * 
     * @param sprite the PImage sprite of this GameObject
     * @param x the x-coordinate of this GameObject
     * @param y the y-coordinate of this GameObject
     * @param name the name of this GameObject
     */
    protected GameObject(PImage sprite, int x, int y, Name name) {

        this.name = name;
        this.sprite = sprite;
        this.x = x;
        this.y = y;
    }

    /**
     * Initialises a GameObject with the specified sprite, tile location,
     * and name. The xy-coordinates of this GameObject will be randomised
     * within the given tile (the size of the tile is pre-defined).
     * 
     * @param sprite the PImage sprite of this GameObject
     * @param location the tile location of this GameObject
     * @param name the name of this GameObject
     */
    protected GameObject(PImage sprite, String location, Name name) {

        this.name = name;
        this.sprite = sprite;

        int row = Integer.parseInt(location.split(",")[0]);
        int col = Integer.parseInt(location.split(",")[1]);

        int xMax = row * SIZE;
        int xMin = xMax - SIZE;
        int yMax = Info.TOPBAR + (col * SIZE);
        int yMin = yMax - SIZE;

        x = xMin + rand.nextInt(xMax - xMin);
        y = yMin + rand.nextInt(yMax - yMin);
    }

    /**
     * Randomises this GameObject's xy-coordinates within the map bounds
     * (The width and height of the map is pre-defined).
     */
    protected void randomiseXY() {

        x = SIZE + rand.nextInt(Info.WIDTH - 2*SIZE);
        y = (Info.TOPBAR + SIZE) +
            rand.nextInt(Info.HEIGHT - (Info.TOPBAR - 2*SIZE));
    }

    /**
     * Draws this GameObject.
     * 
     * @param app the PApplet instance to be drawn in
     */
    public void draw(PApplet app) {

        app.image(sprite, x, y);
    }

    /**
     * Retrieves the x-coordinate of the centre of this GameObject.
     * 
     * @return the central x-coordinate of this GameObject
     */
    public int getMidX() {
        
        return x + (SIZE / 2);
    }

    /**
     * Retrieves the y-coordinate of the centre of this GameObject.
     * 
     * @return the central y-coordinate of this GameObject
     */
    public int getMidY() {
        
        return y + (SIZE / 2);
    }

    /**
     * Retrieves the x-coordinate of this GameObject.
     * 
     * @return the x-coordinate of this GameObject
     */
    public int getX() {
        
        return x;
    }

    /**
     * Retrieves the y-coordinate of this GameObject.
     * 
     * @return the y-coordinate of this GameObject
     */
    public int getY() {
        
        return y;
    }

    /**
     * Retrieves the name of this GameObject.
     * 
     * @return the name of this GameObject
     */
    public Name getName() {
        
        return name;
    }

    /**
     * Retrieves the sprite of this GameObject.
     * 
     * @return the PImage sprite of this GameObject
     */
    public PImage getSprite() {
        
        return sprite;
    }

    /**
     * Sets the name of this GameObject to the specified name.
     * 
     * @param name the name to be set
     */
    public void setName(Name name) {

        this.name = name;
    }

    /**
     * Sets the sprite of this GameObject to the specified sprite.
     * 
     * @param sprite the PImage sprite to be set
     */
    public void setSprite(PImage sprite) {

        this.sprite = sprite;
    }

    /**
     * Sets the string representation of this GameObject.
     * 
     * @see Object#toString()
     */
    @Override
    public String toString() {
        
        return String.format("%s@[%d,%d]", name, x, y);
    }

}
