package xpath.file;

import java.awt.HeadlessException;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import xml_utilities.InvalidXMLFileFormatException;
import xpath.file.XPathLevel;
import xml_utilities.XMLUtilities;
import static xpath.XPathConstants.*;
import xpath.data.XPathDataModel;
import xpath.file.Intersection;
import xpath.file.Road;
import xpath.ui.XPathMiniGame;

/**
 * This class serves as a does the reading and writing of levels to and from XML
 * files.
 *
 * @author Richard McKenna
 */
public class XPathXMLIO implements XPathLevelIO {

    // THIS WILL HELP US PARSE THE XML FILES
    private XMLUtilities xmlUtil;

    // THIS IS THE SCHEMA WE'LL USE
    private File levelSchema;

    private XPathMiniGame miniGame;
    private String DATA_PATH;

    /**
     * Constructor for making our importer/exporter. Note that it initializes
     * the XML utility for processing XML files and it sets up the schema for
     * use.
     */
    public XPathXMLIO(File initLevelSchema) {
        // THIS KNOWS HOW TO READ AND ACCESS XML FILES
        xmlUtil = new XMLUtilities();

        // WE'LL USE THE SCHEMA FILE TO VALIDATE THE XML FILES
        levelSchema = initLevelSchema;
    }

    /**
     * Reads the level data found in levelFile into levelToLoad.
     */
    public boolean loadLevel(File levelFile, XPathModel model) {
        try {
            // WE'LL FILL IN SOME OF THE LEVEL OURSELVES
            XPathLevel levelToLoad = model.getLevel();
            levelToLoad.reset();

            // FIRST LOAD ALL THE XML INTO A TREE
            Document doc = xmlUtil.loadXMLDocument(levelFile.getAbsolutePath(),
                    levelSchema.getAbsolutePath());

            // FIRST LOAD THE LEVEL INFO
            Node levelNode = doc.getElementsByTagName(LEVEL_NODE).item(0);
            NamedNodeMap attributes = levelNode.getAttributes();
            String levelName = attributes.getNamedItem(NAME_ATT).getNodeValue();
            levelToLoad.setLevelName(levelName);
            String bgImageName = attributes.getNamedItem(IMAGE_ATT).getNodeValue();
            model.updateBackgroundImage(bgImageName);

            // THEN LET'S LOAD THE LIST OF ALL THE REGIONS
            loadIntersectionsList(doc, levelToLoad);
            ArrayList<Intersection> intersections = levelToLoad.getIntersections();

            // AND NOW CONNECT ALL THE REGIONS TO EACH OTHER
            loadRoadsList(doc, levelToLoad);

            // LOAD THE START INTERSECTION
            Node startIntNode = doc.getElementsByTagName(START_INTERSECTION_NODE).item(0);
            attributes = startIntNode.getAttributes();
            String startIdText = attributes.getNamedItem(ID_ATT).getNodeValue();
            int startId = Integer.parseInt(startIdText);
            String startImageName = attributes.getNamedItem(IMAGE_ATT).getNodeValue();
            Intersection startingIntersection = intersections.get(startId);
            levelToLoad.setStartingLocation(startingIntersection);
            model.updateStartingLocationImage(startImageName);

            // LOAD THE DESTINATION
            Node destIntNode = doc.getElementsByTagName(DESTINATION_INTERSECTION_NODE).item(0);
            attributes = destIntNode.getAttributes();
            String destIdText = attributes.getNamedItem(ID_ATT).getNodeValue();
            int destId = Integer.parseInt(destIdText);
            String destImageName = attributes.getNamedItem(IMAGE_ATT).getNodeValue();
            levelToLoad.setDestination(intersections.get(destId));
            model.updateDestinationImage(destImageName);

            // LOAD THE MONEY
            Node moneyNode = doc.getElementsByTagName(MONEY_NODE).item(0);
            attributes = moneyNode.getAttributes();
            String moneyText = attributes.getNamedItem(AMOUNT_ATT).getNodeValue();
            int money = Integer.parseInt(moneyText);
            levelToLoad.setMoney(money);

            // LOAD THE NUMBER OF POLICE
            Node policeNode = doc.getElementsByTagName(POLICE_NODE).item(0);
            attributes = policeNode.getAttributes();
            String policeText = attributes.getNamedItem(NUM_ATT).getNodeValue();
            int numPolice = Integer.parseInt(policeText);
            levelToLoad.setNumPolice(numPolice);

            // LOAD THE NUMBER OF BANDITS
            Node banditsNode = doc.getElementsByTagName(BANDITS_NODE).item(0);
            attributes = banditsNode.getAttributes();
            String banditsText = attributes.getNamedItem(NUM_ATT).getNodeValue();
            int numBandits = Integer.parseInt(banditsText);
            levelToLoad.setNumBandits(numBandits);

            // LOAD THE NUMBER OF ZOMBIES
            Node zombiesNode = doc.getElementsByTagName(ZOMBIES_NODE).item(0);
            attributes = zombiesNode.getAttributes();
            String zombiesText = attributes.getNamedItem(NUM_ATT).getNodeValue();
            int numZombies = Integer.parseInt(zombiesText);
            levelToLoad.setNumZombies(numZombies);
        } catch (Exception e) {
            // LEVEL DIDN'T LOAD PROPERLY
            return false;
        }
        // LEVEL LOADED PROPERLY
        return true;
    }

    // PRIVATE HELPER METHOD FOR LOADING INTERSECTIONS INTO OUR LEVEL
    private void loadIntersectionsList(Document doc, XPathLevel levelToLoad) {
        // FIRST GET THE REGIONS LIST
        Node intersectionsListNode = doc.getElementsByTagName(INTERSECTIONS_NODE).item(0);
        ArrayList<Intersection> intersections = levelToLoad.getIntersections();

        // AND THEN GO THROUGH AND ADD ALL THE LISTED REGIONS
        ArrayList<Node> intersectionsList = xmlUtil.getChildNodesWithName(intersectionsListNode, INTERSECTION_NODE);
        for (int i = 0; i < intersectionsList.size(); i++) {
            // GET THEIR DATA FROM THE DOC
            Node intersectionNode = intersectionsList.get(i);
            NamedNodeMap intersectionAttributes = intersectionNode.getAttributes();
            String idText = intersectionAttributes.getNamedItem(ID_ATT).getNodeValue();
            String openText = intersectionAttributes.getNamedItem(OPEN_ATT).getNodeValue();
            String xText = intersectionAttributes.getNamedItem(X_ATT).getNodeValue();
            int x = Integer.parseInt(xText);
            String yText = intersectionAttributes.getNamedItem(Y_ATT).getNodeValue();
            int y = Integer.parseInt(yText);

            // NOW MAKE AND ADD THE INTERSECTION
            Intersection newIntersection = new Intersection(x, y);
            newIntersection.open = Boolean.parseBoolean(openText);
            intersections.add(newIntersection);
        }
    }

    // PRIVATE HELPER METHOD FOR LOADING ROADS INTO OUR LEVEL
    private void loadRoadsList(Document doc, XPathLevel levelToLoad) {
        // FIRST GET THE REGIONS LIST
        Node roadsListNode = doc.getElementsByTagName(ROADS_NODE).item(0);
        ArrayList<Road> roads = levelToLoad.getRoads();
        ArrayList<Intersection> intersections = levelToLoad.getIntersections();

        // AND THEN GO THROUGH AND ADD ALL THE LISTED REGIONS
        ArrayList<Node> roadsList = xmlUtil.getChildNodesWithName(roadsListNode, ROAD_NODE);
        for (int i = 0; i < roadsList.size(); i++) {
            // GET THEIR DATA FROM THE DOC
            Node roadNode = roadsList.get(i);
            NamedNodeMap roadAttributes = roadNode.getAttributes();
            String id1Text = roadAttributes.getNamedItem(INT_ID1_ATT).getNodeValue();
            int int_id1 = Integer.parseInt(id1Text);
            String id2Text = roadAttributes.getNamedItem(INT_ID2_ATT).getNodeValue();
            int int_id2 = Integer.parseInt(id2Text);
            String oneWayText = roadAttributes.getNamedItem(ONE_WAY_ATT).getNodeValue();
            boolean oneWay = Boolean.parseBoolean(oneWayText);
            String speedLimitText = roadAttributes.getNamedItem(SPEED_LIMIT_ATT).getNodeValue();
            int speedLimit = Integer.parseInt(speedLimitText);

            // NOW MAKE AND ADD THE ROAD
            Road newRoad = new Road();
            newRoad.setNode1(intersections.get(int_id1));
            newRoad.setNode2(intersections.get(int_id2));
            newRoad.setOneWay(oneWay);
            newRoad.setSpeedLimit(speedLimit);
            roads.add(newRoad);
        }
    }

    public void saveGame(XPathModel model) {
        try {
            // THESE WILL US BUILD A DOC
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // FIRST MAKE THE DOCUMENT
            Document doc = docBuilder.newDocument();
            // THEN THE LEVEL (i.e. THE ROOT) ELEMENT
            Element levelElement = doc.createElement("game");
            doc.createAttribute("money");
            levelElement.setAttribute("money", Integer.toString(model.playerCar.getMoney()));
            doc.appendChild(levelElement);
            doc.createAttribute("levelsUnlocked");
            levelElement.setAttribute("levelsUnlocked", Integer.toString(model.levelsUnlocked.size()));

            // THE TRANSFORMER KNOWS HOW TO WRITE A DOC TO
            // An XML FORMATTED FILE, SO LET'S MAKE ONE
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "Yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(".\\data\\model.xml");

            // SAVE THE POSE TO AN XML FILE
            transformer.transform(source, result);

        } catch (TransformerException | ParserConfigurationException | DOMException | HeadlessException ex) {
            
        }
    }

    public void loadGame(File currentFile, XPathModel model) {
        try {
            // FIRST LOAD ALL THE XML INTO A TREE
            Document doc = xmlUtil.loadXMLDocument(currentFile.getAbsolutePath(),
                    levelSchema.getAbsolutePath());
            

            // FIRST LOAD THE LEVEL INFO
            Node levelNode = doc.getElementsByTagName("game").item(0);
            NamedNodeMap attributes = levelNode.getAttributes();
            int money = Integer.parseInt(attributes.getNamedItem("money").getNodeValue());
            model.playerCar.setMoney(money);
            int levelsUnlocked = Integer.parseInt(attributes.getNamedItem("levelsUnlocked").getNodeValue());
            for (int i = 0; i < levelsUnlocked-1; i++) {
                model.levelsUnlocked.add(0);
            }
        } catch (InvalidXMLFileFormatException ex) {
            Logger.getLogger(XPathXMLIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
