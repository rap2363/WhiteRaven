package web;

import nes.Joypad;
import nes.NetworkJoypad;
import nes.NoopController;
import screen.MainScreen;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Launches a WhiteRaven server application that can open connections to clients (players).
 */
public class WhiteRavenServer {
    private static final int SERVER_PORT = 8888;
    private static final int FRAMES_PER_SECOND = 30;
    private static final int FRAME_TIME = 34; // 30 FPS
    private static nes.Console console;

    public static void main(String[] args) throws Exception {
        ServerSocket connectionListener = new ServerSocket(SERVER_PORT);
        System.out.println(String.format("Running WhiteRavenServer on port %s", SERVER_PORT));
        if (args.length < 1) {
            System.err.println("Provide a file path to a game!");
            System.exit(1);
        }

        final String pathToGame = args[0];
        console = new nes.Console.Builder().setCartridgePath(pathToGame).build();
        final MainScreen mainScreen = new MainScreen();
        final Timer timer = new Timer();
        final Map<ClientType, List<WhiteRavenPlayer>> players = new HashMap<>();

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
                        mainScreen.push(image);
                        mainScreen.redraw();
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
        final Joypad joypad;
        if (!playerMap.containsKey(ClientType.FIRST_PLAYER)) {
            clientType = ClientType.FIRST_PLAYER;
            joypad = new NetworkJoypad(clientSocket);
        } else if (!playerMap.containsKey(ClientType.SECOND_PLAYER)) {
            clientType = ClientType.SECOND_PLAYER;
            joypad = new NoopController();
        } else {
            clientType = ClientType.VIEWER;
            joypad = new NoopController();
        }
        console.setJoypadOne(joypad);

        final WhiteRavenPlayer player = new WhiteRavenPlayer.Builder()
                                            .setClientType(clientType)
                                            .setSocket(clientSocket)
                                            .setJoypad(joypad)
                                            .build();

        if (!playerMap.containsKey(clientType)) {
            playerMap.put(clientType, new ArrayList<>());
        }
        playerMap.get(clientType).add(player);

        // Run the player's thread
        player.start();
    }
}
