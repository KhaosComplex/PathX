/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xpath.data;

import java.util.Timer;
import xpath.XPath;
import xpath.ui.XPathMiniGame;

/**
 *
 * @author Khaos
 */
public class PlayerCar {

    private float movespeed;
    private int money;
    private int moneyLost;
    private float cooldownPolice;
    private float cooldownBandit;
    private float cooldownZombie;
    private float invincibilityCooldown;
    private float intangibilityCooldown;
    public float moveCooldown = 0;
    public float stealCooldown = 0;
    public boolean hitByTerror = false;
    transient XPathMiniGame game;

    public PlayerCar(XPathMiniGame initGame) {
        movespeed = 1;
        money = 0;
        moneyLost = 0;
        cooldownBandit = 0;
        cooldownZombie = 0;
        game = initGame;
    }

    public void collidePolice() {
        moneyLost = money - (int) (money * .90);
        money = (int) (money * .90);
        if (game.getModel().isSound()) {
            game.getAudio().play(XPath.XPathPropertyType.COLLISION.toString(), true);
        }
        game.getDataModel().pause();
        game.switchToPlay();
        game.showLossScreen();

    }

    public void collideBandit() {
        if (getCooldownBandit() == 0) {
            setCooldownBandit(300);
            game.getModel().getLevel().setMoney((int) (game.getModel().getLevel().getMoney() * .90));
            if (game.getModel().isSound()) {
                game.getAudio().play(XPath.XPathPropertyType.COLLISION.toString(), true);
            }
        }
    }

    public void collideZombie() {
        if (getCooldownZombie() == 0) {
            if (movespeed > .1) {
                movespeed += -(float) .10;
                if (game.getModel().isSound()) {
                    game.getAudio().play(XPath.XPathPropertyType.COLLISION.toString(), true);
                }
            }
            setCooldownZombie(300);
        }
    }

    /**
     * @return the movespeed
     */
    public float getMovespeed() {
        return movespeed;
    }

    /**
     * @param movespeed the movespeed to set
     */
    public void setMovespeed(float movespeed) {
        this.movespeed = movespeed;
    }

    /**
     * @return the money
     */
    public int getMoney() {
        return money;
    }

    /**
     * @param money the money to set
     */
    public void setMoney(int money) {
        this.money = money;
    }

    /**
     * @return the cooldownPolice
     */
    public float getCooldownPolice() {
        return cooldownPolice;
    }

    /**
     * @param cooldownPolice the cooldownPolice to set
     */
    public void setCooldownPolice(float cooldownPolice) {
        this.cooldownPolice = cooldownPolice;
    }

    /**
     * @return the cooldownBandit
     */
    public float getCooldownBandit() {
        return cooldownBandit;
    }

    /**
     * @param cooldownBandit the cooldownBandit to set
     */
    public void setCooldownBandit(float cooldownBandit) {
        this.cooldownBandit = cooldownBandit;
    }

    /**
     * @return the cooldownZombie
     */
    public float getCooldownZombie() {
        return cooldownZombie;
    }

    /**
     * @param cooldownZombie the cooldownZombie to set
     */
    public void setCooldownZombie(float cooldownZombie) {
        this.cooldownZombie = cooldownZombie;
    }

    public void reset() {
        movespeed = 1;
        moneyLost = 0;
        cooldownPolice = 0;
        cooldownBandit = 0;
        cooldownZombie = 0;
        invincibilityCooldown = 0;
        intangibilityCooldown = 0;
        moveCooldown = 0;
        stealCooldown = 0;
        hitByTerror = false;
    }

    /**
     * @return the moneyLost
     */
    public int getMoneyLost() {
        return moneyLost;
    }

    /**
     * @return the invincibilityCooldown
     */
    public float getInvincibilityCooldown() {
        return invincibilityCooldown;
    }

    /**
     * @param invincibilityCooldown the invincibilityCooldown to set
     */
    public void setInvincibilityCooldown(float invincibilityCooldown) {
        this.invincibilityCooldown = invincibilityCooldown;
    }

    /**
     * @return the intangibilityCooldown
     */
    public float getIntangibilityCooldown() {
        return intangibilityCooldown;
    }

    /**
     * @param intangibilityCooldown the intangibilityCooldown to set
     */
    public void setIntangibilityCooldown(float intangibilityCooldown) {
        this.intangibilityCooldown = intangibilityCooldown;
    }
}
