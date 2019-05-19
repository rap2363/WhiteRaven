package main.java.io;

import main.java.web.transport.ButtonMessage;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * A hard coded keyboard controller that listens for events and serializes them into messages over a socket connection.
 */
public class ClientKeyboardController implements Controller {
    private static final int[] buttonMappings = {
            KeyEvent.VK_A,      // A
            KeyEvent.VK_Z,      // B
            KeyEvent.VK_SHIFT,  // SELECT
            KeyEvent.VK_ENTER,  // START
            KeyEvent.VK_UP,     // UP
            KeyEvent.VK_DOWN,   // DOWN
            KeyEvent.VK_LEFT,   // LEFT
            KeyEvent.VK_RIGHT   // RIGHT
    };
    private final DataOutputStream socketWriter;

    public ClientKeyboardController(final Socket socket) throws IOException {
        this.socketWriter = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Initialize a listener for keyboard events and serialize them into messages to send over the socket.
     */
    @Override
    public void initializeListener() {
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
}

