package operations;

import nes.CPU;

class TransferAccumulatorYImplicit extends Operation {

    TransferAccumulatorYImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Transfers contents of A to Y and sets zero and negative flags appropriately
     *
     * Y=A
     */
    @Override
    public void execute(CPU cpu) {
        cpu.Y.writeByte(cpu.A.readAsByte());

        if (cpu.Y.readAsByte() == 0) {
            cpu.P.setZeroFlag();
        } else {
            cpu.P.clearZeroFlag();
        }

        if (cpu.Y.signBit()) {
            cpu.P.setNegativeFlag();
        } else {
            cpu.P.clearNegativeFlag();
        }

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }
}

public class TransferAccumulatorY extends Instruction {

    public TransferAccumulatorY() {
        this.assemblyInstructionName = "TAY";
        this.addOperation(new TransferAccumulatorYImplicit(AddressingMode.Implicit, (byte) 0xA8, 1, 2));
    }
}
