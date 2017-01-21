package operations;

import nes.CPU;

public abstract class BooleanLogicOperation extends Operation {
    BooleanLogicOperation(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * A logical operation is performed, bitwise, on the accumulator contents using
     * the contents of a byte of memory. A subclass should define the logical operation
     * to be used.
     *
     * A,Z,N = operation(A, M)
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        operation(cpu, value);

        // Set the processor status flags
        if (cpu.A.read() == 0) {
            cpu.P.setZeroFlag();
        } else {
            cpu.P.clearZeroFlag();
        }

        if (cpu.A.signBit()) {
            cpu.P.setNegativeFlag();
        } else {
            cpu.P.clearNegativeFlag();
        }

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }

    protected abstract void operation(CPU cpu, byte value);
}
