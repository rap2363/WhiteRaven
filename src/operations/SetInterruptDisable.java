package operations;

import nes.CPU;

class SetInterruptDisableImplicit extends Operation {

    SetInterruptDisableImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Set the InterruptDisable flag.
     */
    @Override
    public void execute(CPU cpu) {
        cpu.P.setInterruptDisableFlag();

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }
}

public class SetInterruptDisable extends Instruction {

    public SetInterruptDisable() {
        this.assemblyInstructionName = "SEI";
        this.addOperation(new SetInterruptDisableImplicit(AddressingMode.Implicit, (byte) 0x78, 1, 2));
    }
}
