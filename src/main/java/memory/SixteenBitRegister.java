package main.java.memory;

import main.java.operations.Utilities;

public class SixteenBitRegister implements Register {
    private static final int MAX_SIXTEEN_BIT_VALUE = 0xFFFF;
    protected int data;

    public SixteenBitRegister() {
        this.data = 0;
    }

    @Override
    public int read() {
        return this.data >= 0 ? this.data : this.data + MAX_SIXTEEN_BIT_VALUE;
    }

    @Override
    public void write(int value) {
        this.data = value & 0xFFFF;
    }

    public void write(byte msb, byte lsb) {
        this.data = Utilities.toUnsignedValue(msb, lsb);
    }

    @Override
    public void increment() {
        if (this.data == MAX_SIXTEEN_BIT_VALUE) {
            this.data = 0;
        } else {
            this.data++;
        }
    }

    /**
     * Decrement the register
     */
    public void decrement() {
        if (this.data == 0) {
            this.data = MAX_SIXTEEN_BIT_VALUE;
        } else {
            this.data--;
        }
    }

    /**
     * Add a signed byte to the register (and include wrap around)
     *
     * @param b
     */
    public void addByte(byte b) {
        this.data = Utilities.addByteToInt(this.data, b);
        if (this.data < 0) {
            this.data += MAX_SIXTEEN_BIT_VALUE;
        } else if (this.data > MAX_SIXTEEN_BIT_VALUE) {
            this.data -= MAX_SIXTEEN_BIT_VALUE;
        }
    }

    /**
     * Read and return the most significant byte
     *
     * @return
     */
    public byte readMSB() {
        return (byte) (this.data >> 8);
    }

    /**
     * Read and return the least significant byte
     *
     * @return
     */
    public byte readLSB() {
        return (byte) this.data;
    }

    public String toString() {
        return Utilities.twoBytesToString((byte) (this.data >> 8), (byte) (this.data & 0x00FF));
    }

    @Override
    public void shiftLeft(int n) {
        this.data = this.data << n;
    }

    @Override
    public void shiftRight(int n) {
        this.data = this.data >> n;
    }
}
