package lawnlayer;

/**
 * An interface for all power ups in the game.
 */
public abstract interface PowerUp {

    /**
     * Checks whether the power up should be deactivated on the
     * current frame.
     * 
     * @param frameCount - the current frame
     * @return true if the power up should be deactivated
     */
    public boolean isTimeToDeactivate(int frameCount);

    /**
     * Checks whether the power up should spawn on the current frame.
     * 
     * @param frameCount - the current frame
     * @return true if the power up should spawn
     */
    public boolean isTimeToSpawn(int frameCount);

    /**
     * Checks whether the power up should despawn on the current frame.
     * 
     * @param frameCount - the current frame
     * @return true if the power up should despawn
     */
    public boolean isTimeToDespawn(int frameCount);

    /**
     * Spawns the power up randomly in the space not yet filled by grass.
     * 
     * @param unfilledTiles - the TileList of unfilled tile spaces
     * @param frameCount - the current frame
     */
    public void spawn(TileList unfilledTiles, int frameCount);

    /**
     * Despawns the power up
     * 
     * @param frameCount - the current frame
     */
    public void despawn(int frameCount);

    /**
     * Activates the power up on the specified object, given it is not
     * overlapping a grass tile.
     * 
     * @param object - the object to be activated on
     * @param overlappedTile - the Tile the object is overlapping
     * @param frameCount - the current frame
     */
    public void activateOn(GameObject object, Tile overlappedTile,
        int frameCount);

    /**
     * Deactivates the power up on the specified object.
     * 
     * @param object - the object to be deactivated on
     */
    public void deactivateOn(GameObject object);
}
