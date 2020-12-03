package aoop.asteroids.controller.net;

import java.io.Serializable;
import java.net.InetAddress;

public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * An address is represented by a port number and an ip address
     */
    private int portNumber;
    private InetAddress ipAddress;

    /**
     * Constructor : assign fields
     */
    public Address(int portNumber, InetAddress ipAddress) {
        this.portNumber = portNumber;
        this.ipAddress = ipAddress;
    }

    /**
     * Getters
     */
    public int getPortNumber() {
        return this.portNumber;
    }

    public InetAddress getIpAddress() {
        return this.ipAddress;
    }

}
