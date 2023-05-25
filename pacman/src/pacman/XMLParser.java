package pacman;

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
    public static final int IS_WALL = 1;

    // static constants for XML Parsing

    public static final String SIZE = "size";
    public static final int LENGTH_INDEX = 0;
    public static final int WIDTH_INDEX = 1;
    public static final int DIMENSIONS = 2;
    public static final String ROW = "row";
    public static final String CELL = "cell";
    public static final int INSTANCE_INDEX =0;
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


    /**
     * XML Parser that iterates through whole file to extract pacActor, monsters, items, and portals.
     * @param xmlFile file containing all locations
     */
    public void parseXML(String xmlFile, ObjectManager manager)
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

        // Create the dimensions, was not used since
        // dimensions are already instantiated before parsing
        // int[] dimList = getDimensions(doc);
        // int width = dimList[WIDTH_INDEX];
        // int lenght = dimList[LENGTH_INDEX];

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
                    case GOLD_TILE:
                        HashLocation.put(manager.getItems(), currLocation, new Gold());
                        break;
                    case PILL_TILE:
                        HashLocation.put(manager.getItems(), currLocation, new Pill());
                        break;
                    case WALL_TILE:
                        HashLocation.put(manager.getWalls(), currLocation, IS_WALL);
                        break;
                    case ICE_TILE:
                        HashLocation.put(manager.getItems(), currLocation, new Ice());
                        break;
                    // This specific case we need to initialize only the location,
                    // random seed and isAuto is instantiated once property file is read
                    case PAC_TILE:
                        manager.getPacActorLocations().add(currLocation);
                        manager.instantiatePacActorLoc(currLocation);
                        break;
                    case TROLL_TILE:
                        Troll troll = new Troll(manager);
                        troll.setInitLocation(currLocation);
                        manager.getMonsters().add(troll);
                        break;
                    case TX5_TILE:
                        TX5 TX5 = new TX5(manager);
                        TX5.setInitLocation(currLocation);
                        manager.getMonsters().add(TX5);
                        break;
                    case PATH_TILE:
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
     * Method to extract the length and width from a given Document object
     * of an XML File; was left unused since the initialization of the Game
     * object already initializes dimensions.
     * @param doc   Document object made from XML File
     * @return      List containing the width and the length of the map
     */
    public int[] getDimensions(Document doc) {
        int[] dimList = new int[DIMENSIONS];
        // First extract the dimensions
        Node dimsNode = doc.getElementsByTagName(SIZE).item(INSTANCE_INDEX);
        String[] dims = dimsNode.getTextContent().strip().split("\n\s+");
        int length = Integer.parseInt(dims[LENGTH_INDEX]);
        int width = Integer.parseInt(dims[WIDTH_INDEX]);
        // Then put dimensions into the list
        dimList[LENGTH_INDEX] = length;
        dimList[WIDTH_INDEX] = width;
        return dimList;
    }
}
