package aoop.asteroids.model;

import aoop.asteroids.controller.PlayerActions;

import java.util.ArrayList;
import java.util.Collection;

public class MultiPlayerGame extends Game {

    private static final long serialVersionUID = 1L;

    private PlayerActions controller;
    private Boolean shouldEnd, isConnected, isHost;
    private transient Database database;

    /**
     * Default constructor : for spectate game
     */
    public MultiPlayerGame() {
        super();
        this.shouldEnd = false;
        this.isConnected = true;
        this.isHost = false;

    }

    /**
     * Constructor : call the superclass constructor
     */
    public MultiPlayerGame(Player player, PlayerActions controller, Boolean isHost) {
        super(player);
        // get the local ship
        this.ship = this.getSpaceShip(player.getIdNumber());
        // link the controller
        this.controller = controller;
        this.linkController(controller);
        this.isConnected = true;
        this.isHost = isHost;
        // assign this to false : see gameOver method
        this.shouldEnd = false;
        if (this.isHost) {
            this.database = new Database();
            this.database.addPlayer(this.getLocalPlayer());
        } else {
            this.database = null;
        }

    }

    public void addPlayer(Player player) {
        this.allPlayers.add(player);
        this.addShip(player);
        this.database.addPlayer(player);
        this.setChanged();
        this.notifyObservers();
    }

    public void checkSpecificCollisions() {
        // check all asteroids collision with all ships
        for (int i = 0; i < this.asteroids.size(); i++) {
            for (int j = 0; j < this.allSpaceships.size(); j++) {
                if (this.asteroids.get(i).collides(this.allSpaceships.get(j))) {
                    // destroy both objects
                    this.asteroids.get(i).destroy();
                    this.allSpaceships.get(j).destroy();
                }
            }
        }
        // check all bullets collision with all ships
        for (int i = 0; i < this.bullets.size(); i++) {
            for (int j = 0; j < this.allSpaceships.size(); j++) {
                if (this.bullets.get(i).collides(this.allSpaceships.get(j))) {
                    // destroy both objects
                    this.bullets.get(i).destroy();
                    this.allSpaceships.get(j).destroy();
                    // award point
                    this.pointsTo.add(this.bullets.get(i).getIdNumber());
                    this.increaseScore();
                }
            }
        }
    }

    /**
     * Method called when the game is over
     */
    public void endGameUpdates() {
        // if this player is the one that won, give him one extra point
        if (this.isWon()) {
            this.pointsTo.add(this.player.getIdNumber());
            this.increaseScore();
        } else {
            this.pointsTo.add(this.allPlayers.get(0).getIdNumber());
            this.increaseScore();
        }
        this.updateObservable();
        // game is over, kill the thread
        this.isRunning = false;

    }

    /**
     * Returns true if the multi-player game is over
     * false otherwise
     */
    public Boolean gameOver() {
        // only one player left and there were multiple clients
        if (this.allPlayers.size() <= 1 && this.shouldEnd) {
            return true;
        }
        return false;
    }

    /**
     * Find spaceship by id number
     */
    public Spaceship getSpaceShip(int id) {
        Spaceship found = null;
        for (Spaceship s : this.allSpaceships) {
            if (s.getIdNumber() == id) {
                found = s;
            }
        }
        return found;
    }

    /**
     * Find player by id number
     */
    public Player getPlayer(int id) {
        Player found = null;
        for (Player p : this.allPlayers) {
            if (p.getIdNumber() == id) {
                found = p;
            }
        }
        return found;
    }

    /**
     * Method to update the current local game based on received game model
     */
    public void updateGame(MultiPlayerGame game) {
        this.asteroids = game.getAsteroids();
        this.bullets = game.getBullets();
        this.allSpaceships = game.getSpaceships();
        this.allPlayers = game.getPlayers();
        this.pointsTo = game.pointsTo;
        this.shouldEnd = true;
    }

    /**
     * Remove player that has a destroyed ship
     */
    public void removePlayer(int id) {
        this.allPlayers.remove(getPlayer(id));
    }


    private Collection<Integer> getAll(ArrayList<Integer> integers) {
        Collection<Integer> c = new ArrayList<>();
        for (Integer id : integers) {
            // increase score by one
            getPlayer(id).increaseScore();
            if (this.isHost) {
                this.database.updateScore(getPlayer(id).getName(), getPlayer(id).getCurrentScore());
            }
            c.add(id);
        }
        return c;
    }

    /**
     * Increase one player's score by one point
     */
    public void increaseScore() {
        Collection<Integer> c = this.getAll(this.pointsTo);
        // remove all the entries
        this.pointsTo.removeAll(c);
    }

    public Database getDatabase() {
        return this.database;
    }

    public Boolean getIsConnected() {
        return this.isConnected;
    }

    public void disconnect() {
        this.isConnected = false;
    }

    public PlayerActions getController() {
        return this.controller;
    }

    public Boolean getIsHost() {
        return this.isHost;
    }

    public void setShouldEnd() {
        this.shouldEnd = true;
    }

}
