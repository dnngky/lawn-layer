package lawnlayer;

import lawnlayer.Info.Name;
import processing.core.PApplet;
import processing.core.PImage;

public class Boost extends GameObject implements PowerUp {

    private static Boost boost = null;

    public static final int EFFECTDURATION = 3;
    public static final int SPAWNDURATION = 10;
    
    private boolean isVisible;
    private boolean inEffect;
    private int delay;
    private int stateChangeFrameCount;
    
    private Boost(PImage sprite, Name name) {

        super(sprite, name);

        isVisible = false;
        inEffect = false;
        delay = 3 + rand.nextInt(Info.SPAWNDELAY - 3);
        stateChangeFrameCount = 0;
    }

    public static Boost createSpeedBoost(PImage sprite, Name name) {

        if (boost == null) {
            boost = new Boost(sprite, name);
            return boost;
        }
        else throw
            new IllegalStateException("SpeedBoost has already been created");
    }

    public static void removeSpeedBoost() {

        if (boost != null) {
            boost = null;
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

        if (!(object instanceof Player))
            throw new IllegalArgumentException
                ("Argument needs to be an instance of Player");
        
        Player player = (Player) object;

        if (overlappedTile == null ||
            overlappedTile.getName() != Name.GRASS)
            player.setSpeed(5);
        
        inEffect = true;
        despawn(frameCount);
    }

    public void deactivateOn(GameObject object) {

        if (!(object instanceof Player))
            throw new IllegalArgumentException
                ("Argument needs to be an instance of Player");

        Player player = (Player) object;
        player.setSpeed(Entity.DEFAULTSPEED);

        inEffect = false;
    }

}
