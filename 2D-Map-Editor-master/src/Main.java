import editor.Controller;
import pacman.src.Game;
import pacman.src.utility.PropertiesLoader;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;


public class Main {
	public static void main(String[] args) throws InterruptedException, InvocationTargetException {
		String propertiesPath = "src/pacman/properties/test1.properties";
		Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
		assert properties != null;
		Game game = new Game(properties);

		Thread t = new Thread(() -> {
			new Controller(game);
			try {
				SwingUtilities.invokeAndWait(game);
			} catch (InterruptedException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		});
		t.start();
	}
}
