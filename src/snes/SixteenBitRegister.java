package snes;

import operations.Utilities;

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

    public String toString() {
        return String.format("0x%02x%02x", this.data >> 8, this.data & 0x00FF);
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
