package web;

import io.ClientKeyboardController;
import io.Controller;
import screen.MainScreen;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is run on clients connecting to a WhiteRavenServer instance over the network.
 */
public class WhiteRavenClient {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Provide a server IP address and port to connect to!");
            System.exit(1);
        }
        final String serverIPAddress = args[0];
        final int serverPort = Integer.parseInt(args[1]);
        final Socket socket = new Socket(serverIPAddress, serverPort);
        System.out.println(String.format("Connected to a WhiteRaven server at: %s:%s", serverIPAddress, serverPort));
        final MainScreen screen = new MainScreen();
        final Controller controller = new ClientKeyboardController(socket);
        ExecutorService clientThread = Executors.newSingleThreadExecutor();
        clientThread.execute(new Runnable() {
            @Override
            public void run() {
                controller.initializeListener();
            }
        });
    }
}
