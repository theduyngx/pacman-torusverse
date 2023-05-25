package editor;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * A class that holds an image, and a character that will be written to the
 * map file.
 * @author  Daniel "MaTachi" Jonsson
 * @version 1
 * @since   v0.0.5
 *
 */
public class Tile {
	// default tile dimensions
	public static final int TILE_WIDTH = 32;
	public static final int TILE_HEIGHT = 32;

	// Tile names in XML
	public static final String WALL_TILE = "WallTile";
	public static final String PILL_TILE = "PillTile";
	public static final String GOLD_TILE = "GoldTile";
	public static final String ICE_TILE = "IceTile";
	public static final String PAC_TILE = "PacTile";
	public static final String TROLL_TILE = "TrollTile";
	public static final String TX5_TILE = "TX5Tile";
	public static final String PORTAL_WHITE_TILE = "PortalWhiteTile";
	public static final String PORTAL_YELLOW_TILE = "PortalYellowTile";
	public static final String PORTAL_DARK_GOLD_TILE = "PortalDarkGoldTile";
	public static final String PORTAL_DARK_GREY_TILE = "PortalDarkGrayTile";
	public static final String PATH_TILE = "PathTile";

	// Tile encoded character on grid
	public static final char WALL_CHAR = 'b';
	public static final char PILL_CHAR = 'c';
	public static final char GOLD_CHAR = 'd';
	public static final char ICE_CHAR = 'e';
	public static final char PAC_CHAR = 'f';
	public static final char TROLL_CHAR = 'g';
	public static final char TX5_CHAR = 'h';
	public static final char PORTAL_WHITE_CHAR = 'i';
	public static final char PORTAL_YELLOW_CHAR = 'j';
	public static final char PORTAL_DARK_GOLD_CHAR = 'k';
	public static final char PORTAL_DARK_GREY_CHAR = 'l';
	public static final char PATH_CHAR = 'a';
	public static final char NULL = '0';


	// The character that will be used in the map file when saved.
	private final char character;

	// The image that will be used in the editor.
	private final BufferedImage image;
	private final String filePath;


	/**
	 * Converting the encoded character of the tiles to its XML string format.
	 * @param tileChar the encoded tile character
	 * @return		   the XML string format
	 */
	protected static String convertToCharTile(char tileChar) {
		return switch (tileChar) {
			case WALL_CHAR 				-> WALL_TILE;
			case PILL_CHAR 				-> PILL_TILE;
			case GOLD_CHAR 				-> GOLD_TILE;
			case ICE_CHAR 				-> ICE_TILE;
			case PAC_CHAR 				-> PAC_TILE;
			case TROLL_CHAR 			-> TROLL_TILE;
			case TX5_CHAR 				-> TX5_TILE;
			case PORTAL_WHITE_CHAR 		-> PORTAL_WHITE_TILE;
			case PORTAL_YELLOW_CHAR 	-> PORTAL_YELLOW_TILE;
			case PORTAL_DARK_GOLD_CHAR 	-> PORTAL_DARK_GOLD_TILE;
			case PORTAL_DARK_GREY_CHAR 	-> PORTAL_DARK_GREY_TILE;
			default 					-> PATH_TILE;
		};
	}

	/**
	 * Convert the XML string format of a tile to its encoded character format.
	 * @param tileName the XML string of tile
	 * @return		   the tile's encoded character
	 */
	protected static char convertToStringTile(String tileName) {
		return switch (tileName) {
			case PATH_TILE 				-> PATH_CHAR;
			case WALL_TILE 				-> WALL_CHAR;
			case PILL_TILE 				-> PILL_CHAR;
			case GOLD_TILE 				-> GOLD_CHAR;
			case ICE_TILE 				-> ICE_CHAR;
			case PAC_TILE 				-> PAC_CHAR;
			case TROLL_TILE 			-> TROLL_CHAR;
			case TX5_TILE 				-> TX5_CHAR;
			case PORTAL_WHITE_TILE 		-> PORTAL_WHITE_CHAR;
			case PORTAL_YELLOW_TILE 	-> PORTAL_YELLOW_CHAR;
			case PORTAL_DARK_GOLD_TILE 	-> PORTAL_DARK_GOLD_CHAR;
			case PORTAL_DARK_GREY_TILE 	-> PORTAL_DARK_GREY_CHAR;
			default -> NULL;
		};
	}
	
	/**
	 * Construct a tile.
	 * @param filePath The path to the file.
	 * @param character The character that will represent the tile when saved.
	 */
	public Tile(final String filePath, BufferedImage image, final char character) {
		this.filePath = filePath;
		this.image = image;
		this.character = character;
	}

	/**
	 * Get the tile as an image.
	 * @return Image The tile icon.
	 */
	public Image getImage() {
		return deepCopy(image);
	}

	/**
	 * Get the tile as an icon.
	 * @return Icon The tile icon.
	 */
	public Icon getIcon() {
		return new ImageIcon(image);
	}
	
	/**
	 * Get the character.
	 * @return char The tile character.
	 */
	public char getCharacter() {
		return character;
	}


	/**
	 * String format for tiles.
	 * @return the string format
	 */
	@Override
	public String toString() {
		return "character: " + character + " - file: " + filePath;
	}

	/**
	 * Get the deep copy of the tile with all of its properties.
	 * @param image the buffered image
	 * @return		the deep copy
	 */
	private static BufferedImage deepCopy(BufferedImage image) {
		ColorModel cm = image.getColorModel();
		boolean isAlphaPreMultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = image.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPreMultiplied, null);
	}
}
