package aoop.asteroids.model;

import aoop.asteroids.controller.PlayerActions;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Random;

public abstract class Game extends Observable implements Runnable, Serializable {

    private static final long serialVersionUID = 1L;

    // all spaceships : one element in single player
    protected ArrayList<Spaceship> allSpaceships;
    // all players :
    protected ArrayList<Player> allPlayers;
    protected ArrayList<Bullet> bullets;
    // list of asteroids
    protected ArrayList<Asteroid> asteroids;
    // random number generator
    private static Random rng;
    // game tick counter for spawning random asteroids
    private int cycleCounter;
    // maximum number of asteroids
    protected int asteroidsLimit;
    // indicates whether a new game is about to be started
    private Boolean aborted;
    // thread loop condition : thread is running
    protected Boolean isRunning;
    // award points to given players
    protected ArrayList<Integer> pointsTo;
    // the local ship
    protected Spaceship ship;
    // the local player
    protected Player player;
    // is the game waiting to start
    private Boolean isWaiting;
    // is this a spectator
    private Boolean isSpectator;

    /**
     * Default constructor : initialises default values
     * called when spectate
     */
    public Game() {
        Game.rng = new Random();
        this.allSpaceships = new ArrayList<>();
        this.allPlayers = new ArrayList<>();
        this.isRunning = true;
        this.pointsTo = new ArrayList<>();
        this.initGameData();
        this.isSpectator = true;
    }

    /**
     * Constructor : initialises default values (see default constructor)
     * one player and its ship
     */
    public Game(Player player) {
        this();
        this.player = player;
        // assign the player a random id number
        this.createPlayerID(player);
        this.allPlayers.add(player);
        this.addShip(player);
        this.isWaiting = true;
        this.isSpectator = false;
    }

    /**
     * Sets all game data to hold the values of a new game.
     */
    public void initGameData() {
        this.aborted = false;
        this.cycleCounter = 0;
        this.asteroidsLimit = 7;
        this.bullets = new ArrayList<>();
        this.asteroids = new ArrayList<>();
        this.reinitShips();
    }

    /**
     * Add a new ship, add all characteristics
     */
    protected void addShip(Player player) {
        Spaceship s = new Spaceship();
        s.setIdNumber(player.getIdNumber());
        s.setColor(player.getColor());
        s.setName(player.getName());
        this.allSpaceships.add(s);
    }

    public void reinitShips() {
        for (Spaceship s : this.allSpaceships) {
            s.reinit();
        }
    }

    /**
     * Returns a clone of the asteroid set, preserving encapsulation.
     */
    public ArrayList<Asteroid> getAsteroids() {
        ArrayList<Asteroid> c = new ArrayList<>();
        for (Asteroid a : this.asteroids) {
            c.add(a.clone());
        }
        return c;
    }

    /**
     * Returns a clone of the bullet set, preserving encapsulation.
     */
    public ArrayList<Bullet> getBullets() {
        ArrayList<Bullet> c = new ArrayList<>();
        for (Bullet b : this.bullets) {
            c.add(b.clone());
        }
        return c;
    }

    /**
     * Returns a clone of the spaceship set, preserving encapsulation
     */
    public ArrayList<Spaceship> getSpaceships() {
        ArrayList<Spaceship> c = new ArrayList<>();
        for (Spaceship s : this.allSpaceships) {
            c.add(s.clone());
        }
        return c;
    }

    /**
     * Returns a clone of the players set, preserving encapsulation
     */
    public ArrayList<Player> getPlayers() {
        ArrayList<Player> c = new ArrayList<>();
        for (Player p : this.allPlayers) {
            c.add(p.clone());
        }
        return c;
    }

    /**
     * Method invoked at every game tick. It updates all game objects first.
     * Then it adds a bullet if the player is firing. Afterwards it checks all
     * objects for collisions and removes the destroyed objects. Finally the
     * game tick counter is updated and a new asteroid is spawn upon every
     * 200th game tick.
     */
    public void update() {
        for (int i = 0; i < this.asteroids.size(); i++) {
            this.asteroids.get(i).nextStep();
        }
        for (int i = 0; i < this.bullets.size(); i++) {
            this.bullets.get(i).nextStep();
        }

        for (int i = 0; i < this.allSpaceships.size(); i++) {
            if (!this.allSpaceships.get(i).isDestroyed()) {
                if (this.allSpaceships.get(i).isFiring()) {
                    double direction = this.allSpaceships.get(i).getDirection();
                    this.bullets.add(new Bullet(this.allSpaceships.get(i).getIdNumber(), this.allSpaceships.get(i).getLocation(), this.allSpaceships.get(i).getVelocityX() + Math.sin(direction) * 15, this.allSpaceships.get(i).getVelocityY() - Math.cos(direction) * 15));
                    this.allSpaceships.get(i).setFired();
                }
            }
            this.allSpaceships.get(i).nextStep();
        }


        this.checkCollisions();
        this.removeDestroyedObjects();

        if (this.cycleCounter == 0 && this.asteroids.size() < this.asteroidsLimit && !this.isSpectator) {
            this.addRandomAsteroid();
        }
        this.cycleCounter++;
        this.cycleCounter %= 200;

        this.updateObservable();
    }


    /**
     * Adds a randomly sized asteroid at least 50 pixels removed from the
     * player.
     */
    private void addRandomAsteroid() {
        int prob = Game.rng.nextInt(3000);
        // add it relatively to a randomly selected ship
        int relativeToShip = Game.rng.nextInt(this.allSpaceships.size());
        Point loc, shipLoc = this.allSpaceships.get(relativeToShip).getLocation();
        int x, y;
        do {
            loc = new Point(Game.rng.nextInt(800), Game.rng.nextInt(800));
            x = loc.x - shipLoc.x;
            y = loc.y - shipLoc.y;
        } while (Math.sqrt(x * x + y * y) < 50);

        if (prob < 1000) {
            this.asteroids.add(new LargeAsteroid(loc, Game.rng.nextDouble() * 6 - 3, Game.rng.nextDouble() * 6 - 3));
        } else if (prob < 2000) {
            this.asteroids.add(new MediumAsteroid(loc, Game.rng.nextDouble() * 6 - 3, Game.rng.nextDouble() * 6 - 3));
        } else {
            this.asteroids.add(new SmallAsteroid(loc, Game.rng.nextDouble() * 6 - 3, Game.rng.nextDouble() * 6 - 3));
        }
    }

    /**
     * Checks all objects for collisions and marks them as destroyed upon
     * collision. All objects can collide with objects of a different type,
     * but not with objects of the same type. I.e. bullets cannot collide with
     * bullets etc.
     */
    private void checkCollisions() {
        // For all bullets.
        for (int i = 0; i < this.bullets.size(); i++) {
            // Check all bullet - asteroid combinations.
            for (int j = 0; j < this.asteroids.size(); j++) {
                if (this.asteroids.get(j).collides(this.bullets.get(i))) {
                    this.asteroids.get(j).destroy();
                    this.bullets.get(i).destroy();
                    this.pointsTo.add(this.bullets.get(i).getIdNumber());
                }
            }
        }
        checkSpecificCollisions();
    }

    /**
     * Removes all destroyed objects. Destroyed asteroids increase the score
     * and spawn two smaller asteroids if it wasn't a small asteroid. New
     * asteroids are faster than their predecessor and travel in opposite
     * direction.
     */
    private void removeDestroyedObjects() {
        ArrayList<Asteroid> newAsts = new ArrayList<>();
        for (Asteroid a : this.asteroids) {
            if (a.isDestroyed()) {
                this.increaseScore();
                ArrayList<Asteroid> successors = a.getSuccessors();
                newAsts.addAll(successors);
            } else {
                newAsts.add(a);
            }
        }

        this.asteroids = newAsts;

        ArrayList<Bullet> newBuls = new ArrayList<>();
        for (Bullet b : this.bullets) {
            if (!b.isDestroyed()) {
                newBuls.add(b);
            }
        }

        this.bullets = newBuls;

        // when a ship is removed, also remove the player
        ArrayList<Spaceship> newShips = new ArrayList<>();
        for (Spaceship s : this.allSpaceships) {
            if (!s.isDestroyed()) {
                newShips.add(s);
            } else {
                removePlayer(s.getIdNumber());
            }
        }

        this.allSpaceships = newShips;
    }

    /**
     * Abort game
     */
    public void abort() {
        this.aborted = true;
    }

    /**
     * This method allows this object to run in its own thread, making sure
     * that the same thread will not perform non essential computations for
     * the game. The thread will not stop running until the program is quit.
     * If the game is aborted or the player died, it will wait 100
     * milliseconds before reevaluating and continuing the simulation.
     * <p>
     * While the game is not aborted and the player is still alive, it will
     * measure the time it takes the program to perform a game tick and wait
     * 40 minus execution time milliseconds to do it all over again. This
     * allows the game to update every 40th millisecond, thus keeping a steady
     * 25 frames per second.
     * <p>
     * Decrease waiting time to increase fps. Note
     * however, that all game mechanics will be faster as well. I.e. asteroids
     * will travel faster, bullets will travel faster and the spaceship may
     * not be as easy to control.
     */
    public void run() {
        // Update -> sleep -> update -> sleep -> etc...
        long executionTime, sleepTime;
        // thread was started, set boolean to false
        this.isWaiting = false;
        while (this.isRunning) {
            if (!this.gameOver() && !this.aborted) {
                executionTime = System.currentTimeMillis();
                this.update();
                executionTime -= System.currentTimeMillis();
                sleepTime = Math.max(0, 40 + executionTime);
            } else {
                sleepTime = 100;
                if (!this.isSpectator) {
                    endGameUpdates();
                    this.updateObservable();
                }
            }

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                System.err.println("Could not perform action: Thread.sleep(...)");
                System.err.println("The thread that needed to sleep is the game thread, responsible for the game loop (update -> wait -> update -> etc).");
                e.printStackTrace();
            }
        }
    }

    /**
     * Create an unique ID for each player
     */
    private void createPlayerID(Player p) {
        int choose = new Random().nextInt(300);
        if (choose <= 100) {
            p.setIdNumber(Math.abs(p.getColor().getRGB() + choose));
        } else if (choose <= 200) {
            p.setIdNumber(Math.abs(p.getColor().getRGB() + choose));
        } else {
            p.setIdNumber(Math.abs(p.getColor().getRGB() + choose));
        }
    }

    /**
     * Notify observers
     */
    public void updateObservable() {
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Set the current ship to the controller
     */
    public void linkController(PlayerActions pa) {
        pa.addShip(this.ship);
    }

    /**
     * Returns a boolean to check whether the player won the game or not.
     */
    public Boolean isWon() {
        if (this.allPlayers.size() > 0) {
            return this.player.getIdNumber() == this.allPlayers.get(0).getIdNumber();
        }
        // maybe last players died hit by the same asteroid
        return false;
    }

    /**
     * Get local player for the current game.
     */
    public Player getLocalPlayer() {
        return this.player;
    }

    /**
     * Get if the current game is waiting to begin
     */
    public Boolean getIsWaiting() {
        return this.isWaiting;
    }

    /**
     * Get if the current game is a spectate game
     */
    public Boolean getIsSpectator() {
        return this.isSpectator;
    }

    /**
     * Abstract methods : to be implemented in the subclasses
     */

    public abstract void checkSpecificCollisions();

    /**
     * Increases the score of the player by one and updates asteroid limit
     * when required
     */
    public abstract void increaseScore();


    /**
     * Return the database
     */
    public abstract Database getDatabase();

    /**
     * for instance: in multi-player last standing player gets +1 point
     */
    public abstract void endGameUpdates();

    /**
     * Check if the game is over : to be implemented in the subclasses
     */
    public abstract Boolean gameOver();

    /**
     * If the ship got destroyed, pass its id number and remove the corresponding player
     */
    public abstract void removePlayer(int id);

    public abstract Player getPlayer(int id);

}
