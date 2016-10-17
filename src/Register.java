
/**
 * This abstract class is an implementation of the RegisterInterface and can
 * read/write to a register
 * 
 * @author rparanjpe
 *
 */
public abstract class Register {
    protected byte[] registerMemory;

    /**
     * Read the bytes from the register
     * 
     * @return
     */
    public byte[] read() {
        return registerMemory;
    }

    /**
     * Write some bytes to the register
     * 
     * @param value
     */
    public void write(byte[] values) {
        for (int i = 0; i < values.length; i++) {
            registerMemory[i] = values[i];
        }
    }

    /**
     * Returns the size of the register in number of bytes
     * 
     * @return
     */
    public int size() {
        return registerMemory.length;
    }
}
