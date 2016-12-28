package operations;

import nes.CPU;

class PullProcessorStatusImplicit extends Operation {

    PullProcessorStatusImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Pull a byte from the stack into the processor status register.
     */
    @Override
    public void execute(CPU cpu) {
        cpu.P.writeByte(cpu.pullFromStack());

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

public class PullProcessorStatus extends Instruction {

    public PullProcessorStatus() {
        this.assemblyInstructionName = "PLP";
        this.addOperation(new PullProcessorStatusImplicit(AddressingMode.Implicit, (byte) 0x28, 1, 4));
    }
}
