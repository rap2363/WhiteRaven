package nes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import memory.SixteenBitRegister;
import memory.EightBitRegister;
import memory.ProcessorStatus;
import memory.ConsoleMemory;
import memory.CPURAM;

import operations.*;

/**
 * Models the NES CPU and 6502 processor architecture
 */
public class CPU extends Processor {
    public ConsoleMemory memory;
    public SixteenBitRegister PC;
    public EightBitRegister SP;
    public EightBitRegister A;
    public EightBitRegister X;
    public EightBitRegister Y;
    public ProcessorStatus P;
    private HashMap<Byte, Operation> operationMap;
    private Interrupt currentInterrupt;

    CPU(final ConsoleMemory consoleMemory) {
        this.memory = consoleMemory;
        this.PC = new SixteenBitRegister();
        this.SP = new EightBitRegister();
        this.A = new EightBitRegister();
        this.X = new EightBitRegister();
        this.Y = new EightBitRegister();
        this.P = new ProcessorStatus();
        this.cycleCount = 0;

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

        currentInterrupt = Interrupt.RESET;
    }

    /**
     * Return the state in multiple lines
     *
     * @return
     */
    public String state() {
        String state = "";

        state += "Cycle Number: " + this.cycleCount + "\n";
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
     * Returns the state in a single line like: C000 A:00 X:00 Y:00 P:24 SP:FD CYC: 0
     *
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
        state += "CYC:" + this.cycleCount + "\n";

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
     * Shouldn't be called except for testing (use triggerInterrupt() instead)
     *
     * @param interrupt
     */
    public void setCurrentInterrupt(Interrupt interrupt) {
        this.currentInterrupt = interrupt;
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
     * Handle an interrupt. This is called internally during an execute() and takes the following steps:
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
        memory.write(Utilities.addUnsignedByteToInt(CPURAM.STACK_OFFSET, SP.readAsByte()), value);
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
        return memory.read(Utilities.addUnsignedByteToInt(CPURAM.STACK_OFFSET, SP.readAsByte()));
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

    /**
     * Simulates one step in the computing cycle. Reads the byte at the PC for
     * the opcode and carries through with execution. Throws an exception if we
     * do not support the opcode fetched from the PC.
     *
     * @throws UnimplementedOpcode
     */
    @Override
    public void execute() {
        handleInterrupt();
        if (this.memory.dma()) {
            this.memory.executeDma();
            this.cycleCount += 512; // Stalls the CPU for 512 cycles.
            return;
        }

        byte opcode = this.memory.read(this.PC.read());
        Operation op = this.operationMap.get(opcode);
        if (op == null) {
            System.err.println("Unimplemented instruction: " + String.format("0x%02x", opcode));
            return;
        }
        op.execute(this);
    }

    public static void main(String[] args) {
        byte value = (byte) 0x00c3;
        System.out.println(String.format(Integer.toHexString(Utilities.toUnsignedValue(value) * 0x100)));
    }
}
