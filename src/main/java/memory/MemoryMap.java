package main.java.memory;

/**
 * This abstract class can read/write to contiguous blocks of main.java.memory.
 */
public class MemoryMap {
    protected byte[] memory;

    public MemoryMap(int memorySize) {
        memory = new byte[memorySize];
    }

    public MemoryMap() {}

    /**
     * Read a byte from an address in main.java.memory. These are generally overwritten in subclasses.
     *
     * @param address
     * @return
     */
    public byte read(int address) {
        return this.memory[address % size()];
    }

    /**
     * Read a number of bytes from main.java.memory starting at an address.
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
     * Writes one byte to an address in main.java.memory.
     *
     * @param address
     */
    public void write(int address, byte value) {
        this.memory[address] = value;
    }

    /**
     * Writes a number of bytes to main.java.memory starting at an address in main.java.memory.
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
     * Returns the size of the main.java.memory map in number of bytes
     *
     * @return
     */
    public int size() {
        return memory.length;
    }
}
