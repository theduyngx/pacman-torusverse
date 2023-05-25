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
 * @see		Game
 */
public class Controller implements ActionListener, GUIInformation {
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
	private final GameType gameType;
	private final ArrayList<String> levels;
	private int levelIndex;
	private final LevelChecker levelChecker;
	private String level;

	/**
	 * Game type - either the game is opened as a folder, a file, or nothing.
	 */
	public enum GameType {
		IS_FOLDER,
		IS_FILE,
		IS_NULL
	}

	/**
	 * Constructor for the program Controller.
	 * @param game 			the game
	 * @param gameType		type of game open
	 * @param levels		the list of game levels
	 * @param gameCallback	the game callback to report to logs
	 * @see	  GameType
	 * @see   GameCallback
	 */
	public Controller(Game game, GameType gameType, ArrayList<String> levels, GameCallback gameCallback) {
		// dimensions for grid
		int width  = game.getDimension().width();
		int height = game.getDimension().height();

		// instantiations
		this.tiles    = TileManager.getTilesFromFolder(GridFileManager.DATA_PATH);
		this.model    = new GridModel(width, height, tiles.get(0).getCharacter());
		this.camera   = new GridCamera(model, width, height);
		this.grid     = new GridView(this, camera, tiles); // Every tile is 30x30 pixels
		this.view     = new View(this, camera, grid, tiles, width, height);
		this.game     = game;
		this.level    = "";
		this.levels   = levels;
		this.gameType = gameType;
		levelChecker  = new LevelChecker(gameCallback);
		gridManager   = new GridFileManager(this);
	}


	/**
	 * Initial call to controller to allow it to start handling the program.
	 */
	public void handle() {
		// game type handling
		boolean setStart = false;
		boolean triggerAction = false;
		if (gameType != GameType.IS_NULL) {
			levelIndex = 0;
			level = levels.get(levelIndex);
			this.game.reset(level);

			// level checking to whether to start the game
			levelChecker.setXmlFile(level);
			setStart = levelChecker.checkLevel(this.game) && gameType == GameType.IS_FOLDER;
			triggerAction = true;
			gridManager.loadCurrGrid(level);
		}
		if (gameType == GameType.IS_FILE) view.open();

		// start game immediately by manually trigger an action
		this.game.setStart(setStart);
		if (triggerAction)
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
				// game type handling
				if (gameType == GameType.IS_NULL) return;
				if (! game.getStart()) return;

				// check game's status (win or lose)
				boolean update  = game.getStatus() == Game.STATUS.LOSE;
				boolean levelUp = game.getStatus() == Game.STATUS.WIN;

				// for folder maps, freeze upon winning the game
				if (gameType == GameType.IS_FOLDER) {
					if (levelUp) levelIndex++;
					if (levelIndex >= levels.size()) game.win();
				}
				// reset the game and update the frame
				level = levels.get(levelIndex);
				game.reset(level);
				boolean setStart = levelChecker.checkLevel(game);

				// set start to game, and the editor's view accordingly
				game.setStart(setStart);
				if ((update || !setStart) || (gameType == GameType.IS_FILE && levelUp)) {
					game.setStart(false);
					gridManager.loadCurrGrid(level);
				}
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

		// save, load, reset
		if 		(e.getActionCommand().equals("save"  )) gridManager.saveFile(level);
		else if (e.getActionCommand().equals("load"  )) gridManager.loadFile(level);
		else if (e.getActionCommand().equals("update")) gridManager.loadCurrGrid(level);

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
		this.tiles 	= TileManager.getTilesFromFolder(GridFileManager.DATA_PATH);
		this.model 	= new GridModel(width, height, tiles.get(0).getCharacter());
		this.camera = new GridCamera(model, width, height);
		this.grid 	= new GridView(this, camera, tiles); // Every tile is 30x30 pixels
		this.view 	= new View(this, camera, grid, tiles, width, height);
		view.setSize(width, height);
	}

	/**
	 * Get the main model.
	 * @return the model
	 * @see	   Grid
	 */
	protected Grid getModel() {
		return model;
	}

	/**
	 * Get the main grid view.
	 * @return the grid view
	 * @see	   GridView
	 */
	protected GridView getGrid() {
		return grid;
	}

	/**
	 * Get the main view.
	 * @return the view
	 * @see	   View
	 */
	protected View getView() {
		return view;
	}

	/**
	 * Document listener registering changes made to a text file document. This is used to
	 * update the loaded file (perhaps).
	 */
	DocumentListener updateSizeFields = new DocumentListener() {
		@Override
		public void changedUpdate(DocumentEvent e) {
			view.getWidth();
			view.getHeight();
		}
		@Override
		public void removeUpdate(DocumentEvent e) {
			view.getWidth();
			view.getHeight();
		}
		@Override
		public void insertUpdate(DocumentEvent e) {
			view.getWidth();
			view.getHeight();
		}
	};

	/**
	 * {@inheritDoc}
	 * @see	Tile
	 */
	@Override
	public Tile getSelectedTile() {
		return selectedTile;
	}
}