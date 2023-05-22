package pacman;


import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;

/**
 * Inanimate Actor abstract class extended from Game Actor class. It represents any actors in the game
 * that are inanimate objects. This can be extended to obstacles such as walls, or later on, if ever,
 * dynamic obstacles that may or may not change the state of the game.
 * @see GameActor
 */
public abstract class InanimateActor extends GameActor {
    // character representing specific block
    private static final char WALL_CHAR = 'x';
    private static final char SPACE_CHAR = ' ';
    private static final char PILL_CHAR = '.';
    private static final char GOLD_CHAR = 'g';
    private static final char ICE_CHAR = 'i';
    private static final char ERROR_CHAR = '\0';


    /**
     * Enumerated block, or inanimate objects, type. This includes
     * <ul>
     *     <li>WALL  - obstructed block which cannot be bypassed
     *     <li>PILL  - the pill item required to be eaten by pacman to win
     *     <li>SPACE - the empty space
     *     <li>GOLD  - the gold piece required to be eaten, but also aggravates monsters
     *     <li>ICE   - the ice piece not required to be eaten, but freezes monsters
     *     <li>ERROR - error block (nonexistent)
     * </ul>
     */
    public enum BlockType {
        WALL(WALL_CHAR),
        PILL(PILL_CHAR),
        SPACE(SPACE_CHAR),
        GOLD(GOLD_CHAR),
        ICE(ICE_CHAR),
        ERROR(ERROR_CHAR);
        public final char BLOCK_CHAR;
        BlockType(char BLOCK_CHAR) {
            this.BLOCK_CHAR = BLOCK_CHAR;
        }
    }


    /**
     * Inanimate object constructor. Calls the constructor of GameActor class.
     * @param src the directory for sprite image of the inanimate object
     */
    public InanimateActor(String src) {
        super(src);
    }


    /**
     * Check if an item is at PacMan's position, meaning PacMan has obtained item in question.
     * It should be noted that this method is used purely for assertion before executing the signal
     * to manager method.
     * @param manager object manager
     * @return        whether PacMan has eaten the item
     */
    public boolean matchPacmanLocation(ObjectManager manager) {
        // assert that player is in fact at the location of item
        int xItem = this.getX();
        int yItem = this.getY();
        int xPac  = manager.getPacActor().getX();
        int yPac  = manager.getPacActor().getY();
        return (xItem == xPac && yItem == yPac);
    }

    /**
     * Abstract method to put itself to the game.
     * @param bg        background of game grid
     * @param game      the game
     * @param location  actor's location
     * @see             GGBackground
     * @see             Game
     * @see             Location
     */
    protected abstract void putActor(GGBackground bg, Game game, Location location);
}
