package editor;
import grid.Camera;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;


/**
 * The view of the application. Based on Daniel "MaTachi" Jonsson's code.
 * @author  The Duy Nguyen
 * @author  Ramon Javier L. Felipe VI
 * @author  Jonathan Chen Jie Kong
 * @author  Daniel "MaTachi" Jonsson
 * @version 1
 * @since   v0.0.5
 */
public class View {
	// JFrame
	private JFrame frame;
	private final int width;
	private final int height;

	// Settings to the right.
	private JTextField txtWidth;
	private JTextField txtHeight;
	private JPanel right = null;
	private Border border = null;
	private JPanel palette = null;

	/**
	 * Constructs the View.
	 * @param controller The controller.
	 */
	public View(Controller controller, Camera camera, JPanel grid, List<? extends Tile> tiles,
				int width, int height) {
		this.width  = width;
		this.height = height;
		grid.setPreferredSize(new Dimension(width * Tile.TILE_WIDTH, height * Tile.TILE_HEIGHT));
		/* Create the panels */
		createBottomPanel(controller, tiles);
		createTopPanel(controller, camera, grid);
	}


	/**
	 * Create the bottom panel, which includes buttons and borders of the panel frame.
	 * @param controller the program controller
	 * @param tiles		 the list of tiles
	 */
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
		right.add(startGameButton);
	}


	/**
	 * Create the top panel for the view.
	 * @param controller program controller
	 * @param camera	 the camera
	 * @param grid		 the editor grid
	 */
	private void createTopPanel(Controller controller, Camera camera, JPanel grid) {
		// create the camera, mouse labels
		CameraInformationLabel   cameraInformationLabel = new CameraInformationLabel(camera);
		GridMouseInformationLabel mouseInformationLabel = new GridMouseInformationLabel(grid);
		JLabel lblWidth = new JLabel("Width(min:32):");
		JLabel lblHeight = new JLabel("Height(min:20):");

		// create buttons and texts
		txtWidth = new JTextField(String.valueOf(width), 3);
		txtWidth.getDocument().addDocumentListener(controller.updateSizeFields);
		txtWidth.setEnabled(false);
		txtHeight = new JTextField(String.valueOf(height), 3);
		txtHeight.getDocument().addDocumentListener(controller.updateSizeFields);
		txtHeight.setEnabled(false);
		JButton updateSize = new JButton("Reset");
		updateSize.addActionListener(controller);
		updateSize.setActionCommand("update");

		// create the top panel
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

		// create layout
		JPanel layout = new JPanel(new BorderLayout());
		JPanel test = new JPanel(new BorderLayout());
		test.add(top, BorderLayout.NORTH);
		test.add(grid, BorderLayout.WEST);
		test.add(right, BorderLayout.CENTER);
		layout.add(test, BorderLayout.NORTH);
		layout.add(palette, BorderLayout.CENTER);

		// create the frame
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Map Editor");
		frame.add(layout);
		frame.pack();
		frame.setFocusable(true);
	}

	/**
	 * Get the view's width.
	 * @return the view's width
	 */
	public int getWidth() {
		String value = txtWidth.getText();
		if (value.equals(""))
			value = String.valueOf(width);
		return Integer.parseInt(value);
	}

	/**
	 * Get the view's height.
	 * @return the view's height
	 */
	public int getHeight() {
		String value = txtHeight.getText();
		if (value.equals(""))
			value = String.valueOf(height);
		return Integer.parseInt(value);
	}

	/**
	 * Set the view's size by specifying its dimensions.
	 * @param width  the view's width
	 * @param height the view's height
	 */
	public void setSize(int width, int height){
		txtWidth.setText(String.valueOf(width));
		txtHeight.setText(String.valueOf(height));
	}

	/**
	 * Close the view (set it invisible).
	 */
	public void close() {
		frame.setVisible(false);
	}

	/**
	 * Set view visible.
	 */
	public void open() {
		frame.setVisible(true);
	}

	/**
	 * Set the frame to a different one
	 * @param frame the specified different frame
	 */
	public void setFrame(JFrame frame) {
		frame.toFront();
		frame.setVisible(true);
		frame.setEnabled(true);
		this.frame.setVisible(false);
		this.frame = frame;
	}
}
