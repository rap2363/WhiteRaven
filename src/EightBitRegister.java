
public class EightBitRegister {
    protected byte register;

    /**
     * Write out a byte to the register
     * 
     * @param value
     */
    public void write(byte value) {
        this.register = value;
    }

    /**
     * Write a byte to the register from an int
     * 
     * @param value
     */
    public void write(int value) {
        this.register = (byte) value;
    }

    /**
     * Read the byte from the register
     * 
     * @return
     */
    public byte readByte() {
        return this.register;
    }

    public String toString() {
        return String.format("0x%02x", this.register);
    }
}
