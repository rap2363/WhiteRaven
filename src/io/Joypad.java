package io;

/**
 * Captures the logic to read from an input controller
 */
public interface Joypad {
    /**
     * Read a byte from the Joypad
     * @return
     */
    byte read();

    /**
     * Write a byte to the Joypad
     */
    void write(byte value);
}
