package snes;

public class EightBitRegister implements Register {
    protected byte data;
    protected static final int MAX_EIGHT_BIT_VALUE = 256;

    public EightBitRegister() {
        this.data = 0;
    }

    @Override
    public int read() {
        return this.data >= 0 ? this.data : this.data + MAX_EIGHT_BIT_VALUE;
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

    public boolean addByte(byte b) {
        this.data += b;
        return (int) this.data < ((int) this.data - (int) b);
    }

    public String toString() {
        return String.format("0x%02x", this.data);
    }
}
