package operations;

import nes.CPU;

class PushAccumulatorImplicit extends Operation {

    PushAccumulatorImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Push the accumulator register onto the stack
     */
    @Override
    public void execute(CPU cpu) {
        cpu.pushOntoStack(cpu.A.readAsByte());

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

public class PushAccumulator extends Instruction {

    public PushAccumulator() {
        this.assemblyInstructionName = "PHA";
        this.addOperation(new PushAccumulatorImplicit(AddressingMode.Implicit, (byte) 0x48, 1, 3));
    }
}
