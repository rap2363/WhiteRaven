package main.java.operations;

import main.java.nes.CPU;

class SetDecimalModeImplicit extends Operation {

    SetDecimalModeImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Set the DecimalMode flag.
     */
    @Override
    public void execute(CPU cpu) {
        cpu.P.setDecimalModeFlag();

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }
}

public class SetDecimalMode extends Instruction {

    public SetDecimalMode() {
        this.assemblyInstructionName = "SED";
        this.addOperation(new SetDecimalModeImplicit(AddressingMode.Implicit, (byte) 0xF8, 1, 2));
    }
}
