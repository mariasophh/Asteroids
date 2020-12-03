package aoop.asteroids.controller;

import aoop.asteroids.model.Spaceship;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;

/**
 * PlayerActions is the controller class that listens to the users input (key events)
 * and forwards those events to the spaceship class, where they will influence
 * the behaviour of the spaceship.
 */
public class PlayerActions implements KeyListener, Serializable {

    private static final long serialVersionUID = 1L;

    // current action of the ship
    private Boolean isFiring;
    private Boolean up;
    private Boolean right;
    private Boolean left;

    // ship that is influenced
    private Spaceship ship;

    public PlayerActions() {
        this.isFiring = false;
        this.up = false;
        this.right = false;
        this.left = false;
    }

    public void addShip(Spaceship ship) {
        this.ship = ship;
    }

    /**
     * This method is invoked when a key is pressed and sets the corresponding
     * fields in the spaceship to true.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                this.ship.setUp(true);
                this.setUp(true);
                break;
            case KeyEvent.VK_LEFT:
                this.ship.setLeft(true);
                this.setLeft(true);
                break;
            case KeyEvent.VK_RIGHT:
                this.ship.setRight(true);
                this.setRight(true);
                break;
            case KeyEvent.VK_SPACE:
                this.ship.setIsFiring(true);
                this.setIsFiring(true);
                break;
        }
    }

    /**
     * This method is invoked when a key is released and sets all the fields to false
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                this.ship.setUp(false);
                this.setUp(false);
                break;
            case KeyEvent.VK_LEFT:
                this.ship.setLeft(false);
                this.setLeft(false);
                break;
            case KeyEvent.VK_RIGHT:
                this.ship.setRight(false);
                this.setRight(false);
                break;
            case KeyEvent.VK_SPACE:
                this.ship.setIsFiring(false);
                this.setIsFiring(false);
                break;
        }
    }

    /**
     * This method doesn't do anything.
     *
     * @param e keyevent that triggered the method.
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Returns a string of ship current actions : used for serialize
     */
    public String getActions() {
        return "actions-" + this.ship.getIdNumber() + "-" + Boolean.toString(this.up) + "-" + Boolean.toString(this.left) + "-" + Boolean.toString(this.right) + "-" + Boolean.toString(this.isFiring) + "-";
    }

    /**
     * setters for fields : up, left, right, isFiring
     */
    public void setIsFiring(boolean b) {
        this.isFiring = b;
    }

    public void setLeft(boolean b) {
        this.left = b;
    }

    public void setRight(boolean b) {
        this.right = b;
    }

    public void setUp(boolean b) {
        this.up = b;
    }
}
