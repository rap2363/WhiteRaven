package nes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import operations.*;

class CPUMemory extends MemoryMap {
    public static final int NES_CPU_MEMORY_SIZE = 0x10000;

    public static final int NES_CPU_ZP_OFFSET = 0x0;
    public static final int NES_CPU_STACK_OFFSET = 0x0100;
    public static final int NES_CPU_RAM_OFFSET = 0x0200;

    public static final int NES_CPU_IO_OFFSET = 0x2000;
    public static final int NES_CPU_EXPANSION_OFFSET = 0x4020;
    public static final int NES_CPU_SRAM_OFFSET = 0x6000;

    public static final int NES_CPU_PRG_LOWER = 0x8000;
    public static final int NES_CPU_PRG_UPPER = 0xC000;

    CPUMemory() {
        this.memory = new byte[NES_CPU_MEMORY_SIZE];
    }
}

/**
 * Models the NES CPU
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

        List<Instruction> instructions = new LinkedList<Instruction>(
            Arrays.asList(
                    new AddWithCarry(),
                    new LogicalAND(),
                    new ArithmeticShiftLeft(),
                    new BranchIfCarryClear(),
                    new BranchIfCarrySet(),
                    new BranchIfEqual(),
                    new BitTest(),
                    new BranchIfMinus(),
                    new BranchIfNotEqual(),
                    new BranchIfPositive(),
                    new Break(),
                    new BranchIfOverflowClear(),
                    new BranchIfOverflowSet(),
                    new ClearCarryFlag(),
                    new ClearDecimalMode(),
                    new ClearInterruptDisable(),
                    new ClearOverflowFlag(),
                    new Compare(),
                    new CompareX(),
                    new CompareY(),
                    new DecrementMemory(),
                    new DecrementX(),
                    new DecrementY(),
                    new LogicalXOR(),
                    new IncrementMemory(),
                    new IncrementX(),
                    new IncrementY(),
                    new Jump(),
//                    new JumpToSubroutine(),
                    new LoadAccumulator(),
                    new LoadX(),
                    new LoadY(),
                    new LogicalShiftRight(),
                    new NoOperation(),
                    new LogicalOR(),
//                    new PushAccumulator(),
//                    new PushProcessorStatus(),
//                    new PullAccumulator(),
//                    new PullProcessorStatus(),
                    new RotateLeft(),
                    new RotateRight(),
//                    new ReturnFromInterrupt(),
//                    new ReturnFromSubroutine(),
                    new SubtractWithCarry(),
                    new SetCarry(),
                    new SetDecimalMode(),
                    new SetInterruptDisable(),
                    new StoreAccumulator(),
                    new StoreX(),
                    new StoreY(),
                    new TransferAccumulatorX(),
                    new TransferAccumulatorY(),
                    new TransferStackPointerX(),
                    new TransferXAccumulator(),
                    new TransferXStackPointer(),
                    new TransferYAccumulator()
            )
        );

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

    /**
     * Push a byte onto the stack (this decrements the stack pointer)
     */
    public void pushOntoStack(byte value) {
        memory.write(SP.read() + CPUMemory.NES_CPU_STACK_OFFSET, value);
        SP.decrement();
    }

    /**
     * Pull a byte from the stack (this increments the stack pointer)
     *
     * @return
     */
    public byte pullFromStack() {
        SP.increment();
        return memory.read(SP.read() + CPUMemory.NES_CPU_STACK_OFFSET);
    }

    /**
     * Read n bytes starting after the PC. For example, if PC=$0320,
     * readAfterPC(3), would read in the bytes located at $0321, $0322, and
     * $0323.
     *
     * @param n
     * @return
     */
    public byte[] readAfterPC(int n) {
        return this.memory.read(Utilities.addUnsignedByteToInt(this.PC.read(), (byte) 0x01), n);
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
            throw new UnimplementedOpcode("Unimplemented instruction: " + String.format("0x%02x", opcode));
        }
        op.execute(this);
    }

    public static void main(String[] args) {
        CPU cpu = new CPU();
        cpu.P.write(0x30);
        cpu.SP.write(0xFF);
        cpu.PC.write(0x0600);

        cpu.memory.write(0x0600, (byte) 0xA9);
        cpu.memory.write(0x0601, (byte) 0x01);
        cpu.memory.write(0x0602, (byte) 0x85);
        cpu.memory.write(0x0603, (byte) 0xf0);
        cpu.memory.write(0x0604, (byte) 0xa9);
        cpu.memory.write(0x0605, (byte) 0xcc);
        cpu.memory.write(0x0606, (byte) 0x85);
        cpu.memory.write(0x0607, (byte) 0xf1);
        cpu.memory.write(0x0608, (byte) 0x6c);
        cpu.memory.write(0x0609, (byte) 0xf0);
        cpu.memory.write(0x060a, (byte) 0x00);

        System.out.println(cpu.state());
        try {
            for (int i = 0; i < 5; i++) {
                cpu.fetchAndExecute();
                System.out.println(cpu.state());
            }
        } catch (UnimplementedOpcode e) {
            System.out.println(e.getMessage());
        }
        System.out.println(String.format("0x%02x", cpu.memory.read(0x0306)));
    }
}
