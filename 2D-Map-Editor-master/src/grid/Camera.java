package grid;

import java.awt.Rectangle;
import java.beans.PropertyChangeListener;

/**
 * A camera interface. A camera uses a grid and shows only a rectangle of it.
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 *
 */
public interface Camera {

	/**
	 * Returns the width of the camera.
	 * @return int The width of the camera.
	 */
	int getWidth();
	
	/**
	 * Returns the height of the camera.
	 * @return int The height of the camera.
	 */
	int getHeight();
	
	/**
	 * Returns the X coordiante of the camera.
	 * @return int The X coordiante of the camera.
	 */
	int getX();
	
	/**
	 * Returns the Y coordiante of the camera.
	 * @return int The Y coordiante of the camera.
	 */
	int getY();
	
	/**
	 * Returns the width of the model.
	 * @return int The width of the model.
	 */
	int getModelWidth();
	
	/**
	 * Returns the height of the model.
	 * @return int The height of the model.
	 */
	int getModelHeight();

	/**
	 * Set the value of a tile.
	 * @param x The X-coordinate of the current view.
	 * @param y The Y-coordinate of the current view.
	 * @param c The character that should be added to the position. 
	 */
	void setTile(int x, int y, char c);
	
	/**
	 * The the value of a tile.
	 * @param x The X-coordinate of the current view.
	 * @param y The Y-coordinate of the current view.
	 * @return char The character on the tile.
	 */
	char getTile(int x, int y);
	
	/**
	 * Returns a copy of the camera.
	 * @return Rectangle A copy of the camera.
	 */
	Rectangle getCamera();
	
	/**
	 * Move the camera in a direction.
	 * @param direction The direction the camera should move one step.
	 */
	void moveCamera(int direction);
	
	/**
	 * Add a listener to the model.
	 * @param listener The listener.
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Remove a listener from the model.
	 * @param listener The listener.
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);
}
