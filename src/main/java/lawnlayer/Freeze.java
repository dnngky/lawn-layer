package lawnlayer;

import java.util.List;

import lawnlayer.Info.Name;
import processing.core.PApplet;
import processing.core.PImage;

public class Freeze extends GameObject implements PowerUp {
    
    private static Freeze freeze = null;

    public static final int EFFECTDURATION = 5;
    public static final int SPAWNDURATION = 10;
    
    private boolean isVisible;
    private boolean inEffect;
    private int delay;
    private int stateChangeFrameCount;
    
    private Freeze(PImage sprite, Name name) {

        super(sprite, name);
        isVisible = false;
        inEffect = false;
        delay = 3 + rand.nextInt(Info.SPAWNDELAY - 3);
        stateChangeFrameCount = 0;
    }

    public static Freeze createFreeze(PImage sprite, Name name) {

        if (freeze == null) {
            freeze = new Freeze(sprite, name);
            return freeze;
        }
        else throw
            new IllegalStateException("SpeedBoost has already been created");
    }

    public static void removeSpeedBoost() {

        if (freeze != null) {
            freeze = null;
        }
    }

    public void setStartingFrameCount(int frameCount) {

        stateChangeFrameCount = frameCount;
    }

    @Override
    public void draw(PApplet app) {

        if (isVisible)
            super.draw(app);
    }

    public boolean isTimeToDeactivate(int frameCount) {

        int frameDuration = Info.FPS * EFFECTDURATION;
        return (!isVisible && inEffect &&
                frameCount - stateChangeFrameCount >= frameDuration);
    }

    public boolean isTimeToDespawn(int frameCount) {

        int frameDuration = Info.FPS * SPAWNDURATION;
        return (isVisible && !inEffect &&
                frameCount - stateChangeFrameCount >= frameDuration);
    }

    public boolean isTimeToSpawn(int frameCount) {
        
        int frameDelay = Info.FPS * delay;
        return (!isVisible && !inEffect &&
                frameCount - stateChangeFrameCount >= frameDelay);
    }

    public void spawn(TileList unfilledTiles, int frameCount) {
        
        int tileIndex = rand.nextInt(unfilledTiles.size());
        Tile spawnTile = unfilledTiles.get(tileIndex);
        
        x = spawnTile.getX();
        y = spawnTile.getY();

        isVisible = true;
        stateChangeFrameCount = frameCount;
    }

    public void despawn(int frameCount) {

        x = -1;
        y = -1;

        isVisible = false;
        stateChangeFrameCount = frameCount;
        delay = 3 + rand.nextInt(Info.SPAWNDELAY - 3);
    }

    public void activateOn(GameObject object, Tile overlappedTile,
        int frameCount) {

        if (!(object instanceof Enemy))
            throw new IllegalArgumentException
                ("Argument needs to be an instance of Enemy");

        Enemy enemy = (Enemy) object;
        if (overlappedTile == null ||
            overlappedTile.getName() != Name.GRASS)
            enemy.setMovement(Movement.STATIONARY);
    }

    public void activateOn(List<Enemy> enemies, Tile overlappedTile,
        int frameCount) {

        for (Enemy enemy : enemies)
            activateOn(enemy, overlappedTile, frameCount);
        
        inEffect = true;
        despawn(frameCount);
    }

    public void deactivateOn(GameObject object) {

        if (!(object instanceof Enemy))
            throw new IllegalArgumentException
                ("Argument needs to be an instance of Enemy");

        Enemy enemy = (Enemy) object;
        enemy.initialiseMovement();

        inEffect = false;
    }

    public void deactivateOn(List<Enemy> enemies) {

        for (Enemy enemy : enemies)
            deactivateOn(enemy);
    }

}
