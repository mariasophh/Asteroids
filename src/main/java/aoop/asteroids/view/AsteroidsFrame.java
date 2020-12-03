package aoop.asteroids.view;


import aoop.asteroids.controller.PlayerActions;
import aoop.asteroids.model.MultiPlayerGame;
import aoop.asteroids.model.SinglePlayerGame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

public class AsteroidsFrame extends JFrame {

    public static final long serialVersionUID = 1L;

    public static final int FRAME_WIDTH = 800, FRAME_HEIGHT = 800;

    // the controller
    private PlayerActions controller;
    // the panel
    private AsteroidsPanel ap;

    /**
     * Constructor for the single-player mode
     */
    public AsteroidsFrame(SinglePlayerGame game, PlayerActions controller) {
        this.controller = controller;

        this.ap = new AsteroidsPanel(game);

        createFrame();

        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                        null, "Quit game?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    new MainMenu();
                    dispose();
                    game.abort();
                }
            }
        };
        this.addWindowListener(exitListener);
    }

    /**
     * Constructor for the multi-player mode
     */
    public AsteroidsFrame(MultiPlayerGame multiplayerGame, PlayerActions controller) {

        this.controller = controller;
        this.ap = new AsteroidsPanel(multiplayerGame);

        createFrame();

        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // do not let the host/ joiner exit the game, unless the game is over/ game has started
                if (!multiplayerGame.getIsHost() && !multiplayerGame.getIsWaiting()) {
                    closeFrame(multiplayerGame);
                } else if (multiplayerGame.getSpaceships().size() == 1) {
                    closeFrame(multiplayerGame);
                    if (multiplayerGame.getDatabase() != null) {
                        multiplayerGame.getDatabase().closeDatabaseConnection();
                    }
                }
            }
        };
        this.addWindowListener(exitListener);
    }

    private void createFrame() {

        this.setTitle("Asteroids");
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setResizable(false);

        if (this.controller != null) this.addKeyListener(this.controller);

        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        this.add(this.ap);

        this.setVisible(true);
    }

    public void closeFrame(MultiPlayerGame multiplayerGame) {
        int confirm = JOptionPane.showOptionDialog(
                null, "Quit game?",
                "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (confirm == 0) {
            multiplayerGame.disconnect();
            new MainMenu();
            dispose();
            multiplayerGame.abort();
        }
    }

}
