package lawnlayer;

import java.util.Random;
import processing.core.PImage;
import processing.core.PApplet;

public abstract class GameObject {

    protected String name;
    protected PImage sprite;
    protected int x;
    protected int y;

    protected static int size = Info.SPRITESIZE;
    protected Random rand = new Random();

    protected GameObject(PImage sprite, String name) {
        /*
         * Randomises spawn location if information is not given when
         * object is instantiated.
         */
        this.name = name;
        this.sprite = sprite;
        randomiseXY();
    }

    protected GameObject(int x, int y) {

        this.x = x;
        this.y = y;
    }

    protected GameObject(PImage sprite, int x, int y) {

        this.sprite = sprite;
        this.x = x;
        this.y = y;
    }

    protected GameObject(PImage sprite, int x, int y, String name) {

        this.name = name;
        this.sprite = sprite;
        this.x = x;
        this.y = y;
    }

    protected GameObject(PImage sprite, String location, String name) {

        this.name = name;
        this.sprite = sprite;

        int row = Integer.parseInt(location.split(",")[0]);
        int col = Integer.parseInt(location.split(",")[1]);

        int xMax = row * size;
        int xMin = xMax - size;
        int yMax = Info.TOPBAR + (col * size);
        int yMin = yMax - size;

        x = xMin + rand.nextInt(xMax - xMin);
        y = yMin + rand.nextInt(yMax - yMin);
    }

    protected void randomiseXY() {

        x = rand.nextInt(Info.WIDTH - 2*size + 1);
        y = Info.TOPBAR + rand.nextInt(Info.HEIGHT - 2*size + 1);
    }

    public int getX() {
        
        return x;
    }

    public int getY() {
        
        return y;
    }

    public int getMidX() {
        
        return x + (size / 2);
    }

    public int getMidY() {
        
        return y + (size / 2);
    }

    public String getName() {
        
        return name;
    }

    public PImage getSprite() {
        
        return sprite;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setSprite(PImage sprite) {

        this.sprite = sprite;
    }

    public void draw(PApplet app) {
        app.image(sprite, x, y);
    }

    @Override
    public String toString() {
        return String.format("%s@[%d,%d]", name, x, y);
    }

}
