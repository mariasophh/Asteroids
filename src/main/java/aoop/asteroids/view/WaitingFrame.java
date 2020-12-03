package aoop.asteroids.view;

import aoop.asteroids.controller.net.Address;
import aoop.asteroids.controller.net.HostServer;
import aoop.asteroids.model.MultiPlayerGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.util.Observable;
import java.util.Observer;

public class WaitingFrame extends JFrame {

    public static final int WF_WIDTH = 650, WF_HEIGHT = 505;
    public static final int TEXT_HEIGHT = 25, AREA_HEIGHT = 50;
    private int port, minOpponents;
    private HostServer hostServer;
    private InetAddress ip;
    private Image background;
    private MultiPlayerGame game;

    public WaitingFrame(MultiPlayerGame game, HostServer hostServer, int minOpponents) {

        this.hostServer = hostServer;
        this.minOpponents = minOpponents;
        this.port = this.hostServer.getPortNumber();
        this.ip = this.hostServer.getIpAddress();
        this.game = game;

        this.setTitle("MultiPlayerGame in Waiting");
        this.setSize(WF_WIDTH, WF_HEIGHT);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        this.background = new ImageIcon("utils/anotherGif.gif").getImage();

        JPanel waitingPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };
        waitingPanel.setLayout(new BoxLayout(waitingPanel, BoxLayout.Y_AXIS));
        this.setContentPane(waitingPanel);

        waitingPanel.add(Box.createRigidArea(new Dimension(0, AREA_HEIGHT + 2)));
        JLabel text = new JLabel("Waiting...");
        text.setForeground(new Color(142, 142, 188));
        text.setPreferredSize(new Dimension(300, TEXT_HEIGHT));
        text.setFont(new Font(Font.MONOSPACED, Font.BOLD + Font.ITALIC, 18));
        waitingPanel.add(text);
        text.setAlignmentX(Component.CENTER_ALIGNMENT);

        waitingPanel.add(Box.createRigidArea(new Dimension(0, AREA_HEIGHT)));
        JLabel ip = new JLabel("Port: " + this.port);
        ip.setForeground(new Color(159, 189, 240));
        ip.setPreferredSize(new Dimension(300, TEXT_HEIGHT));
        ip.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        waitingPanel.add(ip);
        ip.setAlignmentX(Component.CENTER_ALIGNMENT);

        waitingPanel.add(Box.createRigidArea(new Dimension(0, AREA_HEIGHT)));
        JLabel port = new JLabel("Address: " + this.ip.toString());
        port.setForeground(new Color(159, 189, 240));
        port.setPreferredSize(new Dimension(300, TEXT_HEIGHT));
        port.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        waitingPanel.add(port);
        port.setAlignmentX(Component.CENTER_ALIGNMENT);

        waitingPanel.add(Box.createRigidArea(new Dimension(0, AREA_HEIGHT)));
        JLabel expected = new JLabel("Expected opponents: " + this.minOpponents);
        expected.setForeground(new Color(159, 189, 240));
        expected.setPreferredSize(new Dimension(300, TEXT_HEIGHT));
        expected.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        waitingPanel.add(expected);
        expected.setAlignmentX(Component.CENTER_ALIGNMENT);

        waitingPanel.add(Box.createRigidArea(new Dimension(0, AREA_HEIGHT)));
        JLabel connected = new JLabel();
        connected.setForeground(new Color(159, 189, 240));
        connected.setPreferredSize(new Dimension(300, TEXT_HEIGHT));
        connected.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        connected.setText("Connected opponents: " + WaitingFrame.this.hostServer.getNumberOfClients());
        waitingPanel.add(connected);
        connected.setAlignmentX(Component.CENTER_ALIGNMENT);

        game.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                connected.setText("Connected opponents: " + WaitingFrame.this.hostServer.getNumberOfClients());
            }
        });

        waitingPanel.add(Box.createRigidArea(new Dimension(0, AREA_HEIGHT)));
        JButton start = new JButton("Start");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (WaitingFrame.this.hostServer.getNumberOfClients() >= WaitingFrame.this.minOpponents) {
                    // start the host's game thread
                    new Thread(WaitingFrame.this.game).start();
                    WaitingFrame.this.game.setShouldEnd();
                    // send start request to all clients
                    if (hostServer.hasClients()) {
                        for (Address a : hostServer.getClientsAddress()) {
                            hostServer.send("start".getBytes(), a);
                        }
                    }
                    // initialise the game frame
                    new AsteroidsFrame(WaitingFrame.this.game, WaitingFrame.this.game.getController());
                    dispose();
                }
            }
        });
        start.setPreferredSize(new Dimension(150, 40));
        waitingPanel.add(start);
        start.setAlignmentX(Component.CENTER_ALIGNMENT);

        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                        null, "Go back to menu?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    new MainMenu();
                    dispose();
                }
            }
        };
        this.addWindowListener(exitListener);
        this.setVisible(true);

    }

}
