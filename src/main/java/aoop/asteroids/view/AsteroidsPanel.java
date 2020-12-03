package aoop.asteroids.view;

import aoop.asteroids.model.*;
import aoop.asteroids.model.Game;
import aoop.asteroids.model.MultiPlayerGame;
import aoop.asteroids.model.SinglePlayerGame;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.Object;
import java.util.Observable;
import java.util.Observer;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * AsteroidsPanel extends JPanel and thus provides the actual graphical
 * representation of the game model.
 */

public class AsteroidsPanel extends JPanel {

    public static final long serialVersionUID = 4L;

    private Game game;
    private Boolean isMultiPlayer;

    /**
     * Constructs the panel for a single player game
     */
    public AsteroidsPanel(SinglePlayerGame game) {
        this.game = game;
        this.isMultiPlayer = false;
        this.addObserver();
    }

    /**
     * Constructs the panel for a multi player game
     */
    public AsteroidsPanel(MultiPlayerGame game) {
        this.game = game;
        this.isMultiPlayer = true;
        this.addObserver();
    }

    private void addObserver() {
        this.game.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                AsteroidsPanel.this.repaint();
            }
        });
    }

    /**
     * Superclass method called whenever the GUI is refreshed
     */
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (!this.game.getIsSpectator() && this.isMultiPlayer && this.game.getSpaceships().size() <= 1 && !this.game.getIsWaiting()) {
            // game has ended
            this.paintEndGame(g);
            return;
        } else if (!this.game.getIsSpectator() && this.isMultiPlayer && this.game.getIsWaiting()) {
            // game is waiting to begin
            this.paintWaiting(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.setBackground(Color.black);

        this.paintSpaceships(g2);
        this.paintAsteroids(g2);
        this.paintBullets(g2);
        this.paintPlayers(g2);
    }

    /**
     * Draws all bullets as a yellow circle
     */
    private void paintBullets(Graphics2D g) {
        g.setColor(Color.yellow);
        for (Bullet b : this.game.getBullets())
            g.drawOval(b.getLocation().x - 2, b.getLocation().y - 2, 5, 5);
    }


    /**
     * Draws asteroids
     */
    private void paintAsteroids(Graphics2D g) {

        try {
            BufferedImage asteroidPaint = ImageIO.read(new File("utils/asteroidPic.jpg"));

            for (Asteroid a : this.game.getAsteroids()) {
                Ellipse2D.Double e = new Ellipse2D.Double();
                e.setFrame(a.getLocation().x - a.getRadius(), a.getLocation().y - a.getRadius(), 2 * a.getRadius(), 2 * a.getRadius());
                TexturePaint texturePaint = new TexturePaint(asteroidPaint, new Rectangle(a.getLocation().x - a.getRadius() - asteroidPaint.getWidth(), a.getLocation().y - a.getRadius() - asteroidPaint.getHeight(), asteroidPaint.getWidth(), asteroidPaint.getHeight()));
                g.setPaint(texturePaint);
                g.fill(e);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Paints all the spaceships as a triangle using the color chosen by each player
     * when the ship is accelerating it represents a flame at the bottom of the ship
     */
    private void paintSpaceships(Graphics2D g) {
        for (Spaceship s : this.game.getSpaceships()) {
            if (!s.isDestroyed()) {
                // Draw body of the spaceships
                Polygon p = new Polygon();
                p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection()) * 20), (int) (s.getLocation().y - Math.cos(s.getDirection()) * 20));
                p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection() + 0.8 * Math.PI) * 20), (int) (s.getLocation().y - Math.cos(s.getDirection() + 0.8 * Math.PI) * 20));
                p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection() + 1.2 * Math.PI) * 20), (int) (s.getLocation().y - Math.cos(s.getDirection() + 1.2 * Math.PI) * 20));

                g.setColor(s.getColor());
                g.fill(p);
                g.setColor(s.getColor().brighter().brighter());
                g.draw(p);

                g.drawString(s.getName(), s.getLocation().x - 12, s.getLocation().y - 25);

                // Spaceship accelerating .
                if (s.isAccelerating()) {
                    // Draw flame at the exhaust
                    p = new Polygon();
                    p.addPoint((int) (s.getLocation().x - Math.sin(s.getDirection()) * 25), (int) (s.getLocation().y + Math.cos(s.getDirection()) * 25));
                    p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection() + 0.9 * Math.PI) * 15), (int) (s.getLocation().y - Math.cos(s.getDirection() + 0.9 * Math.PI) * 15));
                    p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection() + 1.1 * Math.PI) * 15), (int) (s.getLocation().y - Math.cos(s.getDirection() + 1.1 * Math.PI) * 15));
                    g.setColor(Color.yellow);
                    g.fill(p);
                }
            }
        }
    }

    /**
     * Paints all the players and their corresponding scores
     */
    private void paintPlayers(Graphics2D g2) {
        int i = 0;
        for (Player p : game.getPlayers()) {
            g2.setColor(p.getColor());
            g2.drawString(p.getName(), 20 + i * 100, 20);
            g2.drawString(String.valueOf(p.getCurrentScore()), 20 + i * 100, 40);
            i++;
        }
    }

    private void paintText(String message, Graphics g) {
        AffineTransform af = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(af, true, true);
        Font font = new Font(Font.MONOSPACED, Font.BOLD, 20);
        int textWidth = (int) (font.getStringBounds(message, frc)).getWidth();
        int textHeight = (int) (font.getStringBounds(message, frc)).getHeight();
        g.setFont(font);
        g.setColor(new Color(181, 255, 227));
        g.drawString(message, AsteroidsFrame.FRAME_WIDTH / 2 - (textWidth / 2), AsteroidsFrame.FRAME_HEIGHT / 2 - (textHeight / 2));
    }

    private void paintEndGame(Graphics g) {
        this.setBackground(Color.black);
        String message;
        if (this.game.isWon()) {
            message = "You won! Your score was: " + String.valueOf(this.game.getPlayer(this.game.getLocalPlayer().getIdNumber()).getCurrentScore());
        } else {
            message = "So sad, you lost!";
        }
        this.paintText(message, g);
    }

    private void paintWaiting(Graphics g) {
        this.setBackground(Color.black);
        this.paintText("Please wait for all opponents to connect.", g);
    }

}
