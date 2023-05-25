package pacman;


/**
 * Abstract Item class extended from InanimateActor for any actors in the game that are inanimate objects,
 * but specifically only those that can be acquired by PacMan. It will make use of the object manager to
 * handle its locations, which extends to whether or not a live actor has 'collided' with its location or
 * not. In the case of pacman, that should imply the item, if not obstacles, have been acquired.
 * @see InanimateActor
 * @see ObjectManager
 */
public abstract class Item extends InanimateActor {
    // constant radius value when drawn
    public static final int RADIUS = 5 * Game.STRETCH_RATE;
    // the score that would be acquired if eaten by PacMan
    private int score;

    /**
     * Item constructor. Calls the constructor of InanimateActor abstract parent class.
     * @param src the sprite image directory
     */
    public Item(String src) {
        super(src);
    }

    /**
     * Get the score acquired by eating the item.
     * @return acquired score
     */
    public int getScore() {
        return score;
    }

    /**
     * Deep copy the item.
     * @return the copied item
     */
    public abstract Item deepCopy();

    /**
     * Set the score that would be acquired if item were eaten.
     * @param score set score
     */
    protected void setScore(int score) {
        this.score = score;
    }

    /**
     * Remove item; used when item is eaten by PacMan.
     * @param manager object manager
     */
    protected void removeItem(ObjectManager manager) {
        HashLocation hashLocation = new HashLocation(getLocation());
        manager.getItems().remove(hashLocation);
        removeSelf();
    }

    /**
     * Abstract method to signal the object manager for changes that acquiring the item makes.
     * @param manager the object manager
     */
    protected abstract void signalManager(ObjectManager manager);
}