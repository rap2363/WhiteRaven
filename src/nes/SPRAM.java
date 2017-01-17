package nes;

/**
 * The PPU also has a separate location of memory specifically for SPR-RAM which is distinct from main memory.
 * This is written to directly in DMA's.
 */
public class SPRAM extends MemoryMap {
    private static final int SPRAM_NUM_BYTES = 0x100;
    public int address = 0x0;

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
        if (values.length != 0x100) {
            System.err.println("SPRAM size is exactly 256 bytes!");
        }
        for (int i = 0; i < size(); i++) {
            this.write(i + address, values[i]);
        }
    }
}
