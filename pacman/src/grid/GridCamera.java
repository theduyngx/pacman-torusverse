package grid;

import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * An implementation of the interface Camera. It has a Grid and shows only a
 * rectangle of it.
 * @author  Daniel "MaTachi" Jonsson
 * @version 1
 * @since   v0.0.5
 */
public class GridCamera implements Camera {
	public static final int NORTH = 0;
	public static final int EAST  = 1;
	public static final int SOUTH = 2;
	public static final int WEST  = 3;
	
	private final Grid model;
	private final Rectangle camera;

	/**
	 * Announce changes.
	 */
	private final PropertyChangeSupport changeSupport;

	/**
	 * Constructs the camera with a given model.
	 * @param model The model.
	 * @param cameraWidth The width of the camera.
	 * @param cameraHeight The height of the camera.
	 */
	public GridCamera(Grid model, int cameraWidth, int cameraHeight) {
		this.changeSupport = new PropertyChangeSupport(this);
		this.model = model;
		this.camera = new Rectangle(0, 0, cameraWidth, cameraHeight);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getWidth() {
		return camera.width;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getHeight() {
		return camera.height;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getX() {
		return camera.x;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getY() {
		return camera.y;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getModelWidth() {
		return model.getWidth();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getModelHeight() {
		return model.getHeight();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTile(int x, int y, char c) {
		model.setTile(camera.x + x, camera.y + y, c);
		firePropertyChange(new Point(x, y));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public char getTile(int x, int y) {
		return model.getTile(camera.x + x, camera.y + y);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Rectangle getCamera() {
		return new Rectangle(camera);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void moveCamera(int direction) {
		if (direction == GridCamera.NORTH) {
			if (camera.y > 0)
				camera.setLocation(camera.x, --camera.y);
		}
		else if (direction == GridCamera.EAST) {
			if (camera.x + camera.width < model.getWidth())
				camera.setLocation(++camera.x, camera.y);
		}
		else if (direction == GridCamera.SOUTH) {
			if (camera.y + camera.height < model.getHeight())
				camera.setLocation(camera.x, ++camera.y);
		}
		else if (direction == GridCamera.WEST) {
			if (camera.x > 0)
				camera.setLocation(--camera.x, camera.y);
		}
		firePropertyChange();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}
	
	private void firePropertyChange() {
		changeSupport.firePropertyChange("movedCamera", false, true);
	}
	
	private void firePropertyChange(Object newValue) {
		changeSupport.firePropertyChange("changedTile", false, newValue);
	}
}
