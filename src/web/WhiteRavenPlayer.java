package web;

import nes.Joypad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Encapsulates a player's thread of the WhiteRavenServer over the network. This thread runs on the server and is
 * responsible for sending updates to the client (i.e. screens) and listening for client input.
 */
public class WhiteRavenPlayer extends Thread {
    private static int clientCount = 0;

    private final int clientId;
    private final Socket socket;
    private final ClientType clientType;
    private final Joypad joypad;
    private BufferedReader inputReader;
    private PrintWriter outputWriter;

    private WhiteRavenPlayer(
            final Socket socket,
            final ClientType clientType,
            final Joypad joypad
    ) {
        this.clientId = clientCount++;
        this.socket = socket;
        this.joypad = joypad;
        this.clientType = clientType;
        try {
            inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputWriter = new PrintWriter(socket.getOutputStream(), true);
            outputWriter.println(
                    String.format("Welcome to the WhiteRavenServer!\n" +
                                  "You have connected as a %s ", this.clientType.toString()));
        } catch (IOException e) {
            System.out.println(String.format("%s died: ", this.toString()) + e.getMessage());
        }
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
        private Joypad joypad;

        public Builder setSocket(final Socket socket) {
            this.socket = socket;
            return this;
        }

        public Builder setClientType(final ClientType clientType) {
            this.clientType = clientType;
            return this;
        }

        public Builder setJoypad(final Joypad joypad) {
            this.joypad = joypad;
            return this;
        }

        public WhiteRavenPlayer build() {
            return new WhiteRavenPlayer(socket, clientType, joypad);
        }
    }
}
