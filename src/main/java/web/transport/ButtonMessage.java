package web.transport;

import operations.Utilities;

/**
 * Encapsulates the button presses/releases that are sent from the client to the server during gameplay.
 */
public enum ButtonMessage implements ByteSerializable {
    A_PRESSED,
    A_RELEASED,
    B_PRESSED,
    B_RELEASED,
    SELECT_PRESSED,
    SELECT_RELEASED,
    START_PRESSED,
    START_RELEASED,
    UP_PRESSED,
    UP_RELEASED,
    DOWN_PRESSED,
    DOWN_RELEASED,
    LEFT_PRESSED,
    LEFT_RELEASED,
    RIGHT_PRESSED,
    RIGHT_RELEASED;

    /**
     * Serialize the button event into one byte.
     *
     * @return
     */
    @Override
    public byte[] serialize() {
        return new byte[]{(byte) this.ordinal()};
    }

    /**
     * Deserialize a byte into a button message.
     *
     * @param bytes
     * @return
     */
    public static ButtonMessage deserialize(final byte[] bytes) {
        return ButtonMessage.values()[Utilities.toUnsignedValue(bytes[0])];
    }
}
