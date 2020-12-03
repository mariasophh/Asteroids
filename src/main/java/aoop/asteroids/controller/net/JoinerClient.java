package aoop.asteroids.controller.net;


import aoop.asteroids.model.MultiPlayerGame;
import aoop.asteroids.model.Player;

import java.net.DatagramPacket;

/**
 * This class deals with a joiner's networking part
 * It will first send the local player for it to be added to the host's model
 * Whenever the local player's actions change they will be sent to the host server
 * When the player disconnects, a "disconnect" request will be sent to the host server
 * When it receives a "start" request, it will start the local's game thread
 * When it receives an updated model, it will update the local game model
 */
public class JoinerClient extends Network {

    // its player
    private Player player;
    private MultiPlayerGame game;
    private String spaceshipActions;

    /**
     * Constructor: initialize components
     */
    public JoinerClient(MultiPlayerGame game, Player player, Address hostAddress) {
        super(hostAddress);
        this.game = game;
        this.player = player;
        this.spaceshipActions = "";
    }

    /**
     * Thread loop : sends and receives packages/ messages
     */
    public void run() {
        //sends the player to the host server : interpreted as the "join request"
        super.send(serialize(this.player));

        while (this.isRunning) {

            byte[] incomingData = new byte[4096];
            DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);

            String receivedMessage = new String(this.receive(incomingPacket));

            // receives an updated game, calls super method to update the local game model
            if (receivedMessage.contains("aoop.asteroids.model.MultiPlayerGame")) {
                super.updateGame(incomingPacket.getData(), this.game);
            }

            // receives start request from server, starts the thread of the game
            if (receivedMessage.contains("start")) {
                new Thread(game).start();
            }

            // sends the performed actions for every other client to update them locally
            String newActions = this.game.getController().getActions();
            if (!this.spaceshipActions.equals(newActions)) {
                byte[] outgoingActions = (this.game.getController().getActions().getBytes());
                super.send(outgoingActions);
                // set this as the last actions
                this.spaceshipActions = newActions;
            }

            // disconnect, stop the thread and close the socket
            if (!this.game.getIsConnected()) {
                super.send(("stop" + "-" + this.game.getLocalPlayer().getIdNumber() + "-").getBytes());
                this.isRunning = false;
                this.socket.close();
            }
        }
    }
}