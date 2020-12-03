package aoop.asteroids.model;

import java.awt.Point;

/**
 * The bullet is the ultimate weapon of the player. It has the same mechanics
 * as an asteroid, in which it cannot divert from its trajectory. However, the
 * bullet has the addition that it only exists for a certain amount of game
 * steps.
 *
 * @author Yannick Stoffers, added an unique ID number for each bullet
 */
public class Bullet extends GameObject {

    /**
     * The amount of steps this bullet still is allowed to live. When this
     * value drops below 0, the bullet is removed from the game model.
     */
    private int stepsLeft;

    /**
     * Field to know where did the bullet come from in order to assign points
     */
    private int idNumber;

    /**
     * Constructs a new bullet using the given location and velocity
     * parameters. The amount of steps the bullet gets to live is by default
     * 60.
     *
     * @param idNumber  unique id number of the spaceship that triggered the bullet.
     * @param location  location of the bullet.
     * @param velocityX velocity of the bullet as projected on the X-axis.
     * @param velocityY velocity of the bullet as projected on the Y-axis.
     */
    public Bullet(int idNumber, Point location, double velocityX, double velocityY) {
        this(idNumber, location, velocityX, velocityY, 45);
    }

    /**
     * Constructs a new bullet using the given location and velocity
     * parameters. The amount of steps the bullet gets to live is set to the
     * given value. This constructor is primarily used for the clone ()
     * method.
     *
     * @param idNumber  unique id number of the spaceship that triggered the bullet.
     * @param location  location of the bullet.
     * @param velocityX velocity of the bullet as projected on the X-axis.
     * @param velocityY velocity of the bullet as projected on the Y-axis.
     * @param stepsLeft amount of steps the bullet is allowed to live.
     * @see #clone()
     */
    private Bullet(int idNumber, Point location, double velocityX, double velocityY, int stepsLeft) {
        super(location, velocityX, velocityY, 0);
        this.stepsLeft = stepsLeft;
        this.idNumber = idNumber;
    }

    /**
     * Method that determines the behaviour of the bullet. The behaviour of a
     * bullet is defined by adding the velocity to its location parameters.
     * The location is then restricted to values between 0 and 800 (size of
     * the window).
     */
    @Override
    public void nextStep() {
        this.stepsTilCollide = Math.max(0, this.stepsTilCollide - 1);
        this.locationX = (800 + this.locationX + this.velocityX) % 800;
        this.locationY = (800 + this.locationY + this.velocityY) % 800;
        this.stepsLeft--;

        if (this.stepsLeft < 0)
            this.destroy();
    }

    /**
     * Clones the bullet into an exact copy.
     */
    public Bullet clone() {
        return new Bullet(this.idNumber, this.getLocation(), this.velocityX, this.velocityY, this.stepsLeft);
    }

    /**
     * ID number getter
     */
    public int getIdNumber() {
        return this.idNumber;
    }

}
