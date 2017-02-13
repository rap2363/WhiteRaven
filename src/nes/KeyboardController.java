package nes;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

/**
 * A hard-coded keyboard controller with fixed button mappings
 */
public class KeyboardController implements Joypad {
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
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
                synchronized (KeyboardController.class) {
                    for (int i = 0; i < buttonMappings.length; i++) {
                        if (buttonMappings[i] == ke.getKeyCode()) {
                            if (ke.getID() == KeyEvent.KEY_PRESSED) {
                                buttonsPressed[i] = true;
                            } else if (ke.getID() == KeyEvent.KEY_RELEASED) {
                                buttonsPressed[i] = false;
                            }
                            break;
                        }
                    }
                    return false;
                }
            }
        });
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
    private byte getButton() {
        synchronized (KeyboardController.class) {
            return (byte) (buttonsPressed[currentButton] ? 0x01 : 0x00);
        }
    }

    @Override
    public void write(byte value) {
        strobe = (value & 0x01) == 0x01;
    }
}
