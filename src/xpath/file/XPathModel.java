/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xpath.file;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import mini_game.MiniGame;
import mini_game.Sprite;
import mini_game.Viewport;
import xpath.XPath;
import xpath.XPath.XPathPropertyType;
import static xpath.XPathConstants.*;
import xpath.data.IntersectionComparator;
import xpath.data.PlayerCar;
import xpath.ui.XPathMiniGame;
import xpath.ui.ViewportLevel;
import xpath.file.XPathEditMode.*;
import xpath.ui.Enemy;

/**
 * This class manages the data associated with the pathX level editor app. Note
 * that all the data associated with a given level that needs to be saved to the
 * XML file is inside the PXLE_Level file, and that this model class has the
 * level object currently being edited.
 *
 * @author Richard McKenna & Alexander Greenstein
 */
public class XPathModel {

    // THIS IS THE LEVEL CURRENTLY BEING EDITING
    XPathLevel level;

    private XPathMiniGame miniGame;

    private XPathEditMode editMode;

    public Road selectedRoad;

    public boolean isLoaded = false;

    public String levelName;

    // WE ONLY NEED TO TURN THIS ON ONCE
    BufferedImage backgroundImage;
    BufferedImage startingLocationImage;
    BufferedImage destinationImage;

    // DATA FOR RENDERING
    private ViewportLevel viewport;

    private Stack<Intersection> intersectionPath;
    private Stack<Intersection> currentPath;
    public ArrayList<Intersection> banditNodes;
    private float costToGo = 0;
    boolean firstRun = true;
    public int firstRunInt = 0;

    public ArrayList<Enemy> enemies;
    public ArrayList<Integer> levelsUnlocked;

    // THE SELECTED INTERSECTION OR ROAD MIGHT BE EDITED OR DELETED
    // AND IS RENDERED DIFFERENTLY
    Intersection selectedIntersection;
    Intersection currentIntersection;
    private Intersection previousIntersection;
    private Intersection positionNowInt;

    // WE'LL USE THIS WHEN WE'RE ADDING A NEW ROAD
    Intersection startRoadIntersection;

    // IN CASE WE WANT TO TRACK MOVEMENTS
    int lastMouseX;
    int lastMouseY;

    // THESE BOOLEANS HELP US KEEP TRACK OF
    // @todo DO WE NEED THESE?
    boolean isMousePressed;
    boolean isDragging;
    boolean dataUpdatedSinceLastSave;

    public PlayerCar playerCar;

    public boolean[] powerups;

    private boolean sound = true;
    private boolean music = true;

    /**
     * Default the constructor, it initializes the empty level and all the data
     * needed to start editing.
     */
    public XPathModel(XPathMiniGame miniGameT) {
        level = new XPathLevel();
        startRoadIntersection = null;
        miniGame = miniGameT;
        viewport = new ViewportLevel();
        intersectionPath = new Stack<Intersection>();
        currentPath = new Stack<Intersection>();
        enemies = new ArrayList<Enemy>();
        banditNodes = new ArrayList<Intersection>();
        playerCar = new PlayerCar(miniGame);
        levelsUnlocked = new ArrayList<Integer>();
        levelsUnlocked.add(0);
        powerups = new boolean[16];
    }

    // THESE ARE FOR TESTING WHAT EDIT MODE THE APP CURRENTLY IS IN
    public boolean isNothingSelected() {
        return editMode == XPathEditMode.NOTHING_SELECTED;
    }

    public boolean isIntersectionSelected() {
        return editMode == XPathEditMode.INTERSECTION_SELECTED;
    }

    public boolean isIntersectionDragged() {
        return editMode == XPathEditMode.INTERSECTION_DRAGGED;
    }

    public boolean isRoadSelected() {
        return editMode == XPathEditMode.ROAD_SELECTED;
    }

    public boolean isAddingIntersection() {
        return editMode == XPathEditMode.ADDING_INTERSECTION;
    }

    public boolean isAddingRoadStart() {
        return editMode == XPathEditMode.ADDING_ROAD_START;
    }

    public boolean isAddingRoadEnd() {
        return editMode == XPathEditMode.ADDING_ROAD_END;
    }

    // ACCESSOR METHODS
    public XPathLevel getLevel() {
        return level;
    }

    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    public Image getStartingLocationImage() {
        return startingLocationImage;
    }

    public Image getDesinationImage() {
        return destinationImage;
    }

    public Intersection getSelectedIntersection() {
        return selectedIntersection;
    }

    public Road getSelectedRoad() {
        return selectedRoad;
    }

    public Intersection getStartRoadIntersection() {
        return startRoadIntersection;
    }

    public int getLastMouseX() {
        return lastMouseX;
    }

    public int getLastMouseY() {
        return lastMouseY;
    }

    public Intersection getStartingLocation() {
        return level.startingLocation;
    }

    public Intersection getDestination() {
        return level.destination;
    }

    public boolean isDataUpdatedSinceLastSave() {
        return dataUpdatedSinceLastSave;
    }

    public boolean isStartingLocation(Intersection testInt) {
        return testInt == level.startingLocation;
    }

    public boolean isDestination(Intersection testInt) {
        return testInt == level.destination;
    }

    public boolean isSelectedIntersection(Intersection testIntersection) {
        return testIntersection == selectedIntersection;
    }

    public boolean isSelectedRoad(Road testRoad) {
        return testRoad == selectedRoad;
    }

    // ITERATOR METHODS FOR GOING THROUGH THE GRAPH
    public Iterator intersectionsIterator() {
        ArrayList<Intersection> intersections = level.getIntersections();
        return intersections.iterator();
    }

    public Iterator roadsIterator() {
        ArrayList<Road> roads = level.roads;
        return roads.iterator();
    }

    // MUTATOR METHODS
    public void setLastMousePosition(int initX, int initY) {
        lastMouseX = initX;
        lastMouseY = initY;
    }

    public void setSelectedIntersection(Intersection i) {
        selectedIntersection = i;
        selectedRoad = null;
    }

    public void setSelectedRoad(Road r) {
        selectedRoad = r;
        selectedIntersection = null;
    }

    // AND THEN ALL THE SERVICE METHODS FOR UPDATING THE LEVEL
    // AND APP STATE
    /**
     * Sets up the model to edit a brand new level.
     */
    public void startNewLevel(String levelName) {
        // CLEAR OUT THE OLD GRAPH
        level.reset();

        // FIRST INITIALIZE THE LEVEL
        // WE ALWAYS START WITH A DEFAULT BACKGROUND,
        // AND START AND END LOCATIONS
        level.init(levelName,
                DEFAULT_BG_IMG,
                DEFAULT_START_IMG,
                DEFAULT_START_X,
                DEFAULT_START_Y,
                DEFAULT_DEST_IMG,
                DEFAULT_DEST_X,
                DEFAULT_DEST_Y);

        // NOW MAKE THE LEVEL IMAGES
        backgroundImage = loadImage(LEVELS_PATH + DEFAULT_BG_IMG);
        startingLocationImage = loadImage(LEVELS_PATH + DEFAULT_START_IMG);
        destinationImage = loadImage(LEVELS_PATH + DEFAULT_DEST_IMG);

        // INTERACTIVE SETTINGS
        isMousePressed = false;
        isDragging = false;
        selectedIntersection = null;
        selectedRoad = null;
        dataUpdatedSinceLastSave = false;
    }

    /**
     * Updates the background image.
     */
    public void updateBackgroundImage(String newBgImage) {
        // UPDATE THE LEVEL TO FIT THE BACKGROUDN IMAGE SIZE
        level.backgroundImageFileName = newBgImage;
        backgroundImage = loadImage(LEVELS_PATH + level.backgroundImageFileName);
        int levelWidth = backgroundImage.getWidth(null);
        int levelHeight = backgroundImage.getHeight(null);
        viewport.setLevelDimensions(levelWidth, levelHeight);
    }

    /**
     * Updates the image used for the starting location and forces rendering.
     */
    public void updateStartingLocationImage(String newStartImage) {
        level.startingLocationImageFileName = newStartImage;
        startingLocationImage = loadImage(LEVELS_PATH + level.startingLocationImageFileName);
    }

    /**
     * Updates the image used for the destination and forces rendering.
     */
    public void updateDestinationImage(String newDestImage) {
        level.destinationImageFileName = newDestImage;
        destinationImage = loadImage(LEVELS_PATH + level.destinationImageFileName);
    }

    /**
     * Adds an intersection to the graph
     */
    public void addIntersection(Intersection intToAdd) {
        ArrayList<Intersection> intersections = level.getIntersections();
        intersections.add(intToAdd);
    }

    /**
     * Calculates and returns the distance between two points.
     */
    public double calculateDistanceBetweenPoints(int x1, int y1, int x2, int y2) {
        double diffXSquared = Math.pow(x1 - x2, 2);
        double diffYSquared = Math.pow(y1 - y2, 2);
        return Math.sqrt(diffXSquared + diffYSquared);
    }

    /**
     * Increases the speed limit on the selected road.
     */
    public void increaseSelectedRoadSpeedLimit() {
        if (selectedRoad != null) {
            int speedLimit = selectedRoad.getSpeedLimit();
            if (speedLimit < MAX_SPEED_LIMIT) {
                speedLimit += SPEED_LIMIT_STEP;
                selectedRoad.setSpeedLimit(speedLimit);
            }
        }
    }

    /**
     * Decreases the speed limit on the selected road.
     */
    public void decreaseSelectedRoadSpeedLimit() {
        if (selectedRoad != null) {
            int speedLimit = selectedRoad.getSpeedLimit();
            if (speedLimit > MIN_SPEED_LIMIT) {
                speedLimit -= SPEED_LIMIT_STEP;
                selectedRoad.setSpeedLimit(speedLimit);
            }
        }
    }

    /**
     * Loads an image using the fileName as the full path, returning the
     * constructed and completely loaded Image.
     *
     * @param fileName full path and name of the location of the image file to
     * be loaded.
     *
     * @return the loaded Image, with all data fully loaded.
     */
    public BufferedImage loadImage(String fileName) {
        // LOAD THE IMAGE
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image img = tk.createImage(fileName);

        MediaTracker mt = new MediaTracker(miniGame.getWindow());
        mt.addImage(img, 0);
        try {
            mt.waitForID(0);
        } catch (InterruptedException ie) { /* THIS SHOULD NEVER HAPPEN */ ie.printStackTrace();
        }

        BufferedImage bImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D g2 = bImage.createGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.dispose();

        return bImage;

    }

    /**
     * @return the viewport
     */
    public ViewportLevel getViewport() {
        return viewport;
    }

    /**
     * Searches the level graph and finds and returns the intersection that
     * overlaps (canvasX, canvasY).
     */
    public Intersection findIntersectionAtCanvasLocation(int canvasX, int canvasY) {
        // CHECK TO SEE IF THE USER IS SELECTING AN INTERSECTION
        for (Intersection i : level.intersections) {
            double distance = calculateDistanceBetweenPoints(i.x, i.y, canvasX + viewport.x, canvasY + viewport.y);
            if (distance < INTERSECTION_RADIUS) {
                // MAKE THIS THE SELECTED INTERSECTION
                return i;
            }
        }
        return null;
    }

    /**
     * Unselects any intersection or road that might be selected.
     */
    public void unselectEverything() {
        selectedIntersection = null;
        selectedRoad = null;
        startRoadIntersection = null;
    }

    /**
     * @return the currentIntersection
     */
    public Intersection getCurrentIntersection() {
        return currentIntersection;
    }

    /**
     * @param currentIntersection the currentIntersection to set
     */
    public void setCurrentIntersection(Intersection currentIntersection) {
        this.currentIntersection = currentIntersection;
    }

    public void doArrays(Intersection previous, ArrayList<Intersection> initFixedList) {
        ArrayList<Intersection> intersectionsClose = new ArrayList<Intersection>();
        ArrayList<Intersection> fixedList = new ArrayList<Intersection>(initFixedList.size());
        for (Intersection intersection : initFixedList) {
            fixedList.add(intersection);
        }
        fixedList.remove(previous);
        if (!selectedIntersection.closed && selectedIntersection != currentIntersection) {
            for (Intersection intersection : fixedList) {
                if (!intersection.closed) {
                    for (Road road : level.roads) {
                        if (road.node1 == previous && road.node2 == intersection) {
                            intersectionsClose.add(intersection);
                        } else if (road.node1 == intersection && road.node2 == previous) {
                            intersectionsClose.add(intersection);
                        }
                    }
                }
            }
            for (Intersection intersection2 : intersectionsClose) {
                currentPath.add(intersection2);
                if (intersection2 == selectedIntersection) {
                    if (checkPathForProperOneWays(currentPath)) {
                        if (firstRun) {
                            firstRun = false;
                            intersectionPath = new Stack<Intersection>();
                            for (int i = 0; i < currentPath.size() - 1; i++) {
                                for (Road road : level.roads) {
                                    if (road.node1 == currentPath.get(i) && road.node2 == currentPath.get(i + 1)) {
                                        costToGo += calcDistance(currentPath.get(i), currentPath.get(i + 1)) / (road.getSpeedLimit());
                                    } else if (road.node1 == currentPath.get(i + 1) && road.node2 == currentPath.get(i)) {
                                        costToGo += calcDistance(currentPath.get(i), currentPath.get(i + 1)) / (road.getSpeedLimit());
                                    } else if (road.node1 == currentIntersection && road.node2 == currentPath.get(i)) {
                                        costToGo += calcDistancePC(currentPath.get(i), miniGame.getGUIButtons().get(UI_BUTTON_TYPE + "PC")) / (road.getSpeedLimit());
                                    } else if (road.node1 == currentPath.get(i) && road.node2 == currentIntersection) {
                                        costToGo += calcDistancePC(currentPath.get(i), miniGame.getGUIButtons().get(UI_BUTTON_TYPE + "PC")) / (road.getSpeedLimit());
                                    }
                                }
                            }
                            for (int i = currentPath.size() - 1; i > -1; i--) {
                                intersectionPath.add(currentPath.get(i));
                            }
                        } else {
                            float temp = 0;
                            for (int i = 0; i < currentPath.size() - 1; i++) {
                                for (Road road : level.roads) {
                                    if (road.node1 == currentPath.get(i) && road.node2 == currentPath.get(i + 1)) {
                                        temp += calcDistance(currentPath.get(i), currentPath.get(i + 1)) / (road.getSpeedLimit());
                                    } else if (road.node1 == currentPath.get(i + 1) && road.node2 == currentPath.get(i)) {
                                        temp += calcDistance(currentPath.get(i), currentPath.get(i + 1)) / (road.getSpeedLimit());
                                    } else if (road.node1 == currentIntersection && road.node2 == currentPath.get(i)) {
                                        temp += calcDistancePC(currentPath.get(i), miniGame.getGUIButtons().get(UI_BUTTON_TYPE + "PC")) / (road.getSpeedLimit());
                                    } else if (road.node1 == currentPath.get(i) && road.node2 == currentIntersection) {
                                        temp += calcDistancePC(currentPath.get(i), miniGame.getGUIButtons().get(UI_BUTTON_TYPE + "PC")) / (road.getSpeedLimit());
                                    }
                                }
                            }

                            if (temp < costToGo) {
                                costToGo = temp;
                                intersectionPath = new Stack<Intersection>();
                                for (int i = currentPath.size() - 1; i > -1; i--) {
                                    intersectionPath.add(currentPath.get(i));
                                }
                            }
                        }
                    }
                } else {
                    doArrays(intersection2, fixedList);
                }
                currentPath.remove(intersection2);
            }
        }
        intersectionsClose = new ArrayList<Intersection>();
    }

    public void doArraysMod(Intersection previous, ArrayList<Intersection> initFixedList) {
        ArrayList<Intersection> intersectionsClose = new ArrayList<Intersection>();
        ArrayList<Intersection> fixedList = new ArrayList<Intersection>(initFixedList.size());
        for (Intersection intersection : initFixedList) {
            fixedList.add(intersection);
        }
        fixedList.remove(previous);
        if (!selectedIntersection.closed && selectedIntersection != currentIntersection) {
            for (Intersection intersection : fixedList) {

                for (Road road : level.roads) {
                    if (road.node1 == previous && road.node2 == intersection) {
                        intersectionsClose.add(intersection);
                    } else if (road.node1 == intersection && road.node2 == previous) {
                        intersectionsClose.add(intersection);
                    }
                }

            }
            for (Intersection intersection2 : intersectionsClose) {
                currentPath.add(intersection2);
                if (intersection2 == selectedIntersection) {
                    if (firstRun) {
                        firstRun = false;
                        intersectionPath = new Stack<Intersection>();
                        for (int i = 0; i < currentPath.size() - 1; i++) {
                            for (Road road : level.roads) {
                                if (road.node1 == currentPath.get(i) && road.node2 == currentPath.get(i + 1)) {
                                    costToGo += calcDistance(currentPath.get(i), currentPath.get(i + 1)) / (road.getSpeedLimit());
                                } else if (road.node1 == currentPath.get(i + 1) && road.node2 == currentPath.get(i)) {
                                    costToGo += calcDistance(currentPath.get(i), currentPath.get(i + 1)) / (road.getSpeedLimit());
                                } else if (road.node1 == currentIntersection && road.node2 == currentPath.get(i)) {
                                    costToGo += calcDistancePC(currentPath.get(i), miniGame.getGUIButtons().get(UI_BUTTON_TYPE + "PC")) / (road.getSpeedLimit());
                                } else if (road.node1 == currentPath.get(i) && road.node2 == currentIntersection) {
                                    costToGo += calcDistancePC(currentPath.get(i), miniGame.getGUIButtons().get(UI_BUTTON_TYPE + "PC")) / (road.getSpeedLimit());
                                }
                            }
                        }
                        for (int i = currentPath.size() - 1; i > -1; i--) {
                            intersectionPath.add(currentPath.get(i));
                        }
                    } else {
                        float temp = 0;
                        for (int i = 0; i < currentPath.size() - 1; i++) {
                            for (Road road : level.roads) {
                                if (road.node1 == currentPath.get(i) && road.node2 == currentPath.get(i + 1)) {
                                    temp += calcDistance(currentPath.get(i), currentPath.get(i + 1)) / (road.getSpeedLimit());
                                } else if (road.node1 == currentPath.get(i + 1) && road.node2 == currentPath.get(i)) {
                                    temp += calcDistance(currentPath.get(i), currentPath.get(i + 1)) / (road.getSpeedLimit());
                                } else if (road.node1 == currentIntersection && road.node2 == currentPath.get(i)) {
                                    temp += calcDistancePC(currentPath.get(i), miniGame.getGUIButtons().get(UI_BUTTON_TYPE + "PC")) / (road.getSpeedLimit());
                                } else if (road.node1 == currentPath.get(i) && road.node2 == currentIntersection) {
                                    temp += calcDistancePC(currentPath.get(i), miniGame.getGUIButtons().get(UI_BUTTON_TYPE + "PC")) / (road.getSpeedLimit());
                                }
                            }
                        }

                        if (temp < costToGo) {
                            costToGo = temp;
                            intersectionPath = new Stack<Intersection>();
                            for (int i = currentPath.size() - 1; i > -1; i--) {
                                intersectionPath.add(currentPath.get(i));
                            }
                        }
                    }

                } else {
                    doArraysMod(intersection2, fixedList);
                }
                currentPath.remove(intersection2);
            }
        }
        intersectionsClose = new ArrayList<Intersection>();
    }

    /**
     * @return the intersectionPath
     */
    public Stack<Intersection> getIntersectionPath() {
        return intersectionPath;
    }

    public void resetStack() {
        intersectionPath = new Stack<Intersection>();
        currentPath = new Stack<Intersection>();
        costToGo = 0;
        firstRun = true;
    }

    public boolean isAdjacent(Intersection intersection, Intersection intersectionPathCurrent) {
        for (Road road : level.roads) {
            if (road.node1 == intersection && road.node2 == intersectionPathCurrent) {
                return true;
            }
            if (road.node1 == intersectionPathCurrent && road.node2 == intersection) {
                return true;
            }
        }

        return false;
    }

    public float calcDistance(Intersection o1, Intersection o2) {
        float diffX = o1.x - o2.x;
        float diffY = o1.y - o2.y;
        return (float) Math.sqrt((diffX * diffX) + (diffY * diffY));
    }

    public float calcDistancePC(Intersection o1, Sprite s) {
        float diffX = o1.x - s.getX();
        float diffY = o1.y - s.getY();
        return (float) Math.sqrt((diffX * diffX) + (diffY * diffY));
    }

    public float calcDistancePCX(Intersection o1, Sprite s) {
        float diffX = o1.x - s.getX() - 28;
        return diffX;
    }

    public float calcDistancePCY(Intersection o1, Sprite s) {
        float diffY = o1.y - s.getY() - 28;
        return diffY;
    }

    public float calcDistanceX(Intersection o1, Intersection o2) {
        float diffX = o1.x - o2.x;
        return diffX;
    }

    public float calcDistanceY(Intersection o1, Intersection o2) {
        float diffY = o1.y - o2.y;
        return diffY;
    }

    public Road getRoad(Intersection o1, Intersection o2) {
        for (Road road : level.roads) {
            if (road.node1 == o1 && road.node2 == o2) {
                return road;
            } else if (road.node1 == o2 && road.node2 == o1) {
                return road;
            }
        }
        return null;
    }

    public boolean checkPathForProperOneWays(Stack<Intersection> cPath) {
        if (cPath.size() == 1) {
            for (Road road : level.roads) {
                if (road.isOneWay()) {
                    if (road.node2 == currentIntersection && road.node1 == cPath.get(0)) {
                        return false;
                    }
                }
                if (road.node1 == currentIntersection && road.node2 == cPath.get(0)) {
                    if (road.isClosed()) {
                        return false;
                    }
                }
                if (road.node2 == currentIntersection && road.node1 == cPath.get(0)) {
                    if (road.isClosed()) {
                        return false;
                    }
                }
            }
        } else {
            for (int i = 0; i < cPath.size() - 1; i++) {
                for (Road road : level.roads) {
                    if (road.isOneWay()) {
                        if (road.node2 == currentIntersection && road.node1 == cPath.get(0)) {
                            return false;
                        } else if (road.node2 == cPath.get(i) && road.node1 == cPath.get(i + 1)) {
                            return false;
                        }
                    }
                    if (road.node1 == currentIntersection && road.node2 == cPath.get(0)) {
                        if (road.isClosed()) {
                            return false;
                        }
                    }
                    if (road.node2 == currentIntersection && road.node1 == cPath.get(0)) {
                        if (road.isClosed()) {
                            return false;
                        }
                    }
                    if (road.node1 == cPath.get(i) && road.node2 == cPath.get(i + 1)) {
                        if (road.isClosed()) {
                            return false;
                        }
                    }
                    if (road.node2 == cPath.get(i) && road.node1 == cPath.get(i + 1)) {
                        if (road.isClosed()) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    public boolean checkPathForProperOneWaysAI(Stack<Intersection> cPath, Enemy enemy) {
        if (cPath.size() == 1) {
            for (Road road : level.roads) {
                if (road.isOneWay()) {
                    if (road.node2 == enemy.curInt && road.node1 == cPath.get(0)) {
                        return false;
                    }
                }
                if (road.node1 == enemy.curInt && road.node2 == cPath.get(0)) {
                    if (road.isClosed()) {
                        return false;
                    }
                }
                if (road.node2 == enemy.curInt && road.node1 == cPath.get(0)) {
                    if (road.isClosed()) {
                        return false;
                    }
                }
            }
        } else {
            for (int i = 0; i < cPath.size() - 1; i++) {
                for (Road road : level.roads) {
                    if (road.isOneWay()) {
                        if (road.node2 == enemy.curInt && road.node1 == cPath.get(0)) {
                            return false;
                        } else if (road.node2 == cPath.get(i) && road.node1 == cPath.get(i + 1)) {
                            return false;
                        }
                    }
                    if (road.node1 == enemy.curInt && road.node2 == cPath.get(0)) {
                        if (road.isClosed()) {
                            return false;
                        }
                    }
                    if (road.node2 == enemy.curInt && road.node1 == cPath.get(0)) {
                        if (road.isClosed()) {
                            return false;
                        }
                    }
                    if (road.node1 == cPath.get(i) && road.node2 == cPath.get(i + 1)) {
                        if (road.isClosed()) {
                            return false;
                        }
                    }
                    if (road.node2 == cPath.get(i) && road.node1 == cPath.get(i + 1)) {
                        if (road.isClosed()) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    public void doBanditNodes() {
        int greatestXY = 0;
        int lowestXY = 10000000;

        Intersection lowestCorner = new Intersection(0, 0);
        Intersection highestCorner = new Intersection(0, 0);

        for (Intersection intersection : level.intersections) {
            if (intersection.isOpen()) {
                if (intersection != level.getStartingLocation() && intersection != level.getDestination()) {

                    if ((intersection.x * (480 - intersection.y)) > greatestXY) {
                        greatestXY = (intersection.x * (480 - intersection.y));
                        highestCorner = intersection;
                    }
                    if ((intersection.x * (480 - intersection.y)) < lowestXY) {
                        lowestXY = (intersection.x * (480 - intersection.y));
                        lowestCorner = intersection;
                    }
                }
            }
        }

        banditNodes.add(lowestCorner);
        banditNodes.add(highestCorner);
    }

    public void makeEnemyPath(Intersection previous, ArrayList<Intersection> initFixedList, Boolean TF, Enemy enemy) {
        ArrayList<Intersection> intersectionsClose = new ArrayList<Intersection>();
        ArrayList<Intersection> fixedList = new ArrayList<Intersection>(initFixedList.size());
        for (Intersection intersection : initFixedList) {
            fixedList.add(intersection);
        }
        if (TF = true) {
            fixedList.remove(level.startingLocation);
            fixedList.remove(level.destination);
            TF = false;
        }

        fixedList.remove(previous);
        if (enemy.selInt.isOpen() && enemy.selInt != enemy.curInt) {
            for (Intersection intersection : fixedList) {
                if (intersection.isOpen()) {
                    for (Road road : level.roads) {
                        if (road.node1 == previous && road.node2 == intersection) {
                            intersectionsClose.add(intersection);
                        } else if (road.node1 == intersection && road.node2 == previous) {
                            intersectionsClose.add(intersection);
                        }
                    }
                }
            }

            for (Intersection intersection2 : intersectionsClose) {
                currentPath.add(intersection2);
                if (intersection2 == enemy.selInt) {
                    if (checkPathForProperOneWaysAI(currentPath, enemy)) {
                        if (firstRun) {
                            firstRun = false;
                            enemy.intPath = new Stack<Intersection>();
                            for (int i = 0; i < currentPath.size() - 1; i++) {
                                for (Road road : level.roads) {
                                    if (road.node1 == currentPath.get(i) && road.node2 == currentPath.get(i + 1)) {
                                        costToGo += calcDistance(currentPath.get(i), currentPath.get(i + 1)) / (road.getSpeedLimit());
                                    } else if (road.node1 == currentPath.get(i + 1) && road.node2 == currentPath.get(i)) {
                                        costToGo += calcDistance(currentPath.get(i), currentPath.get(i + 1)) / (road.getSpeedLimit());
                                    } else if (road.node1 == enemy.curInt && road.node2 == currentPath.get(i)) {
                                        costToGo += calcDistance(enemy.curInt, currentPath.get(i)) / (road.getSpeedLimit());
                                    } else if (road.node1 == currentPath.get(i) && road.node2 == enemy.curInt) {
                                        costToGo += calcDistance(enemy.curInt, currentPath.get(i)) / (road.getSpeedLimit());
                                    }
                                }
                            }
                            for (int i = currentPath.size() - 1; i > -1; i--) {
                                enemy.intPath.add(currentPath.get(i));
                            }
                        } else {
                            float temp = 0;
                            for (int i = 0; i < currentPath.size() - 1; i++) {
                                for (Road road : level.roads) {
                                    if (road.node1 == currentPath.get(i) && road.node2 == currentPath.get(i + 1)) {
                                        temp += calcDistance(currentPath.get(i), currentPath.get(i + 1)) / (road.getSpeedLimit());
                                    } else if (road.node1 == currentPath.get(i + 1) && road.node2 == currentPath.get(i)) {
                                        temp += calcDistance(currentPath.get(i), currentPath.get(i + 1)) / (road.getSpeedLimit());
                                    } else if (road.node1 == enemy.curInt && road.node2 == currentPath.get(i)) {
                                        temp += calcDistance(enemy.curInt, currentPath.get(i)) / (road.getSpeedLimit());
                                    } else if (road.node1 == currentPath.get(i) && road.node2 == enemy.curInt) {
                                        temp += calcDistance(enemy.curInt, currentPath.get(i)) / (road.getSpeedLimit());
                                    }
                                }
                            }

                            if (temp < costToGo) {
                                costToGo = temp;
                                enemy.intPath = new Stack<Intersection>();
                                for (int i = currentPath.size() - 1; i > -1; i--) {
                                    enemy.intPath.add(currentPath.get(i));
                                }
                            }
                        }
                    }
                } else {
                    makeEnemyPath(intersection2, fixedList, TF, enemy);
                }
                currentPath.remove(intersection2);
            }
        }
        intersectionsClose = new ArrayList<Intersection>();
    }

    public void makeEnemyPathMod(Intersection previous, ArrayList<Intersection> initFixedList, Boolean TF, Enemy enemy) {
        ArrayList<Intersection> intersectionsClose = new ArrayList<Intersection>();
        ArrayList<Intersection> fixedList = new ArrayList<Intersection>(initFixedList.size());
        for (Intersection intersection : initFixedList) {
            fixedList.add(intersection);
        }
        if (TF = true) {
            fixedList.remove(level.startingLocation);
            fixedList.remove(level.destination);
            TF = false;
        }

        fixedList.remove(previous);
        if (enemy.selInt.isOpen() && enemy.selInt != enemy.curInt) {
            for (Intersection intersection : fixedList) {
                for (Road road : level.roads) {
                    if (road.node1 == previous && road.node2 == intersection) {
                        intersectionsClose.add(intersection);
                    } else if (road.node1 == intersection && road.node2 == previous) {
                        intersectionsClose.add(intersection);
                    }
                }
            }

            for (Intersection intersection2 : intersectionsClose) {
                currentPath.add(intersection2);
                if (intersection2 == enemy.selInt) {
                    if (checkPathForProperOneWaysAI(currentPath, enemy)) {
                        if (firstRun) {
                            firstRun = false;
                            enemy.intPath = new Stack<Intersection>();
                            for (int i = 0; i < currentPath.size() - 1; i++) {
                                for (Road road : level.roads) {
                                    if (road.node1 == currentPath.get(i) && road.node2 == currentPath.get(i + 1)) {
                                        costToGo += calcDistance(currentPath.get(i), currentPath.get(i + 1)) / (road.getSpeedLimit());
                                    } else if (road.node1 == currentPath.get(i + 1) && road.node2 == currentPath.get(i)) {
                                        costToGo += calcDistance(currentPath.get(i), currentPath.get(i + 1)) / (road.getSpeedLimit());
                                    } else if (road.node1 == enemy.curInt && road.node2 == currentPath.get(i)) {
                                        costToGo += calcDistance(enemy.curInt, currentPath.get(i)) / (road.getSpeedLimit());
                                    } else if (road.node1 == currentPath.get(i) && road.node2 == enemy.curInt) {
                                        costToGo += calcDistance(enemy.curInt, currentPath.get(i)) / (road.getSpeedLimit());
                                    }
                                }
                            }
                            for (int i = currentPath.size() - 1; i > -1; i--) {
                                enemy.intPath.add(currentPath.get(i));
                            }
                        } else {
                            float temp = 0;
                            for (int i = 0; i < currentPath.size() - 1; i++) {
                                for (Road road : level.roads) {
                                    if (road.node1 == currentPath.get(i) && road.node2 == currentPath.get(i + 1)) {
                                        temp += calcDistance(currentPath.get(i), currentPath.get(i + 1)) / (road.getSpeedLimit());
                                    } else if (road.node1 == currentPath.get(i + 1) && road.node2 == currentPath.get(i)) {
                                        temp += calcDistance(currentPath.get(i), currentPath.get(i + 1)) / (road.getSpeedLimit());
                                    } else if (road.node1 == enemy.curInt && road.node2 == currentPath.get(i)) {
                                        temp += calcDistance(enemy.curInt, currentPath.get(i)) / (road.getSpeedLimit());
                                    } else if (road.node1 == currentPath.get(i) && road.node2 == enemy.curInt) {
                                        temp += calcDistance(enemy.curInt, currentPath.get(i)) / (road.getSpeedLimit());
                                    }
                                }
                            }

                            if (temp < costToGo) {
                                costToGo = temp;
                                enemy.intPath = new Stack<Intersection>();
                                for (int i = currentPath.size() - 1; i > -1; i--) {
                                    enemy.intPath.add(currentPath.get(i));
                                }
                            }
                        }
                    }
                } else {
                    makeEnemyPathMod(intersection2, fixedList, TF, enemy);
                }
                currentPath.remove(intersection2);
            }
        }
        intersectionsClose = new ArrayList<Intersection>();
    }

    public Intersection generateRandomPosition() {
        ArrayList<Intersection> fixedList = new ArrayList<Intersection>();
        for (Intersection intersection : level.intersections) {
            if (intersection.isOpen()) {
                fixedList.add(intersection);
            }
        }
        fixedList.remove(level.startingLocation);
        fixedList.remove(level.destination);

        Random rand = new Random();
        int randomNum = rand.nextInt(fixedList.size());

        return fixedList.get(randomNum);

    }

    public Intersection generateRandomPositionMod() {
        ArrayList<Intersection> fixedList = new ArrayList<Intersection>();
        for (Intersection intersection : level.intersections) {
            fixedList.add(intersection);
        }
        fixedList.remove(level.startingLocation);
        fixedList.remove(level.destination);

        Random rand = new Random();
        int randomNum = rand.nextInt(fixedList.size());

        return fixedList.get(randomNum);

    }

    public void generateZombiePath(Enemy enemy) {
        ArrayList<Intersection> fixedList = new ArrayList<Intersection>();
        for (Intersection intersection : level.intersections) {
            if (intersection.isOpen()) {
                fixedList.add(intersection);
            }
        }
        fixedList.remove(level.startingLocation);
        fixedList.remove(level.destination);

        for (int i = 0; i < 3; i++) {
            Random rand = new Random();
            int randomNum = rand.nextInt(fixedList.size());

            enemy.nodesToVisit.add(fixedList.get(randomNum));
            fixedList.remove(randomNum);
        }

    }

    public void resetEnemies() {
        enemies = new ArrayList<Enemy>();
        banditNodes = new ArrayList<Intersection>();
        resetStack();
        miniGame.getXpme().intersectionPath = new Stack<Intersection>();
    }

    /**
     * @return the previousIntersection
     */
    public Intersection getPreviousIntersection() {
        return previousIntersection;
    }

    /**
     * @param previousIntersection the previousIntersection to set
     */
    public void setPreviousIntersection(Intersection previousIntersection) {
        this.previousIntersection = previousIntersection;
    }

    /**
     * @return the positiongNowInt
     */
    public Intersection getPositionNowInt() {
        return positionNowInt;
    }

    /**
     * @param positiongNowInt the positiongNowInt to set
     */
    public void setPositionNowInt(Intersection positionNowInt) {
        this.positionNowInt = positionNowInt;
    }

    public void checkForCollision() {
        Enemy enemyToRemove = null;
        for (Enemy enemy : enemies) {
            float x = (float) (enemy.s.getX() + 28.5);
            float y = (float) (enemy.s.getY() + 28.5);
            float pcX = miniGame.getGUIButtons().get(UI_BUTTON_TYPE + "PC").getX();
            float pcY = miniGame.getGUIButtons().get(UI_BUTTON_TYPE + "PC").getY();

            if (x >= pcX && x <= (pcX + 57) && y >= pcY && y <= (pcY + 57)) {
                if (powerups[15]) {
                    enemyToRemove = enemy;
                    if (isSound()) {
                        miniGame.getAudio().play(XPath.XPathPropertyType.COLLISION.toString(), true);
                    }
                } else if (powerups[10]) {
                    if (enemy.robbedCooldown == 0) {
                        playerCar.setMoney(playerCar.getMoney() + 5 + (int) (Math.random() * ((20 - 5) + 1)));
                        if (isSound()) {
                            miniGame.getAudio().play(XPath.XPathPropertyType.COLLISION.toString(), true);
                        }
                        enemy.robbedCooldown = 300;
                    }
                } else if (powerups[12]) {
                } else {
                    if (enemy.type.equals("POLICE")) {
                        playerCar.collidePolice();
                    }
                    if (enemy.type.equals("BANDIT")) {
                        playerCar.collideBandit();
                    }
                    if (enemy.type.equals("ZOMBIE")) {
                        playerCar.collideZombie();
                    }
                }
            }
        }
        enemies.remove(enemyToRemove);
    }

    public void checkForSuperCollisions(Enemy enemyCompare) {
        float pcX = miniGame.getGUIButtons().get(UI_BUTTON_TYPE + "PC").getX();
        float pcY = miniGame.getGUIButtons().get(UI_BUTTON_TYPE + "PC").getY();
        for (Enemy enemy : enemies) {
            float x = (float) (enemy.s.getX() + 28.5);
            float y = (float) (enemy.s.getY() + 28.5);

            if (enemy != enemyCompare) {
                if (x >= enemyCompare.s.getX() && x <= (enemyCompare.s.getX() + 57) && y >= enemyCompare.s.getY() && y <= (enemyCompare.s.getY() + 57)) {
                    if (!enemy.hitByTerror) {
                        enemy.moveCooldown = 600;
                        enemy.hitByTerror = true;
                    }
                }
            }
        }
        if (enemyCompare.s.getX() >= pcX && enemyCompare.s.getX() <= (pcX + 57) && enemyCompare.s.getY() >= pcY && enemyCompare.s.getY() <= (pcY + 57)) {
            if (!playerCar.hitByTerror) {
                playerCar.moveCooldown = 600;
                playerCar.hitByTerror = true;
            }
        }
    }

    /**
     * @return the sound
     */
    public boolean isSound() {
        return sound;
    }

    /**
     * @param sound the sound to set
     */
    public void setSound(boolean sound) {
        this.sound = sound;
    }

    /**
     * @return the music
     */
    public boolean isMusic() {
        return music;
    }

    /**
     * @param music the music to set
     */
    public void setMusic(boolean music) {
        this.music = music;
    }
}
