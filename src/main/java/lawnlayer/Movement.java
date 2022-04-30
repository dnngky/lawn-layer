package lawnlayer;

public enum Movement {
    
    UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT, STATIONARY;

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
