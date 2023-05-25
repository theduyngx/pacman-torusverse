package game;
import game.Game.Dimension;

import editor.Tile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import ch.aplu.jgamegrid.Location;


/**
 * XML Parser is a class meant to extract all the items, enemies, and pacActor
 * and their locations and store it in a hashmap
 */
public class XMLParser {
    private static final int IS_WALL = 1;

    // static constants for XML Parsing
    private static final String SIZE = "size";
    private static final int WIDTH_INDEX = 0;
    private static final int HEIGHT_INDEX = 1;
    private static final String ROW = "row";
    private static final String CELL = "cell";
    private static final int INSTANCE_INDEX = 0;


    /**
     * XML Parser that iterates through whole file to extract pacActor, monsters, items, and portals.
     * @param xmlFile file containing all locations
     */
    public static void parseXML(String xmlFile, ObjectManager manager)
            throws ParserConfigurationException, SAXException, IOException {
        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // an instance of builder to parse the specified xml file
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(xmlFile));
        doc.getDocumentElement().normalize();

        // Need these variables for the portal factory
        ArrayList<String> colors = new ArrayList<>();
        ArrayList<Location> portalLocations = new ArrayList<>();

        // Now loop through every single cell and stores its location
        NodeList rows = doc.getElementsByTagName(ROW);
        for (int i=0; i< rows.getLength(); i++) {
            Node currRow = rows.item(i);
            Element eElement = (Element) currRow;
            NodeList cellTags = eElement.getElementsByTagName(CELL);

            for (int j=0; j<cellTags.getLength(); j++) {
                Location currLocation = new Location(j, i);
                String currCell = cellTags.item(j).getTextContent();

                // Now add onto ObjectManager based on the type of tile
                switch (currCell) {
                    case Tile.GOLD_TILE:
                        HashLocation.put(manager.getItems(), currLocation, new Gold());
                        break;
                    case Tile.PILL_TILE:
                        HashLocation.put(manager.getItems(), currLocation, new Pill());
                        break;
                    case Tile.WALL_TILE:
                        HashLocation.put(manager.getWalls(), currLocation, IS_WALL);
                        break;
                    case Tile.ICE_TILE:
                        HashLocation.put(manager.getItems(), currLocation, new Ice());
                        break;
                    // This specific case we need to initialize only the location,
                    // random seed and isAuto is instantiated once property file is read
                    case Tile.PAC_TILE:
                        manager.getPacActorLocations().add(currLocation);
                        break;
                    case Tile.TROLL_TILE:
                        Troll troll = new Troll(manager);
                        troll.setInitLocation(currLocation);
                        manager.getMonsters().add(troll);
                        break;
                    case Tile.TX5_TILE:
                        TX5 TX5 = new TX5(manager);
                        TX5.setInitLocation(currLocation);
                        manager.getMonsters().add(TX5);
                        break;
                    case Tile.PATH_TILE:
                        break;
                    // For portals, we want to store them into a hashmap then
                    default:
                        colors.add(currCell);
                        portalLocations.add(currLocation);
                        break;
                }
            }
        }
        // After this, we want to construct the portals for the object manager
        manager.getPortalFactory().makePortals(manager.getPortals(), colors, portalLocations);
    }

    /**
     * Method to extract the length and width from a given Document object of an XML File.
     * @param xmlFile XML file path
     * @return        the dimension
     */
    public static Dimension getDimensions(String xmlFile)
            throws ParserConfigurationException, IOException, SAXException
    {
        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // an instance of builder to parse the specified xml file
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(xmlFile));
        doc.getDocumentElement().normalize();

        // First extract the dimensions
        Node dimsNode = doc.getElementsByTagName(SIZE).item(INSTANCE_INDEX);
        String[] dims = dimsNode.getTextContent().strip().split("\n +");
        int width  = Integer.parseInt(dims[WIDTH_INDEX]);
        int height = Integer.parseInt(dims[HEIGHT_INDEX]);
        // Then put dimensions
        return new Dimension(width, height);
    }
}