package memory;

import operations.Utilities;

/**
 * A structure to hold and write to two bytes (high and low). The holder of the latch can make multiple writes
 * to the latch and read out a concatenated 16 bit address (high | low).
 */
public class SixteenBitLatch {
    private byte high;
    private byte low;
    private boolean highOrLow;

    public SixteenBitLatch() {
        reset();
    }

    /**
     * Write a byte to the latch
     *
     * @param b
     */
    public void write(byte b) {
        if (!highOrLow) {
            high = b;
        } else {
            low = b;
        }
        highOrLow = !highOrLow;
    }

    /**
     * Read the current 16-bit concatenated integer. This will always return an unsigned integer from 0 --> 2^16 - 1
     * @return
     */
    public int read() {
        return Utilities.toUnsignedValue(high, low);
    }

    /**
     * Reset the state of the latch
     */
    public void reset() {
        this.highOrLow = false;
        this.high = 0;
        this.low = 0;
    }
}
