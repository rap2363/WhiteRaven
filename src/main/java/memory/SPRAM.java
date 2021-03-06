package memory;

import operations.Utilities;

/**
 * The PPU also has a separate location of main.java.memory specifically for SPR-RAM which is distinct from main main.java.memory.
 * This is written to directly in DMA's.
 */
public class SPRAM extends MemoryMap {
    private static final int SPRAM_NUM_BYTES = 0x100;

    public SPRAM() {
        super(SPRAM_NUM_BYTES);
    }

    public void dmaWrite(byte[] values) {
        this.write(0x0, values);
    }

    /**
     * Fetch all relevant sprites for a line number. We terminate early if our buffer is saturated.
     *
     * For every four bytes:
     * Byte 0: y position of top side - 1 (so we offset by +1)
     * Byte 1: tile index (in pattern tables)
     * Byte 2: attributes
     * Byte 3: x position of left side
     *
     * We return true if we fetched buffer.length sprites
     *
     * @param lineNumber
     * @return
     */
    public boolean fetchSprites(final Sprite[] buffer, int lineNumber) {
        int numSprites = 0;
        for (int i = 0; i < buffer.length; i++) {
            // Clear the buffer before we fetch new sprites
            buffer[i] = null;
        }

        for (int i = 0; i < size() && numSprites < buffer.length; i += 4) {
            int y = Utilities.toUnsignedValue(this.read(i)) + 1;
            int tileIndex = Utilities.toUnsignedValue(this.read(i + 1));
            byte attributes = this.read(i + 2);
            int x = Utilities.toUnsignedValue(this.read(i + 3));

            if (Utilities.inRange(lineNumber, y, y + 7)) {
                buffer[numSprites++] = new Sprite.Builder()
                    .setX(x)
                    .setY(y)
                    .setPriority(i / 4)
                    .setAttributes(attributes)
                    .setPatternTableIndex(tileIndex)
                    .build();
            }
        }
        return numSprites == buffer.length;
    }
}
