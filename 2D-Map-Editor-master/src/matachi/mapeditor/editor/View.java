package matachi.mapeditor.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import matachi.mapeditor.grid.Camera;
import matachi.mapeditor.grid.Grid;


/**
 * The view of the application.
 * 
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 * 
 */
public class View {

	/**
	 * The JFrame.
	 */
	private JFrame frame;

	/**
	 * Settings to the right.
	 */

	private JTextField txtWidth;
	private JTextField txtHeight;
	private JPanel right = null;
	private Border border = null;
	private JPanel palette = null;

	/**
	 * Constructs the View.
	 * 
	 * @param controller
	 *            The controller.
	 */
	public View(Controller controller, Camera camera, JPanel grid, List<? extends Tile> tiles) {

		grid.setPreferredSize(new Dimension(Grid.GRID_WIDTH * Tile.TILE_WIDTH,
				Grid.GRID_HEIGHT * Tile.TILE_HEIGHT));
		/* Create the panels */
		createBottomPanel(controller, tiles);
		createTopPanel(controller, camera, grid);
	}

	private void createBottomPanel(Controller controller, List<? extends Tile> tiles) {
		palette = new JPanel(new FlowLayout());
		for (Tile t : tiles) {
			JButton button = new JButton();
			button.setPreferredSize(new Dimension(Tile.TILE_WIDTH, Tile.TILE_HEIGHT));
			button.setIcon(t.getIcon());
			button.addActionListener(controller);
			button.setActionCommand(Character.toString(t.getCharacter()));
			palette.add(button);
		}

		// save
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(controller);
		saveButton.setActionCommand("save");

		// load
		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(controller);
		loadButton.setActionCommand("load");

		// start game
		JButton startGameButton = new JButton("Start Game");
		startGameButton.addActionListener(controller);
		startGameButton.setActionCommand("start_game");

		// right
		right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

		// border
		border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		right.setBorder(border);
		right.add(saveButton);
		right.add(loadButton);
	}

	private void createTopPanel(Controller controller, Camera camera, JPanel grid) {
		CameraInformationLabel cameraInformationLabel = new CameraInformationLabel(camera);
		GridMouseInformationLabel mouseInformationLabel = new GridMouseInformationLabel(grid);

		JLabel lblWidth = new JLabel("Width(min:32):");
		JLabel lblHeight = new JLabel("Height(min:20):");

		txtWidth = new JTextField(Controller.MAP_WIDTH + "", 3);
		txtWidth.getDocument().addDocumentListener(controller.updateSizeFields);
		txtWidth.setEnabled(false);
		txtHeight = new JTextField(Controller.MAP_HEIGHT + "", 3);
		txtHeight.getDocument().addDocumentListener(controller.updateSizeFields);
		txtHeight.setEnabled(false);
		JButton updateSize = new JButton("Reset");
		updateSize.addActionListener(controller);
		updateSize.setActionCommand("update");

		JPanel top = new JPanel();
		top.setLayout(new FlowLayout(FlowLayout.LEFT));
		top.setBorder(border);
		top.add(cameraInformationLabel);
		top.add(mouseInformationLabel);
		top.add(lblWidth);
		top.add(txtWidth);
		top.add(lblHeight);
		top.add(txtHeight);
		top.add(updateSize);

		JPanel layout = new JPanel(new BorderLayout());
		JPanel test = new JPanel(new BorderLayout());
		test.add(top, BorderLayout.NORTH);
		test.add(grid, BorderLayout.WEST);
		test.add(right, BorderLayout.CENTER);
		layout.add(test, BorderLayout.NORTH);
		layout.add(palette, BorderLayout.CENTER);

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Map Editor");
		frame.add(layout);
		frame.pack();
		frame.setVisible(true);
	}

	public int getWidth() {
		String value = txtWidth.getText();
		if (value.equals("")) {
			value = Controller.MAP_WIDTH + "";
		}
		return Integer.parseInt(value);
	}

	public int getHeight() {
		String value = txtHeight.getText();
		if (value.equals("")) {
			value = Controller.MAP_WIDTH + "";
		}
		return Integer.parseInt(value);
	}
	
	public void setSize(int width, int height){
		txtWidth.setText(String.valueOf(width));
		txtHeight.setText(String.valueOf(height));
	}

	public void close() {
		frame.setVisible(false);
	}
}
