package operations;

import nes.CPU;

class NoOperationImplicit extends Operation {

    NoOperationImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Do nothing other than incrementing the PC and cycles.
     */
    @Override
    public void execute(CPU cpu) {
        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

public class NoOperation extends Instruction {

    public NoOperation() {
        this.assemblyInstructionName = "NOP";
        this.addOperation(new NoOperationImplicit(AddressingMode.Implicit, (byte) 0xEA, 1, 2));
    }
}
