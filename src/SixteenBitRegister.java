
public class SixteenBitRegister {
    protected byte first;
    protected byte second;

    /**
     * Write out two bytes to our two-byte register from an integer
     * 
     * @param value
     */
    public void write(int value) {
        this.first = (byte) (value >> 8);
        this.second = (byte) (value);
    }

    /**
     * Write out two bytes to our two-byte register
     * 
     * @param first
     * @param second
     */
    public void write(byte first, byte second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Read the bytes out from the register into an int
     * 
     * @return
     */
    public int readToInt() {
        return (int) ((this.first << 8) + this.second);
    }

    /**
     * Read the bytes out from the register
     * 
     * @return
     */
    public byte[] read() {
        byte[] bytesToRead = new byte[2];

        bytesToRead[0] = first;
        bytesToRead[1] = second;

        return bytesToRead;
    }

    public String toString() {
        return String.format("0x%02x%02x", this.first, this.second);
    }
}
