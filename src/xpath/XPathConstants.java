/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xpath;

import java.awt.Color;

/**
 *
 * @author Khaos
 */
public class XPathConstants {

    // WE NEED THESE CONSTANTS JUST TO GET STARTED
    // LOADING SETTINGS FROM OUR XML FILES
    public static String PROPERTY_TYPES_LIST = "property_types.txt";
    public static String PROPERTIES_FILE_NAME = "properties.xml";
    public static String PROPERTIES_SCHEMA_FILE_NAME = "properties_schema.xsd";
    public static String PATH_DATA = "./data/";

    // THESE ARE THE TYPES OF CONTROLS, WE USE THESE CONSTANTS BECAUSE WE'LL
    // STORE THEM BY TYPE, SO THESE WILL PROVIDE A MEANS OF IDENTIFYING THEM
    // EACH SCREEN HAS ITS OWN BACKGROUND TYPE
    public static final String BACKGROUND_TYPE = "BACKGROUND_TYPE";
    public static final String BACKGROUND_TYPE_OTHER = "BACKGROUND_TYPE_OTHER";
    public static final String BACKGROUND_MAP = "BACKGROUND_MAP";
    public static final String BG_NOTIFY = "BG_NOTIFY";
    public static final String BG_SETTINGS = "BG_SETTINGS";
    public static final String BG_ABOUT = "BG_ABOUT";

    // THIS REPRESENTS THE BUTTONS ON THE MENU SCREEN FOR LEVEL SELECTION
    public static final String LEVEL_SELECT_BUTTON_TYPE = "LEVEL_SELECT_BUTTON_TYPE";
    
    public static final String POWERUP_BUTTON_TYPE = "POWERUP_BUTTON_TYPE";

    // THIS REPRESENTS THE BUTTONS ON THE MENU SCREEN FOR CHOICE SELECTION
    public static final String MENU_SELECT_BUTTON_TYPE = "MENU_SELECT_BUTTON_TYPE";

    // THIS REPRESENTS THE BUTTONS ON THE MENU SCREEN FOR CHOICE SELECTION
    public static final String UI_BUTTON_TYPE = "UI_BUTTON_TYPE";

    // THIS REPRESENTS THE ARROWS USED FOR SCROLLING
    public static final String ARROW_BUTTON_TYPE = "xARROW_BUTTON_TYPE";

    // LEVEL TYPES
    public static final String LOCKED = "LOCKED";
    public static final String UNLOCKED = "UNLOCKED";
    public static final String COMPLETED = "COMPLETED";

    // IN-GAME UI CONTROL TYPES
    public static final String NEW_GAME_BUTTON_TYPE = "NEW_GAME_BUTTON_TYPE";
    public static final String BACK_BUTTON_TYPE = "BACK_BUTTON_TYPE";
    public static final String MISCASTS_COUNT_TYPE = "TILE_COUNT_TYPE";
    public static final String TIME_TYPE = "TIME_TYPE";
    public static final String STATS_BUTTON_TYPE = "STATS_BUTTON_TYPE";
    public static final String UNDO_BUTTON_TYPE = "UNDO_BUTTON_TYPE";
    public static final String ALGORITHM_TYPE = "ALGORITHM_TYPE";

    // DIALOG TYPES
    public static final String WIN_DIALOG_TYPE = "WIN_DIALOG_TYPE";
    public static final String STATS_DIALOG_TYPE = "STATS_DIALOG_TYPE";

    // WE'LL USE THESE STATES TO CONTROL SWITCHING BETWEEN THE TWO
    public static final String MENU_SCREEN_STATE = "MENU_SCREEN_STATE";
    public static final String MAP_SCREEN_STATE = "MAP_SCREEN_STATE";
    public static final String GAME_SCREEN_STATE = "GAME_SCREEN_STATE";

    // ANIMATION SPEED
    public static int FPS = 30;

    // UI CONTROL SIZE AND POSITION SETTINGS
    public static final int WINDOW_WIDTH = 640;
    public static final int WINDOW_HEIGHT = 480;
    public static final int VIEWPORT_MARGIN_LEFT = 20;
    public static final int VIEWPORT_MARGIN_RIGHT = 20;
    public static final int VIEWPORT_MARGIN_TOP = 20;
    public static final int VIEWPORT_MARGIN_BOTTOM = 20;
    public static final int LEVEL_BUTTON_WIDTH = 120;
    public static final int LEVEL_BUTTON_MARGIN = 5;
    public static final int LEVEL_BUTTON_Y = 350;
    public static final int PLAY_BUTTON_Y = 200;
    public static final int PLAY_BUTTON_X = 260;
    public static final int VIEWPORT_INC = 5;

    // COLORS USED FOR RENDERING VARIOUS THINGS, INCLUDING THE
    // COLOR KEY, WHICH REFERS TO THE COLOR TO IGNORE WHEN
    // LOADING ART.
    public static final Color COLOR_KEY = new Color(255, 174, 201);
    public static final Color COLOR_TEXT_DISPLAY = new Color(10, 160, 10);

    // CONSTANTS FOR LOADING DATA FROM THE XML FILES
    // THESE ARE THE XML NODES
    public static final String LEVEL_NODE = "level";
    public static final String INTERSECTIONS_NODE = "intersections";
    public static final String INTERSECTION_NODE = "intersection";
    public static final String ROADS_NODE = "roads";
    public static final String ROAD_NODE = "road";
    public static final String START_INTERSECTION_NODE = "start_intersection";
    public static final String DESTINATION_INTERSECTION_NODE = "destination_intersection";
    public static final String MONEY_NODE = "money";
    public static final String POLICE_NODE = "police";
    public static final String BANDITS_NODE = "bandits";
    public static final String ZOMBIES_NODE = "zombies";

    // AND THE ATTRIBUTES FOR THOSE NODES
    public static final String NAME_ATT = "name";
    public static final String IMAGE_ATT = "image";
    public static final String ID_ATT = "id";
    public static final String X_ATT = "x";
    public static final String Y_ATT = "y";
    public static final String OPEN_ATT = "open";
    public static final String INT_ID1_ATT = "int_id1";
    public static final String INT_ID2_ATT = "int_id2";
    public static final String SPEED_LIMIT_ATT = "speed_limit";
    public static final String ONE_WAY_ATT = "one_way";
    public static final String AMOUNT_ATT = "amount";
    public static final String NUM_ATT = "num";

    // LEVEL EDITOR PATHS
    public static final String BUTTONS_PATH = PATH_DATA + "buttons/";
    public static final String CURSORS_PATH = PATH_DATA + "cursors/";
    public static final String LEVELS_PATH = PATH_DATA + "xpath/";

    // DEFAULT IMAGE FILES
    public static final String DEFAULT_BG_IMG = "DeathValleyBackground.png";
    public static final String DEFAULT_START_IMG = "DefaultStartLocation.png";
    public static final String DEFAULT_DEST_IMG = "DefaultDestination.png";

    // INITIAL START/DEST LOCATIONS
    public static final int DEFAULT_START_X = 32;
    public static final int DEFAULT_START_Y = 100;
    public static final int DEFAULT_DEST_X = 650;
    public static final int DEFAULT_DEST_Y = 100;

    // FOR INITIALIZING THE SPINNERS
    public static final int MIN_BOTS_PER_LEVEL = 0;
    public static final int MAX_BOTS_PER_LEVEL = 50;
    public static final int BOTS_STEP = 1;
    public static final int MIN_MONEY = 100;
    public static final int MAX_MONEY = 10000;
    public static final int STEP_MONEY = 100;
    public static final int DEFAULT_MONEY = 100;

    // AND FOR THE ROAD SPEED LIMITS
    public static final int DEFAULT_SPEED_LIMIT = 30;
    public static final int MIN_SPEED_LIMIT = 10;
    public static final int MAX_SPEED_LIMIT = 100;
    public static final int SPEED_LIMIT_STEP = 10;

    // FOR LOADING STUFF FROM OUR LEVEL XML FILES    
    // THIS IS THE NAME OF THE SCHEMA
    public static final String LEVEL_SCHEMA = "PathXLevelSchema.xsd";
    public static final String XML_LEVEL_FILE_EXTENSION = ".xml";

    // RENDERING SETTINGS
    public static final int INTERSECTION_RADIUS = 20;
    public static final int INT_STROKE = 3;
    public static final int ONE_WAY_TRIANGLE_HEIGHT = 40;
    public static final int ONE_WAY_TRIANGLE_WIDTH = 60;

    // DEFAULT COLORS
    public static final Color INT_OUTLINE_COLOR = Color.BLACK;
    public static final Color HIGHLIGHTED_COLOR = Color.YELLOW;
    public static final Color OPEN_INT_COLOR = Color.GREEN;
    public static final Color CLOSED_INT_COLOR = Color.RED;
}
