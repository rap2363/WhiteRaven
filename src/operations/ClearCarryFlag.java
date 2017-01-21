package operations;

import nes.CPU;

class ClearCarryFlagImplicit extends Operation {

    ClearCarryFlagImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Clear the carry bit in the processor status register
     */
    @Override
    public void execute(CPU cpu) {
        cpu.P.clearCarryFlag();

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }
}

public class ClearCarryFlag extends Instruction {

    public ClearCarryFlag() {
        this.assemblyInstructionName = "CLC";
        this.addOperation(new ClearCarryFlagImplicit(AddressingMode.Implicit, (byte) 0x18, 1, 2));
    }

}
