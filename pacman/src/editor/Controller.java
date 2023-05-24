package editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import grid.*;
import game.Game;
import game.LevelChecker;
import game.utility.GameCallback;


/**
 * Controller of the application. Based on Daniel "MaTachi" Jonsson's code.
 * @author  The Duy Nguyen
 * @author  Ramon Javier L. Felipe VI
 * @author  Jonathan Chen Jie Kong
 * @author  Daniel "MaTachi" Jonsson
 * @version 1
 * @since   v0.0.5
 */
public class Controller implements ActionListener, GUIInformation {
	// width and height of the map grid editor
	public static final int MAP_WIDTH = 20;
	public static final int MAP_HEIGHT = 11;

	// model, tile, camera
	private Grid model;
	private Tile selectedTile;
	private Camera camera;
	private List<Tile> tiles;

	// the grid and view of the editor
	private GridView grid;
	private View view;
	private int gridWith = MAP_WIDTH;
	private int gridHeight = MAP_HEIGHT;

	// the game itself
	private final Game game;
	private final String[] levels;
	private int levelIndex;
	private final LevelChecker levelChecker;

	/**
	 * Controller constructor.
	 */
	public Controller(Game game, String[] levels, GameCallback gameCallback) {
		this.tiles  = TileManager.getTilesFromFolder("data/");
		this.model  = new GridModel(MAP_WIDTH, MAP_HEIGHT, tiles.get(0).getCharacter());
		this.camera = new GridCamera(model, Grid.GRID_WIDTH, Grid.GRID_HEIGHT);
		this.grid   = new GridView(this, camera, tiles); // Every tile is 30x30 pixels
		this.view   = new View(this, camera, grid, tiles);
		this.game   = game;
		this.levels = levels;
		levelIndex  = 0;
		assert levels.length > 0;
		this.game.reset(levels[levelIndex]);

		/// NOTE: this part of level checking should be within the level checking itself
		/// then the unreachable must be printed and moved to object manager for log update accordingly
		levelChecker = new LevelChecker(gameCallback);
		levelChecker.setXmlFile(levels[levelIndex]);
		boolean setStart = levelChecker.checkLevel(this.game);
		this.game.setStart(setStart);

		// start game immediately by manually trigger an action
		if (this.game.getStart())
			actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
	}


	/**
	 * Listener awaiting actions to be performed.
	 * @param e the event to be processed
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		/*
		 * Swing Worker thread to run the game in the background. It should be noted that the game will
		 * only actually run when it really needs to.
		 */
		SwingWorker<Void, Void> gameWorker = new SwingWorker<>() {
			@Override
			protected Void doInBackground() {
				game.run();
				return null;
			}

			/**
			 * When the game has finished running. This is the logic where level transitioning and
			 * level checking occurs.
			 */
			@Override
			public void done() {
				if (! game.getStart()) return;
				game.setStart(false);

				// check game's status (win or lose)
				boolean update = game.getStatus() == Game.STATUS.LOSE;
				if (! update) {
					levelIndex++;
					if (levelIndex >= levels.length) {
						// WON THE GAME
						System.exit(0);
					}
				}

				// reset the game and update the frame
				game.reset(levels[levelIndex]);
				boolean setStart = levelChecker.checkLevel(game);
				game.setStart(setStart);
				if (update || !setStart) loadCurrGrid();
				actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
			}
		};

		// execute threads
		gameWorker.execute();
		for (Tile t : tiles) {
			if (e.getActionCommand().equals(Character.toString(t.getCharacter()))) {
				selectedTile = t;
				break;
			}
		}

		// check for the action performed
		if 		(e.getActionCommand().equals("save"		 )) saveFile();
		else if (e.getActionCommand().equals("load"		 )) loadFile();
		else if (e.getActionCommand().equals("update"	 )) loadCurrGrid();
		else if (e.getActionCommand().equals("start_game") || game.getStart()) {
			boolean setStart = levelChecker.checkLevel(game);
			game.setStart(setStart);
			if (setStart) view.setFrame(game.getFrame());
		}
	}

	/**
	 * Update the game editor grid (for now this works as a reset).
	 * @param width	 the grid's width
	 * @param height the grid's height
	 */
	public void resetGrid(int width, int height) {
		view.close();
		this.tiles 	= TileManager.getTilesFromFolder("data/");
		this.model 	= new GridModel(width, height, tiles.get(0).getCharacter());
		this.camera = new GridCamera(model, Grid.GRID_WIDTH, Grid.GRID_HEIGHT);
		this.grid 	= new GridView(this, camera, tiles); // Every tile is 30x30 pixels
		this.view 	= new View(this, camera, grid, tiles);
		view.setSize(width, height);
	}


	/**
	 * Document listener registering changes made to a text file document. This is used to
	 * update the loaded file (perhaps).
	 */
	DocumentListener updateSizeFields = new DocumentListener() {
		public void changedUpdate(DocumentEvent e) {
			gridWith   = view.getWidth();
			gridHeight = view.getHeight();
		}

		public void removeUpdate(DocumentEvent e) {
			gridWith   = view.getWidth();
			gridHeight = view.getHeight();
		}

		public void insertUpdate(DocumentEvent e) {
			gridWith   = view.getWidth();
			gridHeight = view.getHeight();
		}
	};


	/**
	 * Method triggered when save file action is performed. This is to save an editor grid to local
	 * user file.
	 */
	private void saveFile() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"xml files", "xml");
		chooser.setFileFilter(filter);
		File workingDirectory = new File(System.getProperty("user.dir")+"\\maps");
		chooser.setCurrentDirectory(workingDirectory);

		int returnVal = chooser.showSaveDialog(null);
		try {
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Element level = new Element("level");
				Document doc = new Document(level);
				doc.setRootElement(level);

				Element size = new Element("size");
				int height = model.getHeight();
				int width = model.getWidth();
				size.addContent(new Element("width").setText(String.valueOf(width)));
				size.addContent(new Element("height").setText(String.valueOf(height)));
				doc.getRootElement().addContent(size);

				for (int y = 0; y < height; y++) {
					Element row = new Element("row");
					for (int x = 0; x < width; x++) {
						char tileChar = model.getTile(x,y);
						String type = switch(tileChar) {
							case 'b' -> "WallTile";
							case 'c' -> "PillTile";
							case 'd' -> "GoldTile";
							case 'e' -> "IceTile";
							case 'f' -> "PacTile";
							case 'g' -> "TrollTile";
							case 'h' -> "TX5Tile";
							case 'i' -> "PortalWhiteTile";
							case 'j' -> "PortalYellowTile";
							case 'k' -> "PortalDarkGoldTile";
							case 'l' -> "PortalDarkGrayTile";
							default  -> "PathTile";
						};
						Element e = new Element("cell");
						row.addContent(e.setText(type));
					}
					doc.getRootElement().addContent(row);
				}
				XMLOutputter xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput.output(doc, new FileWriter(chooser.getSelectedFile()));
			}
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null, "Invalid file!", "error",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Load the specified file to grid.
	 */
	public void loadSpecificFile(File selectedFile) {
		SAXBuilder builder = new SAXBuilder();
		if (selectedFile.canRead() && selectedFile.exists()) {
			try {
				Document document;
				document = builder.build(selectedFile);
				Element rootNode = document.getRootElement();

				List sizeList = rootNode.getChildren("size");
				Element sizeElem = (Element) sizeList.get(0);
				gridHeight = Integer.parseInt(sizeElem.getChildText("height"));
				gridWith = Integer.parseInt(sizeElem.getChildText("width"));
				resetGrid(gridWith, gridHeight);

				List rows = rootNode.getChildren("row");
				for (int y = 0; y < rows.size(); y++) {
					Element cellsElem = (Element) rows.get(y);
					List cells = cellsElem.getChildren("cell");

					for (int x = 0; x < cells.size(); x++) {
						Element cell = (Element) cells.get(x);
						String cellValue = cell.getText();
						char tileNr = switch (cellValue) {
							case "PathTile" -> 'a';
							case "WallTile" -> 'b';
							case "PillTile" -> 'c';
							case "GoldTile" -> 'd';
							case "IceTile" -> 'e';
							case "PacTile" -> 'f';
							case "TrollTile" -> 'g';
							case "TX5Tile" -> 'h';
							case "PortalWhiteTile" -> 'i';
							case "PortalYellowTile" -> 'j';
							case "PortalDarkGoldTile" -> 'k';
							case "PortalDarkGrayTile" -> 'l';
							default -> '0';
						};
						model.setTile(x, y, tileNr);
					}
				}
				grid.redrawGrid();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Load the current level's grid.
	 */
	public void loadCurrGrid() {
		File selectedFile = new File(levels[levelIndex]);
		loadSpecificFile(selectedFile);
	}


	/**
	 * Method triggered when save load file action is performed. This is to load an editor grid from
	 * local user file.
	 */
	public void loadFile() {
		JFileChooser chooser  = new JFileChooser();
		File workingDirectory = new File(System.getProperty("user.dir") + "\\maps");
		File selectedFile;
		chooser.setCurrentDirectory(workingDirectory);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			selectedFile = chooser.getSelectedFile();
			loadSpecificFile(selectedFile);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tile getSelectedTile() {
		return selectedTile;
	}
}
