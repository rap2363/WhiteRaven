package operations;

import nes.CPU;

class TransferXAccumulatorImplicit extends Operation {

    TransferXAccumulatorImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Transfers contents of X to A and sets zero and negative flags appropriately
     *
     * A=X
     */
    @Override
    public void execute(CPU cpu) {
        cpu.A.writeByte(cpu.X.readAsByte());

        if (cpu.A.readAsByte() == 0) {
            cpu.P.setZeroFlag();
        } else {
            cpu.P.clearZeroFlag();
        }

        if (cpu.X.signBit()) {
            cpu.P.setNegativeFlag();
        } else {
            cpu.P.clearNegativeFlag();
        }

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }
}

public class TransferXAccumulator extends Instruction {

    public TransferXAccumulator() {
        this.assemblyInstructionName = "TXA";
        this.addOperation(new TransferXAccumulatorImplicit(AddressingMode.Implicit, (byte) 0x8A, 1, 2));
    }
}
