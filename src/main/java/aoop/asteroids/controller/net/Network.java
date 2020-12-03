package aoop.asteroids.controller.net;

import aoop.asteroids.model.MultiPlayerGame;

import java.io.*;
import java.net.*;

/**
 * This is the abstract superclass for all servers, it contains the methods to serialize/ deserialize an object
 * Also, the methods for sending, receiving and update the game model
 */
public abstract class Network implements Runnable {

    protected InetAddress ip;
    protected DatagramSocket socket;
    protected int port;
    protected Boolean isRunning;

    /**
     * Host server constructor
     */
    public Network() {
        try {
            // bind the host to the local host ip and 8080 port number
            Address addr = new Address(8080, InetAddress.getLocalHost());
            this.port = addr.getPortNumber();
            this.ip = addr.getIpAddress();
            this.socket = new DatagramSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isRunning = true;
    }

    /**
     * Join server/ Spectate server constructor
     */
    public Network(Address address) {
        this.port = address.getPortNumber();
        try {
            this.ip = address.getIpAddress();
            this.socket = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        isRunning = true;
    }

    /**
     * Thread loop : implemented in subclasses
     */
    public abstract void run();

    /**
     * Method to send a DatagramPacket via a DatagramSocket
     */
    public void send(byte[] data) {
        try {
            socket.send(new DatagramPacket(data, data.length, ip, port));
        } catch (IOException e) {
            System.err.println("Outgoing packet was not sent.");
            e.printStackTrace();
        }
    }

    /**
     * Method to receive  a DatagramPacket via a DatagramSocket
     */
    public byte[] receive(DatagramPacket packet) {
        try {
            this.socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packet.getData();
    }

    /**
     * Method to deserialize a received packet : passed as an array of bytes
     * returns the created object
     */
    public static Object deserialize(byte[] bytes) {
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        Object object = null;

        try {
            ObjectInputStream ois = new ObjectInputStream(input);
            object = ois.readObject();
        } catch (Exception e) {
            System.err.println("Could not deserialize.");
            e.printStackTrace();
        }

        return object;
    }

    /**
     * Method to serialize an object : returns an array of bytes
     */
    public static byte[] serialize(Object object) {

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(output);
            oos.writeObject(object);
        } catch (IOException e) {
            System.err.println("Could not serialize.");
            e.printStackTrace();
        }

        return output.toByteArray();
    }

    /**
     * Method that casts the received byte string into a MultiPlayerGame object
     * and updates the local game
     */
    protected void updateGame(byte[] receivedData, MultiPlayerGame game) {
        MultiPlayerGame receivedGame = (MultiPlayerGame) deserialize(receivedData);
        game.updateGame(receivedGame);
    }

}
