/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xpath.data;

import mini_game.MiniGameDataModel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import xpath.XPath.XPathPropertyType;
import mini_game.MiniGame;
import mini_game.MiniGameDataModel;
import mini_game.SpriteType;
import properties_manager.PropertiesManager;
import static xpath.XPathConstants.*;
import xpath.ui.XPathMiniGame;
import xpath.ui.XPathPanel;

/**
 *
 * @author Khaos
 */
public class XPathDataModel extends MiniGameDataModel {
    
    // THIS CLASS HAS A REFERERENCE TO THE MINI GAME SO THAT IT
    // CAN NOTIFY IT TO UPDATE THE DISPLAY WHEN THE DATA MODEL CHANGES
    private MiniGame miniGame;
    
    public float animation = 0;
    public float animationX = 0;
    public float animationY = 0;
    public boolean animating = false;
    
    public int selectedX = 0;
    public int selectedY = 0;
    
     public XPathDataModel(MiniGame initMiniGame)
    {
        // KEEP THE GAME FOR LATER
        miniGame = initMiniGame;
        
    }
    
    @Override
    public void checkMousePressOnSprites(MiniGame game, int x, int y) {
        
    }

    @Override
    public void mouseOverOnSprites(MiniGame game, int x, int y) {
        
    }

    @Override
    public void reset(MiniGame game) {
        
    }

    @Override
    public void updateAll(MiniGame game) {
        
    }

    @Override
    public void updateDebugText(MiniGame game) {
        
    }
    
    public void animate(float x) {
        animation = x;
        animating = true;
    }

    public void updateXY(int x, int y) {
        selectedX = x;
        selectedY = y;
    }

    public void animate2(float x, float y) {
        animationX = x;
        animationY = y;
    }
    
}
