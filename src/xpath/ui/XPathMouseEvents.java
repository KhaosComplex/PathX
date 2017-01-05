/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xpath.ui;

import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import javax.swing.JPanel;
import mini_game.MiniGame;
import static xpath.XPathConstants.FPS;
import static xpath.XPathConstants.GAME_SCREEN_STATE;
import static xpath.XPathConstants.UI_BUTTON_TYPE;
import xpath.data.XPathDataModel;
import xpath.file.Intersection;
import xpath.file.Road;
import xpath.file.XPathEditMode;
import xpath.file.XPathModel;

/**
 *
 * @author Khaos
 */
public class XPathMouseEvents {

    private XPathModel model;
    private XPathMiniGame game;

    public Stack<Intersection> intersectionPath;
    public Stack<Intersection> intersectionPathDragged;

    public boolean flipped = false;

    public boolean waiting;
    public boolean hasMoved;
    public boolean flying;

    public boolean movingEnemy;
    Enemy enemyToMove;

    public XPathMouseEvents(XPathMiniGame initGame, XPathModel initModel) {
        game = initGame;
        model = initModel;
        intersectionPath = new Stack<Intersection>();
        intersectionPathDragged = new Stack<Intersection>();
        waiting = false;
    }

    public void selectIntersection() {
        if (game.getCurrentScreenState().equals(GAME_SCREEN_STATE)) {
            if (!flying) {
                int canvasX = game.getDataModel().getLastMouseX();
                int canvasY = game.getDataModel().getLastMouseY();
                if (movingEnemy) {
                    Intersection i = model.findIntersectionAtCanvasLocation(canvasX, canvasY);
                    if (i != null) {
                        enemyToMove.selInt = i;
                        model.makeEnemyPath(enemyToMove.curInt, game.getModel().getLevel().getIntersections(), Boolean.TRUE, enemyToMove);
                        movingEnemy = false;
                        model.powerups[11] = false;
                    }
                } else {
                    if (model.powerups[5]) {
                        for (Enemy enemy : model.enemies) {
                            if (canvasX + model.getViewport().x < enemy.s.getX() + 57
                                    && canvasX + model.getViewport().x > enemy.s.getX()
                                    && canvasY + model.getViewport().y < enemy.s.getY() + 57
                                    && canvasY + model.getViewport().y > enemy.s.getY()) {
                                enemy.moveCooldown = 300;
                                model.powerups[5] = false;
                            }
                        }
                    } else if (model.powerups[6]) {
                        for (Enemy enemy : model.enemies) {
                            if (canvasX + model.getViewport().x < enemy.s.getX() + 57
                                    && canvasX + model.getViewport().x > enemy.s.getX()
                                    && canvasY + model.getViewport().y < enemy.s.getY() + 57
                                    && canvasY + model.getViewport().y > enemy.s.getY()) {
                                enemy.moveCooldown = 450;
                                model.powerups[6] = false;
                            }
                        }
                    } else if (model.powerups[11]) {
                        for (Enemy enemy : model.enemies) {
                            if (canvasX + model.getViewport().x < enemy.s.getX() + 57
                                    && canvasX + model.getViewport().x > enemy.s.getX()
                                    && canvasY + model.getViewport().y < enemy.s.getY() + 57
                                    && canvasY + model.getViewport().y > enemy.s.getY()) {
                                enemyToMove = enemy;
                                movingEnemy = true;
                            }
                        }
                    } else if (model.powerups[13]) {
                        for (Enemy enemy : model.enemies) {
                            if (canvasX + model.getViewport().x < enemy.s.getX() + 57
                                    && canvasX + model.getViewport().x > enemy.s.getX()
                                    && canvasY + model.getViewport().y < enemy.s.getY() + 57
                                    && canvasY + model.getViewport().y > enemy.s.getY()) {
                                enemy.moveSpeed = 2;
                                enemy.mindlessTerror = true;
                                model.powerups[13] = false;
                            }
                        }
                    } else {
                        // CHECK TO SEE IF THE USER IS SELECTING AN INTERSECTION
                        Intersection i = model.findIntersectionAtCanvasLocation(canvasX, canvasY);
                        if (i != null) {
                            if (model.powerups[0] && !i.isOpen()) {
                                i.setOpen(true);
                                model.powerups[0] = false;
                            } else if (model.powerups[1] && i.isOpen()) {
                                i.setOpen(false);
                                model.powerups[1] = false;
                            } else if (model.powerups[8]) {
                                i.closed = true;
                                i.open = false;
                                for (Road road : model.getLevel().getRoads()) {
                                    if (road.getNode1().closed || road.getNode2().closed) {
                                        road.setClosed(true);
                                    }
                                }
                                model.powerups[8] = false;
                            } else if (model.powerups[9]) {
                                i.closed = false;
                                i.open = true;
                                for (Road road : model.getLevel().getRoads()) {
                                    if (!road.getNode1().closed || !road.getNode2().closed) {
                                        road.setClosed(false);
                                    }
                                }
                                model.powerups[9] = false;
                            } else if (model.powerups[14]) {
                                model.setSelectedIntersection(i);
                                model.doArraysMod(model.getCurrentIntersection(), model.getLevel().getIntersections());
                                if (model.getIntersectionPath().size() <= 5) {
                                    if (model.getIntersectionPath().size() != 0) {
                                        intersectionPath.add(model.getIntersectionPath().get(0));
                                    }
                                }

                            } else {
                                if (i != model.getSelectedIntersection()) {
                                    model.setSelectedIntersection(i);
                                    if (((XPathDataModel) game.getDataModel()).animating == true) {
                                        model.setCurrentIntersection(model.getPreviousIntersection());
                                    }

                                    if (waiting) {
                                        intersectionPath = new Stack<Intersection>();
                                        model.setCurrentIntersection(model.getPreviousIntersection());
                                        model.doArrays(model.getCurrentIntersection(), model.getLevel().getIntersections());
                                        intersectionPath = model.getIntersectionPath();
                                        intersectionPath.add(model.getPreviousIntersection());
                                        intersectionPath.add(intersectionPath.peek());
                                        movePlayerCar();
                                    } else {
                                        model.doArrays(model.getCurrentIntersection(), model.getLevel().getIntersections());
                                        intersectionPath = model.getIntersectionPath();
                                    }

                                    if (model.getCurrentIntersection() != model.getPositionNowInt()) {
                                        ((XPathDataModel) game.getDataModel()).animation = 0;
                                    }
                                    if (((XPathDataModel) game.getDataModel()).animating == true) {
                                        movePlayerCar();
                                    }
                                }
                                return;

                            }
                        } else {
                            Iterator<Road> it = model.getLevel().getRoads().iterator();
                            Line2D.Double tempLine = new Line2D.Double();
                            while (it.hasNext()) {
                                Road r = it.next();
                                tempLine.x1 = r.getNode1().x;
                                tempLine.y1 = r.getNode1().y;
                                tempLine.x2 = r.getNode2().x;
                                tempLine.y2 = r.getNode2().y;
                                double distance = tempLine.ptSegDist(canvasX + model.getViewport().x, canvasY + model.getViewport().y);

                                // IS IT CLOSE ENOUGH?
                                if (distance <= 3) {
                                    // SELECT IT
                                    model.selectedRoad = r;
                                    if (model.powerups[2]) {
                                        model.selectedRoad.setSpeedLimit((int) (model.selectedRoad.getSpeedLimit() * .5));
                                        model.powerups[2] = false;
                                    } else if (model.powerups[3]) {
                                        model.selectedRoad.setSpeedLimit((int) (model.selectedRoad.getSpeedLimit() * 1.5));
                                        model.powerups[3] = false;
                                    } else if (model.powerups[7]) {
                                        model.selectedRoad.setClosed(true);
                                        model.powerups[7] = false;
                                    }
                                }
                            }
                        }
                    }
                }

                // OTHERWISE DESELECT EVERYTHING
                model.unselectEverything();
            }
        }
    }

    public void selectDraggedIntersection() {

        int canvasX = game.getDataModel().getDraggedMouseX();
        int canvasY = game.getDataModel().getDraggedMouseY();

        // CHECK TO SEE IF THE USER IS SELECTING AN INTERSECTION
        Intersection i = model.findIntersectionAtCanvasLocation(canvasX, canvasY);
        if (i != null) {
            if (i != model.getSelectedIntersection()) {
                if (waiting) {
                    waiting = false;
                    intersectionPath = new Stack<Intersection>();
                    intersectionPathDragged.add(model.getPreviousIntersection());
                }
                if (intersectionPathDragged.size() != 0) {
                    if (model.isAdjacent(i, intersectionPathDragged.peek())) {
                        boolean TF = true;

                        for (Road road : model.getLevel().getRoads()) {
                            if (road.isOneWay()) {
                                if (road.getNode1() == i && road.getNode2() == intersectionPathDragged.peek()) {
                                    TF = false;
                                }
                            }
                        }
                        if (TF) {
                            if (!intersectionPathDragged.contains(i)) {
                                // MAKE THIS THE SELECTED INTERSECTION
                                model.setSelectedIntersection(i);
                                intersectionPathDragged.add(i);
                            }
                        }
                    }
                }

            }
        }
    }

    public void movePlayerCar() {
        if (model.getPositionNowInt() != null) {
            if (model.getPositionNowInt().isOpen() && waiting && intersectionPath.size() != 0) {
                intersectionPath.pop();
                waiting = false;
            }
        }

        if (((XPathDataModel) game.getDataModel()).animating == true) {
            if (!intersectionPath.contains(model.getPreviousIntersection()) && model.getCurrentIntersection() != model.getPositionNowInt()) {
                if (intersectionPath.size() != 0) {
                    if (model.getPositionNowInt() != intersectionPath.peek()) {
                        intersectionPath.add(model.getPreviousIntersection());
                    }
                } else {
                    intersectionPath.add(model.getPreviousIntersection());
                }
            }
        }
        if (model.getPositionNowInt() != null) {
            if (model.getPositionNowInt().isOpen() && waiting) {
                waiting = false;
            }
        }

        if (!intersectionPath.isEmpty() && !game.getDataModel().isPaused()) {
            if (model.getRoad(model.getCurrentIntersection(), intersectionPath.peek()) != null) {
                float c = model.calcDistancePC(intersectionPath.peek(), game.getGUIButtons().get(UI_BUTTON_TYPE + "PC"));
                float nX = model.calcDistancePCX(intersectionPath.peek(), game.getGUIButtons().get(UI_BUTTON_TYPE + "PC"));
                float nY = model.calcDistancePCY(intersectionPath.peek(), game.getGUIButtons().get(UI_BUTTON_TYPE + "PC"));
                float ratioX;
                float ratioY = (nY / c) * (model.getRoad(model.getCurrentIntersection(), intersectionPath.peek()).getSpeedLimit() / 10);
                if (intersectionPath.peek().equals(model.getStartingLocation())) {
                    nX = nX + 62;
                    ratioX = ((nX) / c) * (model.getRoad(model.getCurrentIntersection(), intersectionPath.peek()).getSpeedLimit() / 10);
                } else if (model.getCurrentIntersection().equals(model.getStartingLocation())) {
                    ratioX = ((nX) / c) * (model.getRoad(model.getCurrentIntersection(), intersectionPath.peek()).getSpeedLimit() / 10);
                } else {
                    ratioX = (nX / c) * (model.getRoad(model.getCurrentIntersection(), intersectionPath.peek()).getSpeedLimit() / 10);
                }
                ratioX = ratioX * model.playerCar.getMovespeed() * ((float) FPS / (float) 30);
                ratioY = ratioY * model.playerCar.getMovespeed() * ((float) FPS / (float) 30);
                float animate;
                if (nX != 0) {
                    animate = nX / ratioX;
                } else {
                    animate = nY / ratioY;
                }
                if (!intersectionPath.peek().isOpen()) {
                    animate = (float) (animate * .73);
                    waiting = true;
                }
                if (((XPathDataModel) game.getDataModel()).animating == false) {
                    model.setPositionNowInt(intersectionPath.peek());
                }
                ((XPathDataModel) game.getDataModel()).animate(animate);
                ((XPathDataModel) game.getDataModel()).animate2((ratioX), (ratioY));
                model.setPreviousIntersection(model.getCurrentIntersection());
                model.setCurrentIntersection(intersectionPath.pop());
                ((XPathDataModel) game.getDataModel()).updateXY(model.getCurrentIntersection().x, model.getCurrentIntersection().y);
            } else if (model.powerups[14]) {
                model.powerups[14] = false;
                flying = true;
                float c = model.calcDistancePC(intersectionPath.peek(), game.getGUIButtons().get(UI_BUTTON_TYPE + "PC"));
                float nX = model.calcDistancePCX(intersectionPath.peek(), game.getGUIButtons().get(UI_BUTTON_TYPE + "PC"));
                float nY = model.calcDistancePCY(intersectionPath.peek(), game.getGUIButtons().get(UI_BUTTON_TYPE + "PC"));
                float ratioX;
                float ratioY = (nY / c);
                if (intersectionPath.peek().equals(model.getStartingLocation())) {
                    nX = nX + 62;
                    ratioX = ((nX) / c);
                } else if (model.getCurrentIntersection().equals(model.getStartingLocation())) {
                    ratioX = ((nX) / c);
                } else {
                    ratioX = (nX / c);
                }
                ratioX = ratioX * model.playerCar.getMovespeed() * ((float) FPS / (float) 30) * 10;
                ratioY = ratioY * model.playerCar.getMovespeed() * ((float) FPS / (float) 30) * 10;
                float animate;
                if (nX != 0) {
                    animate = nX / ratioX;
                } else {
                    animate = nY / ratioY;
                }

                ((XPathDataModel) game.getDataModel()).animate(animate);
                ((XPathDataModel) game.getDataModel()).animate2((ratioX), (ratioY));
                model.setCurrentIntersection(intersectionPath.pop());
                ((XPathDataModel) game.getDataModel()).updateXY(model.getCurrentIntersection().x, model.getCurrentIntersection().y);

            } else if (!((LevelSprite) game.getGUIButtons().get(UI_BUTTON_TYPE + "PC")).isOnIntersection(intersectionPath.peek()) && model.getCurrentIntersection() != model.getPositionNowInt() && model.getPositionNowInt() != null) {
                float c = model.calcDistancePC(intersectionPath.peek(), game.getGUIButtons().get(UI_BUTTON_TYPE + "PC"));
                float nX = model.calcDistancePCX(intersectionPath.peek(), game.getGUIButtons().get(UI_BUTTON_TYPE + "PC"));
                float nY = model.calcDistancePCY(intersectionPath.peek(), game.getGUIButtons().get(UI_BUTTON_TYPE + "PC"));
                float ratioX;
                float ratioY = (nY / c) * (model.getRoad(model.getCurrentIntersection(), model.getPositionNowInt()).getSpeedLimit() / 10);
                if (intersectionPath.peek().equals(model.getStartingLocation())) {
                    ratioX = ((nX + 62) / c) * (model.getRoad(model.getCurrentIntersection(), model.getPositionNowInt()).getSpeedLimit() / 10);
                } else if (model.getCurrentIntersection().equals(model.getStartingLocation())) {
                    ratioX = ((nX) / c) * (model.getRoad(model.getCurrentIntersection(), model.getPositionNowInt()).getSpeedLimit() / 10);
                } else {
                    ratioX = (nX / c) * (model.getRoad(model.getCurrentIntersection(), model.getPositionNowInt()).getSpeedLimit() / 10);
                }
                ratioX = ratioX * model.playerCar.getMovespeed() * ((float) FPS / (float) 30);
                ratioY = ratioY * model.playerCar.getMovespeed() * ((float) FPS / (float) 30);
                float animate;
                if (nX != 0) {
                    animate = nX / ratioX;
                } else {
                    animate = nY / ratioY;
                }
                if (!intersectionPath.peek().isOpen()) {
                    animate = (float) (animate * .73);
                    waiting = true;
                }
                ((XPathDataModel) game.getDataModel()).animate(animate);
                ((XPathDataModel) game.getDataModel()).animate2((ratioX), (ratioY));
                model.setPreviousIntersection(model.getCurrentIntersection());
                model.setCurrentIntersection(intersectionPath.pop());
                if (model.getCurrentIntersection().isOpen()) {
                    waiting = false;
                    hasMoved = false;
                }
                ((XPathDataModel) game.getDataModel()).updateXY(model.getCurrentIntersection().x, model.getCurrentIntersection().y);
            } else {
                intersectionPath.pop();
                movePlayerCar();
            }
        }
    }

    public void moveEnemyCar(Enemy enemy) {
        if (enemy.moveCooldown == 0) {
            if (model.getRoad(enemy.curInt, enemy.intPath.peek()) != null) {
                float c = model.calcDistance(enemy.curInt, enemy.intPath.peek());
                float nX = model.calcDistanceX(enemy.intPath.peek(), enemy.curInt);
                float nY = model.calcDistanceY(enemy.intPath.peek(), enemy.curInt);
                float ratioX = (nX / c) * (model.getRoad(enemy.curInt, enemy.intPath.peek()).getSpeedLimit() / 10);
                float ratioY = (nY / c) * (model.getRoad(enemy.curInt, enemy.intPath.peek()).getSpeedLimit() / 10);
                float animate = c / (model.getRoad(enemy.curInt, enemy.intPath.peek()).getSpeedLimit() / 10);
                ratioX = ratioX * ((float) FPS / (float) 30) * enemy.moveSpeed;
                ratioY = ratioY * ((float) FPS / (float) 30) * enemy.moveSpeed;
                animate = animate * ((float) 30 / (float) FPS) * (1 / enemy.moveSpeed);
                enemy.animate(animate * (float) 1.1);
                enemy.animate2(ratioX * (float) .9, ratioY * (float) .9);
                enemy.curInt = enemy.intPath.pop();
            }
        }
    }

    public void reset() {
        intersectionPath = new Stack<Intersection>();

    }

    public void resetDragged() {

        intersectionPathDragged = new Stack<Intersection>();
        flipped = false;

    }

    public void goDragged() {
        reverseStack();
        intersectionPath = intersectionPathDragged;

    }

    /**
     * @return the intersectionPathDragged
     */
    public Stack<Intersection> getIntersectionPathDragged() {
        return intersectionPathDragged;
    }

    public void reverseStack() {
        Stack<Intersection> temp = new Stack<Intersection>();
        int x = intersectionPathDragged.size();
        for (int i = 0; i < x; i++) {
            temp.add(intersectionPathDragged.pop());
        }
        intersectionPathDragged = temp;
        intersectionPathDragged.pop();
        flipped = true;
    }

}
