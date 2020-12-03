package aoop.asteroids.model;

import aoop.asteroids.controller.PlayerActions;

public class SinglePlayerGame extends Game {

    private static final long serialVersionUID = 1L;

    private Player player;
    private PlayerActions controller;

    /**
     * Constructor : call the default constructor in the super class that initialises one ship
     */
    public SinglePlayerGame(Player player, PlayerActions controller) {
        super(player);
        this.ship = this.allSpaceships.get(0);
        this.player = player;
        this.controller = controller;
    }


    public void checkSpecificCollisions() {
        // for all asteroids check if they collide with the ship
        for (int i = 0; i < this.asteroids.size(); i++) {
            if (this.asteroids.get(i).collides(this.ship)) {
                // destroy both objects
                this.asteroids.get(i).destroy();
                this.ship.destroy();
            }
        }
    }

    /**
     * Increase score for player;
     * maximize number of asteroids to gradually increase difficulty
     */
    public void increaseScore() {
        this.player.increaseScore();
        if (this.player.getCurrentScore() % 5 == 0) {
            super.asteroidsLimit++;
        }
    }

    /**
     * The game will automatically restart so create a new ship and link its controller
     */
    public void endGameUpdates() {
        this.addShip(this.player);
        this.ship = this.allSpaceships.get(0);
        this.initGameData();
        this.linkController(controller);
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * A single player game ends when the only ship is destroyed
     */
    public Boolean gameOver() {
        return this.ship.isDestroyed();
    }

    /**
     * The ship got destroyed, reset the score of the player
     * game will restart at the next thread iteration
     */
    public void removePlayer(int id) {
        this.player.resetScore();
    }

    public Player getPlayer(int id) {
        return this.player;
    }


    /**
     * No database for a SinglePlayer state.
     */
    public Database getDatabase() {
        return null;
    }
}
