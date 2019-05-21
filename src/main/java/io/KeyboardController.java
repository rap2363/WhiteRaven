package io;

import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

/**
 * A hard-coded keyboard controller with fixed button mappings
 */
public class KeyboardController implements Joypad, Controller {
    private int currentButton;
    private boolean strobe;

    private boolean[] buttonsPressed = new boolean[8];
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

    public KeyboardController() {
        reset();
        initializeListener();
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
    private synchronized byte getButton() {
        return (byte) (buttonsPressed[currentButton] ? 0x41 : 0x40);
    }

    @Override
    public void write(byte value) {
        strobe = (value & 0x01) == 0x01;
    }

    /**
     * Initialize a listener for keyboard events and set the appropriate buttons.
     */
    @Override
    public void initializeListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEvent -> {
            for (int i = 0; i < buttonMappings.length; i++) {
                if (buttonMappings[i] == keyEvent.getKeyCode()) {
                    if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                        buttonsPressed[i] = true;
                    } else if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
                        buttonsPressed[i] = false;
                    }
                    break;
                }
            }
            return false;
        });
    }
}
