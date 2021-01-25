/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Custom key listener.
 *
 * @author letha
 */
public class Key implements KeyListener {

    private boolean press = false;
    private boolean release = true;

    /**
     * Execute when a key is typed.
     *
     * @param e key from keyboard
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Execute when a key is pressed.
     *
     * @param e key from keyboard
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            if (release) {
                press = true;
                release = false;
            }
        }
    }

    /**
     * Execute when a key is released.
     *
     * @param e key from keyboard
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            release = true;
        }
    }

    /**
     * Check if a key is pressed.
     *
     * @return <Code>boolean</Code> datatype
     */
    public boolean isPress() {
        return press;
    }

    /**
     * Set press status of key.
     *
     * @param press <Code>boolean</Code> datatype
     */
    public void setPress(boolean press) {
        this.press = press;
    }

    /**
     * Check if a key is released.
     *
     * @return <Code>boolean</Code> datatype
     */
    public boolean isRelease() {
        return release;
    }

    /**
     * Set release status of key.
     *
     * @param release <Code>boolean</Code> datatype
     */
    public void setRelease(boolean release) {
        this.release = release;
    }

}
