package aoop.asteroids.model;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.swing.*;
import java.util.List;

/**
 * This is the Database class, which stores the players' highscores by using the ObjectDB.
 * This class can add players to the database, remove them, update their scores or return a player's
 * highscore.
 */

public class Database {

    private EntityManager em;

    private EntityManagerFactory emf;

    public Database() {
        this.emf = Persistence.createEntityManagerFactory("./Database.odb");
        this.em = this.emf.createEntityManager();
    }

    /**
     * Get all players in the database.
     */
    public List<Player> getPlayers() {
        TypedQuery<Player> query = em.createQuery("SELECT p FROM Player p", Player.class);
        return query.getResultList();
    }

    /**
     * Add player to database. Avoid duplicates by searching the database for the player's name.
     */
    public void addPlayer(Player player) {
        if (this.em.find(Player.class, player.getName()) == null) {
            this.em.getTransaction().begin();
            this.em.persist(player);
            this.em.getTransaction().commit();
        }
    }

    /**
     * Find a player and its score in the database.
     */
    public void findScore(String name) {
        Player player = this.em.find(Player.class, name);
        if (player != null) {
            JOptionPane.showMessageDialog(null, "Player " + name + " has highscore: " + player.getCurrentScore());
        } else {
            JOptionPane.showMessageDialog(null, "Sorry! Player " + name + " is not in the database.");
        }
    }

    /**
     * Update database once a player obtaines a new highscore.
     */
    public void updateScore(String name, int score) {
        Player player = this.em.find(Player.class, name);
        if (player.getCurrentScore() <= score) {
            this.em.getTransaction().begin();
            player.setScore(score);
            this.em.getTransaction().commit();
        }
    }

    /**
     * Delete player from the database.
     */
    public void deletePlayer(String givenName) {
        List<Player> playerList = getPlayers();

        em.getTransaction().begin();
        for (Player p : playerList) {
            if (givenName.equals(p.getName())) {
                em.remove(p);
            }
        }
        em.getTransaction().commit();
    }

    /**
     * Close the connection to the database.
     */
    public void closeDatabaseConnection() {
        em.close();
        emf.close();
    }

}
