package web;

import screen.MainScreen;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is run on clients connecting to a WhiteRavenServer instance over the network.
 */
public class WhiteRavenClient {
    // Hardcoded button mappings (this should be abstracted to a client controller)
    protected static final int[] buttonMappings = {
            KeyEvent.VK_A,      // A
            KeyEvent.VK_Z,      // B
            KeyEvent.VK_SHIFT,  // SELECT
            KeyEvent.VK_ENTER,  // START
            KeyEvent.VK_UP,     // UP
            KeyEvent.VK_DOWN,   // DOWN
            KeyEvent.VK_LEFT,   // LEFT
            KeyEvent.VK_RIGHT   // RIGHT
    };

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Provide a server IP address and port to connect to!");
            System.exit(1);
        }
        final String serverIPAddress = args[0];
        final int serverPort = Integer.parseInt(args[1]);
        final Socket socket = new Socket(serverIPAddress, serverPort);
        System.out.println(String.format("Connected to a WhiteRaven server at: %s:%s", serverIPAddress, serverPort));
        final DataOutputStream socketWriter = new DataOutputStream(socket.getOutputStream());
        final MainScreen screen = new MainScreen();
        ExecutorService clientThread = Executors.newSingleThreadExecutor();
        clientThread.execute(new Runnable() {
            @Override
            public void run() {
                // Listen for keyboard events from the client
                KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
                    @Override
                    public boolean dispatchKeyEvent(KeyEvent ke) {
                        try {
                            for (int i = 0; i < buttonMappings.length; i++) {
                                if (buttonMappings[i] == ke.getKeyCode()) {
                                    if (ke.getID() == KeyEvent.KEY_PRESSED) {
                                        socketWriter.write(ButtonMessage.values()[i * 2].serialize());
                                        break;
                                    } else if (ke.getID() == KeyEvent.KEY_RELEASED) {
                                        socketWriter.write(ButtonMessage.values()[i * 2 + 1].serialize());
                                        break;
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });
            }
        });
    }
}
