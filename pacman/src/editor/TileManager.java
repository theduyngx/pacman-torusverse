package editor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * This class supports the Tile list with methods.
 * @author  Daniel "MaTachi" Jonsson
 * @version 1
 * @since   v0.0.5
 *
 */
public class TileManager {

	/**
	 * Returns a list with Tiles, constructed with images from the given folderPath.
	 * @param folderPath Path to image folder.
	 * @return List<Tile> List of tiles.
	 */
	public static List<Tile> getTilesFromFolder(final String folderPath) {
		List<Tile> tiles = new ArrayList<>();
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		int character = 'a';
		Map<String, File> map = new TreeMap<>();

		assert listOfFiles != null;
		for (File f : listOfFiles)
			map.put(f.getName(), f);
		for (File f : map.values()) {

			// check for valid path
			String filePath = f.getPath();
			try {
				BufferedImage image = ImageIO.read(new File(filePath));
				tiles.add(new Tile(filePath, image, (char)character++));
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println(e.getMessage());
				System.err.println("Bad file path: " + filePath);
				System.exit(0);
			}
		}
		return tiles;
	}
}
