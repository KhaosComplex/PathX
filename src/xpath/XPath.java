/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xpath;

import xpath.ui.XPathMiniGame;
import xpath.ui.XPathErrorHandler;
import xml_utilities.InvalidXMLFileFormatException;
import properties_manager.PropertiesManager;
import static xpath.XPathConstants.*;

/**
 *
 * @author Khaos
 */
public class XPath {

    static XPathMiniGame miniGame = new XPathMiniGame();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // LOAD THE SETTINGS FOR STARTING THE APP
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            props.addProperty(PropertiesManager.DATA_PATH_PROPERTY, PATH_DATA);
            props.loadProperties(PROPERTIES_FILE_NAME, PROPERTIES_SCHEMA_FILE_NAME);

            // THEN WE'LL LOAD THE GAME FLAVOR AS SPECIFIED BY THE PROPERTIES FILE
            String gameFlavorFile = props.getProperty(XPathPropertyType.FILE_GAME_PROPERTIES);
            props.loadProperties(gameFlavorFile, PROPERTIES_SCHEMA_FILE_NAME);

            // NOW WE CAN LOAD THE UI, WHICH WILL USE ALL THE FLAVORED CONTENT
            String appTitle = props.getProperty(XPathPropertyType.TEXT_TITLE_BAR_GAME);
            miniGame.initMiniGame(appTitle, FPS, WINDOW_WIDTH, WINDOW_HEIGHT);

            miniGame.startGame();

        } // THERE WAS A PROBLEM LOADING THE PROPERTIES FILE
        catch (InvalidXMLFileFormatException ixmlffe) {
            // LET THE ERROR HANDLER PROVIDE THE RESPONSE
            XPathErrorHandler errorHandler = miniGame.getErrorHandler();
            errorHandler.processError(XPathPropertyType.TEXT_ERROR_LOADING_XML_FILE);
        }
    }

    /**
     * XPathPropertyType represents the types of data that will need to be
     * extracted from XML files.
     */
    public enum XPathPropertyType {
        // LOADED FROM properties.xml

        /* SETUP FILE NAMES */
        FILE_GAME_PROPERTIES,
        FILE_PLAYER_RECORD,
        /* DIRECTORY PATHS FOR FILE LOADING */
        PATH_AUDIO,
        PATH_IMG,
        // LOADED FROM THE GAME FLAVOR PROPERTIES XML FILE
        // xpath_properties.xml

        /* IMAGE FILE NAMES */
        IMAGE_BACKGROUND_GAME,
        IMAGE_BACKGROUND_MENU,
        IMAGE_BACKGROUND_MAP,
        IMAGE_BACKGROUND_OTHER,
        IMAGE_BACKGROUND_OTHER2,
        IMAGE_BACKGROUND_OTHER3,
        IMAGE_PAUSE,
        IMAGE_PAUSE_MO,
        IMAGE_PLAY,
        IMAGE_PLAY_MO,
        IMAGE_MAP,
        IMAGE_BUTTON_NEW,
        IMAGE_BUTTON_NEW_MOUSE_OVER,
        IMAGE_BUTTON_BACK,
        IMAGE_BUTTON_BACK_MOUSE_OVER,
        IMAGE_BUTTON_STATS,
        IMAGE_BUTTON_STATS_MOUSE_OVER,
        IMAGE_BUTTON_UNDO,
        IMAGE_BUTTON_UNDO_MOUSE_OVER,
        IMAGE_BUTTON_TEMP_TILE,
        IMAGE_BUTTON_TEMP_TILE_MOUSE_OVER,
        IMAGE_CURSOR_WAND,
        IMAGE_DECOR_TIME,
        IMAGE_DECOR_MISCASTS,
        IMAGE_DIALOG_STATS,
        IMAGE_DIALOG_WIN,
        IMAGE_PLAY_BUTTON,
        IMAGE_PLAY_BUTTON_MO,
        IMAGE_BUTTON_LEFT,
        IMAGE_BUTTON_LEFT_MO,
        IMAGE_BUTTON_RIGHT,
        IMAGE_BUTTON_RIGHT_MO,
        IMAGE_BUTTON_UP,
        IMAGE_BUTTON_UP_MO,
        IMAGE_BUTTON_DOWN,
        IMAGE_BUTTON_DOWN_MO,
        IMAGE_BUTTON_QUIT,
        IMAGE_BUTTON_QUIT_MOUSE_OVER,
        IMAGE_BUTTON_LOCKED,
        IMAGE_BUTTON_UNLOCKED,
        IMAGE_BUTTON_ROBBED,
        IMAGE_SPRITE_SHEET_CHARACTER_TILES,
        IMAGE_TILE_BACKGROUND,
        IMAGE_TILE_BACKGROUND_SELECTED,
        IMAGE_TILE_BACKGROUND_MOUSE_OVER,
        IMAGE_BG_NOTIFY,
        IMAGE_CLOSE,
        IMAGE_PC,
        IMAGE_CLOSE_MO,
        IMAGE_CLOSE_S,
        IMAGE_CLOSE_S_MO,
        IMAGE_QUIT_S,
        IMAGE_QUIT_S_MO,
        IMAGE_PA_S,
        IMAGE_PA_S_MO,
        IMAGE_SETTINGS,
        IMAGE_CHECK,
        IMAGE_ABOUT,
        IMAGE_WINDOW_ICON,
        /* GAME TEXT */
        TEXT_ERROR_LOADING_AUDIO,
        TEXT_ERROR_LOADING_LEVEL,
        TEXT_ERROR_LOADING_RECORD,
        TEXT_ERROR_LOADING_XML_FILE,
        TEXT_ERROR_SAVING_RECORD,
        TEXT_LABEL_STATS_ALGORITHM,
        TEXT_LABEL_STATS_GAMES,
        TEXT_LABEL_STATS_WINS,
        TEXT_LABEL_STATS_PERFECT_WINS,
        TEXT_LABEL_STATS_FASTEST_PERFECT_WIN,
        TEXT_PROMPT_EXIT,
        TEXT_TITLE_BAR_GAME,
        TEXT_TITLE_BAR_ERROR,
        /* AUDIO CUES */
        EFFECT,
        COLLISION,
        SONG_CUE_MENU_SCREEN,
        /* TILE LOADING STUFF */
        LEVEL_OPTIONS,
        LEVEL_OPTIONS_X,
        LEVEL_OPTIONS_Y,
        POWERUP_OPTIONS,
        LEVEL_IMAGE_OPTIONS,
        LEVEL_MOUSE_OVER_IMAGE_OPTIONS,
        MAIN_MENU_IMAGE_OPTIONS,
        MAIN_MENU_MOUSE_OVER_IMAGE_OPTIONS,
    }

    public enum XPathGUIState {

        INVISIBLE_STATE,
        VISIBLE_STATE,
        SELECTED_STATE,
        MOUSE_OVER_STATE
    }
}
