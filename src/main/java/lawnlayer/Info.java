package lawnlayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Info {

    // Game setup
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static final int TOPBAR = 80;
    public static final int FPS = 60;
    public static final int ENDGAMESCREENDELAY = 3;

    // Entities info
    public static final int SPRITESIZE = 20;
    public static final int SPEED = 2;
    public static final int FPP = 3; // Frames per (red path) propagation
    public static final int MAXQUEUESIZE = 2; // Number of delayed key presses
    public static final List<Integer> SPAWNPOINT =
        Collections.unmodifiableList(Arrays.asList(0, 0));

    // Names
    public static final String CONCRETE = "Concrete";
    public static final String GRASS = "Grass";
    public static final String PATH = "Path";
    public static final String PLAYER = "Player";
    public static final String BEETLE = "Beetle";
    public static final String WORM = "Worm";

    // Restricts instantiation by other classes
    private Info() {
    }

}