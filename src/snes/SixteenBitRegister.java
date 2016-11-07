package snes;

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

    public String toString() {
        return String.format("0x%02x%02x", this.data >> 8, this.data & 0x00FF);
    }
}
