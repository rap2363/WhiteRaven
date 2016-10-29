package snes;

public class EightBitRegister {
    public byte register;

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
     * Increment the register
     */
    public void increment() {
        this.register++;
    }

    /**
     * Decrement the register
     */
    public void decrement() {
        this.register--;
    }

    /**
     * Add a byte's value to the register. Return true if overflow occurs.
     * 
     * @return
     */
    public boolean addByte(byte b) {
        System.out.println((int) this.register);
        this.register += (int) b;
        System.out.println(((int) this.register) + ", " + ((int) this.register - (int) b));
        return (int) this.register < ((int) this.register - (int) b);
    }

    public String toString() {
        return String.format("0x%02x", this.register);
    }
}
