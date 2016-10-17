
class CPUMemory extends MemoryMap {
    private static final int NES_CPU_MEMORY_SIZE = 0x10000;

    private static final int NES_CPU_ZP_OFFSET = 0x0;
    private static final int NES_CPU_STACK_OFFSET = 0x0100;
    private static final int NES_CPU_RAM_OFFSET = 0x0200;

    private static final int NES_CPU_IO_OFFSET = 0x2000;
    private static final int NES_CPU_EXPANSION_OFFSET = 0x4020;
    private static final int NES_CPU_SRAM_OFFSET = 0x6000;

    private static final int NES_CPU_PRG_LOWER = 0x8000;
    private static final int NES_CPU_PRG_UPPER = 0xC000;

    CPUMemory() {
        this.memory = new byte[NES_CPU_MEMORY_SIZE];
    }
}

class ProcessorStatus extends EightBitRegister {

    private boolean getStatusBitAt(int position) {
        return ((this.register >> position) & 1) == 0x1;
    }

    void setCarryFlag() {
        this.register = (byte) (this.register | (0x01 << 0));
    }

    void clearCarryFlag() {
        this.register = (byte) (this.register & ~(0x01 << 0));
    }

    boolean carryFlag() {
        return getStatusBitAt(0);
    }

    void setZeroFlag() {
        this.register = (byte) (this.register | (0x01 << 1));
    }

    void clearZeroFlag() {
        this.register = (byte) (this.register & ~(0x01 << 1));
    }

    boolean zeroFlag() {
        return getStatusBitAt(1);
    }

    void setInterruptDisableFlag() {
        this.register = (byte) (this.register | (0x01 << 2));
    }

    void clearInterruptDisableFlag() {
        this.register = (byte) (this.register & ~(0x01 << 2));
    }

    boolean interruptDisableFlag() {
        return getStatusBitAt(2);
    }

    void setDecimalModeFlag() {
        this.register = (byte) (this.register | (0x01 << 3));
    }

    void clearDecimalModeFlag() {
        this.register = (byte) (this.register & ~(0x01 << 3));
    }

    boolean decimalModeFlag() {
        return getStatusBitAt(3);
    }

    void setBreakFlag() {
        this.register = (byte) (this.register | (0x01 << 4));
    }

    void clearBreakFlag() {
        this.register = (byte) (this.register & ~(0x01 << 4));
    }

    boolean breakFlag() {
        return getStatusBitAt(4);
    }

    void setOverflowFlag() {
        this.register = (byte) (this.register | (0x01 << 6));
    }

    void clearOverflowFlag() {
        this.register = (byte) (this.register & ~(0x01 << 6));
    }

    boolean overflowFlag() {
        return getStatusBitAt(6);
    }

    void setNegativeFlag() {
        this.register = (byte) (this.register | (0x01 << 7));
    }

    void clearNegativeFlag() {
        this.register = (byte) (this.register & ~(0x01 << 7));
    }

    boolean negativeFlag() {
        return getStatusBitAt(7);
    }

    public String toString() {
        String s = String.format("%8s", Integer.toBinaryString(this.register)).replace(" ", "0");
        return s.substring(s.length() - 8, s.length());
    }
}

/**
 * Models the NES CPU
 *
 * @author rparanjpe
 *
 */
public class CPU {
    public MemoryMap cpuMemory;
    public SixteenBitRegister PC;
    public EightBitRegister A;
    public EightBitRegister X;
    public EightBitRegister Y;
    public ProcessorStatus P;

    CPU() {
        this.cpuMemory = new CPUMemory();
        this.PC = new SixteenBitRegister();
        this.A = new EightBitRegister();
        this.X = new EightBitRegister();
        this.Y = new EightBitRegister();
        this.P = new ProcessorStatus();
    }

    public String state() {
        String state = "";
        state += "PC: " + this.PC + "\n";
        state += "A:  " + this.A + "\n";
        state += "X:  " + this.X + "\n";
        state += "Y:  " + this.Y + "\n";
        state += "P:  NV-BDIZC\n";
        state += "    " + this.P + "\n";

        return state;
    }

    public static void main(String[] args) {
        CPU cpu = new CPU();
        cpu.PC.write(0xfe31);
        cpu.X.write(0xfe);
        System.out.println(cpu.state());
    }
}
