package main.java.operations;

import main.java.nes.CPU;

class ClearDecimalModeImplicit extends Operation {

    ClearDecimalModeImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Clear the decimal mode bit in the processor status register
     */
    @Override
    public void execute(CPU cpu) {
        cpu.P.clearDecimalModeFlag();

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }
}

public class ClearDecimalMode extends Instruction {

    public ClearDecimalMode() {
        this.assemblyInstructionName = "CLD";
        this.addOperation(new ClearDecimalModeImplicit(AddressingMode.Implicit, (byte) 0xD8, 1, 2));
    }

}
