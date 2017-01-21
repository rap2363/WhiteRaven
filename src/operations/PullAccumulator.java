package operations;

import nes.CPU;

class PullAccumulatorImplicit extends Operation {

    PullAccumulatorImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Pull the accumulator register from the stack. Set the zero flag and negative flags according to A's value.
     */
    @Override
    public void execute(CPU cpu) {
        cpu.A.writeByte(cpu.pullFromStack());

        if (cpu.A.read() == 0) {
            cpu.P.setZeroFlag();
        } else {
            cpu.P.clearZeroFlag();
        }

        if (cpu.A.signBit()) {
            cpu.P.setNegativeFlag();
        } else {
            cpu.P.clearNegativeFlag();
        }

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }
}

public class PullAccumulator extends Instruction {

    public PullAccumulator() {
        this.assemblyInstructionName = "PLA";
        this.addOperation(new PullAccumulatorImplicit(AddressingMode.Implicit, (byte) 0x68, 1, 4));
    }
}
