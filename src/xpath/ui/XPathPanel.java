/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xpath.ui;

import java.awt.BasicStroke;
import javax.swing.JPanel;
import xpath.data.XPathDataModel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JPanel;
import mini_game.MiniGame;
import mini_game.Sprite;
import mini_game.SpriteType;
import mini_game.Viewport;
import properties_manager.PropertiesManager;
import static xpath.XPathConstants.*;
import xpath.XPath.XPathPropertyType;
import xpath.XPath.XPathGUIState;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import xpath.file.Intersection;
import xpath.file.Road;
import xpath.file.XPathModel;

/**
 *
 * @author Khaos
 */
public class XPathPanel extends JPanel {

    // THIS IS ACTUALLY OUR Sorting Hat APP, WE NEED THIS
    // BECAUSE IT HAS THE GUI STUFF THAT WE NEED TO RENDER
    private MiniGame game;

    // AND HERE IS ALL THE GAME DATA THAT WE NEED TO RENDER
    private XPathDataModel data;

    // WE'LL USE THIS TO FORMAT SOME TEXT FOR DISPLAY PURPOSES
    private NumberFormat numberFormatter;

    private XPathModel model;

    // MANAGES PORTION OF LEVEL TO RENDER
    ViewportLevel viewport;

    private int TF = 0;

    // WE'LL RECYCLE THESE DURING RENDERING
    Ellipse2D.Double recyclableCircle;
    Line2D.Double recyclableLine;
    HashMap<Integer, BasicStroke> recyclableStrokes;
    int triangleXPoints[] = {-ONE_WAY_TRIANGLE_WIDTH / 2, -ONE_WAY_TRIANGLE_WIDTH / 2, ONE_WAY_TRIANGLE_WIDTH / 2};
    int triangleYPoints[] = {ONE_WAY_TRIANGLE_WIDTH / 2, -ONE_WAY_TRIANGLE_WIDTH / 2, 0};
    GeneralPath recyclableTriangle;

    /**
     * This constructor stores the game and data references, which we'll need
     * for rendering.
     *
     * @param initGame The Sorting Hat game that is using this panel for
     * rendering.
     *
     * @param initData The Sorting Hat game data.
     */
    public XPathPanel(MiniGame initGame, XPathDataModel initData, XPathModel initModel) {
        game = initGame;
        data = initData;
        model = initModel;
        numberFormatter = NumberFormat.getNumberInstance();
        numberFormatter.setMinimumFractionDigits(3);
        numberFormatter.setMaximumFractionDigits(3);

        // KEEP THESE FOR LATER
        model = initModel;
        viewport = model.getViewport();

        // MAKE THE RENDER OBJECTS TO BE RECYCLED
        recyclableCircle = new Ellipse2D.Double(0, 0, INTERSECTION_RADIUS * 2, INTERSECTION_RADIUS * 2);
        recyclableLine = new Line2D.Double(0, 0, 0, 0);
        recyclableStrokes = new HashMap();
        for (int i = 1; i <= 10; i++) {
            recyclableStrokes.put(i, new BasicStroke(i * 2));
        }

        // MAKING THE TRIANGLE FOR ONE WAY STREETS IS A LITTLE MORE INVOLVED
        recyclableTriangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD,
                triangleXPoints.length);
        recyclableTriangle.moveTo(triangleXPoints[0], triangleYPoints[0]);
        for (int index = 1; index < triangleXPoints.length; index++) {
            recyclableTriangle.lineTo(triangleXPoints[index], triangleYPoints[index]);
        };
        recyclableTriangle.closePath();
    }

    /**
     * This is where rendering starts. This method is called each frame, and the
     * entire game application is rendered here with the help of a number of
     * helper methods.
     *
     * @param g The Graphics context for this panel.
     */
    @Override
    public void paintComponent(Graphics g) {
        try {
            // MAKE SURE WE HAVE EXCLUSIVE ACCESS TO THE GAME DATA
            game.beginUsingData();

            // CLEAR THE PANEL
            super.paintComponent(g);

            // RENDER THE BACKGROUND, WHICHEVER SCREEN WE'RE ON
            renderBackground(g);

            // ONLY RENDER THIS STUFF IF WE'RE ACTUALLY IN-GAME
            if (!data.notStarted()) {

                if (!data.won()) {

                }

                // AND THE DIALOGS, IF THERE ARE ANY
                renderDialogs(g);

            }

            // AND THE BUTTONS AND DECOR
            renderGUIDecor(g);

            if (((XPathMiniGame) game).getCurrentScreenState().equals(GAME_SCREEN_STATE) && model.isLoaded == true) {

                // RENDER THE BACKGROUND IMAGE
                renderLevelBackground((Graphics2D) g);

                // RENDER THE ROADS
                renderRoads((Graphics2D) g);

                if (game.getDataModel().isMouseClicked() && !model.getViewport().buttonPressed) {
                    if (game.getGUIDecor().get(BG_NOTIFY).getState().equals(XPathGUIState.INVISIBLE_STATE.toString())) {
                        ((XPathMiniGame) game).getXpme().selectIntersection();
                    }
                }
                if (game.getDataModel().isDragged()) {
                    if (!((XPathMiniGame) game).getXpme().intersectionPathDragged.contains(model.getCurrentIntersection()) && !model.getViewport().buttonPressed) {
                        ((XPathMiniGame) game).getXpme().intersectionPathDragged.add(model.getCurrentIntersection());
                    }
                    ((XPathMiniGame) game).getXpme().selectDraggedIntersection();
                }
                if (!game.getDataModel().isDragged() && ((XPathMiniGame) game).getXpme().getIntersectionPathDragged().size() != 0 && !((XPathMiniGame) game).getXpme().flipped && !model.getViewport().buttonPressed) {
                    ((XPathMiniGame) game).getXpme().goDragged();
                }

                // RENDER THE INTERSECTIONS
                renderIntersections((Graphics2D) g);

                for (Enemy enemy : ((XPathMiniGame) game).getModel().enemies) {
                    if (enemy.type.equals("POLICE")) {
                        if (enemy.intPath.size() == 0 && enemy.animation == 0) {
                            ((XPathMiniGame) game).getEventHandler().movePolice(enemy);
                        }
                    } else if (enemy.type.equals("BANDIT")) {
                        if (enemy.intPath.size() == 0 && enemy.animation == 0) {
                            ((XPathMiniGame) game).getEventHandler().moveBandit(enemy);
                        }
                    } else if (enemy.type.equals("ZOMBIE")) {
                        if (enemy.intPath.size() == 0 && enemy.animation == 0) {
                            ((XPathMiniGame) game).getEventHandler().moveZombie(enemy);
                        }
                    }
                    renderEnemyCar(g, enemy.s, enemy);
                    ((XPathMiniGame) game).getModel().resetStack();
                }

                renderPlayerCar(g, game.getGUIButtons().get(UI_BUTTON_TYPE + "PC"));

                if (game.getGUIDecor().get(BG_NOTIFY).getState().equals(XPathGUIState.VISIBLE_STATE.toString())) {
                    renderBGNotify(g);
                } else {
                    model.firstRunInt = 1;
                }

                renderInformationStrings(g);

                if (!game.getDataModel().isPaused()) {
                    model.checkForCollision();
                }
                for (Enemy enemy : model.enemies) {
                    if (enemy.mindlessTerror) {
                        model.checkForSuperCollisions(enemy);
                    }
                }

            } else {
                viewport.x = 0;
                viewport.y = 0;
                model.firstRunInt = 0;
                model.playerCar.setCooldownBandit(0);
                model.playerCar.setCooldownZombie(0);
            }

            renderGUIButtons(g);
            renderTop(g);
            model.getViewport().buttonPressed = false;
            game.getDataModel().setMouseClicked(false);

        } finally {
            // RELEASE THE LOCK
            game.endUsingData();
        }
    }

    public void renderBackground(Graphics g) {
        // THERE IS ONLY ONE CURRENTLY SET
        Sprite bg = game.getGUIDecor().get(BACKGROUND_TYPE);
        renderSprite(g, bg);
    }

    /**
     * Renders all the GUI decor and buttons.
     *
     * @param g this panel's rendering context.
     */
    public void renderGUIDecor(Graphics g) {
        // GET EACH DECOR IMAGE ONE AT A TIME
        Collection<Sprite> decorSprites = game.getGUIDecor().values();
        for (Sprite s : decorSprites) {
            if (s.getSpriteType().getSpriteTypeID() != BACKGROUND_TYPE) {
                if (!s.getSpriteType().getSpriteTypeID().equals(BACKGROUND_TYPE_OTHER)) {
                    renderSprite(g, s);
                }
            }
        }
    }

    /**
     * Renders the s Sprite into the Graphics context g. Note that each Sprite
     * knows its own x,y coordinate location.
     *
     * @param g the Graphics context of this panel
     *
     * @param s the Sprite to be rendered
     */
    public void renderSprite(Graphics g, Sprite s) {
        // ONLY RENDER THE VISIBLE ONES
        if (!s.getState().equals(XPathGUIState.INVISIBLE_STATE.toString())) {
            if (((XPathMiniGame) game).getViewport().isButtonScrolled()) {
                ((XPathMiniGame) game).getDataModel().setDragged(false);
            }
            if (game.getDataModel().isDragged() && !((XPathMiniGame) game).getCurrentScreenState().equals(GAME_SCREEN_STATE)) {
                if (game.getDataModel().getDraggedMouseY() > 100 && game.getDataModel().getDraggedMouseY() < 440) {
                    if (game.getDataModel().getDraggedMouseOX() != game.getDataModel().getDraggedMouseX() || game.getDataModel().getDraggedMouseOY() != game.getDataModel().getDraggedMouseY()) {
                        ((XPathMiniGame) game).getViewport().scroll((game.getDataModel().getDraggedMouseOX() - game.getDataModel().getDraggedMouseX()), (game.getDataModel().getDraggedMouseOY() - game.getDataModel().getDraggedMouseY()));
                        game.getDataModel().setDraggedMouseOX(game.getDataModel().getDraggedMouseX());
                        game.getDataModel().setDraggedMouseOY(game.getDataModel().getDraggedMouseY());
                    }
                }
            }

            SpriteType bgST = s.getSpriteType();
            BufferedImage img = bgST.getStateImage(s.getState());
            PropertiesManager props = PropertiesManager.getPropertiesManager();

            if (bgST.getSpriteTypeID().equals(LEVEL_SELECT_BUTTON_TYPE)) {
                if (((XPathMiniGame) game).getViewport().isScrolled()) {
                    s.setX(((LevelSprite) s).getOx() - (((XPathMiniGame) game).getViewport().getViewportX()));
                    s.setY(((LevelSprite) s).getOy() - (((XPathMiniGame) game).getViewport().getViewportY()));
                    TF++;
                }
                g.drawImage(img, (int) s.getX(), (int) s.getY(), null);
            }

            if (TF == props.getPropertyOptionsList(XPathPropertyType.LEVEL_OPTIONS).size()) {
                ((XPathMiniGame) game).getViewport().setScrolled(false);
                TF = 0;
            }
            if (s.equals(game.getGUIDecor().get(BACKGROUND_MAP))) {
                g.drawImage(img.getSubimage(((XPathMiniGame) game).getViewport().getViewportX(), ((XPathMiniGame) game).getViewport().getViewportY(), 626, 335), 3, 106, null);

            } else if (s.equals(game.getGUIDecor().get(BG_NOTIFY)) || s.equals(game.getGUIButtons().get(UI_BUTTON_TYPE + "_CLOSE_S"))) {

            } else if (s.equals(game.getGUIButtons().get(UI_BUTTON_TYPE + "PC"))) {

            } else {
                g.drawImage(img, (int) s.getX(), (int) s.getY(), bgST.getWidth(), bgST.getHeight(), null);
            }

        }
        ((XPathMiniGame) game).getViewport().setButtonScrolled(false);

    }

    public void renderDialogs(Graphics g) {
        // GET EACH DECOR IMAGE ONE AT A TIME
        Collection<Sprite> dialogSprites = game.getGUIDialogs().values();
        for (Sprite s : dialogSprites) {
            // RENDER THE DIALOG, NOTE IT WILL ONLY DO IT IF IT'S VISIBLE
            renderSprite(g, s);
        }
    }

    public void renderTop(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.setColor(Color.BLACK);
        if (((XPathMiniGame) game).getCurrentScreenState().equals(MAP_SCREEN_STATE)) {
            g.drawString("Balance: $" + model.playerCar.getMoney(), 250, 40);
            if (model.levelsUnlocked.size() < 20) {
                g.drawString("Goal: New York", 250, 80);
            } else {
                g.drawString("Goal: Live Rich!", 250, 80);
            }
        }
    }

    // HELPER METHOD FOR RENDERING THE LEVEL BACKGROUND
    private void renderLevelBackground(Graphics2D g2) {
        BufferedImage backgroundImage = model.getBackgroundImage();

        g2.drawImage(backgroundImage.getSubimage(viewport.x, viewport.y, 510, 445), 125, 5, null);

    }

    // HELPER METHOD FOR RENDERING THE LEVEL ROADS
    private void renderRoads(Graphics2D g2) {
        // GO THROUGH THE ROADS AND RENDER ALL OF THEM
        ViewportLevel viewport = model.getViewport();
        Iterator<Road> it = model.roadsIterator();
        g2.setStroke(recyclableStrokes.get(INT_STROKE));
        while (it.hasNext()) {
            Road road = it.next();
            if (!model.isSelectedRoad(road)) {
                renderRoad(g2, road, INT_OUTLINE_COLOR);
            }
        }

        // NOW DRAW THE LINE BEING ADDED, IF THERE IS ONE
        if (model.isAddingRoadEnd()) {
            Intersection startRoadIntersection = model.getStartRoadIntersection();
            recyclableLine.x1 = startRoadIntersection.x - viewport.x;
            recyclableLine.y1 = startRoadIntersection.y - viewport.y;
            recyclableLine.x2 = model.getLastMouseX() - viewport.x;
            recyclableLine.y2 = model.getLastMouseY() - viewport.y;
            g2.draw(recyclableLine);
        }

        // AND RENDER THE SELECTED ONE, IF THERE IS ONE
        Road selectedRoad = model.getSelectedRoad();
        if (selectedRoad != null) {
            renderRoad(g2, selectedRoad, HIGHLIGHTED_COLOR);
        }
    }

    // HELPER METHOD FOR RENDERING A SINGLE ROAD
    private void renderRoad(Graphics2D g2, Road road, Color c) {
        g2.setColor(c);
        int strokeId = road.getSpeedLimit() / 10;

        // CLAMP THE SPEED LIMIT STROKE
        if (strokeId < 1) {
            strokeId = 1;
        }
        if (strokeId > 10) {
            strokeId = 10;
        }
        g2.setStroke(recyclableStrokes.get(strokeId));
        if (road.isClosed()) {
            g2.setColor(Color.RED);
        }

        // LOAD ALL THE DATA INTO THE RECYCLABLE LINE
        recyclableLine.x1 = road.getNode1().x - viewport.x;
        recyclableLine.y1 = road.getNode1().y - viewport.y;
        recyclableLine.x2 = road.getNode2().x - viewport.x;
        recyclableLine.y2 = road.getNode2().y - viewport.y;

        // AND DRAW IT
        g2.draw(recyclableLine);

        // AND IF IT'S A ONE WAY ROAD DRAW THE MARKER
        if (road.isOneWay()) {
            this.renderOneWaySignalsOnRecyclableLine(g2);
        }
    }

    // HELPER METHOD FOR RENDERING AN INTERSECTION
    private void renderIntersections(Graphics2D g2) {
        Iterator<Intersection> it = model.intersectionsIterator();
        while (it.hasNext()) {
            Intersection intersection = it.next();

            // ONLY RENDER IT THIS WAY IF IT'S NOT THE START OR DESTINATION
            // AND IT IS IN THE VIEWPORT
            if ((!model.isStartingLocation(intersection))
                    && (!model.isDestination(intersection))) {
                // FIRST FILL
                if (intersection.isOpen()) {
                    g2.setColor(OPEN_INT_COLOR);
                } else {
                    g2.setColor(CLOSED_INT_COLOR);
                }
                recyclableCircle.x = intersection.x - viewport.x - INTERSECTION_RADIUS;
                recyclableCircle.y = intersection.y - viewport.y - INTERSECTION_RADIUS;
                g2.fill(recyclableCircle);

                // AND NOW THE OUTLINE
                if (model.isSelectedIntersection(intersection)) {
                    g2.setColor(HIGHLIGHTED_COLOR);
                } else {
                    g2.setColor(INT_OUTLINE_COLOR);
                }
                Stroke s = recyclableStrokes.get(INT_STROKE);
                g2.setStroke(s);
                g2.draw(recyclableCircle);
            }
        }

        // AND NOW RENDER THE START AND DESTINATION LOCATIONS
        Image startImage = model.getStartingLocationImage();
        Intersection startInt = model.getStartingLocation();
        renderIntersectionImage(g2, startImage, startInt);

        Image destImage = model.getDesinationImage();
        Intersection destInt = model.getDestination();
        renderIntersectionImage(g2, destImage, destInt);
    }

    // HELPER METHOD FOR RENDERING AN IMAGE AT AN INTERSECTION, WHICH IS
    // NEEDED BY THE STARTING LOCATION AND THE DESTINATION
    private void renderIntersectionImage(Graphics2D g2, Image img, Intersection i) {
        // CALCULATE WHERE TO RENDER IT
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        int x1 = i.x - (w / 2);
        int y1 = i.y - (h / 2);
        int x2 = x1 + img.getWidth(null);
        int y2 = y1 + img.getHeight(null);

        g2.drawImage(img, x1 - viewport.x, y1 - viewport.y, null);

    }

    // YOU'LL LIKELY AT THE VERY LEAST WANT THIS ONE. IT RENDERS A NICE
    // LITTLE POINTING TRIANGLE ON ONE-WAY ROADS
    private void renderOneWaySignalsOnRecyclableLine(Graphics2D g2) {
        // CALCULATE THE ROAD LINE SLOPE
        double diffX = recyclableLine.x2 - recyclableLine.x1;
        double diffY = recyclableLine.y2 - recyclableLine.y1;
        double slope = diffY / diffX;

        // AND THEN FIND THE LINE MIDPOINT
        double midX = (recyclableLine.x1 + recyclableLine.x2) / 2.0;
        double midY = (recyclableLine.y1 + recyclableLine.y2) / 2.0;

        // GET THE RENDERING TRANSFORM, WE'LL RETORE IT BACK
        // AT THE END
        AffineTransform oldAt = g2.getTransform();

        // CALCULATE THE ROTATION ANGLE
        double theta = Math.atan(slope);
        if (recyclableLine.x2 < recyclableLine.x1) {
            theta = (theta + Math.PI);
        }

        // MAKE A NEW TRANSFORM FOR THIS TRIANGLE AND SET IT
        // UP WITH WHERE WE WANT TO PLACE IT AND HOW MUCH WE
        // WANT TO ROTATE IT
        AffineTransform at = new AffineTransform();
        at.setToIdentity();
        at.translate(midX, midY);
        at.rotate(theta);
        g2.setTransform(at);

        // AND RENDER AS A SOLID TRIANGLE
        g2.fill(recyclableTriangle);

        // RESTORE THE OLD TRANSFORM SO EVERYTHING DOESN'T END UP ROTATED 0
        g2.setTransform(oldAt);
    }

    private void renderBGNotify(Graphics g) {
        Sprite s = game.getGUIDecor().get(BG_NOTIFY);
        SpriteType bgST = s.getSpriteType();
        BufferedImage img = bgST.getStateImage(s.getState());

        g.drawImage(img, (int) s.getX(), (int) s.getY(), bgST.getWidth(), bgST.getHeight(), null);

        if (model.firstRunInt == 0) {
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(Color.BLACK);
            g.drawString(model.getLevel().getLevelName(), 146, 100);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.drawString("Rob this " + model.getLevel().getLevelName() + " bank and", 146, 150);
            g.drawString("make your getaway to earn $" + model.getLevel().getMoney(), 146, 172);

            s = game.getGUIButtons().get(UI_BUTTON_TYPE + "_CLOSE_S");
            bgST = s.getSpriteType();
            img = bgST.getStateImage(s.getState());

            g.drawImage(img, (int) s.getX(), (int) s.getY(), bgST.getWidth(), bgST.getHeight(), null);
        } else if (model.firstRunInt == 1) {
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(Color.BLACK);
            g.drawString("You got caught!", 146, 100);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.drawString("You've been caught by the police", 146, 150);
            g.drawString("and must hand over: $" + (int) (model.playerCar.getMoneyLost()), 146, 172);

            s = game.getGUIButtons().get(UI_BUTTON_TYPE + "_QUIT_S");
            bgST = s.getSpriteType();
            img = bgST.getStateImage(s.getState());

            g.drawImage(img, (int) s.getX(), (int) s.getY(), bgST.getWidth(), bgST.getHeight(), null);

            s = game.getGUIButtons().get(UI_BUTTON_TYPE + "_PA_S");
            bgST = s.getSpriteType();
            img = bgST.getStateImage(s.getState());
            g.drawImage(img, (int) s.getX(), (int) s.getY(), bgST.getWidth(), bgST.getHeight(), null);

        } else {
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(Color.BLACK);
            g.drawString("You did it!", 146, 100);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.drawString("You got to the end without getting", 146, 150);
            g.drawString("caught! You earned: $" + model.getLevel().getMoney(), 146, 172);

            s = game.getGUIButtons().get(UI_BUTTON_TYPE + "_QUIT_S");
            bgST = s.getSpriteType();
            img = bgST.getStateImage(s.getState());

            g.drawImage(img, (int) s.getX(), (int) s.getY(), bgST.getWidth(), bgST.getHeight(), null);

            s = game.getGUIButtons().get(UI_BUTTON_TYPE + "_PA_S");
            bgST = s.getSpriteType();
            img = bgST.getStateImage(s.getState());

            g.drawImage(img, (int) s.getX(), (int) s.getY(), bgST.getWidth(), bgST.getHeight(), null);
        }
    }

    private void renderGUIButtons(Graphics g) {
        renderSprite(g, game.getGUIDecor().get(BACKGROUND_TYPE_OTHER + "3"));

        PropertiesManager props = PropertiesManager.getPropertiesManager();
        ArrayList<String> powerups = props.getPropertyOptionsList(XPathPropertyType.POWERUP_OPTIONS);

        boolean TF = false;

        // AND NOW RENDER THE BUTTONS
        Collection<Sprite> buttonSprites = game.getGUIButtons().values();
        for (Sprite s : buttonSprites) {
            for (int i = 0; i < powerups.size(); i++) {
                if (s.equals(game.getGUIButtons().get(powerups.get(i)))) {
                    TF = true;
                }
            }
            if (s.equals(game.getGUIButtons().get(UI_BUTTON_TYPE + "PC"))) {

            } else if (TF) {
                TF = false;
            } else {
                renderSprite(g, s);
            }
        }

        renderSprite(g, game.getGUIDecor().get(BACKGROUND_TYPE_OTHER + "2"));
        renderSprite(g, game.getGUIDecor().get(BACKGROUND_TYPE_OTHER));

        renderSprite(g, game.getGUIButtons().get(UI_BUTTON_TYPE + 1));
        renderSprite(g, game.getGUIButtons().get(UI_BUTTON_TYPE + 2));

        for (int i = 0; i < model.levelsUnlocked.size(); i++) {
            if (i != 0) {
                renderSprite(g, game.getGUIButtons().get(powerups.get(i - 1)));
            }
            if (i == 16) {
                break;
            }
        }
    }

    private void renderEnemyCar(Graphics g, Sprite s, Enemy enemy) {
        if (enemy.animation == 0 && enemy.intPath.size() != 0) {
            ((XPathMiniGame) game).getXpme().moveEnemyCar(enemy);
        }
        SpriteType bgST = s.getSpriteType();
        BufferedImage img = bgST.getStateImage(s.getState());
        if (!game.getDataModel().isPaused()) {
            float nX = enemy.animationX;
            float nY = enemy.animationY;
            float animation = enemy.animation;
            if (animation != 0 && animation >= 1) {
                s.setX(((LevelSprite) s).getX() + nX);
                s.setY(((LevelSprite) s).getY() + nY);
                ((LevelSprite) s).setOx((int) s.getX());
                ((LevelSprite) s).setOy((int) s.getY());
                enemy.animation--;
            }

            if (animation > 0 && animation < 1) {
                s.setX(enemy.curInt.x - 28);
                s.setY(enemy.curInt.y - 28);
                ((LevelSprite) s).setOx((int) s.getX());
                ((LevelSprite) s).setOy((int) s.getY());
                enemy.animation = 0;

            }

            if (enemy.intPath.size() == 0) {
                if (((XPathMiniGame) game).getXpme().enemyToMove == enemy && !((XPathMiniGame) game).getXpme().movingEnemy) {
                    enemy.moveCooldown = (600);
                    ((XPathMiniGame) game).getXpme().enemyToMove = null;
                }
                enemy.reset();
                model.resetStack();
            }
        }

        if (enemy.moveCooldown != 0) {
            enemy.moveCooldown += -((float) FPS / (float) 30);
        }
        if (enemy.moveCooldown < 0) {
            enemy.moveCooldown = 0;
        }
        if (enemy.robbedCooldown != 0) {
            enemy.robbedCooldown += -((float) FPS / (float) 30);
        }
        if (enemy.robbedCooldown < 0) {
            enemy.robbedCooldown = 0;
        }
        g.drawImage(img, (int) s.getX() - model.getViewport().x, (int) s.getY() - model.getViewport().y, bgST.getWidth(), bgST.getHeight(), null);

    }

    private void renderPlayerCar(Graphics g, Sprite s) {
        if (((XPathDataModel) data).animation == 0
                && ((XPathMiniGame) game).getXpme().intersectionPath.size() != 0
                && ((XPathDataModel) data).animating == false && !((XPathMiniGame) game).getXpme().waiting) {
            ((XPathMiniGame) game).getXpme().movePlayerCar();
        }

        SpriteType bgST = s.getSpriteType();
        BufferedImage img = bgST.getStateImage(s.getState());
        if (!game.getDataModel().isPaused()) {
            float nX = ((XPathDataModel) data).animationX;
            float nY = ((XPathDataModel) data).animationY;
            float animation = ((XPathDataModel) data).animation;
            if (animation != 0 && animation >= 1 && model.playerCar.moveCooldown == 0) {
                s.setX(((LevelSprite) s).getX() + nX);
                s.setY(((LevelSprite) s).getY() + nY);
                ((LevelSprite) s).setOx((int) s.getX());
                ((LevelSprite) s).setOy((int) s.getY());
                ((XPathDataModel) data).animation--;
            }

            if (animation > 0 && animation < 1 && !model.getCurrentIntersection().equals(model.getStartingLocation()) && !((XPathMiniGame) game).getXpme().waiting && model.playerCar.moveCooldown == 0) {
                s.setX(model.getCurrentIntersection().x - 28);
                s.setY(model.getCurrentIntersection().y - 28);
                ((LevelSprite) s).setOx((int) s.getX());
                ((LevelSprite) s).setOy((int) s.getY());

            }
            if (animation > 0 && animation < 1) {
                ((XPathDataModel) data).animation = 0;
            }

            if (((XPathDataModel) data).animation == 0) {
                ((XPathDataModel) data).animating = false;
                if (((XPathMiniGame) game).getXpme().flying) {
                    ((XPathMiniGame) game).getXpme().flying = false;
                }
                if (model.getCurrentIntersection() == model.getDestination()) {
                    model.firstRunInt = 2;
                    game.getDataModel().pause();
                    ((XPathMiniGame) game).showWinScreen();
                }
            }

            if (((XPathMiniGame) game).getXpme().intersectionPath.size() == 0) {
                ((XPathMiniGame) game).getXpme().reset();
                model.resetStack();
            }
            if (((XPathMiniGame) game).getXpme().intersectionPathDragged.size() == 0 || ((XPathMiniGame) game).getXpme().intersectionPath.size() != 0) {
                ((XPathMiniGame) game).getXpme().resetDragged();
            }
            if (model.playerCar.stealCooldown != 0) {
                model.playerCar.stealCooldown += -((float) FPS / (float) 30);
            }
            if (model.playerCar.getInvincibilityCooldown() != 0) {
                model.playerCar.setInvincibilityCooldown(model.playerCar.getInvincibilityCooldown() - ((float) FPS / (float) 30));
            }
            if (model.playerCar.getIntangibilityCooldown() != 0) {
                model.playerCar.setIntangibilityCooldown(model.playerCar.getIntangibilityCooldown() - ((float) FPS / (float) 30));
            }
            if (model.playerCar.getCooldownPolice() != 0) {
                model.playerCar.setCooldownPolice(model.playerCar.getCooldownPolice() - ((float) FPS / (float) 30));
            }
            if (model.playerCar.getCooldownBandit() != 0) {
                model.playerCar.setCooldownBandit(model.playerCar.getCooldownBandit() - ((float) FPS / (float) 30));
            }
            if (model.playerCar.getCooldownZombie() != 0) {
                model.playerCar.setCooldownZombie(model.playerCar.getCooldownZombie() - ((float) FPS / (float) 30));
            }
            if (model.playerCar.stealCooldown <= 0) {
                model.playerCar.stealCooldown = 0;
                model.powerups[10] = false;
            }
            if (model.playerCar.getInvincibilityCooldown() <= 0) {
                model.playerCar.setInvincibilityCooldown(0);
                model.powerups[15] = false;
            }
            if (model.playerCar.getIntangibilityCooldown() <= 0) {
                model.playerCar.setIntangibilityCooldown(0);
                model.powerups[12] = false;
            }
            if (model.playerCar.getCooldownPolice() < 0) {
                model.playerCar.setCooldownPolice(0);
            }
            if (model.playerCar.getCooldownBandit() < 0) {
                model.playerCar.setCooldownBandit(0);
            }
            if (model.playerCar.getCooldownZombie() < 0) {
                model.playerCar.setCooldownZombie(0);
            }
            if (model.playerCar.moveCooldown != 0) {
                model.playerCar.moveCooldown += -((float) FPS / (float) 30);
            }
            if (model.playerCar.moveCooldown < 0) {
                model.playerCar.moveCooldown = 0;
            }
        }
        g.drawImage(img, (int) s.getX() - model.getViewport().x, (int) s.getY() - model.getViewport().y, bgST.getWidth(), bgST.getHeight(), null);

    }

    public void renderInformationStrings(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        g.drawString("Current Money: $" + model.playerCar.getMoney(), 127, 28);
        g.drawString("Movespeed: " + ((int) (model.playerCar.getMovespeed() * 100)) + "%", 458, 28);
        g.drawString("Level Money to Earn: $" + model.getLevel().getMoney(), 127, 443);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        String x = "";
        if (model.powerups[0]) {
            x = "Green Light";
        }
        if (model.powerups[1]) {
            x = "Red Light";
        }
        if (model.powerups[2]) {
            x = "Decrease Speed Limit";
        }
        if (model.powerups[3]) {
            x = "Increase Speed Limit";
        }
        if (model.powerups[5]) {
            x = "Flat Tire";
        }
        if (model.powerups[6]) {
            x = "Empty Gas Tank";
        }
        if (model.powerups[7]) {
            x = "Close Road";
        }
        if (model.powerups[8]) {
            x = "Close Intersection";
        }
        if (model.powerups[9]) {
            x = "Open Intersection";
        }
        if (model.powerups[10]) {
            x = "Steal";
        }
        if (model.powerups[11]) {
            x = "Mind Control";
        }
        if (model.powerups[12]) {
            x = "Intangibility";
        }
        if (model.powerups[13]) {
            x = "Mindless Terror";
        }
        if (model.powerups[14]) {
            x = "Flying";
        }
        if (model.powerups[15]) {
            x = "Invincibility";
        }
        g.drawString("Powerup: " + x, 440, 443);
    }
}
