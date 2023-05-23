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
    public static final String DIMENSION = "size";
    public static final String LENGTH = "length";
    public static final String WIDTH = "width";
    public static final String ROW = "row";
    public static final String CELL = "cell";
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
     * XML Parser that iterates through whole file to extract
     * pacActor, monsters, items, and portals
     * @param xmlFile   file containing all locations
     */
    public void parseXML(String xmlFile, ObjectManager manager) throws ParserConfigurationException, SAXException,
            IOException {
        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //an instance of builder to parse the specified xml file
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(xmlFile));
        doc.getDocumentElement().normalize();

        // Need these variables for the portal factory
        ArrayList<String> colors = new ArrayList<>();
        ArrayList<Location> locations = new ArrayList<>();

        // Get the needed files from the document
        NodeList dimensions = doc.getElementsByTagName(DIMENSION);
        int length;
        int width;
        // Load the dimensions from the XML file
        for (int i=0; i<dimensions.getLength(); i++) {
            // Get the length and width from the dimensions XML tag
            Node node = dimensions.item(i);
            Element eElement  = (Element) node;
            String strLength = eElement.getElementsByTagName(LENGTH).item(0).getTextContent();
            length = Integer.parseInt(strLength);

            String strWidth = eElement.getElementsByTagName(WIDTH).item(0).getTextContent();
            width = Integer.parseInt(strWidth);
        }

        // Now loop through every single cell and stores its location
        NodeList rows = doc.getElementsByTagName(ROW);
        for (int i=0; i < rows.getLength(); i++) {
            Node currRow = rows.item(i);
            Element eElement = (Element) currRow;
            NodeList cellTags = eElement.getElementsByTagName(CELL);

            for (int j=0; j<cellTags.getLength(); j++) {
                Location currLoc = new Location(i, j);
                String currCell = cellTags.item(j).getTextContent();

                // Now add onto ObjectManager based on the type of tile
                switch (currCell) {
                    case GOLD_TILE:
                        HashLocation.put(manager.getItems(), currLoc, new Gold());
                        break;
                    case PILL_TILE:
                        HashLocation.put(manager.getItems(), currLoc, new Pill());
                        break;
                    case WALL_TILE:
                        HashLocation.put(manager.getWalls(), currLoc, IS_WALL);
                        break;
                    case ICE_TILE:
                        HashLocation.put(manager.getItems(), currLoc, new Ice());
                        break;
                    // This specific case we need to initialize only the location,
                    // random seed and isAuto is instantiated once property file is read
                    case PAC_TILE:
                        manager.getPacActorLocations().add(currLoc);
                        manager.instantiatePacActorLoc(currLoc);
                        break;
                    case TROLL_TILE:
                        Troll addTroll = new Troll(manager);
                        addTroll.setInitLocation(currLoc);
                        manager.getMonsters().add(addTroll);
                        break;
                    case TX5_TILE:
                        TX5 addTX5 = new TX5(manager);
                        addTX5.setInitLocation(currLoc);
                        manager.getMonsters().add(addTX5);
                        break;
                    case PATH_TILE:
                        break;
                    // For portals, we want to store them into a hashmap then
                    default:
                        colors.add(currCell);
                        locations.add(currLoc);
                        break;
                }
            }
        }

        // After this, we want to construct the portals for the object manager
        manager.getPortalFactory().makePortals(manager.getPortalMap(), colors, locations);
    }

}
