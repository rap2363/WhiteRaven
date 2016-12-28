package operations;

import nes.CPU;

class PushProcessorStatusImplicit extends Operation {

    PushProcessorStatusImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Push the processor status register onto the stack
     */
    @Override
    public void execute(CPU cpu) {
        cpu.pushOntoStack(cpu.P.readAsByte());

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

public class PushProcessorStatus extends Instruction {

    public PushProcessorStatus() {
        this.assemblyInstructionName = "PHP";
        this.addOperation(new PushProcessorStatusImplicit(AddressingMode.Implicit, (byte) 0x08, 1, 3));
    }
}
