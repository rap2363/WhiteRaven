package io;

import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * A hard-coded keyboard controller with fixed button mappings
 */
public class KeyboardController implements Joypad, Controller {
    private int currentButton;
    private boolean strobe;

    private static final int[] buttonMappings = {
        KeyEvent.VK_R,      // A
        KeyEvent.VK_Z,      // B
        KeyEvent.VK_SHIFT,  // SELECT
        KeyEvent.VK_ENTER,  // START
        KeyEvent.VK_UP,     // UP
        KeyEvent.VK_DOWN,   // DOWN
        KeyEvent.VK_LEFT,   // LEFT
        KeyEvent.VK_RIGHT   // RIGHT
    };

    private final Map<Integer, Boolean> buttonsPressedMap;

    public KeyboardController() {
        currentButton = 0;
        strobe = false;
        buttonsPressedMap = new HashMap<>();
        buttonsPressedMap.put(KeyEvent.VK_R, false);
        buttonsPressedMap.put(KeyEvent.VK_Z, false);
        buttonsPressedMap.put(KeyEvent.VK_SHIFT, false);
        buttonsPressedMap.put(KeyEvent.VK_ENTER, false);
        buttonsPressedMap.put(KeyEvent.VK_UP, false);
        buttonsPressedMap.put(KeyEvent.VK_DOWN, false);
        buttonsPressedMap.put(KeyEvent.VK_LEFT, false);
        buttonsPressedMap.put(KeyEvent.VK_RIGHT, false);
        initializeListener();
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
        return (byte) (buttonsPressedMap.get(buttonMappings[currentButton]) ? 0x41 : 0x40);
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
            if (!buttonsPressedMap.containsKey(keyEvent.getKeyCode())) {
                return true;
            }
            if (keyEvent.getID() == KeyEvent.KEY_PRESSED || keyEvent.getID() == KeyEvent.KEY_RELEASED) {
                buttonsPressedMap.put(keyEvent.getKeyCode(), (keyEvent.getID() == KeyEvent.KEY_PRESSED));
            }
            return true;
        });
    }
}
