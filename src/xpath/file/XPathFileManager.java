/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xpath.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.filechooser.FileFilter;
import static xpath.XPathConstants.*;
import xpath.data.XPathDataModel;
import xpath.ui.XPathMiniGame;

/**
 *
 * @author Khaos
 */
public class XPathFileManager {

    XPathLevelIO levelIO;
    String levelFileExtension;
    FileFilter fileFilter;

    // THE DATA TO BE UPDATED DURING LOADING
    private XPathModel model;

    // WE'LL STORE THE FILE CURRENTLY BEING WORKED ON
    // AND THE NAME OF THE FILE
    private File currentFile;
    private String currentFileName;
    private String DATA_PATH;

    public XPathFileManager(XPathModel initModel, String levelFileFormat) {
        // INIT THE FILE I/O COMPONENT
        levelFileExtension = levelFileFormat;
        if (levelFileFormat.equals(XML_LEVEL_FILE_EXTENSION)) {
            levelIO = new XPathXMLIO(new File(LEVELS_PATH + LEVEL_SCHEMA));
        }

        // KEEP THESE REFERENCE FOR LATER
        model = initModel;

        // NOTHING YET
        currentFile = null;
        currentFileName = null;
    }

    public void openLevel(String pathToLevel) {

        currentFile = new File(pathToLevel);
        XPathXMLIO levelIO = new XPathXMLIO(new File(LEVELS_PATH + LEVEL_SCHEMA));

        levelIO.loadLevel(currentFile, model);

        model.isLoaded = true;
        model.getViewport().setLevelDimensions(model.getBackgroundImage().getWidth() - 510, model.getBackgroundImage().getHeight() - 445);

    }

    public void saveGame() {
        XPathXMLIO levelIO = new XPathXMLIO(new File(PATH_DATA + "\\PropSchem.xsd"));
        levelIO.saveGame(model);
    }

    public void loadModel() {

        currentFile = new File(PATH_DATA + "\\model.xml");

        if (currentFile.exists()) {
            XPathXMLIO levelIO = new XPathXMLIO(new File(PATH_DATA + "\\PropSchem.xsd"));
            levelIO.loadGame(currentFile, model);
        }

    }

    /**
     * @return the model
     */
    public XPathModel getModel() {
        return model;
    }

}
