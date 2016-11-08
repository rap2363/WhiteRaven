package snes;

import operations.Utilities;

public class EightBitRegister implements Register {
    protected byte data;
    protected static final int MAX_EIGHT_BIT_VALUE = 256;

    public EightBitRegister() {
        this.data = 0;
    }

    @Override
    public int read() {
        return Utilities.toUnsignedValue(this.data);
    }

    public byte readAsByte() {
        return data;
    }

    @Override
    public void write(int value) {
        this.data = (byte) value;
    }

    @Override
    public void increment() {
        if (this.data == (byte) MAX_EIGHT_BIT_VALUE) {
            this.data = 0;
        } else {
            this.data++;
        }
    }

    @Override
    public void decrement() {
        if (this.data == 0) {
            this.data = (byte) MAX_EIGHT_BIT_VALUE;
        } else {
            this.data--;
        }
    }

    public boolean addByte(byte b, boolean carryBit) {
        boolean overflow = Utilities.toUnsignedValue(this.data) + Utilities.toUnsignedValue(b)
                + (carryBit ? 1 : 0) > MAX_EIGHT_BIT_VALUE;
        this.data += b + (carryBit ? 1 : 0);
        return overflow;
    }

    public boolean signBit() {
        return this.data >> 7 == 0x01;
    }

    public String toString() {
        return String.format("0x%02x", this.data);
    }
}
