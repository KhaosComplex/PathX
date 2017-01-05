/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xpath.ui;

import mini_game.Sprite;
import mini_game.SpriteType;
import xpath.file.Intersection;

/**
 *
 * @author Khaos
 */
public class LevelSprite extends Sprite {

    private float ox = 0;
    private float oy = 0;
    private String state = "";
    private int levelNumber = 0;

    public LevelSprite(SpriteType initSpriteType, float initX, float initY, float initVx, float initVy, String initState, String state, int initLevelNumber) {
        super(initSpriteType, initX, initY, initVx, initVy, initState);
        ox = this.getX();
        oy = this.getY();
        this.state = state;
        levelNumber = initLevelNumber;
    }

    /**
     * @return the ox
     */
    public float getOx() {
        return ox;
    }

    /**
     * @param ox the ox to set
     */
    public void setOx(int ox) {
        this.ox = ox;
    }

    /**
     * @return the oy
     */
    public float getOy() {
        return oy;
    }

    /**
     * @param oy the oy to set
     */
    public void setOy(int oy) {
        this.oy = oy;
    }

    public void update() {
        ox = this.x;
        oy = this.y;
    }

    /**
     * @return the state
     */
    public String getType() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setType(String state) {
        this.state = state;
    }

    public boolean isOnIntersection(Intersection i) {
        if (i.getX() == x-28 && i.getY() == y-28) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return the levelNumber
     */
    public int getLevelNumber() {
        return levelNumber;
    }

}
