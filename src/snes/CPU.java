package snes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    public EightBitRegister SP;
    public EightBitRegister A;
    public EightBitRegister X;
    public EightBitRegister Y;
    public ProcessorStatus P;

    public long cycles;

    private HashMap<Byte, Operation> operationMap;

    CPU() {
        this.memory = new CPUMemory();
        this.PC = new SixteenBitRegister();
        this.SP = new EightBitRegister();
        this.A = new EightBitRegister();
        this.X = new EightBitRegister();
        this.Y = new EightBitRegister();
        this.P = new ProcessorStatus();
        this.cycles = 0;

        operationMap = new HashMap<Byte, Operation>();

        List<Instruction> instructions = Arrays.asList(new AddWithCarry());
        for (Instruction instruction : instructions) {
            for (Operation operation : instruction.getOperations()) {
                operationMap.put(operation.opcode, operation);
            }
        }
    }

    public String state() {
        String state = "";

        state += "Cycle Number: " + this.cycles + "\n";
        state += "PC: " + this.PC + "\n";
        state += "SP: " + this.SP + "\n";
        state += "A:  " + this.A + "\n";
        state += "X:  " + this.X + "\n";
        state += "Y:  " + this.Y + "\n";
        state += "P:  NV-BDIZC\n";
        state += "    " + this.P + "\n";

        return state;
    }

    public byte[] readAtPC(int n) {
        return this.memory.read(this.PC.read(), n);
    }

    public byte readOpcode() {
        return this.memory.read(this.PC.read());
    }

    /**
     * Simulates one step in the computing cycle. Reads the byte at the PC for
     * the opcode and carries through with execution. Throws an exception if we
     * do not support the opcode fetched from the PC.
     * 
     * @throws UnimplementedOpcode
     */
    public void fetchAndExecute() throws UnimplementedOpcode {
        byte opcode = this.memory.read(this.PC.read());
        Operation op = this.operationMap.get(opcode);
        if (op == null) {
            throw new UnimplementedOpcode("Unimplemented instruction: " + opcode);
        }
        op.execute(this);
    }

    public static void main(String[] args) {
        CPU cpu = new CPU();
        cpu.P.write(0x30);
        cpu.PC.write(0x0603);
        cpu.memory.write(0x0603, (byte) 0x69);
        cpu.memory.write(0x0604, (byte) 0x32);
        cpu.memory.write(0x0605, (byte) 0x69);
        cpu.memory.write(0x0606, (byte) 0x01);
        cpu.memory.write(0x0607, (byte) 0x69);
        cpu.memory.write(0x0608, (byte) 0x01);

        cpu.A.write(0xdf);
        cpu.P.setCarryFlag();

        try {
            cpu.fetchAndExecute();
            System.out.println(cpu.state());
            cpu.fetchAndExecute();
            System.out.println(cpu.state());
            cpu.fetchAndExecute();
            System.out.println(cpu.state());
        } catch (UnimplementedOpcode e) {
            System.out.println(e.getMessage());
        }
    }
}
