package operations;

import nes.CPU;

class SetCarryImplicit extends Operation {

    SetCarryImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Set the carry flag.
     */
    @Override
    public void execute(CPU cpu) {
        cpu.P.setCarryFlag();

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }
}

public class SetCarry extends Instruction {

    public SetCarry() {
        this.assemblyInstructionName = "SEC";
        this.addOperation(new SetCarryImplicit(AddressingMode.Implicit, (byte) 0x38, 1, 2));
    }
}
