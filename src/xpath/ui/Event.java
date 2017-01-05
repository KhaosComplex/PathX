/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xpath.ui;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mini_game.MiniGame;
import xpath.XPathConstants;

/**
 *
 * @author Khaos
 */
public class Event implements ChangeListener {

    private JSlider slider;
    private MiniGame game;
    
    public Event(JSlider sliderT, MiniGame gameT) {
        slider = sliderT;
        game = gameT;
    }
    public void stateChanged(ChangeEvent e) {
        XPathConstants.FPS = slider.getValue();
        
        game.setFramesPerSecond(XPathConstants.FPS);
    }
}
