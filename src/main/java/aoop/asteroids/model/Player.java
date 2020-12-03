package aoop.asteroids.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.awt.*;
import java.io.Serializable;

@Entity
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    private int currentScore;

    @Transient
    private Color color;

    @Id
    private String name;

    private int idNumber;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.currentScore = 0;
    }


    public int getCurrentScore() {
        return this.currentScore;
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
     * increase player's score by one
     */
    public void increaseScore() {
        this.currentScore++;
    }

    public void setIdNumber(int idNumber) {
        this.idNumber = idNumber;
    }

    public void setScore(int score) {
        this.currentScore = score;
    }

    public void resetScore() {
        this.setScore(0);
    }

    public Player clone() {
        Player p = new Player(this.name, this.color);
        p.setIdNumber(this.idNumber);
        p.setScore(this.currentScore);
        return p;
    }

}
