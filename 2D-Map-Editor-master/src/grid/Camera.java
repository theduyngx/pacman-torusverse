package grid;

import java.awt.Rectangle;
import java.beans.PropertyChangeListener;

/**
 * A camera interface. A camera uses a grid and shows only a rectangle of it.
 * @author  Daniel "MaTachi" Jonsson
 * @version 1
 * @since   v0.0.5
 */
public interface Camera {

	/**
	 * Get the width of the camera.
	 * @return the width of the camera.
	 */
	int getWidth();
	
	/**
	 * Get the height of the camera.
	 * @return the height of the camera.
	 */
	int getHeight();
	
	/**
	 * Get the x-coordinate of the camera.
	 * @return x-coordinate of the camera.
	 */
	int getX();
	
	/**
	 * Get the y-coordinate of the camera.
	 * @return y-coordinate of the camera.
	 */
	int getY();
	
	/**
	 * Get the width of the model.
	 * @return the width of the model.
	 */
	int getModelWidth();
	
	/**
	 * Get the height of the model.
	 * @return the height of the model.
	 */
	int getModelHeight();

	/**
	 * Set the value of a tile.
	 * @param x  x-coordinate of the current view.
	 * @param y  y-coordinate of the current view.
	 * @param c  the character that should be added to the position.
	 */
	void setTile(int x, int y, char c);
	
	/**
	 * the value of a tile.
	 * @param x  x-coordinate of the current view.
	 * @param y  y-coordinate of the current view.
	 * @return   the character on the tile.
	 */
	char getTile(int x, int y);
	
	/**
	 * Returns a copy of the camera.
	 * @return A copy of the camera.
	 */
	Rectangle getCamera();
	
	/**
	 * Move the camera in a direction.
	 * @param direction the direction the camera should move one step.
	 */
	void moveCamera(int direction);
	
	/**
	 * Add a listener to the model.
	 * @param listener the listener.
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Remove a listener from the model.
	 * @param listener the listener.
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);
}
