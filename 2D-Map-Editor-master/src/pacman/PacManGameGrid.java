package pacman;
import ch.aplu.jgamegrid.*;


/**
 * PacManGameGrid class representing the game grid, which primarily deals with visualizing inanimate objects
 * on the grid via enumerated identification.
 */
public class PacManGameGrid {
    // grid constants
    private final int X_LEFT;
    private final int Y_TOP;
    private final int X_RIGHT;
    private final int Y_BOTTOM;
    // overestimated maximal distance between any 2 given locations
    public final int INF;

    // number of horizontal cells of the grid
    private final int numHorizontalCells;
    // number of vertical cells of the grid
    private final int numVerticalCells;
    // the grid data structure, represented by a 2-dimensional array of blocks
    private final InanimateActor.BlockType[][] mazeArray;


    /**
     * PacManGameGrid constructor.
     * @param numHorizontalCells the number of horizontal cells of the grid
     * @param numVerticalCells   the number of vertical cells of the grid
     */
    public PacManGameGrid(int numHorizontalCells, int numVerticalCells) {
        this.numHorizontalCells = numHorizontalCells;
        this.numVerticalCells = numVerticalCells;
        this.INF = numHorizontalCells + numVerticalCells;

        // Setup grid border
        X_LEFT   = 0;
        Y_TOP    = 0;
        X_RIGHT  = numHorizontalCells;
        Y_BOTTOM = numVerticalCells;
        mazeArray = new InanimateActor.BlockType[numVerticalCells][numHorizontalCells];
        String maze =

                        "xxxxxxxxxxxxxxxxxxxx" + // 0
                        "xi.           x    x" + // 1
                        "xxxxxx xxxxxx x xx x" + // 2
                        "x x              x x" + // 3
                        "x.x xx xx  xx xx x x" + // 4
                        "x      x    x      x" + // 5
                        "x x xx xxxxxx xx x.x" + // 6
                        "x x              x.x" + // 7
                        "xixx x xxxxxx x.xx.x" + // 8
                        "x    x        x....x" + // 9
                        "xxxxxxxxxxxxxxxxxxxx";  // 10

        // Copy structure into integer array
        for (int i = 0; i < numVerticalCells; i++)
            for (int k = 0; k < numHorizontalCells; k++) {
                InanimateActor.BlockType value = toType(maze.charAt(numHorizontalCells * i + k));
                mazeArray[i][k] = value;
            }
    }

    /**
     * Get the block type from a specific cell in a specified location.
     * @param location the specified location
     * @return         the block type in said location
     * @see            Location
     */
    public InanimateActor.BlockType getCell(Location location) {
        return mazeArray[location.y][location.x];
    }

    /**
     * Override existing mazeArray cell with value given in .properties
     * @param location the specified location
     * @param value    the value to be replaced with
     * @see            Location
     * @see            InanimateActor
     */
    protected void setCell(Location location, InanimateActor.BlockType value) {
        mazeArray[location.x][location.y] = value;
    }

    /**
     * Get the number of horizontal cells of the grid.
     * @return the number of horizontal cells
     */
    public int getNumHorizontalCells() {
        return numHorizontalCells;
    }

    /**
     * Get the number of vertical cells of the grid.
     * @return the number of vertical cells
     */
    public int getNumVerticalCells() {
        return numVerticalCells;
    }

    /**
     * Get the grid itself, represented by a 2-dimensional array.
     * @return the grid
     * @see    InanimateActor
     */
    protected InanimateActor.BlockType[][] getMazeArray() {
        return mazeArray;
    }

    /**
     * Get leftmost x-coordinate of the grid; used for border checking when initiating movement.
     * @return leftmost x-coordinate
     */
    public int getXLeft() {
        return X_LEFT;
    }

    /**
     * Get topmost y-coordinate of the grid; used for border checking when initiating movement.
     * @return leftmost y-coordinate
     */
    public int getYTop() {
        return Y_TOP;
    }

    /**
     * Get rightmost x-coordinate of the grid; used for border checking when initiating movement.
     * @return rightmost x-coordinate
     */
    public int getXRight() {
        return X_RIGHT;
    }

    /**
     * Get bottommost y-coordinate of the grid; used for border checking when initiating movement.
     * @return bottommost y-coordinate
     */
    public int getYBottom() {
        return Y_BOTTOM;
    }

    /**
     * Convert a maze string where each cell is represented by a character, to its corresponding block type.
     * @param c cell character
     * @return  its block type
     * @see     InanimateActor
     */
    private InanimateActor.BlockType toType(char c) {
        if (c == InanimateActor.BlockType.WALL.BLOCK_CHAR ) return InanimateActor.BlockType.WALL ;
        if (c == InanimateActor.BlockType.PILL.BLOCK_CHAR ) return InanimateActor.BlockType.PILL ;
        if (c == InanimateActor.BlockType.GOLD.BLOCK_CHAR ) return InanimateActor.BlockType.GOLD ;
        if (c == InanimateActor.BlockType.ICE.BLOCK_CHAR  ) return InanimateActor.BlockType.ICE  ;
        if (c == InanimateActor.BlockType.SPACE.BLOCK_CHAR) return InanimateActor.BlockType.SPACE;
        else return InanimateActor.BlockType.ERROR;
    }
}
