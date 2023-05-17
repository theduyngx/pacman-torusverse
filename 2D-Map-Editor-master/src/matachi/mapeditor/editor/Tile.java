package matachi.mapeditor.editor;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * A class that holds a image, and a character that will be written to the
 * map file.
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 *
 */
public class Tile {

	/**
	 * The character that will be used in the map file when saved.
	 */
	private char character;


	/**
	 * The image that will be used in the editor.
	 */
	private BufferedImage image;
	private String filePath;
	
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

	@Override
	public String toString() {
		return "character: " + character + " - file: " + filePath;
	}

	/**
	 * Get the tile as a image.
	 * @return Image The tile icon.
	 */
	public Image getImage() {
		return deepCopy(image);
	}


	/**
	 * Get the tile as a icon.
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
	
	private static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
}
