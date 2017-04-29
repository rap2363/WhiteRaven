package nes;

/**
 * This is a dummy class that is used by VIEWER clients in the web application (because we don't need to actually listen
 * for their button presses).
 */
public class NoopController implements Joypad {
    @Override
    public byte read() {
        return 0x0;
    }

    @Override
    public void write(byte value) {
    }
}
