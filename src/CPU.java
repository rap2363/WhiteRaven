
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

class ProgramCounter extends Register {
    ProgramCounter() {
        this.registerMemory = new byte[2];
    }
}

class StackPointer extends SingleByteRegister {
}

class Accumulator extends SingleByteRegister {
}

class RegisterX extends SingleByteRegister {
}

class RegisterY extends SingleByteRegister {
}

class ProcessorStatus extends SingleByteRegister {

    private boolean getStatusBitAt(int position) {
        return ((this.registerMemory[0] >> position) & 1) == 0x1;
    }

    void setCarryFlag() {
        this.registerMemory[0] = (byte) (this.registerMemory[0] | (0x01 << 0));
    }

    void clearCarryFlag() {
        this.registerMemory[0] = (byte) (this.registerMemory[0] & ~(0x01 << 0));
    }

    boolean carryFlag() {
        return getStatusBitAt(0);
    }

    void setZeroFlag() {
        this.registerMemory[0] = (byte) (this.registerMemory[0] | (0x01 << 1));
    }

    void clearZeroFlag() {
        this.registerMemory[0] = (byte) (this.registerMemory[0] & ~(0x01 << 1));
    }

    boolean zeroFlag() {
        return getStatusBitAt(1);
    }

    void setInterruptDisableFlag() {
        this.registerMemory[0] = (byte) (this.registerMemory[0] | (0x01 << 2));
    }

    void clearInterruptDisableFlag() {
        this.registerMemory[0] = (byte) (this.registerMemory[0] & ~(0x01 << 2));
    }

    boolean interruptDisableFlag() {
        return getStatusBitAt(2);
    }

    void setDecimalModeFlag() {
        this.registerMemory[0] = (byte) (this.registerMemory[0] | (0x01 << 3));
    }

    void clearDecimalModeFlag() {
        this.registerMemory[0] = (byte) (this.registerMemory[0] & ~(0x01 << 3));
    }

    boolean decimalModeFlag() {
        return getStatusBitAt(3);
    }

    void setBreakFlag() {
        this.registerMemory[0] = (byte) (this.registerMemory[0] | (0x01 << 4));
    }

    void clearBreakFlag() {
        this.registerMemory[0] = (byte) (this.registerMemory[0] & ~(0x01 << 4));
    }

    boolean breakFlag() {
        return getStatusBitAt(4);
    }

    void setOverflowFlag() {
        this.registerMemory[0] = (byte) (this.registerMemory[0] | (0x01 << 6));
    }

    void clearOverflowFlag() {
        this.registerMemory[0] = (byte) (this.registerMemory[0] & ~(0x01 << 6));
    }

    boolean overflowFlag() {
        return getStatusBitAt(6);
    }

    void setNegativeFlag() {
        this.registerMemory[0] = (byte) (this.registerMemory[0] | (0x01 << 7));
    }

    void clearNegativeFlag() {
        this.registerMemory[0] = (byte) (this.registerMemory[0] & ~(0x01 << 7));
    }

    boolean negativeFlag() {
        return getStatusBitAt(7);
    }
}

/**
 * Models the NES CPU
 *
 * @author rparanjpe
 *
 */
public class CPU {

}
