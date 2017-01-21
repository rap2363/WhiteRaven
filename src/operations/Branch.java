package operations;

import nes.CPU;

public abstract class Branch extends Operation {
    public Branch(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    /**
     * Branch by a specific amount based on a branching condition
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(AddressingMode.Immediate, cpu, cpu.readAfterPC(numBytes - 1));

        // Add a cycle if we cross the page boundary
        if (Utilities.getOverflowFlag((byte) cpu.PC.read(), value, false)) {
            cpu.cycleCount++;
        }

        if (branchCondition(cpu)) {
            cpu.PC.addByte(value);
            cpu.cycleCount++;
        }

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }

    /**
     * To be implemented in base Branch instructions. This is the condition that
     * is checked before executing a branch
     *
     * @return
     */
    protected abstract boolean branchCondition(CPU cpu);
}
