package aoop.asteroids.view;

import aoop.asteroids.model.Database;
import aoop.asteroids.model.Player;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;

public class DatabaseFrame extends JFrame implements KeyListener {

    public static final int DF_WIDTH = 550, DF_HEIGHT = 500;
    private Image background;
    private Database database;

    public DatabaseFrame() {

        this.database = new Database();

        this.setTitle("Database");
        this.setSize(DF_WIDTH, DF_HEIGHT);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addKeyListener(this);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);


        this.background = new ImageIcon("utils/anotherGif.gif").getImage();

        JPanel panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null);
        this.setContentPane(panel);

        JLabel label = new JLabel("Database Entries");
        label.setBounds(185, 35, 200, 20);
        label.setForeground(Color.WHITE);
        label.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
        panel.add(label);

        // The text obtained from the database
        String text = "";
        for (Player p : database.getPlayers()) {
            text = text.concat(p.getName() + " - " + p.getCurrentScore() + "\n");
        }

        JTextPane textPane = new JTextPane();
        textPane.setText(text);
        textPane.setBounds(125, 80, 300, 300);
        textPane.setForeground(new Color(73, 93, 134));
        textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        textPane.setEditable(false);

        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        panel.add(textPane);

        JLabel delete = new JLabel("* Press D to delete an entry *");
        delete.setBounds(185, 400, 300, 20);
        delete.setForeground(Color.WHITE);
        delete.setFont(new Font(Font.MONOSPACED, Font.ITALIC, 12));
        panel.add(delete);

        JLabel find = new JLabel("* Press F to find an entry *");
        find.setBounds(185, 430, 300, 20);
        find.setForeground(Color.WHITE);
        find.setFont(new Font(Font.MONOSPACED, Font.ITALIC, 12));
        panel.add(find);

        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseFrame.this.dispose();
                new MainMenu();
            }
        };
        this.addWindowListener(exitListener);

        this.setVisible(true);
    }

    /**
     * If key D is pressed, the user has the possibility of removing entries from the database
     */
    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_D) {
            JFrame frame = new JFrame();
            String name = JOptionPane.showInputDialog(frame, "Name of player to delete:");
            this.database.deletePlayer(name);

            JOptionPane.showMessageDialog(frame, "Refresh");

            new MainMenu();
            dispose();
        }
        if (event.getKeyCode() == KeyEvent.VK_F) {
            String name = JOptionPane.showInputDialog("Name of player to find:");
            if (name != null) {
                this.database.findScore(name);
            }
        }
    }

    /**
     * This method doesn't do anything.
     */
    @Override
    public void keyReleased(KeyEvent event) {

    }

    /**
     * This method doesn't do anything.
     */
    @Override
    public void keyTyped(KeyEvent event) {

    }

}
