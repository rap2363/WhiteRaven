package nes;

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

    public void writeByte(byte value) {
        this.data = value;
    }

    @Override
    public void increment() {
        this.data++;
    }

    @Override
    public void decrement() {
        this.data--;
    }

    @Override
    public void shiftLeft(int n) {
        this.data = Utilities.bitShift(this.data, -n);
    }

    @Override
    public void shiftRight(int n) {
        this.data = Utilities.bitShift(this.data, n);
    }

    public boolean addByte(byte b, boolean carryBit) {
        boolean overflow = Utilities.toUnsignedValue(this.data) + Utilities.toUnsignedValue(b)
                + (carryBit ? 1 : 0) >= MAX_EIGHT_BIT_VALUE;
        this.data += b + (carryBit ? 1 : 0);
        return overflow;
    }

    public void andByte(byte b) {
        this.data &= b;
    }

    public void orByte(byte b) {
        this.data |= b;
    }

    public void xorByte(byte b) {
        this.data ^= b;
    }

    public boolean signBit() {
        return this.data < 0;
    }

    public String toString() {
        return Utilities.byteToString(this.data);
    }
}
