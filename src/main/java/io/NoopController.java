package io;

/**
 * This is a dummy class that is used by VIEWER clients in the main.java.web application (because we don't need to actually listen
 * for their button presses).
 */
public class NoopController implements Joypad, Controller {
    @Override
    public byte read() {
        return 0x0;
    }

    @Override
    public void write(byte value) {
    }

    @Override
    public void initializeListener() {
    }
}
