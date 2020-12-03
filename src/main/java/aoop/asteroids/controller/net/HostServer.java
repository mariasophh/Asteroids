package aoop.asteroids.controller.net;

import aoop.asteroids.model.MultiPlayerGame;
import aoop.asteroids.model.Player;
import aoop.asteroids.model.Spaceship;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * This class implements the singleton pattern, it is only instantiated once
 * because it binds its corresponding DatagramSocket to the local ip address and a specific port number
 * It will receive information from its clients and update the local game accordingly
 * Whenever the local game is updated, it will be sent to all clients in order for them to update their local games
 */
public class HostServer extends Network implements Observer {

    // create an unique object of HostServer
    private static HostServer uniqueInstance = new HostServer();

    private MultiPlayerGame gameModel;
    private ArrayList<Address> clientsAddress;
    private DatagramPacket incomingPacket;
    private byte[] receivedData;
    private int numberOfSpectators;

    /**
     * Private constructor : this class cannot be instantiated
     */
    private HostServer() {
        super();
        this.clientsAddress = new ArrayList<>();
        numberOfSpectators = 0;
    }

    /**
     * Thread loop : tries to receive data from clients and choose further action
     */
    public void run() {

        String receivedMessage;

        while (this.isRunning) {

            this.receivedData = new byte[4096];
            this.incomingPacket = new DatagramPacket(receivedData, receivedData.length);

            // get message, choose further action
            receivedMessage = new String(this.receive(this.incomingPacket));

            if (receivedMessage.trim().toLowerCase().contains("stop")) {
                this.disconnectJoiner();
            } else if (receivedMessage.trim().toLowerCase().equals("spectate")) {
                this.spectate();
            } else if (receivedMessage.startsWith("actions")) {
                this.updateActions();
            } else if (receivedMessage.contains("aoop.asteroids.model.Player")) {
                this.updatePlayer();
            } else if (receivedMessage.trim().toLowerCase().equals("disconnect")) {
                this.disconnect();
            }

        }
    }

    /**
     * Received spectate message, add a new client
     */
    private void spectate() {
        this.clientsAddress.add(new Address(this.incomingPacket.getPort(), this.incomingPacket.getAddress()));
        this.numberOfSpectators++;
    }

    /**
     * Received a new player, update the model by adding the player
     * the model will be updated, so it will be sent to all clients (see constructor)
     */
    private void updatePlayer() {
        // this can be interpreted as a "join" request : add the client to the list
        this.clientsAddress.add(new Address(this.incomingPacket.getPort(), this.incomingPacket.getAddress()));
        // retrieve player
        Player player = (Player) deserialize(this.receivedData);
        // add the player to the model : will also add his ship
        this.gameModel.addPlayer(player);
    }

    /**
     * Received an update for the actions of one ship
     * retrieve the ship and link it to a corresponding one in the game model and update its actions
     */
    private void updateActions() {
        String[] parts = splitMessage();
        // retrieve the id number of the spaceship
        int idNumber = Integer.parseInt(parts[1]);
        Spaceship s = this.gameModel.getSpaceShip(idNumber);
        // update spaceship's actions
        s.setActions(Boolean.valueOf(parts[2]), Boolean.valueOf(parts[3]), Boolean.valueOf(parts[4]), Boolean.valueOf(parts[5]));
    }

    /**
     * Received disconnect message, remove the disconnected address from the clients list
     */
    private void disconnect() {
        ArrayList<Address> clientsRest = new ArrayList<>();
        for (Address a : this.clientsAddress) {
            if (a.getPortNumber() != incomingPacket.getPort() || !a.getIpAddress().toString().equals(this.incomingPacket.getAddress().toString())) {
                clientsRest.add(a);
            }
        }
        this.clientsAddress = clientsRest;
    }

    /**
     * Remove the disconnected address from the clients list
     * Also remove the player from the current game
     */
    private void disconnectJoiner() {
        this.disconnect();
        String[] parts = splitMessage();
        this.gameModel.getSpaceShip(Integer.parseInt(parts[1])).destroy();
    }

    /**
     * This method overloads the super class method for sending
     */
    public void send(byte[] data, Address address) {
        try {
            socket.send(new DatagramPacket(data, data.length, address.getIpAddress(), address.getPortNumber()));
        } catch (IOException e) {
            System.err.println("Outgoing packet was not sent.");
            e.printStackTrace();
        }
    }

    /**
     * Whenever the model changes, update all clients by sending the updated model
     */
    public void update(Observable o, Object arg) {
        try {
            if (this.hasClients()) {
                for (Address address : this.clientsAddress) {
                    byte[] outgoingData = serialize(this.gameModel);
                    this.send(outgoingData, address);
                }
            }
        } catch (Exception e) {
            System.err.println("Server couldn't send updated data.");
            e.printStackTrace();
        }
    }

    /**
     * Method that returns a String array
     */
    public String[] splitMessage() {
        String message = new String(this.receivedData);
        return message.split("-");
    }


    /**
     * Method to get the only object available
     */
    public static HostServer getHostServerInstance() {
        return uniqueInstance;
    }

    /**
     * Add the game model, add this as its observer
     */
    public void initialiseComponents(MultiPlayerGame game) {
        this.gameModel = game;
        this.gameModel.addObserver(this);
    }

    /**
     * Clear the clients list
     */
    public void clearClients() {
        Collection<Address> c = new ArrayList<>();
        for (Address a : this.clientsAddress) {
            c.add(a);
        }
        this.clientsAddress.removeAll(c);
    }

    /**
     * Checks if there are clients connected to this server
     */
    public Boolean hasClients() {
        return (this.clientsAddress.size() > 0);
    }

    /**
     * Getters
     */

    public ArrayList<Address> getClientsAddress() {
        return this.clientsAddress;
    }

    public int getNumberOfClients() {
        return this.clientsAddress.size() - this.numberOfSpectators;
    }

    public InetAddress getIpAddress() {
        return this.ip;
    }

    public int getPortNumber() {
        return this.port;
    }

}
