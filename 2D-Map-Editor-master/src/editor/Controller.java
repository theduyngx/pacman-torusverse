package editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import grid.Camera;
import grid.Grid;
import grid.GridCamera;
import grid.GridModel;
import grid.GridView;
import pacman.src.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import pacman.src.utility.GameCallback;
import pacman.src.utility.PropertiesLoader;
import pacman.src.Game;

/**
 * Controller of the application.
 * 
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 * 
 */
public class Controller implements ActionListener, GUIInformation {
	public static final int MAP_WIDTH = 20;
	public static final int MAP_HEIGHT = 11;

	/**
	 * The model of the map editor.
	 */
	private Grid model;

	private Tile selectedTile;
	private Camera camera;

	private List<Tile> tiles;

	private GridView grid;
	private View view;
	private int gridWith = MAP_WIDTH;
	private int gridHeight = MAP_HEIGHT;

	/**
	 * Construct the controller.
	 */
	public Controller() {
		this.tiles  = TileManager.getTilesFromFolder("data/");
		this.model  = new GridModel(MAP_WIDTH, MAP_HEIGHT, tiles.get(0).getCharacter());
		this.camera = new GridCamera(model, Grid.GRID_WIDTH, Grid.GRID_HEIGHT);
		this.grid   = new GridView(this, camera, tiles); // Every tile is 30x30 pixels
		this.view   = new View(this, camera, grid, tiles);
	}

	/**
	 * Different commands that comes from the view.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		for (Tile t : tiles) {
			if (e.getActionCommand().equals(
					Character.toString(t.getCharacter()))) {
				selectedTile = t;
				break;
			}
		}
		if (e.getActionCommand().equals("save")) {
			saveFile();
		} else if (e.getActionCommand().equals("load")) {
			loadFile();
		} else if (e.getActionCommand().equals("update")) {
			updateGrid(gridWith, gridHeight);
		} else if (e.getActionCommand().equals("start_game")) {
			String propertiesPath = "src/pacman/properties/test2.properties";
			final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
			GameCallback gameCallback = new GameCallback();
			assert properties != null;
			new Game(gameCallback, properties);
		}
	}

	public void updateGrid(int width, int height) {
		view.close();
		this.tiles 	= TileManager.getTilesFromFolder("data/");
		this.model 	= new GridModel(width, height, tiles.get(0).getCharacter());
		this.camera = new GridCamera(model, Grid.GRID_WIDTH, Grid.GRID_HEIGHT);
		this.grid 	= new GridView(this, camera, tiles); // Every tile is 30x30 pixels
		this.view 	= new View(this, camera, grid, tiles);
		view.setSize(width, height);
	}

	DocumentListener updateSizeFields = new DocumentListener() {

		public void changedUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}

		public void removeUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}

		public void insertUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}
	};

	private void saveFile() {

		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"xml files", "xml");
		chooser.setFileFilter(filter);
		File workingDirectory = new File(System.getProperty("user.dir"));
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

	public void loadFile() {
		SAXBuilder builder = new SAXBuilder();
		try {
			JFileChooser chooser = new JFileChooser();
			File selectedFile;
			File workingDirectory = new File(System.getProperty("user.dir"));
			chooser.setCurrentDirectory(workingDirectory);

			int returnVal = chooser.showOpenDialog(null);
			Document document;
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				selectedFile = chooser.getSelectedFile();
				if (selectedFile.canRead() && selectedFile.exists()) {
					document = builder.build(selectedFile);

					Element rootNode = document.getRootElement();

					List sizeList = rootNode.getChildren("size");
					Element sizeElem = (Element) sizeList.get(0);
					int height = Integer.parseInt(sizeElem
							.getChildText("height"));
					int width = Integer
							.parseInt(sizeElem.getChildText("width"));
					updateGrid(width, height);

					List rows = rootNode.getChildren("row");
					for (int y = 0; y < rows.size(); y++) {
						Element cellsElem = (Element) rows.get(y);
						List cells = cellsElem.getChildren("cell");

						for (int x = 0; x < cells.size(); x++) {
							Element cell = (Element) cells.get(x);
							String cellValue = cell.getText();

							char tileNr = switch (cellValue) {
								case "PathTile" 		  -> 'a';
								case "WallTile" 		  -> 'b';
								case "PillTile" 		  -> 'c';
								case "GoldTile" 		  -> 'd';
								case "IceTile" 			  -> 'e';
								case "PacTile" 			  -> 'f';
								case "TrollTile" 		  -> 'g';
								case "TX5Tile" 			  -> 'h';
								case "PortalWhiteTile"    -> 'i';
								case "PortalYellowTile"   -> 'j';
								case "PortalDarkGoldTile" -> 'k';
								case "PortalDarkGrayTile" -> 'l';
								default 				  -> '0';
							};
							model.setTile(x, y, tileNr);
						}
					}
					grid.redrawGrid();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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