/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xpath.ui;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import mini_game.MiniGameEventRelayer;
import mini_game.Sprite;
import mini_game.SpriteType;
import properties_manager.PropertiesManager;
import static xpath.XPathConstants.GAME_SCREEN_STATE;
import static xpath.XPathConstants.MENU_SCREEN_STATE;
import static xpath.XPathConstants.VIEWPORT_INC;
import xpath.XPath;
import xpath.XPath.XPathPropertyType;
import static xpath.XPathConstants.COLOR_KEY;
import static xpath.XPathConstants.MAP_SCREEN_STATE;
import static xpath.XPathConstants.PATH_DATA;
import static xpath.XPathConstants.UI_BUTTON_TYPE;
import static xpath.XPathConstants.UNLOCKED;
import xpath.data.XPathDataModel;
import xpath.file.Intersection;
import xpath.file.XPathFileManager;
import xpath.file.XPathModel;

/**
 *
 * @author Khaos
 */
public class XPathEventHandler {

    // THE SORTING HAT GAME, IT PROVIDES ACCESS TO EVERYTHING
    private XPathMiniGame game;

    int banditPos = 0;
    int zombiePos = 0;

    /**
     * Constructor, it just keeps the game for when the events happen.
     */
    public XPathEventHandler(XPathMiniGame initGame) {
        game = initGame;
    }

    public void respondToExitRequest() {
        // IF THE GAME IS STILL GOING ON, END IT AS A LOSS
        if (game.getDataModel().inProgress()) {
            game.getDataModel().endGameAsLoss();
        }
        // AND CLOSE THE ALL
        System.exit(0);
    }

    public void respondToSelectLevelRequest(String levelFile) {
        XPathFileManager fileManager = game.getFileManager();
        fileManager.openLevel(PATH_DATA + levelFile);
        game.getModel().getLevel().offsetIntersections();
        ((XPathMiniGame) game).getModel().setCurrentIntersection(((XPathMiniGame) game).getModel().getLevel().getStartingLocation());
        game.getGUIButtons().get(UI_BUTTON_TYPE + 1).setX(game.getGUIButtons().get(UI_BUTTON_TYPE + 1).getX() - 515);
        game.getGUIButtons().get(UI_BUTTON_TYPE + 2).setX(game.getGUIButtons().get(UI_BUTTON_TYPE + 2).getX() - 515);
        game.getGUIButtons().get(UI_BUTTON_TYPE + 1).setY(game.getGUIButtons().get(UI_BUTTON_TYPE + 1).getY() + 60);
        game.getGUIButtons().get(UI_BUTTON_TYPE + 2).setY(game.getGUIButtons().get(UI_BUTTON_TYPE + 2).getY() + 60);
        game.getGUIButtons().get(UI_BUTTON_TYPE + "PC").setX(game.getModel().getStartingLocation().x + 32);
        game.getGUIButtons().get(UI_BUTTON_TYPE + "PC").setY(game.getModel().getStartingLocation().y - 28);
        ((LevelSprite) game.getGUIButtons().get(UI_BUTTON_TYPE + "PC")).update();
        game.switchToGameScreen();

    }

    public void respondToSelectLevelRequestAF(String levelFile) {
        XPathFileManager fileManager = game.getFileManager();
        fileManager.openLevel(PATH_DATA + levelFile);
        game.getModel().getLevel().offsetIntersections();
        ((XPathMiniGame) game).getModel().setCurrentIntersection(((XPathMiniGame) game).getModel().getLevel().getStartingLocation());
        game.getGUIButtons().get(UI_BUTTON_TYPE + "PC").setX(game.getModel().getStartingLocation().x + 32);
        game.getGUIButtons().get(UI_BUTTON_TYPE + "PC").setY(game.getModel().getStartingLocation().y - 28);
        ((LevelSprite) game.getGUIButtons().get(UI_BUTTON_TYPE + "PC")).update();
    }

    public void respondToKeyPress(int ke) {
        if (game.getCurrentScreenState().equals(MAP_SCREEN_STATE)) {
            switch (ke) {
                case KeyEvent.VK_UP:
                    game.getViewport().setButtonScrolled(true);
                    game.getViewport().scroll(0, -50);
                    game.getDataModel().setButtonIn(false);
                    break;
                case KeyEvent.VK_DOWN:
                    game.getViewport().setButtonScrolled(true);
                    game.getViewport().scroll(0, 50);
                    game.getDataModel().setButtonIn(false);
                    break;
                case KeyEvent.VK_LEFT:
                    game.getViewport().setButtonScrolled(true);
                    game.getViewport().scroll(-100, 0);
                    game.getDataModel().setButtonIn(false);
                    break;
                case KeyEvent.VK_RIGHT:
                    game.getViewport().setButtonScrolled(true);
                    game.getViewport().scroll(100, 0);
                    game.getDataModel().setButtonIn(false);
                    break;
                case KeyEvent.VK_F1:
                    game.getModel().playerCar.setMoney(game.getModel().playerCar.getMoney() + 1000);
                    break;
                case KeyEvent.VK_F2:
                    if (game.getModel().levelsUnlocked.size() != 20) {
                        game.getModel().levelsUnlocked.add(0);
                        game.updateLevels();
                    }
            }
        }
        if (game.getCurrentScreenState().equals(GAME_SCREEN_STATE)) {
            switch (ke) {
                case KeyEvent.VK_UP:
                    ((XPathMiniGame) game).getModel().getViewport().move(0, -25);
                    break;
                case KeyEvent.VK_DOWN:
                    ((XPathMiniGame) game).getModel().getViewport().move(0, 25);
                    break;
                case KeyEvent.VK_LEFT:
                    ((XPathMiniGame) game).getModel().getViewport().move(-50, 0);
                    break;
                case KeyEvent.VK_RIGHT:
                    ((XPathMiniGame) game).getModel().getViewport().move(50, 0);
                    break;
                case KeyEvent.VK_F:
                    if (game.getDataModel().isPaused()) {
                        game.getDataModel().unpause();
                    } else {
                        game.getDataModel().pause();
                    }
                    break;
                case KeyEvent.VK_G:
                    respondToPowerupRequest("./xpath/GreenLightPU.png");
                    break;
                case KeyEvent.VK_R:
                    respondToPowerupRequest("./xpath/RedLightPU.png");
                    break;
                case KeyEvent.VK_Z:
                    respondToPowerupRequest("./xpath/DecreaseSpeedLimitPU.png");
                    break;
                case KeyEvent.VK_X:
                    respondToPowerupRequest("./xpath/IncreaseSpeedLimitPU.png");
                    break;
                case KeyEvent.VK_P:
                    respondToPowerupRequest("./xpath/IncreasePlayerSpeedPU.png");
                    break;
                case KeyEvent.VK_T:
                    respondToPowerupRequest("./xpath/FlatTirePU.png");
                    break;
                case KeyEvent.VK_E:
                    respondToPowerupRequest("./xpath/EmptyGasTankPU.png");
                    break;
                case KeyEvent.VK_H:
                    respondToPowerupRequest("./xpath/RoadClosedPU.png");
                    break;
                case KeyEvent.VK_C:
                    respondToPowerupRequest("./xpath/CloseIntersectionPU.png");
                    break;
                case KeyEvent.VK_O:
                    respondToPowerupRequest("./xpath/OpenIntersectionPU.png");
                    break;
                case KeyEvent.VK_Q:
                    respondToPowerupRequest("./xpath/StealPU.png");
                    break;
                case KeyEvent.VK_M:
                    respondToPowerupRequest("./xpath/MindControlPU.png");
                    break;
                case KeyEvent.VK_B:
                    respondToPowerupRequest("./xpath/IntangibilityPU.png");
                    break;
                case KeyEvent.VK_L:
                    respondToPowerupRequest("./xpath/MindlessTerrorPU.png");
                    break;
                case KeyEvent.VK_Y:
                    respondToPowerupRequest("./xpath/FlyingPU.png");
                    break;
                case KeyEvent.VK_V:
                    respondToPowerupRequest("./xpath/InvincibilityPU.png");
                    break;
            }
        }
    }

    void loadEnemies() {
        int numPolice = game.getModel().getLevel().getNumPolice();
        int numBandits = game.getModel().getLevel().getNumBandits() + numPolice;
        int numZombies = game.getModel().getLevel().getNumZombies() + numBandits;

        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imgPath = props.getProperty(XPathPropertyType.PATH_IMG);
        Sprite s;
        SpriteType sT;
        BufferedImage img;

        // Police
        sT = new SpriteType("UI_CAR");
        img = game.loadImageWithColorKey(imgPath + "./xpath/PolC.png", COLOR_KEY);
        sT.addState(XPath.XPathGUIState.VISIBLE_STATE.toString(), img);
        img = game.loadImageWithColorKey(imgPath + "./xpath/PolC.png", COLOR_KEY);
        sT.addState(XPath.XPathGUIState.MOUSE_OVER_STATE.toString(), img);

        for (int i = 0; i < numPolice; i++) {
            game.getModel().enemies.add(new Enemy("POLICE", new LevelSprite(sT, 0, 0, 0, 0, XPath.XPathGUIState.VISIBLE_STATE.toString(), UNLOCKED, 0)));
        }
        // Bandits
        sT = new SpriteType("UI_CAR");
        img = game.loadImageWithColorKey(imgPath + "./xpath/BC.png", COLOR_KEY);
        sT.addState(XPath.XPathGUIState.VISIBLE_STATE.toString(), img);
        img = game.loadImageWithColorKey(imgPath + "./xpath/BC.png", COLOR_KEY);
        sT.addState(XPath.XPathGUIState.MOUSE_OVER_STATE.toString(), img);

        for (int i = numPolice; i < numBandits; i++) {
            game.getModel().enemies.add(new Enemy("BANDIT", new LevelSprite(sT, 0, 0, 0, 0, XPath.XPathGUIState.VISIBLE_STATE.toString(), UNLOCKED, 0)));
        }

        // Zombies
        sT = new SpriteType("UI_CAR");
        img = game.loadImageWithColorKey(imgPath + "./xpath/ZC.png", COLOR_KEY);
        sT.addState(XPath.XPathGUIState.VISIBLE_STATE.toString(), img);
        img = game.loadImageWithColorKey(imgPath + "./xpath/ZC.png", COLOR_KEY);
        sT.addState(XPath.XPathGUIState.MOUSE_OVER_STATE.toString(), img);

        for (int i = numBandits; i < numZombies; i++) {
            game.getModel().enemies.add(new Enemy("ZOMBIE", new LevelSprite(sT, 0, 0, 0, 0, XPath.XPathGUIState.VISIBLE_STATE.toString(), UNLOCKED, 0)));
        }

        game.getModel().doBanditNodes();

        for (Enemy enemy : game.getModel().enemies) {
            enemy.curInt = game.getModel().generateRandomPosition();
            enemy.s.setX(enemy.curInt.x - 28);
            enemy.s.setY(enemy.curInt.y - 28);
            if (enemy.type.equals("POLICE")) {
                movePolice(enemy);
            } else if (enemy.type.equals("BANDIT")) {
                moveBandit(enemy);
            } else if (enemy.type.equals("ZOMBIE")) {
                game.getModel().generateZombiePath(enemy);
                moveZombie(enemy);
            }
            game.getModel().resetStack();
        }

    }

    public void movePolice(Enemy enemy) {
        if (enemy.mindlessTerror) {
            enemy.selInt = game.getModel().generateRandomPositionMod();
            game.getModel().makeEnemyPathMod(enemy.curInt, game.getModel().getLevel().getIntersections(), Boolean.TRUE, enemy);
        } else {
            enemy.selInt = game.getModel().generateRandomPosition();
            game.getModel().makeEnemyPath(enemy.curInt, game.getModel().getLevel().getIntersections(), Boolean.TRUE, enemy);
        }
    }

    public void moveBandit(Enemy enemy) {
        if (enemy.mindlessTerror) {
            enemy.selInt = game.getModel().generateRandomPositionMod();
            game.getModel().makeEnemyPathMod(enemy.curInt, game.getModel().getLevel().getIntersections(), Boolean.TRUE, enemy);
        } else {
            if (banditPos == 0) {
                enemy.selInt = game.getModel().banditNodes.get(banditPos);
                game.getModel().makeEnemyPath(enemy.curInt, game.getModel().getLevel().getIntersections(), Boolean.TRUE, enemy);
                banditPos++;
            } else {
                enemy.selInt = game.getModel().banditNodes.get(banditPos);
                game.getModel().makeEnemyPath(enemy.curInt, game.getModel().getLevel().getIntersections(), Boolean.TRUE, enemy);
                banditPos--;
            }
        }
    }

    public void moveZombie(Enemy enemy) {
        if (enemy.mindlessTerror) {
            enemy.selInt = game.getModel().generateRandomPositionMod();
            game.getModel().makeEnemyPathMod(enemy.curInt, game.getModel().getLevel().getIntersections(), Boolean.TRUE, enemy);
        } else {
            if (zombiePos == 0) {
                enemy.selInt = enemy.nodesToVisit.get(zombiePos);
                game.getModel().makeEnemyPath(enemy.curInt, game.getModel().getLevel().getIntersections(), Boolean.TRUE, enemy);
                if (enemy.intPath.isEmpty()) {
                    enemy.selInt = enemy.nodesToVisit.get(zombiePos + 1);
                    game.getModel().makeEnemyPath(enemy.curInt, game.getModel().getLevel().getIntersections(), Boolean.TRUE, enemy);
                }
                zombiePos++;
            } else if (zombiePos == 1) {
                enemy.selInt = enemy.nodesToVisit.get(zombiePos);
                game.getModel().makeEnemyPath(enemy.curInt, game.getModel().getLevel().getIntersections(), Boolean.TRUE, enemy);
                if (enemy.intPath.isEmpty()) {
                    enemy.selInt = enemy.nodesToVisit.get(zombiePos + 1);
                    game.getModel().makeEnemyPath(enemy.curInt, game.getModel().getLevel().getIntersections(), Boolean.TRUE, enemy);
                }
                zombiePos++;
            } else {
                enemy.selInt = enemy.nodesToVisit.get(zombiePos);
                game.getModel().makeEnemyPath(enemy.curInt, game.getModel().getLevel().getIntersections(), Boolean.TRUE, enemy);
                if (enemy.intPath.isEmpty()) {
                    enemy.selInt = enemy.nodesToVisit.get(0);
                    game.getModel().makeEnemyPath(enemy.curInt, game.getModel().getLevel().getIntersections(), Boolean.TRUE, enemy);
                }
                zombiePos = 0;
            }
        }
    }

    void loseLevel() {

    }

    void winLevel() {
        if (game.getModel().levelsUnlocked.size() == ((LevelSprite) game.getGUIButtons().get(game.getModel().levelName)).getLevelNumber() + 1) {
            game.getModel().levelsUnlocked.add(0);
        }
        game.updateLevels();
    }

    void respondToPowerupRequest(String actionCommand) {
        int x = -1;

        if (actionCommand.equals("./xpath/GreenLightPU.png")) {
            if (game.getModel().playerCar.getMoney() >= 10 && !game.getModel().powerups[0]) {
                game.getModel().playerCar.setMoney(game.getModel().playerCar.getMoney() - 10);
                x = 0;

            }
        } else if (actionCommand.equals("./xpath/RedLightPU.png")) {
            if (game.getModel().playerCar.getMoney() >= 10 && !game.getModel().powerups[1]) {
                game.getModel().playerCar.setMoney(game.getModel().playerCar.getMoney() - 10);
                x = 1;
            }
        } else if (actionCommand.equals("./xpath/DecreaseSpeedLimitPU.png")) {
            if (game.getModel().playerCar.getMoney() >= 15 && !game.getModel().powerups[2]) {
                game.getModel().playerCar.setMoney(game.getModel().playerCar.getMoney() - 15);
                x = 2;
            }
        } else if (actionCommand.equals("./xpath/IncreaseSpeedLimitPU.png")) {
            if (game.getModel().playerCar.getMoney() >= 15 && !game.getModel().powerups[3]) {
                game.getModel().playerCar.setMoney(game.getModel().playerCar.getMoney() - 15);
                x = 3;
            }
        } else if (actionCommand.equals("./xpath/IncreasePlayerSpeedPU.png")) {
            if (game.getModel().playerCar.getMoney() >= 20 && !game.getModel().powerups[4]) {
                game.getModel().playerCar.setMoney(game.getModel().playerCar.getMoney() - 20);
                game.getModel().playerCar.setMovespeed((float) (game.getModel().playerCar.getMovespeed() + .2));
                if (game.getModel().isSound()) {
                    game.getAudio().play(XPath.XPathPropertyType.EFFECT.toString(), true);
                }
            }
        } else if (actionCommand.equals("./xpath/FlatTirePU.png")) {
            if (game.getModel().playerCar.getMoney() >= 20 && !game.getModel().powerups[5]) {
                game.getModel().playerCar.setMoney(game.getModel().playerCar.getMoney() - 20);
                x = 5;
            }
        } else if (actionCommand.equals("./xpath/EmptyGasTankPU.png")) {
            if (game.getModel().playerCar.getMoney() >= 20 && !game.getModel().powerups[6]) {
                game.getModel().playerCar.setMoney(game.getModel().playerCar.getMoney() - 20);
                x = 6;
            }

        } else if (actionCommand.equals("./xpath/RoadClosedPU.png")) {
            if (game.getModel().playerCar.getMoney() >= 25 && !game.getModel().powerups[7]) {
                game.getModel().playerCar.setMoney(game.getModel().playerCar.getMoney() - 25);
                x = 7;
            }
        } else if (actionCommand.equals("./xpath/CloseIntersectionPU.png")) {
            if (game.getModel().playerCar.getMoney() >= 25 && !game.getModel().powerups[8]) {
                game.getModel().playerCar.setMoney(game.getModel().playerCar.getMoney() - 25);
                x = 8;
            }
        } else if (actionCommand.equals("./xpath/OpenIntersectionPU.png")) {
            if (game.getModel().playerCar.getMoney() >= 25 && !game.getModel().powerups[9]) {
                game.getModel().playerCar.setMoney(game.getModel().playerCar.getMoney() - 25);
                x = 9;
            }
        } else if (actionCommand.equals("./xpath/StealPU.png")) {
            if (game.getModel().playerCar.getMoney() >= 30 && !game.getModel().powerups[10]) {
                game.getModel().playerCar.setMoney(game.getModel().playerCar.getMoney() - 30);
                game.getModel().playerCar.stealCooldown = 300;
                x = 10;
            }
        } else if (actionCommand.equals("./xpath/MindControlPU.png")) {
            if (game.getModel().playerCar.getMoney() >= 30 && !game.getModel().powerups[11]) {
                game.getModel().playerCar.setMoney(game.getModel().playerCar.getMoney() - 30);
                x = 11;
            }
        } else if (actionCommand.equals("./xpath/IntangibilityPU.png")) {
            if (game.getModel().playerCar.getMoney() >= 30 && !game.getModel().powerups[12]) {
                game.getModel().playerCar.setMoney(game.getModel().playerCar.getMoney() - 30);
                game.getModel().playerCar.setIntangibilityCooldown(300);
                x = 12;
            }
        } else if (actionCommand.equals("./xpath/MindlessTerrorPU.png")) {
            if (game.getModel().playerCar.getMoney() >= 30 && !game.getModel().powerups[13]) {
                game.getModel().playerCar.setMoney(game.getModel().playerCar.getMoney() - 30);
                x = 13;
            }
        } else if (actionCommand.equals("./xpath/FlyingPU.png")) {
            if (game.getModel().playerCar.getMoney() >= 40 && !game.getModel().powerups[14]) {
                game.getModel().playerCar.setMoney(game.getModel().playerCar.getMoney() - 40);
                x = 14;
            }
        } else if (actionCommand.equals("./xpath/InvincibilityPU.png")) {
            if (game.getModel().playerCar.getMoney() >= 40 && !game.getModel().powerups[15]) {
                game.getModel().playerCar.setMoney(game.getModel().playerCar.getMoney() - 40);
                game.getModel().playerCar.setInvincibilityCooldown(150);
                x = 15;
            }
        }
        if (x != -1) {
            for (int i = 0; i < game.getModel().powerups.length; i++) {
                game.getModel().powerups[i] = false;
            }
            game.getModel().powerups[x] = true;
            if (game.getModel().isSound()) {
                game.getAudio().play(XPath.XPathPropertyType.EFFECT.toString(), true);
            }
        }
    }

    void resetGame() {
        for (int i = 0; i < game.getModel().levelsUnlocked.size()-1; i++) {
            game.getModel().levelsUnlocked.remove(i);
            i--;
        }
        game.getModel().playerCar.setMoney(0);
        game.updateLevels();
    }

}
