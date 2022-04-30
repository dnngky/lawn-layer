package lawnlayer;

/**
 * A constants-only class containing the information which are used across
 * multiple classes in the 'lawnlayer' package. As its sole purpose is an
 * information container, the class along with its attributes have been
 * marked final to prevent inheritence and modifications.
 */
public final class Info {

    /**
     * The width of the map (pixels).
     */
    public static final int WIDTH = 1280;
    /**
     * The height of the map (pixels).
     */
    public static final int HEIGHT = 720;
    /**
     * The height of the top bar (pixels).
     */
    public static final int TOPBAR = 80;
    /**
     * The number of frames per second.
     */
    public static final int FPS = 60;
    /**
     * The size of a square sprite (pixels).
     */
    public static final int SPRITESIZE = 20;
    /**
     * The maximum delay interval between each power up's spawn.
     */
    public static final int SPAWNDELAY = 10;

    /**
     * An enum containing the pre-defined names of the GameObjects (seconds).
     */
    public enum Name {

        CONCRETE, GRASS, PATH, PLAYER, BEETLE, WORM, BOOST, FREEZE, UNNAMED;
    }

    // Explicitly defining a private constructor to prevent instantiation
    private Info() {
    }
    
}