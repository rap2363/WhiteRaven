package web;

import io.NetworkJoypad;
import web.transport.ImageMessage;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Launches a WhiteRaven server application that can open connections to clients (players).
 */
public class WhiteRavenServer {
    private static final int SERVER_PORT = 8888;
    static final int FRAMES_PER_SECOND = 60;
    static final int FRAME_TIME = 17;
    private static nes.Console console;

    public static void main(String[] args) throws Exception {
        ServerSocket connectionListener = new ServerSocket(SERVER_PORT);
        System.out.println(String.format("Running WhiteRavenServer on port %s", SERVER_PORT));
        if (args.length < 1) {
            System.err.println("Provide a file path to a game!");
            System.exit(1);
        }

        final String pathToGame = args[0];
        console = new nes.Console.Builder()
                .setCartridgePath(pathToGame).build();
        final Timer timer = new Timer();
        final Map<ClientType, List<WhiteRavenPlayer>> players = new HashMap<>();
        // Initialize the map
        for (ClientType clientType : ClientType.values()) {
            players.put(clientType, new ArrayList<>());
        }

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < screen.WhiteRavenLauncher.CPU_CYCLES_PER_SECOND / FRAMES_PER_SECOND; i++) {
                    console.ppu.executeCycles(3);
                    console.cpu.executeCycle();
                    if (console.ppu.triggerVerticalBlank) {
                        console.cpu.triggerInterrupt(nes.Interrupt.NMI);
                        console.ppu.triggerVerticalBlank = false;
                        int[] image = console.ppu.getImage();
                        final ImageMessage imageMessage = new ImageMessage(image);
                        for (final List<WhiteRavenPlayer> playerList : players.values()) {
                            for (final Iterator<WhiteRavenPlayer> playerIter = playerList.iterator(); playerIter.hasNext();) {
                                final WhiteRavenPlayer player = playerIter.next();
                                if (player.alive()) {
                                    player.sendImage(imageMessage.serialize());
                                } else {
                                    playerIter.remove();
                                    System.out.println("Player has died: " + player.toString());
                                }
                            }
                        }
                    }
                }
            }
        }, 0, FRAME_TIME);

        // Open connections to new clients as they connect to the server. Close the socket once we've exited.
        try {
            while (true) {
                // Blocks until we have a new connection
                final Socket clientSocket = connectionListener.accept();
                handleNewClient(clientSocket, players);
            }
        } finally {
            connectionListener.close();
        }
    }

    /**
     * Handles a new connection by assigning this player a role and adding it to the map of current players.
     *
     * @param clientSocket
     * @param playerMap
     */
    private static void handleNewClient(
            final Socket clientSocket,
            final Map<ClientType, List<WhiteRavenPlayer>> playerMap) {
        final ClientType clientType;

        if (playerMap.get(ClientType.FIRST_PLAYER).size() == 0) {
            clientType = ClientType.FIRST_PLAYER;
            console.setJoypadOne(new NetworkJoypad(clientSocket));
        } else if (playerMap.get(ClientType.SECOND_PLAYER).size() == 0) {
            clientType = ClientType.SECOND_PLAYER;
            console.setJoypadTwo(new NetworkJoypad(clientSocket));
        } else {
            clientType = ClientType.VIEWER;
        }

        final WhiteRavenPlayer player = new WhiteRavenPlayer.Builder()
                                            .setClientType(clientType)
                                            .setSocket(clientSocket)
                                            .build();

        playerMap.get(clientType).add(player);

        // Run the player's thread
        player.start();
    }
}
