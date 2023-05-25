package editor;

import grid.Grid;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


/**
 * Grid File Manager class which deals with loading, saving grids from local user files.
 */
public class GridFileManager {
    public static final String DATA_PATH = "data/";
    private final Controller controller;

    /**
     * Class constructor.
     * @param controller the main controller
     */
    public GridFileManager(Controller controller) {
        this.controller = controller;
    }


    /**
     * Method triggered when save file action is performed. This is to save an editor grid to local
     * user file.
     */
    protected void saveFile(String path) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "xml files", "xml");
        chooser.setFileFilter(filter);
        File workingDirectory = new File(path);
        chooser.setCurrentDirectory(workingDirectory);

        int returnVal = chooser.showSaveDialog(null);
        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Grid model = controller.getModel();

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
                        String type = Tile.convertToCharTile(tileChar);
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

                List<?> sizeList = rootNode.getChildren("size");
                Element sizeElem = (Element) sizeList.get(0);
                int gridHeight = Integer.parseInt(sizeElem.getChildText("height"));
                int gridWith = Integer.parseInt(sizeElem.getChildText("width"));
                controller.resetGrid(gridWith, gridHeight);

                List<?> rows = rootNode.getChildren("row");
                for (int y = 0; y < rows.size(); y++) {
                    Element cellsElem = (Element) rows.get(y);
                    List<?> cells = cellsElem.getChildren("cell");

                    for (int x = 0; x < cells.size(); x++) {
                        Element cell = (Element) cells.get(x);
                        String cellValue = cell.getText();
                        char tileNr = Tile.convertToStringTile(cellValue);
                        controller.getModel().setTile(x, y, tileNr);
                    }
                }
                controller.getGrid().redrawGrid();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Load the current level's grid.
     */
    public void loadCurrGrid(String path) {
        File selectedFile = new File(path);
        loadSpecificFile(selectedFile);
    }

    /**
     * Method triggered when save load file action is performed. This is to load an editor grid from
     * local user file.
     */
    public String loadFile(String path) {
        JFileChooser chooser  = new JFileChooser();
        File workingDirectory = new File(path);
        File selectedFile;
        chooser.setCurrentDirectory(workingDirectory);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            loadSpecificFile(selectedFile);
            return selectedFile.getName();
        }
        return null;
    }
}
