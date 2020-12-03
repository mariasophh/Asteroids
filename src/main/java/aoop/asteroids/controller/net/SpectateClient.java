package aoop.asteroids.controller.net;

import aoop.asteroids.model.MultiPlayerGame;

import java.net.DatagramPacket;

/**
 * This class deals with a spectator's networking part
 * It will first send a "spectate" request in order for its address to be added to the client's list in the host server
 * It will also send a disconnect message when the spectator quits the game
 * It will receive an updated game model and update it locally
 */
public class SpectateClient extends Network {

    // the address of the host server : always send information to this one
    protected Address hostAddress;
    // the game that is currently being spectated
    protected MultiPlayerGame game;

    /**
     * Constructor
     */
    public SpectateClient(MultiPlayerGame game, Address hostAddress) {
        super(hostAddress);
        this.game = game;
        this.hostAddress = hostAddress;
    }

    /**
     * Thread loop : sends and receives packages/ messages
     */
    public void run() {

        // sends spectate request to the server
        super.send("spectate".getBytes());
        byte[] receivedData;

        while (this.isRunning) {

            byte[] incomingGameData = new byte[6000];
            DatagramPacket incomingGamePacket = new DatagramPacket(incomingGameData, incomingGameData.length);

            this.receive(incomingGamePacket);
            receivedData = incomingGamePacket.getData();
            String receivedMessage = new String(receivedData);

            //updates the local game according to the received model
            if (receivedMessage.contains("aoop.asteroids.model.MultiPlayerGame")) {
                super.updateGame(receivedData, this.game);
            }

            // disconnect, stop the thread and close the socket
            if (!this.game.getIsConnected()) {
                super.send("disconnect".getBytes());
                this.isRunning = false;
                this.socket.close();
            }
        }
    }
}
