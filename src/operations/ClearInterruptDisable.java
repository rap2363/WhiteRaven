package operations;

import nes.CPU;

class ClearInterruptDisableImplicit extends Operation {

    ClearInterruptDisableImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Clear the interrupt disable bit in the processor status register
     */
    @Override
    public void execute(CPU cpu) {
        cpu.P.clearInterruptDisableFlag();

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

public class ClearInterruptDisable extends Instruction {

    public ClearInterruptDisable() {
        this.assemblyInstructionName = "CLI";
        this.addOperation(new ClearInterruptDisableImplicit(AddressingMode.Implicit, (byte) 0xD8, 1, 2));
    }

}
