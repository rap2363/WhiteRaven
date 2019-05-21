package operations;

import nes.CPU;

class PullProcessorStatusImplicit extends Operation {

    PullProcessorStatusImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Pull a byte from the stack into the processor status register, while ignoring bits 4 and 5.
     */
    @Override
    public void execute(CPU cpu) {
        byte flagValues = (byte) (cpu.pullFromStack() & 0xcf);
        byte nonFlagValues = (byte) (cpu.P.readAsByte() & 0x30);

        cpu.P.writeByte((byte) (flagValues | nonFlagValues));

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }
}

public class PullProcessorStatus extends Instruction {

    public PullProcessorStatus() {
        this.assemblyInstructionName = "PLP";
        this.addOperation(new PullProcessorStatusImplicit(AddressingMode.Implicit, (byte) 0x28, 1, 4));
    }
}
