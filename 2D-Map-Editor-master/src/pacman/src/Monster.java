// Monster.java
// Used for PacMan
package pacman.src;

import ch.aplu.jgamegrid.*;
import java.awt.Color;
import java.util.*;

public class Monster extends Actor {
    private final Game game;
    private final MonsterType type;
    private final ArrayList<Location> visitedList = new ArrayList<>();
    private boolean stopMoving = false;
    private final Random randomizer = new Random(0);

    public Monster(Game game, MonsterType type) {
        super("sprites/" + type.getImageName());
        this.game = game;
        this.type = type;
    }

    public void stopMoving(int seconds) {
        this.stopMoving = true;
        Timer timer = new Timer(); // Instantiate Timer Object
        int SECOND_TO_MILLISECONDS = 1000;
        final Monster monster = this;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                monster.stopMoving = false;
            }
        }, (long) seconds * SECOND_TO_MILLISECONDS);
    }

    public void setSeed(int seed) {
        randomizer.setSeed(seed);
    }

    public void setStopMoving(boolean stopMoving) {
        this.stopMoving = stopMoving;
    }

    public void act() {
        if (stopMoving) return;
        walkApproach();
        setHorzMirror(!(getDirection() > 150) || !(getDirection() < 210));
    }

    private void walkApproach() {
        Location pacLocation = game.pacActor.getLocation();
        double oldDirection = getDirection();

        // Walking approach:
        // TX5: Determine direction to pacActor and try to move in that direction. Otherwise, random walk.
        // Troll: Random walk.
        Location.CompassDirection compassDir =
                getLocation().get4CompassDirectionTo(pacLocation);
        Location next = getLocation().getNeighbourLocation(compassDir);
        setDirection(compassDir);
        if (type == MonsterType.TX5 &&
                !isVisited(next) && canMove(next))
            setLocation(next);
        else {
            // Random walk
            int sign = randomizer.nextDouble() < 0.5 ? 1 : -1;
            setDirection(oldDirection);
            turn(sign * 90);  // Try to turn left/right
            next = getNextMoveLocation();
            if (canMove(next))
                setLocation(next);
            else {
                setDirection(oldDirection);
                next = getNextMoveLocation();
                if (canMove(next)) // Try to move forward
                    setLocation(next);
                else {
                    setDirection(oldDirection);
                    turn(-sign * 90);  // Try to turn right/left
                    next = getNextMoveLocation();
                    if (canMove(next))
                        setLocation(next);
                    else {
                        setDirection(oldDirection);
                        turn(180);  // Turn backward
                        next = getNextMoveLocation();
                        setLocation(next);
                    }
                }
            }
        }
        game.getGameCallback().monsterLocationChanged(this);
        addVisitedList(next);
    }

    public MonsterType getType() {
        return type;
    }

    private void addVisitedList(Location location) {
        visitedList.add(location);
        int listLength = 10;
        if (visitedList.size() == listLength)
            visitedList.remove(0);
    }

    private boolean isVisited(Location location) {
        for (Location loc : visitedList)
            if (loc.equals(location))
                return true;
        return false;
    }

    private boolean canMove(Location location) {
        Color c = getBackground().getColor(location);
        return !c.equals(Color.gray) && location.getX() < game.getNumHorzCells()
                && location.getX() >= 0 && location.getY() < game.getNumVertCells() && location.getY() >= 0;
    }
}
