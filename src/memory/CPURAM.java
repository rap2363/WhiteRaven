package memory;

public class CPURAM extends MemoryMap {
    public static final int STACK_OFFSET = 0x0100;
    private static final int RAM_SIZE_IN_BYTES = 0x2000;
    private static final int MIRROR_OFFSET = 0x0800;

    public CPURAM() {
        super(MIRROR_OFFSET);
    }

    /**
     * Read a byte from memory (implemented with memory mirrors)
     *
     * @param address
     * @return
     */
    @Override
    public byte read(int address) {
        return memory[address % MIRROR_OFFSET];
    }

    /**
     * Write a byte into memory (implemented with memory mirrors)
     *
     * @param address
     * @param value
     */
    @Override
    public void write(int address, byte value) {
        memory[address % MIRROR_OFFSET] = value;
    }

    @Override
    public int size() {
        return RAM_SIZE_IN_BYTES;
    }
}
