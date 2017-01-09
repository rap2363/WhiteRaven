package operations;

public abstract class Utilities {
    protected static final int MAX_EIGHT_BIT_VALUE = 256;

    /**
     * Returns true if b1 and b2 added together would result in setting an
     * overflow flag
     *
     * @param b1
     * @param b2
     * @return
     */
    public static boolean getOverflowFlag(byte b1, byte b2, boolean carry) {
        byte s = (byte) (b1 + b2);
        if (carry) {
            s += (byte) 1;
        }

        if (b1 >= 0 && b2 >= 0) {
            return s < 0;
        } else if (b1 <= 0 && b2 <= 0) {
            return s > 0;
        }
        return false;
    }

    /**
     * Convert a byte to an unsigned value represented as an integer
     *
     * @param b
     * @return
     */
    public static int toUnsignedValue(byte b) {
        return b >= 0 ? b : b + MAX_EIGHT_BIT_VALUE;
    }

    /**
     * From two bytes, concatenate to create a 16-bit unsigned integer (which
     * will cover a range from 0 --> 16^4 - 1). e.g. high = 0xda, low = 0x03,
     * the concatenated bytes become 0xda03, which is 55811 in the unsigned
     * representation.
     *
     * @param high
     * @param low
     * @return
     */
    public static int toUnsignedValue(byte high, byte low) {
        return toUnsignedValue(high) * MAX_EIGHT_BIT_VALUE + toUnsignedValue(low);
    }

    /**
     * Add a byte to an integer
     *
     * @param x
     * @param b
     * @return
     */
    public static int addUnsignedByteToInt(int x, byte b) {
        return x + toUnsignedValue(b);
    }

    /**
     * Adds a signed byte to an integer
     *
     * @param x
     * @param b
     * @return
     */
    public static int addByteToInt(int x, byte b) {
        return x + b;
    }

    /**
     * Bit shifts by an amount n. If n is positive, shifts to the right, and if
     * negative shifts to the left.
     *
     * @param n
     * @return
     */
    public static byte bitShift(byte b, int n) {
        if (n < 0) {
            return (byte) (b << (-n));
        }
        return (byte) ((b >> n) & (0xff >> n));
    }

    /**
     * Return the bit value in a byte at the nth location. Bit 7 is the
     * left most bit and bit 0 is the rightmost.
     *
     * @param b
     * @param n
     * @return
     */
    public static boolean bitAt(byte b, int n) {
        return (bitShift(b, n) & (byte) 0x01) == (byte) 0x01;
    }

    /**
     * Return a nice hexadecimal representation of one byte
     *
     * @param b
     * @return
     */
    public static String byteToString(byte b) {
        return String.format("0x%02x", b);
    }

    public static String twoBytesToString(byte msb, byte lsb) {
        return String.format("0x%02x%02x", msb, lsb);
    }

    public static String twoBytesToString(int x) {
        return twoBytesToString((byte) (x >> 8), (byte) x);
    }
}
