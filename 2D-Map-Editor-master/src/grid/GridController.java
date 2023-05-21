package grid;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import editor.GUIInformation;
import editor.Tile;


/**
 * Takes inputs from the GridView and communicated with a Camera.
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 *
 */
public class GridController implements MouseListener, MouseMotionListener, ActionListener, KeyListener {

	/**
	 * The camera of the grid.
	 */
	private final Camera camera;
	
	/**
	 * The last tile that the user clicked.
	 */
	private int lastClickedTileX;
	private int lastClickedTileY;
	
	/**
	 * The class that provides with GUI information.
	 */
	private final GUIInformation guiInformation;

	/**
	 * The GridController which the GridView needs.
	 * @param camera The camera which the GridController will command.
	 * @param guiInformation The class that the GridController will query for
	 * information.
	 */
	public GridController(Camera camera, GUIInformation guiInformation) {
		this.camera = camera;
		this.guiInformation = guiInformation;
	}

	@Override
	public void mouseClicked(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }
	
	/**
	 * If a mouse button is clicked.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		lastClickedTileX = e.getX() / Tile.TILE_WIDTH;
		lastClickedTileY = e.getY() / Tile.TILE_HEIGHT;
		if (ifLeftMouseButtonPressed(e)) {
			updateTile(lastClickedTileX, lastClickedTileY);
		}
	}

	private boolean ifLeftMouseButtonPressed(MouseEvent e) {
		return (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK;
	}
	
	private boolean ifRightMouseButtonPressed(MouseEvent e) {
		return (e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) == MouseEvent.BUTTON3_DOWN_MASK;
	}
	
	private void updateTile(int xCor, int yCor) {
		xCor = Math.max(0, Math.min(xCor, Grid.GRID_WIDTH-1));
		yCor = Math.max(0, Math.min(yCor, Grid.GRID_HEIGHT-1));
		if (guiInformation.getSelectedTile() != null) {
			camera.setTile(xCor, yCor, guiInformation.getSelectedTile().getCharacter());
		}
	}

	private void updateCamera(int newTileX, int newTileY) {
		if (newTileX != lastClickedTileX) {
			int camDirection = (newTileX > lastClickedTileX) ? GridCamera.WEST : GridCamera.EAST;
			camera.moveCamera(camDirection);
		}
		if (newTileY != lastClickedTileY) {
			int camDirection = (newTileY > lastClickedTileY) ? GridCamera.NORTH : GridCamera.SOUTH;
			camera.moveCamera(camDirection);
		}
		lastClickedTileX = newTileX;
		lastClickedTileY = newTileY;
	}

	@Override
	public void mouseReleased(MouseEvent e) { }

	/**
	 * If the user keeps the mouse button pressed it will keep drawing if it is
	 * in drawing mode, which it is by default.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (ifRightMouseButtonPressed(e)) {
			int newTileX = e.getX() / Tile.TILE_WIDTH;
			int newTileY = e.getY() / Tile.TILE_HEIGHT;
			updateCamera(newTileX, newTileY);
		}
		this.mousePressed(e);
	}

	/**
	 * If the mouse cursor in moved.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) { }

	/**
	 * If a key is pressed down.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			camera.moveCamera(GridCamera.NORTH);
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			camera.moveCamera(GridCamera.EAST);
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			camera.moveCamera(GridCamera.SOUTH);
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			camera.moveCamera(GridCamera.WEST);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) { }
	
	@Override
	public void keyTyped(KeyEvent e) { }
}
