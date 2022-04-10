package lawnlayer;

import processing.core.PImage;
import processing.core.PApplet;

public abstract class GameObject {

    protected PImage sprite;
    protected int size;
    protected int x;
    protected int y;

    protected GameObject(PImage sprite, int x, int y) {
        this.sprite = sprite;
        size = Info.SPRITESIZE;
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

    public PImage getSprite() {
        return sprite;
    }

    public void draw(PApplet app) {
        app.image(sprite, x, y);
    }

}
