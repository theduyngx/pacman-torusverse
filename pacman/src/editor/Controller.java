package editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
	private final GridFileManager gridManager;

	// the game itself
	private final Game game;
	private final ArrayList<String> levels;
	private int levelIndex;
	private final LevelChecker levelChecker;

	public enum GameType {
		IS_FOLDER,
		IS_FILE,
		IS_NULL
	}

	/**
	 * Controller constructor.
	 */
	public Controller(Game game, GameType gameType, ArrayList<String> levels, GameCallback gameCallback) {
		this.tiles  = TileManager.getTilesFromFolder("data/");
		this.model  = new GridModel(MAP_WIDTH, MAP_HEIGHT, tiles.get(0).getCharacter());
		this.camera = new GridCamera(model, Grid.GRID_WIDTH, Grid.GRID_HEIGHT);
		this.grid   = new GridView(this, camera, tiles); // Every tile is 30x30 pixels
		this.view   = new View(this, camera, grid, tiles);
		this.game   = game;
		this.levels = levels;
		gridManager = new GridFileManager(this);
		levelIndex  = 0;
		assert levels.size() > 0;
		this.game.reset(levels.get(levelIndex));

		/// NOTE: this part of level checking should be within the level checking itself
		/// then the unreachable must be printed and moved to object manager for log update accordingly
		levelChecker = new LevelChecker(gameCallback);
		levelChecker.setXmlFile(levels.get(levelIndex));
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
				boolean update  = game.getStatus() == Game.STATUS.LOSE;
				boolean levelUp = game.getStatus() == Game.STATUS.WIN;
				if (levelUp) levelIndex++;

				if (levelIndex >= levels.size()) game.win();
				else {
					// reset the game and update the frame
					game.reset(levels.get(levelIndex));
					boolean setStart = levelChecker.checkLevel(game);
					game.setStart(setStart);
					if (update) game.setStart(false);
					if (update || !setStart) gridManager.loadCurrGrid(levels.get(levelIndex));
					actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
				}
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

		// save the current grid
		if (e.getActionCommand().equals("save")) {
			gridManager.saveFile(levels.get(levelIndex));
			game.reset(levels.get(levelIndex));
		}
		// load a grid will add to the list of levels
		else if (e.getActionCommand().equals("load")) {
			String level = gridManager.loadFile(levels.get(levelIndex));
			if (level != null)
				game.reset(levels.get(levelIndex));
		}
		// resetting the current grid to its default, un-saved state
		else if (e.getActionCommand().equals("update"))
			gridManager.loadCurrGrid(levels.get(levelIndex));
		// starting test mode
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
	 * Get the main model.
	 * @return the model
	 */
	protected Grid getModel() {
		return model;
	}

	/**
	 * Get the main grid view.
	 * @return the grid view
	 */
	public GridView getGrid() {
		return grid;
	}

	/**
	 * Document listener registering changes made to a text file document. This is used to
	 * update the loaded file (perhaps).
	 */
	DocumentListener updateSizeFields = new DocumentListener() {
		public void changedUpdate(DocumentEvent e) {
			view.getWidth();
			view.getHeight();
		}
		public void removeUpdate(DocumentEvent e) {
			view.getWidth();
			view.getHeight();
		}
		public void insertUpdate(DocumentEvent e) {
			view.getWidth();
			view.getHeight();
		}
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tile getSelectedTile() {
		return selectedTile;
	}
}