package main.java.io;

import main.java.web.transport.ButtonMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A joypad that listens for button presses over a network connection. This runs on the server and its counterpart
 * (a ClientController) should run on the client.
 */
public class NetworkJoypad implements Joypad {
    private int currentButton;
    private boolean strobe;
    private final boolean[] buttonsPressed = new boolean[8];

    public NetworkJoypad(final Socket listenSocket) {
        reset();
        final ScheduledExecutorService listener = Executors.newSingleThreadScheduledExecutor();
        try {
            final SocketHandler socketHandler = new SocketHandler(listenSocket);
            listener.execute(socketHandler);
        } catch (IOException e) {
            System.out.println("IOException during socket handler creation: " + e);
        } finally {
            listener.shutdown();
        }
    }

    private void reset() {
        currentButton = 0;
        strobe = false;
    }

    @Override
    public byte read() {
        byte value = getButton();
        if (!strobe) {
            currentButton = (currentButton + 1) % 8;
        }
        return value;
    }

    /**
     * Synchronized to keep the buttonsPressed locked
     *
     * @return
     */
    protected synchronized byte getButton() {
        return (byte) (buttonsPressed[currentButton] ? 0x41 : 0x40);
    }

    @Override
    public void write(byte value) {
        strobe = (value & 0x01) == 0x01;
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
                    final ButtonMessage buttonMessage = ButtonMessage.deserialize(readBuffer);
                    NetworkJoypad.this.handleButtonMessage(buttonMessage);
                } catch (IOException e) {
                    System.out.println("IOException thrown while reading from input stream: " + e.getStackTrace());
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
