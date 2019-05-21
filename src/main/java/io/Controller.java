package io;

/**
 * An interface to interact with user inputs. Usually used in conjunction with a Joypad to connect a user's input to
 * button presses.
 */
public interface Controller {
    /**
     * Initialize a listener for user input.
     */
    void initializeListener();
}
