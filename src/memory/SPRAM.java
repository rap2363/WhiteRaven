package memory;

import operations.Utilities;

/**
 * The PPU also has a separate location of memory specifically for SPR-RAM which is distinct from main memory.
 * This is written to directly in DMA's.
 */
public class SPRAM extends MemoryMap {
    private static final int SPRAM_NUM_BYTES = 0x100;
    private static final int SPRITE_SIZE = 0x08;
    public int currentAddress = 0x0;

    public SPRAM() {
        super(SPRAM_NUM_BYTES);
    }

    @Override
    public byte read(int address) {
        return this.memory[address % size()];
    }

    @Override
    public void write(int address, byte value) {
        this.memory[address % size()] = value;
    }

    public void dmaWrite(byte[] values) {
        this.write(currentAddress, values);
    }

    public void setCurrentAddress(int address) {
        this.currentAddress = address;
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
            int y = Utilities.toUnsignedValue(read(i)) + 1;
            int tileIndex = Utilities.toUnsignedValue(read(i + 1));
            byte attributes = read(i + 2);
            int x = Utilities.toUnsignedValue(read(i + 3)) - 8; // A hacky workaround for now TODO: offset in PPU.java

            if (lineNumber >= y && lineNumber < y + SPRITE_SIZE) {
                buffer[numSprites++] = new Sprite.Builder()
                                        .setX(x)
                                        .setY(y)
                                        .setPriority(i)
                                        .setAttributes(attributes)
                                        .setPatternTableIndex(tileIndex)
                                        .build();
            }
        }
        return numSprites == buffer.length;
    }
}
