import java.util.Arrays;

/**
 * This abstract class is a MemoryInterface and can read/write to contiguous
 * blocks of memory.
 * 
 * @author rparanjpe
 *
 */
public abstract class MemoryMap {
    protected byte[] memory;

    /**
     * Read a byte from an address in memory
     * 
     * @param address
     * @return
     */
    public byte read(int address) {
        return memory[address];
    }

    /**
     * Read a number of bytes from memory starting at an address
     * 
     * @param address
     * @param numBytes
     * @return
     */
    public byte[] read(int address, int numBytes) {
        return Arrays.copyOfRange(memory, address, address + numBytes);
    }

    /**
     * Writes one byte to an address in memory
     * 
     * @param address
     * @param byteToWrite
     */
    public void write(int address, byte value) {
        memory[address] = value;
    }

    /**
     * Writes a number of bytes to memory starting at an address
     * 
     * @param address
     * @param numBytes
     * @param bytesToWrite
     */
    public void write(int address, byte[] values) {
        for (int i = 0; i < values.length; i++) {
            memory[address + i] = values[i];
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
