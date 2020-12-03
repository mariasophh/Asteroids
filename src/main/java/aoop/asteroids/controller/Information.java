package aoop.asteroids.controller;

import javax.swing.*;
import java.awt.*;

/**
 * This class displays a JOptionPane with multiple components
 * it is used to get input information from the user (i.e. nickname, color, host address/port number etc)
 */
public class Information extends JFrame {

    private JTextField name;
    private JColorChooser color;
    private JTextField port;
    private JTextField ipAddress;
    private JTextField numberOpponents;

    /**
     * 1 for single player, 2 for host game, 3 for join, 4 for spectate
     */
    public Information(int gameState) {
        switch (gameState) {
            case 1:
                this.singlePlayerOptions("Asteroids");
                break;
            case 2:
                this.hostOptions("Asteroids");
                break;
            case 3:
                this.joinOptions("Asteroids");
                break;
            case 4:
                this.spectateOptions("Asteroids");
                break;
        }
    }


    private void singlePlayerOptions(String message) {
        this.name = new JTextField();
        this.color = new JColorChooser();
        Object[] components = {"Your name:", this.name, "Choose a spaceship color that suits you :", this.color};
        int confirm = JOptionPane.showConfirmDialog(null, components, message, JOptionPane.OK_CANCEL_OPTION);
        if (this.closeOperation(confirm)) {
            this.name.setText("");
            return;
        }
        if (this.name.getText().isEmpty()) {
            this.singlePlayerOptions("Asteroids : please enter you details again");
        }
    }

    private void hostOptions(String message) {
        this.name = new JTextField();
        this.numberOpponents = new JTextField();
        this.color = new JColorChooser();
        Object[] components = {"Your name:", this.name, "Minimum number of opponents:", this.numberOpponents, "Choose a spaceship color that suits you :", this.color};
        int confirm = JOptionPane.showConfirmDialog(null, components, message, JOptionPane.OK_CANCEL_OPTION);
        if (this.closeOperation(confirm)) {
            this.name.setText("");
            return;
        }
        if ((this.name.getText() != null && this.name.getText().isEmpty()) || (this.numberOpponents.getText() != null && (this.numberOpponents.getText().isEmpty() || (!this.numberOpponents.getText().isEmpty() && Integer.parseInt(this.numberOpponents.getText()) < 1)))) {
            this.hostOptions("Asteroids : please enter your details again");
        }
    }

    private void joinOptions(String message) {
        this.name = new JTextField();
        this.ipAddress = new JTextField();
        this.port = new JTextField();
        this.color = new JColorChooser();
        Object[] components = {"Your name:", this.name, "Host ip address:", this.ipAddress, "Host port number:", this.port, "Choose a spaceship color that suits you :", this.color};
        int confirm = JOptionPane.showConfirmDialog(null, components, message, JOptionPane.OK_CANCEL_OPTION);
        if (this.closeOperation(confirm)) {
            this.name.setText("");
            return;
        }
        if ((this.name.getText() != null && this.name.getText().isEmpty()) || (this.port.getText() != null && this.port.getText().isEmpty()) || (this.ipAddress.getText() != null && this.ipAddress.getText().isEmpty())) {
            this.joinOptions("Asteroids : please enter your details again");
        }
    }

    private void spectateOptions(String message) {
        this.ipAddress = new JTextField();
        this.port = new JTextField();
        Object[] components = {"Host ip address:", this.ipAddress, "Host port number:", this.port};
        int confirm = JOptionPane.showConfirmDialog(null, components, message, JOptionPane.OK_CANCEL_OPTION);
        if (this.closeOperation(confirm)) {
            this.ipAddress.setText("");
            return;
        }
        if ((this.port.getText() != null && this.port.getText().isEmpty()) || (this.ipAddress.getText() != null && this.ipAddress.getText().isEmpty())) {
            this.spectateOptions("Asteroids : please enter your details again");
        }
    }

    private Boolean closeOperation(int confirm) {
        return (confirm == 2 || confirm == -1);
    }

    public String getPlayerName() {
        if (this.name.getText().isEmpty()) {
            return null;
        }
        return this.name.getText();
    }

    public Color getPlayerColor() {
        return this.color.getColor();
    }

    public int getPlayerPort() {
        if (this.port.getText().isEmpty()) {
            return -1;
        }
        return Integer.parseInt(this.port.getText());
    }

    public String getIpAddress() {
        if (this.ipAddress.getText().isEmpty()) {
            return null;
        }
        return this.ipAddress.getText();
    }

    public int getNumberOfOpponents() {
        if (this.numberOpponents.getText().isEmpty()) {
            return -1;
        }
        return Integer.parseInt(this.numberOpponents.getText());
    }

}
