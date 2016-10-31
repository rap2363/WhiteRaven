package snes;

import operations.AddWithCarry;
import operations.Instruction;
import operations.Operation;

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

/**
 * Models the NES CPU
 *
 * @author rparanjpe
 *
 */
public class CPU {
    public MemoryMap memory;
    public SixteenBitRegister PC;
    public EightBitRegister A;
    public EightBitRegister X;
    public EightBitRegister Y;
    public ProcessorStatus P;

    public long cycles;

    CPU() {
        this.memory = new CPUMemory();
        this.PC = new SixteenBitRegister();
        this.A = new EightBitRegister();
        this.X = new EightBitRegister();
        this.Y = new EightBitRegister();
        this.P = new ProcessorStatus();
        this.cycles = 0;
    }

    public String state() {
        String state = "";

        state += "Cycle Number: " + this.cycles + "\n";
        state += "PC: " + this.PC + "\n";
        state += "A:  " + this.A + "\n";
        state += "X:  " + this.X + "\n";
        state += "Y:  " + this.Y + "\n";
        state += "P:  NV-BDIZC\n";
        state += "    " + this.P + "\n";

        return state;
    }

    public byte[] readPC() {
        return this.memory.read(this.PC.first, 2);
    }

    public byte readOpcode() {
        return this.memory.read(this.PC.first);
    }

    /**
     * Returns true if b1 and b2 added together would result in setting the
     * overflow flag
     * 
     * @param b1
     * @param b2
     * @return
     */
    public boolean getOverflowFlag(byte b1, byte b2, boolean carry) {
        byte s = (byte) (b1 + b2);
        if (carry) {
            s += (byte) 1;
        }
        System.out.println(b1 + " + " + b2 + " = " + s);
        if (b1 >= 0 && b2 >= 0) {
            return s < 0;
        } else if (b1 <= 0 && b2 <= 0) {
            return s > 0;
        }
        return false;
    }

    public static void main(String[] args) {
        CPU cpu = new CPU();
        cpu.PC.write(0xffff);
        cpu.PC.increment();
        cpu.PC.decrement();
        cpu.X.write(0xfd);
        cpu.P.increment();
        cpu.A.write(0x7F);
        System.out.println(cpu.state());
        Instruction tmp = new AddWithCarry();
        Operation op = tmp.getOperations().get(0);
        op.execute(cpu);
        System.out.println(cpu.state());
    }
}
