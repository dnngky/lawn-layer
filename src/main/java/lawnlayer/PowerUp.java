package lawnlayer;

/**
 * An interface for all power ups in the game.
 */
public interface PowerUp {

    /**
     * Retrieves the progress bar information used for drawing.
     * 
     * @param barWidth the original width of the progress bar
     * @param frameCount the current frame
     * @return the width of the remaining progress bar
     */
    public double getProgressBarWidth(int barWidth, int frameCount);

    /**
     * Retrieves the colour of the power up's progress bar.
     * 
     * @return the RGB value of the colour
     */
    public int[] getRGB();

    /**
     * Retrieves the frame of the power up's most recent state change.
     * 
     * @return the frame of the most recent state change
     */
    public int getStateChangeFrameCount();

    /**
     * Checks if the power up is currently in effect.
     * 
     * @return true if the power up is in effect
     */
    public boolean isInEffect();

    /**
     * Checks whether the power up should be deactivated on the
     * current frame.
     * 
     * @param frameCount the current frame
     * @return true if the power up should be deactivated
     */
    public boolean isTimeToDeactivate(int frameCount);

    /**
     * Checks whether the power up should spawn on the current frame.
     * 
     * @param frameCount the current frame
     * @return true if the power up should spawn
     */
    public boolean isTimeToSpawn(int frameCount);

    /**
     * Checks whether the power up should despawn on the current frame.
     * 
     * @param frameCount the current frame
     * @return true if the power up should despawn
     */
    public boolean isTimeToDespawn(int frameCount);

    /**
     * Sets the starting frame of the power up (i.e., when
     * the level loads).
     * 
     * @param frameCount the frame to be set to
     */
    public void setStartingFrameCount(int frameCount);

    /**
     * Spawns the power up randomly in the space not yet filled by grass.
     * 
     * @param unfilledTiles the TileList of unfilled tile spaces
     * @param frameCount the current frame
     */
    public void spawn(TileList unfilledTiles, int frameCount);

    /**
     * Despawns the power up
     * 
     * @param frameCount the current frame
     */
    public void despawn(int frameCount);

    /**
     * Activates the power up on the specified entity, given it is not
     * overlapping a grass tile.
     * 
     * @param entity the entity to be activated on
     * @param overlappedTile the Tile the entity is overlapping
     * @param frameCount the current frame
     */
    public void activateOn(Entity entity, Tile overlappedTile,
        int frameCount);

    /**
     * Deactivates the power up on the specified entity.
     * 
     * @param entity the entity to be deactivated on
     */
    public void deactivateOn(Entity entity);
}