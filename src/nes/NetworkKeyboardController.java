package nes;

import web.ButtonMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A controller that listens for key presses over a network. Same hard coded buttons as the KeyboardController.
 */
public class NetworkKeyboardController extends KeyboardController implements Joypad {
    private final ScheduledExecutorService listener = Executors.newSingleThreadScheduledExecutor();
    private SocketHandler socketHandler;

    public NetworkKeyboardController(final Socket listenSocket) {
        reset();

        try {
            this.socketHandler = new SocketHandler(listenSocket);
            listener.execute(socketHandler);
        } catch (IOException e) {
            System.out.println("IOException during socket handler creation: " + e);
        } finally {
            listener.shutdown();
        }
    }

    /**
     * A Runnable that reads updates on the listen socket and deserializes bytes into button events.
     */
    protected class SocketHandler implements Runnable {
        private final InputStream inputStream;
        public SocketHandler(final Socket listenSocket) throws IOException {
            this.inputStream = listenSocket.getInputStream();
        }

        @Override
        public void run() {
            for(;;) {
                try {
                    byte[] readBuffer = new byte[1];
                    inputStream.read(readBuffer);
                    final ButtonMessage buttonMessage = ButtonMessage.deserializeFrom(readBuffer);
                    NetworkKeyboardController.this.handleButtonMessage(buttonMessage);
                } catch (IOException e) {
                    // This will get hit if we are receiving no key events (which is fine).
                }
            }
        }
    }

    /**
     * Handle the button message by writing to the buttonsPressed array. The ButtonsMessage ordering is mapped exactly
     * to the buttonsPressed array ordering. Since we have the ordinals as:
     * A_PRESSED,       --> 0
     * A_RELEASED,      --> 1
     * B_PRESSED,       --> 2
     * B_RELEASED,      --> 3
     * ...
     * RIGHT_PRESSED,   --> 14
     * RIGHT_RELEASED,  --> 15
     *
     * Dividing the ordinal by 2 (integer division) will give us the correct index of the buttonsPressed array, and
     * the ordinal being even or odd indicates whether the button was pressed or released.
     *
     * @param buttonMessage
     */
    private synchronized void handleButtonMessage(ButtonMessage buttonMessage) {
        final int ordinal = buttonMessage.ordinal();
        this.buttonsPressed[ordinal / 2] = (ordinal % 2 == 0);
    }
}
