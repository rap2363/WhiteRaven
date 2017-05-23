package main.java.web;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Encapsulates a player's thread of the WhiteRavenServer over the network. This thread runs on the server and is
 * responsible for sending updates to the client (i.e. screens) and listening for client input.
 */
public class WhiteRavenPlayer extends Thread {
    private static int clientCount = 0;

    // If we catch NUM_SOCKET_EXCEPTIONS socket exceptions in a row during image serialization, we'll mark this player
    // as dead and it will be ejected from the client map.
    private final static int NUM_SOCKET_EXCEPTIONS = 3;
    private final int clientId;
    private final ClientType clientType;
    private OutputStream outputStream;
    private int numRetries;
    private boolean alive;

    private WhiteRavenPlayer(
            final Socket socket,
            final ClientType clientType
    ) {
        this.clientId = clientCount++;
        this.clientType = clientType;
        this.numRetries = NUM_SOCKET_EXCEPTIONS;
        this.alive = true;
        try {
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println(String.format("Can't connect to player: %s: ", this.toString()) + e.getMessage());
        }
    }

    /**
     * Sends an image to the player depending on whether or not
     */
    public void sendImage(final byte[] imageData) {
        try {
            this.getOutputStream().write(imageData);
            this.numRetries = 0;
        } catch (SocketException e) {
            if (++numRetries >= NUM_SOCKET_EXCEPTIONS) {
                this.alive = false;
            }
        } catch (IOException e) {
            System.out.println("Error serializing image to socket for client: " + this.clientId);
        }
    }

    public boolean alive() {
        return alive;
    }

    private OutputStream getOutputStream() {
        return outputStream;
    }

    public String toString() {
        return clientId + ": " + clientType.toString();
    }

    @Override
    public void run() {
        System.out.println(String.format("Client %d has connected as a %s", this.clientId, this.clientType.toString()));
    }

    public static class Builder {
        private Socket socket;
        private ClientType clientType;

        public Builder setSocket(final Socket socket) {
            this.socket = socket;
            return this;
        }

        public Builder setClientType(final ClientType clientType) {
            this.clientType = clientType;
            return this;
        }

        public WhiteRavenPlayer build() {
            return new WhiteRavenPlayer(socket, clientType);
        }
    }
}
