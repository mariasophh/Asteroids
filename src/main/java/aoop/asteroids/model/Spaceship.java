package aoop.asteroids.model;

import java.awt.*;

/**
 * This class represents the player in the Asteroids game. A spaceship is able
 * to shoot every 20 game ticks (twice per second).
 * <p>
 * A massive difference with other game objects is that the spaceship has a
 * certain direction in which it is pointed. This influences the way it is
 * drawn and determines the direction of spawned bullets. Although the game
 * setting is in outer space, the spaceship is subject to traction and will
 * slowly deaccelerate.
 *
 * @author Yannick Stoffers, added an unique ID number to link the ship to its player
 */
public class Spaceship extends GameObject {

    // direction of the spaceship
    private double direction;
    // amount of game ticks left, until the spaceship can fire again
    private int stepsTillFire;
    // current action of the ship */
    private Boolean isFiring;
    private Boolean up;
    private Boolean right;
    private Boolean left;

    // set a correlation between a player and its corresponding ship
    private int idNumber;
    // color and name from the player : view purposes
    private Color color;
    private String name;

    /**
     * Constructs a new spaceship with default values
     */
    public Spaceship() {
        this(new Point(400, 400), 0, 0, 15, 0, false);
    }

    /**
     * Constructs a new spaceship with specified values
     */
    private Spaceship(Point location, double velocityX, double velocityY, int radius, double direction, boolean up) {
        super(location, velocityX, velocityY, radius);
        this.direction = direction;
        this.up = up;
        this.isFiring = false;
        this.left = false;
        this.right = false;
        this.stepsTillFire = 0;
    }

    /**
     * Resets all parameters to default values, so a new game can be started
     */
    public void reinit() {
        this.locationX = 400;
        this.locationY = 400;
        this.velocityX = 0;
        this.velocityY = 0;
        this.direction = 0;
        this.up = false;
        this.isFiring = false;
        this.left = false;
        this.right = false;
        this.destroyed = false;
        this.stepsTillFire = 0;
    }

    /**
     * Set the actions of the ship : used for deserialize
     */
    public void setActions(Boolean up, Boolean left, Boolean right, Boolean isFiring) {
        this.setUp(up);
        this.setLeft(left);
        this.setRight(right);
        this.setIsFiring(isFiring);
    }

    /**
     * Defines the behaviour of the spaceship. In each game tick the ship
     * turns when a turn button is pressed. The speed at which it turns is 2%
     * of a full rotation per game tick. Afterwards the spaceships velocity
     * will be updated if the player wants to accelerate. The velocity however
     * is restricted to 10 pixels per game tick in both X and Y direction.
     * Afterwards the location of the ship will be updated and the velocity
     * decreased to account for traction.
     */
    @Override
    public void nextStep() {
        this.stepsTilCollide = Math.max(0, this.stepsTilCollide - 1);

        // Update direction if turning.
        if (this.left) this.direction -= 0.04 * Math.PI;
        if (this.right) this.direction += 0.04 * Math.PI;

        if (this.up) {
            // Update speed if accelerating, but constrain values.
            this.velocityX = Math.max(-10, Math.min(10, this.velocityX + Math.sin(direction) * 0.4));
            this.velocityY = Math.max(-10, Math.min(10, this.velocityY - Math.cos(direction) * 0.4));
        }

        // Update location.
        this.locationX = (800 + this.locationX + this.velocityX) % 800;
        this.locationY = (800 + this.locationY + this.velocityY) % 800;

        // Decrease speed due to traction.
        this.velocityX *= 0.99;
        this.velocityY *= 0.99;

        // Decrease firing step counter.
        if (this.stepsTillFire != 0) {
            this.stepsTillFire--;
        }
    }


    /**
     * Returns a copy of the spaceship : call second constructor with exact current values
     */
    public Spaceship clone() {
        Spaceship s = new Spaceship(this.getLocation(), this.velocityX, this.velocityY, this.radius, this.direction, this.up);
        s.setIdNumber(this.idNumber);
        s.setColor(this.color);
        s.setName(this.name);
        return s;
    }

    /**
     * getters
     */
    public boolean isAccelerating() {
        return this.up;
    }

    public boolean isFiring() {
        return this.isFiring && this.stepsTillFire == 0;
    }

    public int getIdNumber() {
        return this.idNumber;
    }

    public Color getColor() {
        return this.color;
    }

    public String getName() {
        return this.name;
    }

    /**
     * setters
     */
    public double getDirection() {
        return this.direction;
    }

    public void setFired() {
        this.stepsTillFire = 20;
    }

    public void setIdNumber(int idNumber) {
        this.idNumber = idNumber;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
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
