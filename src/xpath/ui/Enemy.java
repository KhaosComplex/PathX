/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xpath.ui;

import java.util.ArrayList;
import java.util.Stack;
import mini_game.Sprite;
import xpath.file.Intersection;

/**
 *
 * @author Alexander
 */
public class Enemy {

    public String type;
    public int speedModifier = 1;
    public LevelSprite s;
    public Intersection selInt;
    public Intersection curInt;
    public ArrayList<Intersection> nodesToVisit;
    public Stack<Intersection> intPath;
    public float animation = 0;
    public float animationX = 0;
    public float animationY = 0;
    public int moveCooldown = 0;
    public float moveSpeed = 1;
    public boolean mindlessTerror = false;
    public boolean hitByTerror = false;
    public float robbedCooldown;

    Enemy(String initType, LevelSprite initS) {
        type = initType;
        s = initS;
        intPath = new Stack<Intersection>();
        nodesToVisit = new ArrayList<Intersection>();
    }

    public void animate(float x) {
        animation = x;
    }

    public void animate2(float x, float y) {
        animationX = x;
        animationY = y;
    }

    void reset() {
        intPath = new Stack<Intersection>();
    }
}
