package editor;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;

import javax.swing.JLabel;

import grid.Camera;
import grid.Grid;

/**
 * A label that shows information about a Camera.
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.6
 *
 */
public class CameraInformationLabel extends JLabel implements PropertyChangeListener {

	@Serial
	private static final long serialVersionUID = -6772431080845388524L;

	private final Camera camera;
	
	/**
	 * A label that shows information about a Camera.
	 * @param camera The camera that the label should get data from.
	 */
	public CameraInformationLabel(Camera camera) {
		this.camera = camera;
		camera.addPropertyChangeListener(this);

		this.setPreferredSize(new Dimension(220, 15));
		this.setText("Showing: 0 - " + Grid.GRID_WIDTH + "/" + camera.getModelWidth() +
				", 0 - " + Grid.GRID_HEIGHT + "/" + camera.getModelHeight());

	}

	/**
	 * If the camera has moved, update the label.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("movedCamera")) {
			updateCameraInformation();
		}
	}
	
	/**
	 * Update the label's text.
	 */
	private void updateCameraInformation() {
		this.setText("Showing: " + camera.getX() + " - " + (camera.getX() + camera.getWidth()) + "/" + camera.getModelWidth()
				+ ", " + camera.getY() + " - " + (camera.getY() + camera.getHeight()) + "/" + camera.getModelHeight());
	}
}
