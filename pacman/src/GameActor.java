package src;
import ch.aplu.jgamegrid.Actor;


/**
 * Game Actor abstract class extended from Actor class. It represents any actors in the game, live
 * or inanimate. As long as it is an element within the game's grid, it is considered, or can be
 * extended.
 * @see GameActor
 */
public abstract class GameActor extends Actor {
    // object's name
    private String name;

    /**
     * The game actor's constructor. Calls the constructor of Actor class. This constructor is for actors
     * who only have a single display for their sprites.
     * @param src the directory for sprite image of the inanimate object
     */
    public GameActor(String src) {
        super(src);
    }

    /**
     * Another game actor's constructor. Calls the constructor of Actor class. This constructor is for
     * actors who have multiple different sprites.
     * @param isRotatable whether the actor is rotatable; important for displaying sprite images
     * @param directory   the directory of sprite image
     * @param numSprites  number of sprite images
     */
    public GameActor(boolean isRotatable, String directory, int numSprites) {
        super(isRotatable, directory, numSprites);
    }

    /**
     * Get the item's name. Used for printing to log in game callback.
     * @return the item's name
     */
    protected String getName() {
        return name;
    }

    /**
     * Set the item's name.
     * @param name item's name
     */
    protected void setName(String name) {
        this.name = name;
    }


    /**
     * Check whether 2 actors within a game have collided with each other or not. This can be used
     * to check whether pacman has reached an item's location, meaning pacman has eaten the item,
     * or if pacman has hit a wall, or if pacman has hit a monster (game over condition).
     * @param other other actor
     * @return      boolean value indicating whether the 2 actors have collided or not
     */
    public boolean actorCollide(GameActor other) {
        return this.getLocation().equals(other.getLocation());
    }
}
