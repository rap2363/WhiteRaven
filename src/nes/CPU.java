package nes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import operations.*;

class CPUMemory extends MemoryMap {
    private static final int MEMORY_SIZE = 0x10000;
    private static final int RAM_OFFSET = 0x2000;
    private static final int IO_REGISTER_OFFSET = 0x4000;

    public static final int STACK_OFFSET = 0x0100;
    public static final int PRG_LOWER_BANK = 0x8000;
    public static final int PRG_UPPER_BANK = 0xC000;
    public static final int PPU_ADDRESS = 0x2006;
    public static final int PPU_DATA = 0x2007;
    public static final int DMA_REGISTER = 0x4014;

    CPUMemory() {
        this.memory = new byte[MEMORY_SIZE];
    }

    /**
     * Read a byte from memory (implemented with memory mirrors)
     *
     * @param address
     * @return
     */
    @Override
    public byte read(int address) {
        // RAM mirrored every 2 kiB
        if (address < RAM_OFFSET) {
            return memory[address % 0x0800];
        } else if (address < IO_REGISTER_OFFSET) {
            // I/O registers mirrored (8 I/O registers total)
            return memory[IO_REGISTER_OFFSET + address % 0x0008];
        }

        return memory[address % MEMORY_SIZE];
    }

    /**
     * Write a byte into memory (implemented with memory mirrors)
     *
     * @param address
     * @param value
     */
    @Override
    public void write(int address, byte value) {
        if (address % 0x0800 == 0)
        if (address < RAM_OFFSET) {
            // RAM mirrored every 2 kiB
            memory[address % 0x0800] = value;
        } else if (address < IO_REGISTER_OFFSET) {
            // I/O registers mirrored (8 I/O registers total)
            memory[IO_REGISTER_OFFSET + address % 0x0008] = value;
        }

        memory[address % MEMORY_SIZE] = value;
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
    private Interrupt currentInterrupt;

    CPU() {
        this.memory = new CPUMemory();
        this.PC = new SixteenBitRegister();
        this.SP = new EightBitRegister();
        this.A = new EightBitRegister();
        this.X = new EightBitRegister();
        this.Y = new EightBitRegister();
        this.P = new ProcessorStatus();
        this.cycles = 0;

        operationMap = new HashMap<>();

        List<Instruction> instructions = new LinkedList<>(
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
                    new JumpToSubroutine(),
                    new LoadAccumulator(),
                    new LoadX(),
                    new LoadY(),
                    new LogicalShiftRight(),
                    new NoOperation(),
                    new LogicalOR(),
                    new PushAccumulator(),
                    new PushProcessorStatus(),
                    new PullAccumulator(),
                    new PullProcessorStatus(),
                    new RotateLeft(),
                    new RotateRight(),
                    new ReturnFromInterrupt(),
                    new ReturnFromSubroutine(),
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

        // Initial state
        P.write(0x24);
        SP.write(0xFD);
        A.write(0x0);
        X.write(0x0);
        Y.write(0x0);

//        currentInterrupt = Interrupt.RESET;
        currentInterrupt = Interrupt.NONE;
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
     * Returns the state in a single line like:
     *
     * C000 A:00 X:00 Y:00 P:24 SP:FD CYC: 0
     * @return
     */
    public String singleLineState() {
        String state = "";

        state += Utilities.twoBytesToString(this.PC.read()) + "  ";
        state += "A:" + this.A + " ";
        state += "X:" + this.X + " ";
        state += "Y:" + this.Y + " ";
        state += "P:" + Utilities.byteToString(this.P.readAsByte()) + " ";
        state += "SP:" + this.SP + " ";
        state += "CYC:" + this.cycles + "\n";

        return state;

    }

    /**
     * This method is called externally (by the PPU or other things). It automatically handles interrupt priority.
     *
     */
    public void triggerInterrupt(Interrupt interrupt) {
        if (this.currentInterrupt.ordinal() < interrupt.ordinal()) {
            this.currentInterrupt = interrupt;
        }
    }

    /**
     * Should we ignore the current interrupt?
     *
     * @return
     */
    private boolean shouldIgnoreInterrupt() {
        return this.currentInterrupt == Interrupt.NONE
                || (this.currentInterrupt == Interrupt.IRQ && this.P.interruptDisableFlag());
    }

    /**
     * Handle an interrupt. This is called internally during a fetchAndExecute() and takes the following steps:
     *
     * 1. Check if we should ignore the interrupt, if we should, just exit quickly. Otherwise:
     * 2. Push the PC and the status register onto the stack
     * 3. Set the interrupt disable flag
     * 4. Load the address of the interrupt handling routine (somewhere in 0xFFFA-0xFFFF)
     *
     */
    private void handleInterrupt() {
        if (shouldIgnoreInterrupt()) {
            return;
        }

        pushPCOntoStack();
        pushOntoStack(this.P.readAsByte());
        this.P.setInterruptDisableFlag();

        int targetAddress;

        switch (this.currentInterrupt) {
            case IRQ:
                targetAddress = 0xFFFE;
                break;
            case NMI:
                targetAddress = 0xFFFA;
                break;
            case RESET:
                targetAddress = 0xFFFC;
                break;
            default: // we shouldn't get here!
                System.err.println("Bad state!");
                return;
        }

        byte lsb = this.memory.read(targetAddress);
        byte msb = this.memory.read(targetAddress + 1);

        PC.write(msb, lsb);
        this.currentInterrupt = Interrupt.NONE;
    }

    /**
     * Push a byte onto the stack (this decrements the stack pointer)
     */
    public void pushOntoStack(byte value) {
        memory.write(Utilities.addUnsignedByteToInt(CPUMemory.STACK_OFFSET, SP.readAsByte()), value);
        SP.decrement();
    }

    /**
     * Helper method to specifically push the PC onto the stack (MSB then LSB).
     */
    public void pushPCOntoStack() {
        pushOntoStack(PC.readMSB());
        pushOntoStack(PC.readLSB());
    }

    /**
     * Helper method to specifically pull the PC from the stack (LSB then MSB).
     */
    public void pullPCFromStack() {
        byte lsb = pullFromStack();
        byte msb = pullFromStack();
        PC.write(msb, lsb);
    }

    /**
     * Pull a byte from the stack (this increments the stack pointer)
     *
     * @return
     */
    public byte pullFromStack() {
        SP.increment();
        return memory.read(Utilities.addUnsignedByteToInt(CPUMemory.STACK_OFFSET, SP.readAsByte()));
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
        handleInterrupt();

        byte opcode = this.memory.read(this.PC.read());
        Operation op = this.operationMap.get(opcode);
        if (op == null) {
            op = this.operationMap.get((byte) 0xEA);
//            throw new UnimplementedOpcode("Unimplemented instruction: " + String.format("0x%02x", opcode));
        }
        op.execute(this);
    }

    /**
     * Load a cartridge into main memory
     *
     * @param cartridge
     */
    public void loadCartridge(Cartridge cartridge) {
        // Hard coded (Refactor!)
        final byte[] bytes = cartridge.getPRGRomBank(0);
        this.memory.write(CPUMemory.PRG_LOWER_BANK, bytes);
        this.memory.write(CPUMemory.PRG_UPPER_BANK, bytes);
    }

    public static void main(String[] args) {
        CPU cpu = new CPU();
        cpu.P.write(0x20);
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
