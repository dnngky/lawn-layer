package lawnlayer;

/**
 * An enum indicating the movement of Enemy types.
 */
public enum Movement {
    
    UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT, STATIONARY;

    /**
     * Flips the current movement on the vertical axis.
     * 
     * @return the vertically flipped movement.
     */
    public Movement flipVertically() {

        switch (this) {

            case UPLEFT:
                return DOWNLEFT;
            case UPRIGHT:
                return DOWNRIGHT;
            case DOWNLEFT:
                return UPLEFT;
            case DOWNRIGHT:
                return UPRIGHT;
            default:
                return null;
        }
    }

    /**
     * Flips the current movement on the horizontal axis.
     * 
     * @return the horizontally flipped movement.
     */
    public Movement flipHorizontally() {

        switch (this) {

            case UPLEFT:
                return UPRIGHT;
            case UPRIGHT:
                return UPLEFT;
            case DOWNLEFT:
                return DOWNRIGHT;
            case DOWNRIGHT:
                return DOWNLEFT;
            default:
                return null;
        }
    }

}
