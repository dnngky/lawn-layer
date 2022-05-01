package lawnlayer;

import lawnlayer.Info.Name;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * A subclass of GameObject and implementation of PowerUp.
 * This power up turns the Player invincible for a brief period
 * of time, which includes all the other three power ups (boost,
 * freeze, and shield).
 */
public class Invincible extends GameObject implements PowerUp {

    private static Invincible invincible = null;

    private static final int EFFECTDURATION  = 5;
    private static final int SPAWNDURATION = 7;
    private static final int[] SPAWNDELAY = new int[] { 20, 25 };
    private static final int[] RGB = new int[] { 255, 165, 0 };

    private boolean isVisible;
    private boolean inEffect;
    private int delay;
    private int stateChangeFrameCount;
    
    /**
     * Initialises the Invincible power up with the specified sprite and name.
     * 
     * @param sprite - the PImage sprite of the boost
     * @param name - the name of the boost
     */
    private Invincible(PImage sprite, Name name) {

        super(sprite, name);
        
        delay = SPAWNDELAY[0] + rand.nextInt(SPAWNDELAY[1] - SPAWNDELAY[0]);
        isVisible = false;
        inEffect = false;
        stateChangeFrameCount = 0;
    }

    /**
     * A 'static constructor' for Player. If multiple instantiation of Player
     * is attempted, an exception is thrown.
     * 
     * @param sprite - the PImage sprite of the Boost
     * @param name - the name of the boost
     * @return a single instance of Player
     * 
     * @see #Invincible(PImage, int, int)
     * @throws IllegalStateException if an instance already exists
     */
    public static Invincible createInvincible(PImage sprite, Name name) {

        if (invincible == null) {
            invincible = new Invincible(sprite, name);
            return invincible;
        }
        else throw
            new IllegalStateException("Invicible has already been created");
    }

    /**
     * Removes the single instance of Invincible.
     */
    public static void removeInvincible() {

        if (invincible != null) {
            invincible = null;
        }
    }

    /**
     * Draws the power up.
     * 
     * @param app - the PApplet instance to be drawn in
     * 
     * @see GameObject#draw(PApplet)
     */
    @Override
    public void draw(PApplet app) {

        if (isVisible)
            super.draw(app);
    }

    /**
     * @see PowerUp#getProgressBarWidth(int, int)
     */
    public double getProgressBarWidth(int barWidth, int frameCount) {

        int frameDuration = Info.FPS * EFFECTDURATION;
        int frameRemaining =
            frameDuration - (frameCount - stateChangeFrameCount);
        
        return (barWidth * ((double) frameRemaining / frameDuration));
    }

    /**
     * @see PowerUp#getRGB()
     */
    public int[] getRGB() {

        return RGB;
    }

    /**
     * @see PowerUp#getStateChangeFrameCount()
     */
    public int getStateChangeFrameCount() {

        return stateChangeFrameCount;
    }

    /**
     * @see PowerUp#isInEffect()
     */
    public boolean isInEffect() {

        return inEffect;
    }

    /**
     * @see PowerUp#setStartingFrameCount(int)
     */
    public void setStartingFrameCount(int frameCount) {

        stateChangeFrameCount = frameCount;
    }

    /**
     * @see PowerUp#isTimeToDeactivate(int)
     */
    public boolean isTimeToDeactivate(int frameCount) {

        int frameDuration = Info.FPS * EFFECTDURATION;
        return (!isVisible && inEffect &&
                frameCount - stateChangeFrameCount >= frameDuration);
    }

    /**
     * @see PowerUp#isTimeToDespawn(int)
     */
    public boolean isTimeToDespawn(int frameCount) {

        int frameDuration = Info.FPS * SPAWNDURATION;
        return (isVisible && !inEffect &&
                frameCount - stateChangeFrameCount >= frameDuration);
    }

    /**
     * @see PowerUp#isTimeToSpawn(int)
     */
    public boolean isTimeToSpawn(int frameCount) {
        
        int frameDelay = Info.FPS * delay;
        return (!isVisible && !inEffect &&
                frameCount - stateChangeFrameCount >= frameDelay);
    }

    /**
     * @see PowerUp#spawn(TileList, int)
     */
    public void spawn(TileList unfilledTiles, int frameCount) {
        
        int tileIndex = rand.nextInt(unfilledTiles.size());
        Tile spawnTile = unfilledTiles.get(tileIndex);
        
        x = spawnTile.getX();
        y = spawnTile.getY();

        isVisible = true;
        stateChangeFrameCount = frameCount;
    }

    /**
     * @see PowerUp#despawn(int)
     */
    public void despawn(int frameCount) {

        x = -1;
        y = -1;

        isVisible = false;
        stateChangeFrameCount = frameCount;
        delay = SPAWNDELAY[0] + rand.nextInt(SPAWNDELAY[1] - SPAWNDELAY[0]);
    }

    /**
     * @see PowerUp#activateOn(Entity, Tile, int)
     */
    public void activateOn(Entity entity, Tile overlappedTile,
        int frameCount) {

        if (!(entity instanceof Player || entity instanceof Enemy))
            throw new IllegalArgumentException
                ("Argument needs to be an instance of Player or Enemy");
        
        if (entity instanceof Player) {

            Player player = (Player) entity;

            if (overlappedTile == null ||
                overlappedTile.getName() != Name.GRASS) {
                
                player.setSpeed(Entity.DEFAULTSPEED + 3);
                player.enableShield();
            }
        }
        if (entity instanceof Enemy) {

            Enemy enemy = (Enemy) entity;

            if (overlappedTile == null ||
                overlappedTile.getName() != Name.GRASS) {
                
                enemy.setMovement(Movement.STATIONARY);
                inEffect = true;
            }
        }
        despawn(frameCount);
    }

    /**
     * @see PowerUp#deactivateOn(Entity)
     */
    public void deactivateOn(Entity entity) {

        if (!(entity instanceof Player || entity instanceof Enemy))
            throw new IllegalArgumentException
                ("Argument needs to be an instance of Player or Enemy");
            
        if (entity instanceof Player) {

            Player player = (Player) entity;
            player.setSpeed(Entity.DEFAULTSPEED);
            player.disableShield();
        }
        if (entity instanceof Enemy) {

            Enemy enemy = (Enemy) entity;
            enemy.initialiseMovement();
        }
        inEffect = false;
    }
    
}