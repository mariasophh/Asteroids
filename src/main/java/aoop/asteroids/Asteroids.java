package aoop.asteroids;

import aoop.asteroids.controller.Information;
import aoop.asteroids.controller.PlayerActions;
import aoop.asteroids.controller.net.Address;
import aoop.asteroids.controller.net.HostServer;
import aoop.asteroids.controller.net.JoinerClient;
import aoop.asteroids.controller.net.SpectateClient;
import aoop.asteroids.model.MultiPlayerGame;
import aoop.asteroids.view.AsteroidsFrame;
import aoop.asteroids.view.MainMenu;
import aoop.asteroids.model.Player;
import aoop.asteroids.model.SinglePlayerGame;
import aoop.asteroids.view.WaitingFrame;

import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Asteroids {

    /**
     * Constructor : calls the menu which will start a new instance of the game
     */
    public Asteroids() {
        new MainMenu();
    }

    /**
     * main function
     */
    public static void main(String[] args) {
        new Asteroids();
    }

    public static void startSinglePlayerGame() {
        Information playerInfo = new Information(1);

        String name = playerInfo.getPlayerName();
        Color color = playerInfo.getPlayerColor();

        // No name or color takes the user back to the Main Menu
        if (name == null || color == null) {
            new MainMenu();
            return;
        }

        // initialise controller
        PlayerActions controller = new PlayerActions();

        // initialise player and single player game
        Player player = new Player(name, color);
        SinglePlayerGame game = new SinglePlayerGame(player, controller);
        game.linkController(controller);

        // start the game thread and initialise frame
        new Thread(game).start();
        new AsteroidsFrame(game, controller);
    }

    public static void hostMultiPlayerGame() throws UnknownHostException {

        Information playerInfo = new Information(2);

        String name = playerInfo.getPlayerName();
        Color color = playerInfo.getPlayerColor();
        int minOpponents = playerInfo.getNumberOfOpponents();

        // No name or color takes the user back to the Main Menu
        if (name == null || color == null || minOpponents == -1) {
            new MainMenu();
            return;
        }

        Player player = new Player(name, color);

        PlayerActions controller = new PlayerActions();
        MultiPlayerGame game = new MultiPlayerGame(player, controller, true);

        // initialise host server (singleton, get the only available object)
        HostServer hostServer = HostServer.getHostServerInstance();
        // add its model
        hostServer.initialiseComponents(game);
        hostServer.clearClients();
        System.out.println("Ip address:" + InetAddress.getLocalHost().toString());

        // start its thread
        new Thread(hostServer).start();

        // wait until all the opponents have connected
        new WaitingFrame(game, hostServer, minOpponents);

    }

    public static void joinMultiPlayerGame() throws UnknownHostException {

        Information playerInfo = new Information(3);

        String name = playerInfo.getPlayerName();
        Color color = playerInfo.getPlayerColor();
        String ipAddress = playerInfo.getIpAddress();
        int port = playerInfo.getPlayerPort();

        // No name, color, ipAddress or port number takes the user back to the Main Menu
        if (name == null || color == null || ipAddress == null || port == -1) {
            new MainMenu();
            return;
        }

        Player player = new Player(name, color);
        PlayerActions controller = new PlayerActions();
        // initialise game, the thread game will begin upon "start" request from host
        MultiPlayerGame game = new MultiPlayerGame(player, controller, false);

        // initialise joiner's client and start its thread
        JoinerClient js = new JoinerClient(game, player, new Address(port, InetAddress.getByName(ipAddress)));
        new Thread(js).start();
        new AsteroidsFrame(game, controller);
    }


    public static void spectateMultiPlayerGame() throws UnknownHostException {

        Information playerInfo = new Information(4);

        String ipAddress = playerInfo.getIpAddress();
        int port = playerInfo.getPlayerPort();

        // No ipAddress or no port will take the player back to the Main Menu
        if (ipAddress == null || port == -1) {
            new MainMenu();
            return;
        }

        MultiPlayerGame game = new MultiPlayerGame();
        SpectateClient sp = new SpectateClient(game, new Address(port, InetAddress.getByName(ipAddress)));
        new Thread(sp).start();
        new Thread(game).start();
        new AsteroidsFrame(game, null);

    }

}
