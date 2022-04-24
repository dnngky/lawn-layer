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

    protected GameObject(PImage sprite) {
        /*
         * Randomises spawn location if information is not given when
         * object is instantiated.
         */
        this.sprite = sprite;
        this.x = rand.nextInt(Info.WIDTH - 2*size + 1);
        this.y = rand.nextInt(Info.HEIGHT - 2*size + 1);
    }

    protected GameObject(PImage sprite, int x, int y) {

        this.sprite = sprite;
        this.x = x;
        this.y = y;
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
