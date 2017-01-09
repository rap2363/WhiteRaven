package operations;

import nes.CPU;

class ClearOverflowFlagImplicit extends Operation {

    ClearOverflowFlagImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Clear the overflow flag in the processor status register
     */
    @Override
    public void execute(CPU cpu) {
        cpu.P.clearOverflowFlag();

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

public class ClearOverflowFlag extends Instruction {

    public ClearOverflowFlag() {
        this.assemblyInstructionName = "CLV";
        this.addOperation(new ClearOverflowFlagImplicit(AddressingMode.Implicit, (byte) 0xB8, 1, 2));
    }

}
