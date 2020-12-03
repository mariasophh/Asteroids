package aoop.asteroids.view;

import aoop.asteroids.Asteroids;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainMenu extends JFrame {

    public static final int WIDTH = 550, HEIGHT = 390;
    private Image background;
    private JPanel menuPanel;
    private JLabel text;
    private ArrayList<JButton> buttonList;
    private JButton single, host, join, spectate, database, quit;


    public MainMenu() {

        this.setTitle("Asteroids");
        this.setSize(WIDTH, HEIGHT);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.background = new ImageIcon("utils/anotherGif.gif").getImage();

        this.menuPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        this.setContentPane(menuPanel);

        this.buttonList = new ArrayList<JButton>();

        this.single = new JButton("SinglePlayer Game");
        this.single.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainMenu.this.dispose();
                Asteroids.startSinglePlayerGame();
            }
        });
        this.buttonList.add(this.single);

        this.host = new JButton("Host MultiPlayer Game");
        this.host.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainMenu.this.dispose();
                try {
                    Asteroids.hostMultiPlayerGame();
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        });
        this.buttonList.add(this.host);

        this.join = new JButton("Join MultiPlayer Game");
        this.join.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainMenu.this.dispose();
                try {
                    Asteroids.joinMultiPlayerGame();
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        });
        this.buttonList.add(this.join);

        this.spectate = new JButton("Watch MultiPlayer Game");
        this.spectate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainMenu.this.dispose();
                try {
                    Asteroids.spectateMultiPlayerGame();
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        });
        this.buttonList.add(this.spectate);

        this.database = new JButton("See Database");
        this.database.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DatabaseFrame();
                MainMenu.this.dispose();
            }
        });
        this.buttonList.add(this.database);

        this.quit = new JButton("Quit");
        this.quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        this.buttonList.add(this.quit);

        createMenu();

        this.setVisible(true);
    }

    public void createMenu() {
        this.text = new JLabel("Welcome to Asteroids!");
        this.text.setFont(new Font("", Font.BOLD, 20));
        this.text.setForeground(new Color(129, 169, 186));
        this.text.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        this.menuPanel.add(text);
        this.menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        for (JButton b : this.buttonList) {
            b.setPreferredSize(new Dimension(150, 40));
            b.setForeground(new Color(41, 85, 141));
            this.menuPanel.add(b);
            this.menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
    }
}
