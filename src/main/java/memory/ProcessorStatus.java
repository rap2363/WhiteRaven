package main.java.memory;

public class ProcessorStatus extends EightBitRegister {

    public boolean getStatusBitAt(int position) {
        return ((this.data >> position) & 1) == 0x1;
    }

    public void setCarryFlag() {
        this.data = (byte) (this.data | (0x01 << 0));
    }

    public void clearCarryFlag() {
        this.data = (byte) (this.data & ~(0x01 << 0));
    }

    public boolean carryFlag() {
        return getStatusBitAt(0);
    }

    public void setZeroFlag() {
        this.data = (byte) (this.data | (0x01 << 1));
    }

    public void clearZeroFlag() {
        this.data = (byte) (this.data & ~(0x01 << 1));
    }

    public boolean zeroFlag() {
        return getStatusBitAt(1);
    }

    public void setInterruptDisableFlag() {
        this.data = (byte) (this.data | (0x01 << 2));
    }

    public void clearInterruptDisableFlag() {
        this.data = (byte) (this.data & ~(0x01 << 2));
    }

    public boolean interruptDisableFlag() {
        return getStatusBitAt(2);
    }

    public void setDecimalModeFlag() {
        this.data = (byte) (this.data | (0x01 << 3));
    }

    public void clearDecimalModeFlag() {
        this.data = (byte) (this.data & ~(0x01 << 3));
    }

    public boolean decimalModeFlag() {
        return getStatusBitAt(3);
    }

    public void setBreakFlag() {
        this.data = (byte) (this.data | (0x01 << 4));
    }

    public void clearBreakFlag() {
        this.data = (byte) (this.data & ~(0x01 << 4));
    }

    public boolean breakFlag() {
        return getStatusBitAt(4);
    }

    public void setOverflowFlag() {
        this.data = (byte) (this.data | (0x01 << 6));
    }

    public void clearOverflowFlag() {
        this.data = (byte) (this.data & ~(0x01 << 6));
    }

    public boolean overflowFlag() {
        return getStatusBitAt(6);
    }

    public void setNegativeFlag() {
        this.data = (byte) (this.data | (0x01 << 7));
    }

    public void clearNegativeFlag() {
        this.data = (byte) (this.data & ~(0x01 << 7));
    }

    public boolean negativeFlag() {
        return getStatusBitAt(7);
    }

    @Override
    public String toString() {
        String s = String.format("%8s", Integer.toBinaryString(this.data)).replace(" ", "0");
        return s.substring(s.length() - 8, s.length());
    }
}
