package memory;

/**
 * The PPU also has a separate location of memory specifically for SPR-RAM which is distinct from main memory.
 * This is written to directly in DMA's.
 */
public class SPRAM extends MemoryMap {
    private static final int SPRAM_NUM_BYTES = 0x100;
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
}
