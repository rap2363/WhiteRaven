package nes;

/**
 * This abstract class can read/write to contiguous blocks of memory.
 */
public abstract class MemoryMap {
    protected byte[] memory;

    /**
     * Read a byte from an address in memory
     *
     * @param address
     * @return
     */
    public abstract byte read(int address);

    /**
     * Read a number of bytes from memory starting at an address.
     *
     * @param address
     * @param numBytes
     * @return
     */
    public final byte[] read(int address, int numBytes) {
        byte[] readBytes = new byte[numBytes];
        for (int i = 0; i < numBytes; i++) {
            readBytes[i] = this.read(address + i);
        }
        return readBytes;
    }

    /**
     * Writes one byte to an address in memory.
     *
     * @param address
     */
    public abstract void write(int address, byte value);

    /**
     * Writes a number of bytes to memory starting at an address in memory.
     *
     * @param address
     * @param values
     */
    public final void write(int address, byte[] values) {
        for (int i = 0; i < values.length; i++) {
            this.write(address + i, values[i]);
        }
    }

    /**
     * Returns the size of the memory map in number of bytes
     *
     * @return
     */
    public int size() {
        return memory.length;
    }
}
