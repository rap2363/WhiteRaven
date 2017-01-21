package operations;

import nes.CPU;

class PushProcessorStatusImplicit extends Operation {

    PushProcessorStatusImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Push the processor status register onto the stack.
     * Note, this copies the six flags over, but sets bits 4 and 5 in the copy, which are technically not flags.
     */
    @Override
    public void execute(CPU cpu) {
        byte value = cpu.P.readAsByte();
        value |= 0x10;

        cpu.pushOntoStack(value);
        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }
}

public class PushProcessorStatus extends Instruction {

    public PushProcessorStatus() {
        this.assemblyInstructionName = "PHP";
        this.addOperation(new PushProcessorStatusImplicit(AddressingMode.Implicit, (byte) 0x08, 1, 3));
    }
}
