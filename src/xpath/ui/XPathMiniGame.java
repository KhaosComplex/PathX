package xpath.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mini_game.MiniGame;
import mini_game.MiniGameState;
import mini_game.Sprite;
import mini_game.SpriteType;
import mini_game.Viewport;
import properties_manager.PropertiesManager;
import xpath.XPath.XPathGUIState;
import xpath.XPath.XPathPropertyType;
import static xpath.XPathConstants.*;
import xpath.data.XPathDataModel;
import xpath.file.XPathFileManager;
import xpath.file.XPathModel;

/**
 *
 * @author Alexander Greenstein ID: 109360498
 */
public class XPathMiniGame extends MiniGame {

    // HANDLES ERROR CONDITIONS
    private XPathErrorHandler errorHandler;

    // THE SCREEN CURRENTLY BEING PLAYED
    private String currentScreenState;

    // HANDLES GAME UI EVENTS
    private XPathEventHandler eventHandler;

    // MANAGES LOADING OF LEVELS AND THE PLAYER RECORDS FILES
    private XPathFileManager fileManager;

    // THE LEVEL
    private XPathModel model;

    // VIEWPORT
    private Viewport viewport = new Viewport();

    private XPathMouseEvents xpme;

    @Override
    public void initAudioContent() {
        try {
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            String audioPath = props.getProperty(XPathPropertyType.PATH_AUDIO);

            // LOAD ALL THE AUDIO
            loadAudioCue(XPathPropertyType.EFFECT);
            loadAudioCue(XPathPropertyType.COLLISION);
            loadAudioCue(XPathPropertyType.SONG_CUE_MENU_SCREEN);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InvalidMidiDataException | MidiUnavailableException e) {

        }
    }

    /**
     * This helper method loads the audio file associated with audioCueType,
     * which should have been specified via an XML properties file.
     */
    private void loadAudioCue(XPathPropertyType audioCueType)
            throws UnsupportedAudioFileException, IOException, LineUnavailableException,
            InvalidMidiDataException, MidiUnavailableException {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String audioPath = props.getProperty(XPathPropertyType.PATH_AUDIO);
        String cue = props.getProperty(audioCueType.toString());
        audio.loadAudio(audioCueType.toString(), audioPath + cue);
    }

    @Override
    public void initData() {
        // INIT OUR ERROR HANDLER
        errorHandler = new XPathErrorHandler(getWindow());

        // INIT OUR DATA MANAGER
        data = new XPathDataModel(this);

        model = new XPathModel(this);

        fileManager = new XPathFileManager(model, XML_LEVEL_FILE_EXTENSION);

        fileManager.loadModel();

        xpme = new XPathMouseEvents(this, model);

        if (model.isMusic()) {
            audio.play(XPathPropertyType.SONG_CUE_MENU_SCREEN.toString(), true);
        }
    }

    @Override
    public void initGUIControls() {
        // WE'LL USE AND REUSE THESE FOR LOADING STUFF
        BufferedImage img;
        float x, y;
        SpriteType sT;
        Sprite s;

        // FIRST PUT THE ICON IN THE WINDOW
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imgPath = props.getProperty(XPathPropertyType.PATH_IMG);
        String windowIconFile = props.getProperty(XPathPropertyType.IMAGE_WINDOW_ICON);
        img = loadImage(imgPath + windowIconFile);
        getWindow().setIconImage(img);

        // CONSTRUCT THE PANEL WHERE WE'LL DRAW EVERYTHING
        canvas = new XPathPanel(this, (XPathDataModel) data, getModel());

        // LOAD THE BACKGROUNDS, WHICH ARE GUI DECOR
        currentScreenState = MENU_SCREEN_STATE;
        img = loadImage(imgPath + props.getProperty(XPathPropertyType.IMAGE_BACKGROUND_MENU));
        sT = new SpriteType(BACKGROUND_TYPE);
        sT.addState(MENU_SCREEN_STATE, img);
        img = loadImage(imgPath + props.getProperty(XPathPropertyType.IMAGE_BACKGROUND_GAME));
        sT.addState(GAME_SCREEN_STATE, img);
        img = loadImage(imgPath + props.getProperty(XPathPropertyType.IMAGE_BACKGROUND_MAP));
        sT.addState(MAP_SCREEN_STATE, img);
        s = new Sprite(sT, 0, 0, 0, 0, MENU_SCREEN_STATE);
        guiDecor.put(BACKGROUND_TYPE, s);

        viewport.setGameWorldSize(1346, 875);
        viewport.setViewportSize(625, 334);
        viewport.setScreenSize(640, 480);
        viewport.updateViewportMaxMin();

        img = loadImage(imgPath + props.getProperty(XPathPropertyType.IMAGE_MAP));
        sT = new SpriteType(BACKGROUND_MAP);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        s = new Sprite(sT, 0, 106, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.put(BACKGROUND_MAP, s);

        img = loadImage(imgPath + props.getProperty(XPathPropertyType.IMAGE_BACKGROUND_OTHER));
        sT = new SpriteType(BACKGROUND_TYPE_OTHER);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        s = new Sprite(sT, 0, 103, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.put(BACKGROUND_TYPE_OTHER, s);

        img = loadImage(imgPath + props.getProperty(XPathPropertyType.IMAGE_BACKGROUND_OTHER2));
        sT = new SpriteType(BACKGROUND_TYPE_OTHER);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        s = new Sprite(sT, 0, 0, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.put(BACKGROUND_TYPE_OTHER + "2", s);

        img = loadImage(imgPath + props.getProperty(XPathPropertyType.IMAGE_BACKGROUND_OTHER3));
        sT = new SpriteType(BACKGROUND_TYPE_OTHER);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        s = new Sprite(sT, 0, 0, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.put(BACKGROUND_TYPE_OTHER + "3", s);

        img = loadImage(imgPath + props.getProperty(XPathPropertyType.IMAGE_SETTINGS));
        sT = new SpriteType(BG_SETTINGS);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        s = new Sprite(sT, 60, 5, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.put(BG_SETTINGS, s);

        img = loadImage(imgPath + props.getProperty(XPathPropertyType.IMAGE_ABOUT));
        sT = new SpriteType(BG_ABOUT);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        s = new Sprite(sT, 60, 5, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.put(BG_ABOUT, s);

        img = loadImage(imgPath + props.getProperty(XPathPropertyType.IMAGE_BG_NOTIFY));
        sT = new SpriteType(BG_NOTIFY);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        s = new Sprite(sT, 129, 60, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.put(BG_NOTIFY, s);

        // ADD A BUTTON FOR EACH MENU OPTION AVAILABLE
        ArrayList<String> menuImageNames = props.getPropertyOptionsList(XPathPropertyType.MAIN_MENU_IMAGE_OPTIONS);
        ArrayList<String> menuMouseOverImageNames = props.getPropertyOptionsList(XPathPropertyType.MAIN_MENU_MOUSE_OVER_IMAGE_OPTIONS);
        float totalWidth = menuImageNames.size() * (LEVEL_BUTTON_WIDTH + LEVEL_BUTTON_MARGIN) - LEVEL_BUTTON_MARGIN;
        Viewport viewport = data.getViewport();
        x = (viewport.getScreenWidth() - totalWidth) / 2.0f;
        for (int i = 0; i < menuImageNames.size(); i++) {
            sT = new SpriteType(MENU_SELECT_BUTTON_TYPE);
            img = loadImageWithColorKey(imgPath + menuImageNames.get(i), COLOR_KEY);
            sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
            img = loadImageWithColorKey(imgPath + menuMouseOverImageNames.get(i), COLOR_KEY);
            sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
            s = new Sprite(sT, x, LEVEL_BUTTON_Y + 25, 0, 0, XPathGUIState.VISIBLE_STATE.toString());
            guiButtons.put(MENU_SELECT_BUTTON_TYPE + i, s);
            x += LEVEL_BUTTON_WIDTH + LEVEL_BUTTON_MARGIN;
        }

        // ADD A BUTTON FOR EACH LEVEL OPTION AVAILABLE
        ArrayList<String> levels = props.getPropertyOptionsList(XPathPropertyType.LEVEL_OPTIONS);
        ArrayList<String> levelsX = props.getPropertyOptionsList(XPathPropertyType.LEVEL_OPTIONS_X);
        ArrayList<String> levelsY = props.getPropertyOptionsList(XPathPropertyType.LEVEL_OPTIONS_Y);
        String whatDo = LOCKED;
        for (int i = 0; i < levels.size(); i++) {
            sT = new SpriteType(LEVEL_SELECT_BUTTON_TYPE);
            if (model.levelsUnlocked.size() > i) {
                img = loadImageWithColorKey(imgPath + props.getProperty(XPathPropertyType.IMAGE_BUTTON_UNLOCKED), COLOR_KEY);
                whatDo = UNLOCKED;
            } else {
                img = loadImageWithColorKey(imgPath + props.getProperty(XPathPropertyType.IMAGE_BUTTON_LOCKED), COLOR_KEY);
                whatDo = LOCKED;
            }
            sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
            sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
            s = new LevelSprite(sT, Float.parseFloat(levelsX.get(i)), Float.parseFloat(levelsY.get(i)), 0, 0, XPathGUIState.INVISIBLE_STATE.toString(), whatDo, i);
            guiButtons.put(levels.get(i), s);
            guiButtons.get(levels.get(i)).setActionCommand(levels.get(i));
        }

        ArrayList<String> powerups = props.getPropertyOptionsList(XPathPropertyType.POWERUP_OPTIONS);

        // ADD A BUTTON FOR EACH POWERUP
        for (int i = 0; i < 16; i++) {
            sT = new SpriteType(POWERUP_BUTTON_TYPE);
            img = loadImageWithColorKey(imgPath + powerups.get(i), COLOR_KEY);
            sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
            sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
            s = new LevelSprite(sT, +((int) (i % 4 * 30)), 215 + (((int) (i / 4)) * 30), 0, 0, XPathGUIState.INVISIBLE_STATE.toString(), UNLOCKED, 0);
            guiButtons.put(powerups.get(i), s);
            guiButtons.get(powerups.get(i)).setActionCommand(powerups.get(i));
        }

        // CLOSE BUTTON
        String close = props.getProperty(XPathPropertyType.IMAGE_CLOSE);
        sT = new SpriteType(UI_BUTTON_TYPE);
        img = loadImageWithColorKey(imgPath + close, COLOR_KEY);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        String closeMO = props.getProperty(XPathPropertyType.IMAGE_CLOSE_MO);
        img = loadImageWithColorKey(imgPath + closeMO, COLOR_KEY);
        sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
        s = new Sprite(sT, 253, 360, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.put(UI_BUTTON_TYPE + "_CLOSE", s);

        // PLAYER CAR
        String playerCar = props.getProperty(XPathPropertyType.IMAGE_PC);
        sT = new SpriteType(UI_BUTTON_TYPE);
        img = loadImageWithColorKey(imgPath + playerCar, COLOR_KEY);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        img = loadImageWithColorKey(imgPath + playerCar, COLOR_KEY);
        sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
        s = new LevelSprite(sT, 0, 0, 0, 0, XPathGUIState.INVISIBLE_STATE.toString(), UNLOCKED, 0);
        guiButtons.put(UI_BUTTON_TYPE + "PC", s);

        // SMALL CLOSE BUTTON
        String closeS = props.getProperty(XPathPropertyType.IMAGE_CLOSE_S);
        sT = new SpriteType(UI_BUTTON_TYPE);
        img = loadImageWithColorKey(imgPath + closeS, COLOR_KEY);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        String closeMOS = props.getProperty(XPathPropertyType.IMAGE_CLOSE_S_MO);
        img = loadImageWithColorKey(imgPath + closeMOS, COLOR_KEY);
        sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
        s = new Sprite(sT, 275, 330, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.put(UI_BUTTON_TYPE + "_CLOSE_S", s);

        // SMALL QUIT BUTTON
        String quitS = props.getProperty(XPathPropertyType.IMAGE_QUIT_S);
        sT = new SpriteType(UI_BUTTON_TYPE);
        img = loadImageWithColorKey(imgPath + quitS, COLOR_KEY);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        String quitSMO = props.getProperty(XPathPropertyType.IMAGE_QUIT_S_MO);
        img = loadImageWithColorKey(imgPath + quitSMO, COLOR_KEY);
        sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
        s = new Sprite(sT, 200, 330, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.put(UI_BUTTON_TYPE + "_QUIT_S", s);

        // SMALL PLAY AGAIN BUTTON
        String paS = props.getProperty(XPathPropertyType.IMAGE_PA_S);
        sT = new SpriteType(UI_BUTTON_TYPE);
        img = loadImageWithColorKey(imgPath + paS, COLOR_KEY);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        String paSMO = props.getProperty(XPathPropertyType.IMAGE_PA_S_MO);
        img = loadImageWithColorKey(imgPath + paSMO, COLOR_KEY);
        sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
        s = new Sprite(sT, 350, 330, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.put(UI_BUTTON_TYPE + "_PA_S", s);

        // PAUSE BUTTON
        String pause = props.getProperty(XPathPropertyType.IMAGE_PAUSE);
        sT = new SpriteType(UI_BUTTON_TYPE);
        img = loadImageWithColorKey(imgPath + pause, COLOR_KEY);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        String pauseMO = props.getProperty(XPathPropertyType.IMAGE_PAUSE_MO);
        img = loadImageWithColorKey(imgPath + pauseMO, COLOR_KEY);
        sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
        s = new Sprite(sT, 30, 140, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.put(UI_BUTTON_TYPE + "PAUSE", s);

        // PLAY BUTTON
        String play = props.getProperty(XPathPropertyType.IMAGE_PLAY);
        sT = new SpriteType(UI_BUTTON_TYPE);
        img = loadImageWithColorKey(imgPath + play, COLOR_KEY);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        String playMO = props.getProperty(XPathPropertyType.IMAGE_PLAY_MO);
        img = loadImageWithColorKey(imgPath + playMO, COLOR_KEY);
        sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
        s = new Sprite(sT, 30, 140, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.put(UI_BUTTON_TYPE + "PLAY", s);

        // CHECK BUTTON
        String check = props.getProperty(XPathPropertyType.IMAGE_CHECK);
        sT = new SpriteType(UI_BUTTON_TYPE);
        img = loadImageWithColorKey(imgPath + check, COLOR_KEY);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        img = loadImageWithColorKey(imgPath + check, COLOR_KEY);
        sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
        s = new Sprite(sT, 222, 153, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.put(UI_BUTTON_TYPE + "_CHECK_SOUND", s);
        s = new Sprite(sT, 222, 212, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.put(UI_BUTTON_TYPE + "_CHECK_MUSIC", s);

        // LEFT ARROW
        String leftArrow = props.getProperty(XPathPropertyType.IMAGE_BUTTON_LEFT);
        sT = new SpriteType(ARROW_BUTTON_TYPE + "_LEFT");
        img = loadImageWithColorKey(imgPath + leftArrow, COLOR_KEY);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        String leftArrowMO = props.getProperty(XPathPropertyType.IMAGE_BUTTON_LEFT_MO);
        img = loadImageWithColorKey(imgPath + leftArrowMO, COLOR_KEY);
        sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
        s = new Sprite(sT, 15, 362, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.put(ARROW_BUTTON_TYPE + "_LEFT", s);

        // RIGHT ARROW
        String rightArrow = props.getProperty(XPathPropertyType.IMAGE_BUTTON_RIGHT);
        sT = new SpriteType(ARROW_BUTTON_TYPE + "_RIGHT");
        img = loadImageWithColorKey(imgPath + rightArrow, COLOR_KEY);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        String rightArrowMO = props.getProperty(XPathPropertyType.IMAGE_BUTTON_RIGHT_MO);
        img = loadImageWithColorKey(imgPath + rightArrowMO, COLOR_KEY);
        sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
        s = new Sprite(sT, 73, 362, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.put(ARROW_BUTTON_TYPE + "_RIGHT", s);

        // UP ARROW
        String upArrow = props.getProperty(XPathPropertyType.IMAGE_BUTTON_UP);
        sT = new SpriteType(ARROW_BUTTON_TYPE + "_UP");
        img = loadImageWithColorKey(imgPath + upArrow, COLOR_KEY);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        String upArrowMO = props.getProperty(XPathPropertyType.IMAGE_BUTTON_UP_MO);
        img = loadImageWithColorKey(imgPath + upArrowMO, COLOR_KEY);
        sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
        s = new Sprite(sT, 42, 352, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.put(ARROW_BUTTON_TYPE + "_UP", s);

        // DOWN ARROW
        String downArrow = props.getProperty(XPathPropertyType.IMAGE_BUTTON_DOWN);
        sT = new SpriteType(ARROW_BUTTON_TYPE + "_DOWN");
        img = loadImageWithColorKey(imgPath + downArrow, COLOR_KEY);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        String downArrowMO = props.getProperty(XPathPropertyType.IMAGE_BUTTON_DOWN_MO);
        img = loadImageWithColorKey(imgPath + downArrowMO, COLOR_KEY);
        sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
        s = new Sprite(sT, 42, 378, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.put(ARROW_BUTTON_TYPE + "_DOWN", s);

        // HOME BUTTON
        String home = props.getProperty(XPathPropertyType.IMAGE_BUTTON_BACK);
        sT = new SpriteType(UI_BUTTON_TYPE);
        img = loadImageWithColorKey(imgPath + home, COLOR_KEY);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        String homeMO = props.getProperty(XPathPropertyType.IMAGE_BUTTON_BACK_MOUSE_OVER);
        img = loadImageWithColorKey(imgPath + homeMO, COLOR_KEY);
        sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
        s = new Sprite(sT, 525, 20, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.put(UI_BUTTON_TYPE + 1, s);

        // QUIT BUTTON
        String quit = props.getProperty(XPathPropertyType.IMAGE_BUTTON_QUIT);
        sT = new SpriteType(UI_BUTTON_TYPE);
        img = loadImageWithColorKey(imgPath + quit, COLOR_KEY);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        String quitMO = props.getProperty(XPathPropertyType.IMAGE_BUTTON_QUIT_MOUSE_OVER);
        img = loadImageWithColorKey(imgPath + quitMO, COLOR_KEY);
        sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
        s = new Sprite(sT, 580, 21, 0, 0, XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.put(UI_BUTTON_TYPE + 2, s);

        // NOW ADD THE DIALOGS
        // AND THE STATS DISPLAY
        String statsDialog = props.getProperty(XPathPropertyType.IMAGE_DIALOG_STATS);
        sT = new SpriteType(STATS_DIALOG_TYPE);
        img = loadImageWithColorKey(imgPath + statsDialog, COLOR_KEY);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        x = (viewport.getScreenWidth() / 2) - (img.getWidth(null) / 2);
        y = (viewport.getScreenHeight() / 2) - (img.getHeight(null) / 2);
        s = new Sprite(sT, x, y, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiDialogs.put(STATS_DIALOG_TYPE, s);

        // AND THE WIN CONDITION DISPLAY
        String winDisplay = props.getProperty(XPathPropertyType.IMAGE_DIALOG_WIN);
        sT = new SpriteType(WIN_DIALOG_TYPE);
        img = loadImageWithColorKey(imgPath + winDisplay, COLOR_KEY);
        sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
        x = (viewport.getScreenWidth() / 2) - (img.getWidth(null) / 2);
        y = (viewport.getScreenHeight() / 2) - (img.getHeight(null) / 2);
        s = new Sprite(sT, x, y, 0, 0, XPathGUIState.INVISIBLE_STATE.toString());
        guiDialogs.put(WIN_DIALOG_TYPE, s);

        makeSlider();

        removeSettingsAndHelpAndNotify();

    }

    @Override
    public void initGUIHandlers() {
        // WE'LL RELAY UI EVENTS TO THIS OBJECT FOR HANDLING
        eventHandler = new XPathEventHandler(this);

        // WE'LL HAVE A CUSTOM RESPONSE FOR WHEN THE USER CLOSES THE WINDOW
        getWindow().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        getWindow().addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                getEventHandler().respondToExitRequest();
            }
        });

        // SEND ALL LEVEL SELECTION HANDLING OFF TO THE EVENT HANDLER
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        ArrayList<String> levels = props.getPropertyOptionsList(XPathPropertyType.LEVEL_OPTIONS);
        ArrayList<String> powerups = props.getPropertyOptionsList(XPathPropertyType.POWERUP_OPTIONS);

        // MENU BUTTON EVENT HANDLER
        guiButtons.get(MENU_SELECT_BUTTON_TYPE + 0).setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                removeSettingsAndHelpAndNotify();
                switchToMapScreen();
            }
        });

        // MENU BUTTON EVENT HANDLER
        guiButtons.get(MENU_SELECT_BUTTON_TYPE + 1).setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                eventHandler.resetGame();
            }
        });

        // MENU BUTTON EVENT HANDLER
        guiButtons.get(MENU_SELECT_BUTTON_TYPE + 2).setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (currentScreenState == MENU_SCREEN_STATE) {
                    showSettings();
                }
            }
        });

        // MENU BUTTON EVENT HANDLER
        guiButtons.get(MENU_SELECT_BUTTON_TYPE + 3).setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                showHelp();
            }
        });

        // MENU BUTTON EVENT HANDLER
        guiButtons.get(UI_BUTTON_TYPE + 1).setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (currentScreenState == GAME_SCREEN_STATE) {
                    guiButtons.get(UI_BUTTON_TYPE + 1).setX(guiButtons.get(UI_BUTTON_TYPE + 1).getX() + 515);
                    guiButtons.get(UI_BUTTON_TYPE + 2).setX(guiButtons.get(UI_BUTTON_TYPE + 2).getX() + 515);
                    guiButtons.get(UI_BUTTON_TYPE + 1).setY(guiButtons.get(UI_BUTTON_TYPE + 1).getY() - 60);
                    guiButtons.get(UI_BUTTON_TYPE + 2).setY(guiButtons.get(UI_BUTTON_TYPE + 2).getY() - 60);
                }
                switchToMenuScreen();
                ((XPathDataModel) data).animating = false;
                ((XPathDataModel) data).animation = 0;
                removeSettingsAndHelpAndNotify();
                model.resetStack();
                model.resetEnemies();
                model.playerCar.reset();
                for (int i = 0; i < 16; i++) {
                    model.powerups[i] = false;
                }
            }
        });
        // MENU BUTTON EVENT HANDLER
        guiButtons.get(UI_BUTTON_TYPE + 2).setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (currentScreenState == MENU_SCREEN_STATE || currentScreenState == MAP_SCREEN_STATE) {
                    System.exit(0);
                } else {
                    guiButtons.get(UI_BUTTON_TYPE + 1).setX(guiButtons.get(UI_BUTTON_TYPE + 1).getX() + 515);
                    guiButtons.get(UI_BUTTON_TYPE + 2).setX(guiButtons.get(UI_BUTTON_TYPE + 2).getX() + 515);
                    guiButtons.get(UI_BUTTON_TYPE + 1).setY(guiButtons.get(UI_BUTTON_TYPE + 1).getY() - 60);
                    guiButtons.get(UI_BUTTON_TYPE + 2).setY(guiButtons.get(UI_BUTTON_TYPE + 2).getY() - 60);
                    switchToMapScreen();
                    ((XPathDataModel) data).animating = false;
                    ((XPathDataModel) data).animation = 0;
                    removeSettingsAndHelpAndNotify();
                    model.resetStack();
                    model.resetEnemies();
                    model.playerCar.reset();
                    for (int i = 0; i < 16; i++) {
                        model.powerups[i] = false;
                    }
                }
            }
        });

        // PAUSE BUTTON
        guiButtons.get(UI_BUTTON_TYPE + "PAUSE").setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                data.pause();
                guiButtons.get(UI_BUTTON_TYPE + "PLAY").setState(XPathGUIState.VISIBLE_STATE.toString());
                guiButtons.get(UI_BUTTON_TYPE + "PLAY").setEnabled(true);
                guiButtons.get(UI_BUTTON_TYPE + "PAUSE").setState(XPathGUIState.INVISIBLE_STATE.toString());
                guiButtons.get(UI_BUTTON_TYPE + "PAUSE").setEnabled(false);
            }
        });

        // PLAY BUTTON
        guiButtons.get(UI_BUTTON_TYPE + "PLAY").setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                data.unpause();
                guiButtons.get(UI_BUTTON_TYPE + "PLAY").setState(XPathGUIState.INVISIBLE_STATE.toString());
                guiButtons.get(UI_BUTTON_TYPE + "PLAY").setEnabled(false);
                guiButtons.get(UI_BUTTON_TYPE + "PAUSE").setState(XPathGUIState.VISIBLE_STATE.toString());
                guiButtons.get(UI_BUTTON_TYPE + "PAUSE").setEnabled(true);
            }
        });

        // KEY LISTENER - LET'S US PROVIDE CUSTOM RESPONSES
        this.setKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                getEventHandler().respondToKeyPress(ke.getKeyCode());
            }
        });

        // SEND ALL LEVEL SELECTION HANDLING OFF TO THE EVENT HANDLER
        for (final String levelFile : levels) {
            LevelSprite levelButton = (LevelSprite) guiButtons.get(levelFile);
            levelButton.setActionListener(new ActionListener() {
                LevelSprite s;

                public ActionListener init(LevelSprite initS) {
                    s = initS;
                    return this;
                }

                public void actionPerformed(ActionEvent ae) {
                    if (!data.isButtonIn()) {
                        if (s.getType().equals(UNLOCKED)) {
                            getEventHandler().respondToSelectLevelRequest(s.getActionCommand());
                            model = fileManager.getModel();
                            getEventHandler().loadEnemies();
                            model.levelName = levelFile;
                        }
                    } else {
                        data.setButtonIn(false);
                    }
                }

            }.init(levelButton));
        }

        // SEND ALL LEVEL SELECTION HANDLING OFF TO THE EVENT HANDLER
        for (final String powerup : powerups) {
            LevelSprite powerupButton = (LevelSprite) guiButtons.get(powerup);
            powerupButton.setActionListener(new ActionListener() {
                LevelSprite s;

                public ActionListener init(LevelSprite initS) {
                    s = initS;
                    return this;
                }

                public void actionPerformed(ActionEvent ae) {
                    getEventHandler().respondToPowerupRequest(s.getActionCommand());
                }

            }.init(powerupButton));
        }

        // ARROW BUTTON EVENT HANDLER
        guiButtons.get(ARROW_BUTTON_TYPE + "_LEFT").setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (!currentScreenState.equals(GAME_SCREEN_STATE)) {
                    viewport.setButtonScrolled(true);
                    viewport.scroll(-100, 0);
                    data.setButtonIn(false);
                } else {
                    getModel().getViewport().buttonPressed = true;
                    getModel().getViewport().move(-50, 0);
                }

            }
        });

        // ARROW BUTTON EVENT HANDLER
        guiButtons.get(ARROW_BUTTON_TYPE + "_RIGHT").setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (!currentScreenState.equals(GAME_SCREEN_STATE)) {
                    viewport.setButtonScrolled(true);
                    viewport.scroll(100, 0);
                    data.setButtonIn(false);
                } else {
                    getModel().getViewport().buttonPressed = true;
                    getModel().getViewport().move(50, 0);
                }
            }
        });

        // ARROW BUTTON EVENT HANDLER
        guiButtons.get(ARROW_BUTTON_TYPE + "_UP").setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (!currentScreenState.equals(GAME_SCREEN_STATE)) {
                    viewport.setButtonScrolled(true);
                    viewport.scroll(0, -50);
                    data.setButtonIn(false);
                } else {
                    getModel().getViewport().buttonPressed = true;
                    getModel().getViewport().move(0, -25);
                }
            }
        });

        // ARROW BUTTON EVENT HANDLER
        guiButtons.get(ARROW_BUTTON_TYPE + "_DOWN").setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (!currentScreenState.equals(GAME_SCREEN_STATE)) {
                    viewport.setButtonScrolled(true);
                    viewport.scroll(0, 50);
                    data.setButtonIn(false);
                } else {
                    getModel().getViewport().buttonPressed = true;
                    getModel().getViewport().move(0, 25);
                }
            }
        });

        // CLOSE
        guiButtons.get(UI_BUTTON_TYPE + "_CLOSE").setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (currentScreenState.equals(MENU_SCREEN_STATE)) {
                    removeSettingsAndHelpAndNotify();
                    switchToMenuScreen();
                }
            }
        });

        // SMALL CLOSE
        guiButtons.get(UI_BUTTON_TYPE + "_CLOSE_S").setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                removeSettingsAndHelpAndNotify();
            }
        });

        // SMALL QUIT
        guiButtons.get(UI_BUTTON_TYPE + "_QUIT_S").setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                guiButtons.get(UI_BUTTON_TYPE + 1).setX(guiButtons.get(UI_BUTTON_TYPE + 1).getX() + 515);
                guiButtons.get(UI_BUTTON_TYPE + 2).setX(guiButtons.get(UI_BUTTON_TYPE + 2).getX() + 515);
                guiButtons.get(UI_BUTTON_TYPE + 1).setY(guiButtons.get(UI_BUTTON_TYPE + 1).getY() - 60);
                guiButtons.get(UI_BUTTON_TYPE + 2).setY(guiButtons.get(UI_BUTTON_TYPE + 2).getY() - 60);
                switchToMapScreen();
                ((XPathDataModel) data).animating = false;
                ((XPathDataModel) data).animation = 0;
                removeSettingsAndHelpAndNotify();
                model.resetStack();
                model.resetEnemies();
                model.playerCar.reset();
                for (int i = 0; i < 16; i++) {
                    model.powerups[i] = false;
                }
            }
        });

        // SMALL PLAY AGAIN
        guiButtons.get(UI_BUTTON_TYPE + "_PA_S").setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ((XPathDataModel) data).animating = false;
                ((XPathDataModel) data).animation = 0;
                model.resetStack();
                model.resetEnemies();
                model.playerCar.reset();
                for (int i = 0; i < 16; i++) {
                    model.powerups[i] = false;
                }
                model.firstRunInt = 0;
                String levelFile = model.levelName;
                getEventHandler().respondToSelectLevelRequestAF(levelFile);
                model = fileManager.getModel();
                getEventHandler().loadEnemies();
                model.levelName = levelFile;
                removeSettingsAndHelpAndNotify();
                data.unpause();

            }
        });

        guiButtons.get(UI_BUTTON_TYPE + "_CHECK_SOUND").setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                model.setSound(!model.isSound());
                showSettings();

            }
        });

        guiButtons.get(UI_BUTTON_TYPE + "_CHECK_MUSIC").setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                model.setMusic(!model.isMusic());
                showSettings();
                if (model.isMusic()) {
                    audio.play(XPathPropertyType.SONG_CUE_MENU_SCREEN.toString(), true);
                } else {
                    audio.stop(XPathPropertyType.SONG_CUE_MENU_SCREEN.toString());
                }
            }
        });

    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateGUI() {
        // GO THROUGH THE VISIBLE BUTTONS TO TRIGGER MOUSE OVERS
        Iterator<Sprite> buttonsIt = guiButtons.values().iterator();
        while (buttonsIt.hasNext()) {
            Sprite button = buttonsIt.next();

            // ARE WE ENTERING A BUTTON?
            if (button.getState().equals(XPathGUIState.VISIBLE_STATE.toString())) {
                if (button.containsPoint(data.getLastMouseX(), data.getLastMouseY())) {
                    button.setState(XPathGUIState.MOUSE_OVER_STATE.toString());
                }
            } // ARE WE EXITING A BUTTON?
            else if (button.getState().equals(XPathGUIState.MOUSE_OVER_STATE.toString())) {
                if (!button.containsPoint(data.getLastMouseX(), data.getLastMouseY())) {
                    button.setState(XPathGUIState.VISIBLE_STATE.toString());
                }
            }
        }

    }

    public XPathErrorHandler getErrorHandler() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void switchToMenuScreen() {
        fileManager.saveGame();

        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // CHANGE THE BACKGROUND
        guiDecor.get(BACKGROUND_TYPE).setState(MAP_SCREEN_STATE);

        // DEACTIVATE THE MAIN MENU BUTTONS
        int sizeOfArrMenu = props.getPropertyOptionsList(XPathPropertyType.MAIN_MENU_IMAGE_OPTIONS).size();
        for (int i = 0; i < sizeOfArrMenu; i++) {
            guiButtons.get(MENU_SELECT_BUTTON_TYPE + i).setState(XPathGUIState.INVISIBLE_STATE.toString());
            guiButtons.get(MENU_SELECT_BUTTON_TYPE + i).setEnabled(false);
        }

        guiDecor.get(BACKGROUND_MAP).setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.get(BACKGROUND_MAP).setEnabled(false);
        guiDecor.get(BACKGROUND_TYPE_OTHER).setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.get(BACKGROUND_TYPE_OTHER + "2").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.get(BACKGROUND_TYPE_OTHER + "3").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.get(BACKGROUND_TYPE).setEnabled(false);

        ArrayList<String> levels = props.getPropertyOptionsList(XPathPropertyType.LEVEL_OPTIONS);
        for (int i = 0; i < levels.size(); i++) {
            guiButtons.get(levels.get(i)).setState(XPathGUIState.INVISIBLE_STATE.toString());
            guiButtons.get(levels.get(i)).setEnabled(false);
        }

        ArrayList<String> powerups = props.getPropertyOptionsList(XPathPropertyType.POWERUP_OPTIONS);
        for (int i = 0; i < 16; i++) {
            guiButtons.get(powerups.get(i)).setState(XPathGUIState.INVISIBLE_STATE.toString());
            guiButtons.get(powerups.get(i)).setEnabled(false);
        }

        switchToPause();

        guiButtons.get(ARROW_BUTTON_TYPE + "_LEFT").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(ARROW_BUTTON_TYPE + "_LEFT").setEnabled(false);
        guiButtons.get(ARROW_BUTTON_TYPE + "_RIGHT").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(ARROW_BUTTON_TYPE + "_RIGHT").setEnabled(false);
        guiButtons.get(ARROW_BUTTON_TYPE + "_UP").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(ARROW_BUTTON_TYPE + "_UP").setEnabled(false);
        guiButtons.get(ARROW_BUTTON_TYPE + "_DOWN").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(ARROW_BUTTON_TYPE + "_DOWN").setEnabled(false);

        // CHANGE THE BACKGROUND
        guiDecor.get(BACKGROUND_TYPE).setState(MENU_SCREEN_STATE);

        ArrayList<String> menus = props.getPropertyOptionsList(XPathPropertyType.MAIN_MENU_IMAGE_OPTIONS);
        for (int i = 0; i < menus.size(); i++) {
            guiButtons.get(MENU_SELECT_BUTTON_TYPE + i).setState(XPathGUIState.VISIBLE_STATE.toString());
            guiButtons.get(MENU_SELECT_BUTTON_TYPE + i).setEnabled(true);
        }

        guiButtons.get(UI_BUTTON_TYPE + 1).setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + 1).setEnabled(false);
        guiButtons.get(UI_BUTTON_TYPE + "PC").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "PC").setEnabled(false);
        guiButtons.get(UI_BUTTON_TYPE + "PAUSE").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "PAUSE").setEnabled(false);
        guiButtons.get(UI_BUTTON_TYPE + "PLAY").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "PLAY").setEnabled(false);

        // MAKE THE CURRENT SCREEN THE MENU SCREEN
        currentScreenState = MENU_SCREEN_STATE;

        // AND UPDATE THE DATA GAME STATE
        data.setGameState(MiniGameState.NOT_STARTED);

        // PLAY THE WELCOME SCREEN SONG
        /**
         * audio.play(XPathPropertyType.SONG_CUE_MENU_SCREEN.toString(), true);*
         */
        //audio.stop(XPathPropertyType.SONG_CUE_GAME_SCREEN.toString());
        data.unpause();
    }

    public void switchToMapScreen() {
        fileManager.saveGame();

        PropertiesManager props = PropertiesManager.getPropertiesManager();

        //audio.stop(XPathPropertyType.SONG_CUE_MENU_SCREEN.toString());
        // CHANGE THE BACKGROUND
        guiDecor.get(BACKGROUND_TYPE).setState(MAP_SCREEN_STATE);

        // DEACTIVATE THE MAIN MENU BUTTONS
        int sizeOfArrMenu = props.getPropertyOptionsList(XPathPropertyType.MAIN_MENU_IMAGE_OPTIONS).size();
        for (int i = 0; i < sizeOfArrMenu; i++) {
            guiButtons.get(MENU_SELECT_BUTTON_TYPE + i).setState(XPathGUIState.INVISIBLE_STATE.toString());
            guiButtons.get(MENU_SELECT_BUTTON_TYPE + i).setEnabled(false);
        }

        guiDecor.get(BACKGROUND_MAP).setState(XPathGUIState.VISIBLE_STATE.toString());
        guiDecor.get(BACKGROUND_MAP).setEnabled(true);
        guiDecor.get(BACKGROUND_TYPE_OTHER).setState(XPathGUIState.VISIBLE_STATE.toString());
        guiDecor.get(BACKGROUND_TYPE_OTHER + "2").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiDecor.get(BACKGROUND_TYPE_OTHER + "3").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.get(BACKGROUND_TYPE).setEnabled(true);

        ArrayList<String> levels = props.getPropertyOptionsList(XPathPropertyType.LEVEL_OPTIONS);
        for (int i = 0; i < levels.size(); i++) {
            guiButtons.get(levels.get(i)).setState(XPathGUIState.VISIBLE_STATE.toString());
            guiButtons.get(levels.get(i)).setEnabled(true);
        }

        ArrayList<String> powerups = props.getPropertyOptionsList(XPathPropertyType.POWERUP_OPTIONS);
        for (int i = 0; i < 16; i++) {
            guiButtons.get(powerups.get(i)).setState(XPathGUIState.INVISIBLE_STATE.toString());
            guiButtons.get(powerups.get(i)).setEnabled(false);
        }

        switchToPause();

        guiButtons.get(ARROW_BUTTON_TYPE + "_LEFT").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(ARROW_BUTTON_TYPE + "_LEFT").setEnabled(true);
        guiButtons.get(ARROW_BUTTON_TYPE + "_RIGHT").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(ARROW_BUTTON_TYPE + "_RIGHT").setEnabled(true);
        guiButtons.get(ARROW_BUTTON_TYPE + "_UP").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(ARROW_BUTTON_TYPE + "_UP").setEnabled(true);
        guiButtons.get(ARROW_BUTTON_TYPE + "_DOWN").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(ARROW_BUTTON_TYPE + "_DOWN").setEnabled(true);

        guiButtons.get(UI_BUTTON_TYPE + 1).setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + 1).setEnabled(true);

        guiButtons.get(UI_BUTTON_TYPE + "PC").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "PC").setEnabled(false);
        guiButtons.get(UI_BUTTON_TYPE + "PAUSE").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "PAUSE").setEnabled(false);
        guiButtons.get(UI_BUTTON_TYPE + "PLAY").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "PLAY").setEnabled(false);

        // MAKE THE CURRENT SCREEN THE MENU SCREEN
        currentScreenState = MAP_SCREEN_STATE;

        // AND UPDATE THE DATA GAME STATE
        data.setGameState(MiniGameState.NOT_STARTED);

        data.unpause();

    }

    public void switchToGameScreen() {

        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // CHANGE THE BACKGROUND
        guiDecor.get(BACKGROUND_TYPE).setState(MAP_SCREEN_STATE);

        // DEACTIVATE THE MAIN MENU BUTTONS
        int sizeOfArrMenu = props.getPropertyOptionsList(XPathPropertyType.MAIN_MENU_IMAGE_OPTIONS).size();
        for (int i = 0; i < sizeOfArrMenu; i++) {
            guiButtons.get(MENU_SELECT_BUTTON_TYPE + i).setState(XPathGUIState.INVISIBLE_STATE.toString());
            guiButtons.get(MENU_SELECT_BUTTON_TYPE + i).setEnabled(false);
        }

        guiDecor.get(BACKGROUND_MAP).setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.get(BACKGROUND_MAP).setEnabled(false);
        guiDecor.get(BACKGROUND_TYPE_OTHER).setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.get(BACKGROUND_TYPE_OTHER + "2").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.get(BACKGROUND_TYPE_OTHER + "3").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiDecor.get(BACKGROUND_TYPE).setEnabled(true);

        guiDecor.get(BG_NOTIFY).setState(XPathGUIState.VISIBLE_STATE.toString());
        guiDecor.get(BG_NOTIFY).setEnabled(true);

        ArrayList<String> levels = props.getPropertyOptionsList(XPathPropertyType.LEVEL_OPTIONS);
        for (int i = 0; i < levels.size(); i++) {
            guiButtons.get(levels.get(i)).setState(XPathGUIState.INVISIBLE_STATE.toString());
            guiButtons.get(levels.get(i)).setEnabled(false);
        }

        ArrayList<String> powerups = props.getPropertyOptionsList(XPathPropertyType.POWERUP_OPTIONS);
        for (int i = 0; i < 16; i++) {
            guiButtons.get(powerups.get(i)).setState(XPathGUIState.VISIBLE_STATE.toString());
            guiButtons.get(powerups.get(i)).setEnabled(true);
        }

        guiButtons.get(ARROW_BUTTON_TYPE + "_LEFT").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(ARROW_BUTTON_TYPE + "_LEFT").setEnabled(true);
        guiButtons.get(ARROW_BUTTON_TYPE + "_RIGHT").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(ARROW_BUTTON_TYPE + "_RIGHT").setEnabled(true);
        guiButtons.get(ARROW_BUTTON_TYPE + "_UP").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(ARROW_BUTTON_TYPE + "_UP").setEnabled(true);
        guiButtons.get(ARROW_BUTTON_TYPE + "_DOWN").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(ARROW_BUTTON_TYPE + "_DOWN").setEnabled(true);
        guiButtons.get(UI_BUTTON_TYPE + "_CLOSE_S").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "_CLOSE_S").setEnabled(true);
        guiButtons.get(UI_BUTTON_TYPE + "PC").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "PC").setEnabled(true);
        if (data.isPaused()) {
            guiButtons.get(UI_BUTTON_TYPE + "PLAY").setState(XPathGUIState.VISIBLE_STATE.toString());
            guiButtons.get(UI_BUTTON_TYPE + "PLAY").setEnabled(true);
        } else {
            guiButtons.get(UI_BUTTON_TYPE + "PAUSE").setState(XPathGUIState.VISIBLE_STATE.toString());
            guiButtons.get(UI_BUTTON_TYPE + "PAUSE").setEnabled(true);
        }

        // MAKE THE CURRENT SCREEN THE MENU SCREEN
        currentScreenState = GAME_SCREEN_STATE;

        // AND UPDATE THE DATA GAME STATE
        data.setGameState(MiniGameState.IN_PROGRESS);
    }

    public void showSettings() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // DEACTIVATE THE MAIN MENU BUTTONS
        int sizeOfArrMenu = props.getPropertyOptionsList(XPathPropertyType.MAIN_MENU_IMAGE_OPTIONS).size();
        for (int i = 0; i < sizeOfArrMenu; i++) {
            guiButtons.get(MENU_SELECT_BUTTON_TYPE + i).setState(XPathGUIState.INVISIBLE_STATE.toString());
            guiButtons.get(MENU_SELECT_BUTTON_TYPE + i).setEnabled(false);
        }

        guiDecor.get(BG_SETTINGS).setState(XPathGUIState.VISIBLE_STATE.toString());
        guiDecor.get(BG_SETTINGS).setEnabled(true);
        guiButtons.get(UI_BUTTON_TYPE + "_CLOSE").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "_CLOSE").setEnabled(true);

        if (model.isSound()) {
            guiButtons.get(UI_BUTTON_TYPE + "_CHECK_SOUND").setState(XPathGUIState.VISIBLE_STATE.toString());
            guiButtons.get(UI_BUTTON_TYPE + "_CHECK_SOUND").setEnabled(true);
        } else {
            guiButtons.get(UI_BUTTON_TYPE + "_CHECK_SOUND").setState(XPathGUIState.INVISIBLE_STATE.toString());
            guiButtons.get(UI_BUTTON_TYPE + "_CHECK_SOUND").setEnabled(true);
        }

        if (model.isMusic()) {
            guiButtons.get(UI_BUTTON_TYPE + "_CHECK_MUSIC").setState(XPathGUIState.VISIBLE_STATE.toString());
            guiButtons.get(UI_BUTTON_TYPE + "_CHECK_MUSIC").setEnabled(true);
        } else {
            guiButtons.get(UI_BUTTON_TYPE + "_CHECK_MUSIC").setState(XPathGUIState.INVISIBLE_STATE.toString());
            guiButtons.get(UI_BUTTON_TYPE + "_CHECK_MUSIC").setEnabled(true);
        }

        ((JSlider) canvas.getComponent(0)).setVisible(true);
    }

    public void removeSettingsAndHelpAndNotify() {
        guiDecor.get(BG_SETTINGS).setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.get(BG_SETTINGS).setEnabled(false);
        guiDecor.get(BG_ABOUT).setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.get(BG_ABOUT).setEnabled(false);
        guiDecor.get(BG_NOTIFY).setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiDecor.get(BG_NOTIFY).setEnabled(false);
        guiButtons.get(UI_BUTTON_TYPE + "_CLOSE").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "_CLOSE").setEnabled(false);
        guiButtons.get(UI_BUTTON_TYPE + "_CLOSE_S").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "_CLOSE_S").setEnabled(false);
        guiButtons.get(UI_BUTTON_TYPE + "_QUIT_S").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "_QUIT_S").setEnabled(false);
        guiButtons.get(UI_BUTTON_TYPE + "_PA_S").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "_PA_S").setEnabled(false);
        guiButtons.get(UI_BUTTON_TYPE + "_CHECK_SOUND").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "_CHECK_SOUND").setEnabled(false);
        guiButtons.get(UI_BUTTON_TYPE + "_CHECK_MUSIC").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "_CHECK_MUSIC").setEnabled(false);
        ((JSlider) canvas.getComponent(0)).setVisible(false);
    }

    public void showHelp() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // DEACTIVATE THE MAIN MENU BUTTONS
        int sizeOfArrMenu = props.getPropertyOptionsList(XPathPropertyType.MAIN_MENU_IMAGE_OPTIONS).size();
        for (int i = 0; i < sizeOfArrMenu; i++) {
            guiButtons.get(MENU_SELECT_BUTTON_TYPE + i).setState(XPathGUIState.INVISIBLE_STATE.toString());
            guiButtons.get(MENU_SELECT_BUTTON_TYPE + i).setEnabled(false);
        }

        guiDecor.get(BG_ABOUT).setState(XPathGUIState.VISIBLE_STATE.toString());
        guiDecor.get(BG_ABOUT).setEnabled(true);
        guiButtons.get(UI_BUTTON_TYPE + "_CLOSE").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "_CLOSE").setEnabled(true);
    }

    public void showLossScreen() {
        guiDecor.get(BG_NOTIFY).setState(XPathGUIState.VISIBLE_STATE.toString());
        guiDecor.get(BG_NOTIFY).setEnabled(true);
        guiButtons.get(UI_BUTTON_TYPE + "_QUIT_S").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "_QUIT_S").setEnabled(true);
        guiButtons.get(UI_BUTTON_TYPE + "_PA_S").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "_PA_S").setEnabled(true);
        eventHandler.loseLevel();
    }

    public void showWinScreen() {
        guiDecor.get(BG_NOTIFY).setState(XPathGUIState.VISIBLE_STATE.toString());
        guiDecor.get(BG_NOTIFY).setEnabled(true);
        guiButtons.get(UI_BUTTON_TYPE + "_QUIT_S").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "_QUIT_S").setEnabled(true);
        guiButtons.get(UI_BUTTON_TYPE + "_PA_S").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "_PA_S").setEnabled(true);
        model.playerCar.setMoney(model.playerCar.getMoney() + model.getLevel().getMoney());
        eventHandler.winLevel();
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void reRenderGUIButton() {

    }

    public String getCurrentScreenState() {
        return currentScreenState;

    }

    /**
     * Accessor method for getting the app's file manager.
     *
     * @return The file manager.
     */
    public XPathFileManager getFileManager() {
        return fileManager;
    }

    public void makeSlider() {
        JSlider gameSpeedSlider = new JSlider(JSlider.HORIZONTAL, 10, 60, 30);
        gameSpeedSlider.setMajorTickSpacing(5);
        gameSpeedSlider.setMinorTickSpacing(1);
        gameSpeedSlider.setPaintLabels(true);
        gameSpeedSlider.setPaintTicks(true);
        gameSpeedSlider.setPaintTrack(true);
        gameSpeedSlider.setAutoscrolls(true);
        gameSpeedSlider.setPreferredSize(new Dimension(300, 50));
        gameSpeedSlider.setOpaque(false);

        gameSpeedSlider.setVisible(false);

        getCanvas().setLayout(null);
        getCanvas().add(gameSpeedSlider, 0);

        gameSpeedSlider.setBounds(164, 307, 300, 50);

        Event e = new Event(((JSlider) canvas.getComponent(0)), this);
        gameSpeedSlider.addChangeListener(e);

    }

    /**
     * @return the model
     */
    public XPathModel getModel() {
        return model;
    }

    /**
     * @return the eventHandler
     */
    public XPathEventHandler getEventHandler() {
        return eventHandler;
    }

    /**
     * @return the xpme
     */
    public XPathMouseEvents getXpme() {
        return xpme;
    }

    public void switchToPlay() {
        guiButtons.get(UI_BUTTON_TYPE + "PLAY").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "PLAY").setEnabled(true);
        guiButtons.get(UI_BUTTON_TYPE + "PAUSE").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "PAUSE").setEnabled(false);
    }

    public void switchToPause() {
        guiButtons.get(UI_BUTTON_TYPE + "PLAY").setState(XPathGUIState.INVISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "PLAY").setEnabled(false);
        guiButtons.get(UI_BUTTON_TYPE + "PAUSE").setState(XPathGUIState.VISIBLE_STATE.toString());
        guiButtons.get(UI_BUTTON_TYPE + "PAUSE").setEnabled(true);
    }

    public void updateLevels() {
        // WE'LL USE AND REUSE THESE FOR LOADING STUFF
        BufferedImage img;
        float x, y;
        SpriteType sT;
        LevelSprite s;

        // FIRST PUT THE ICON IN THE WINDOW
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imgPath = props.getProperty(XPathPropertyType.PATH_IMG);

        // ADD A BUTTON FOR EACH LEVEL OPTION AVAILABLE
        ArrayList<String> levels = props.getPropertyOptionsList(XPathPropertyType.LEVEL_OPTIONS);
        ArrayList<String> levelsX = props.getPropertyOptionsList(XPathPropertyType.LEVEL_OPTIONS_X);
        ArrayList<String> levelsY = props.getPropertyOptionsList(XPathPropertyType.LEVEL_OPTIONS_Y);
        String whatDo = LOCKED;
        for (int i = 0; i < levels.size(); i++) {
            sT = new SpriteType(LEVEL_SELECT_BUTTON_TYPE);
            if (model.levelsUnlocked.size() > i) {
                img = loadImageWithColorKey(imgPath + props.getProperty(XPathPropertyType.IMAGE_BUTTON_UNLOCKED), COLOR_KEY);
                whatDo = UNLOCKED;
            } else {
                img = loadImageWithColorKey(imgPath + props.getProperty(XPathPropertyType.IMAGE_BUTTON_LOCKED), COLOR_KEY);
                whatDo = LOCKED;
            }
            sT.addState(XPathGUIState.VISIBLE_STATE.toString(), img);
            sT.addState(XPathGUIState.MOUSE_OVER_STATE.toString(), img);
            guiButtons.get(levels.get(i)).setSpriteType(sT);
            ((LevelSprite) guiButtons.get(levels.get(i))).setType(whatDo);
        }
    }
}
