package operations;

import nes.CPU;

class TransferYAccumulatorImplicit extends Operation {

    TransferYAccumulatorImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Transfers contents of Y to A and sets zero and negative flags appropriately
     *
     * A=Y
     */
    @Override
    public void execute(CPU cpu) {
        cpu.A.writeByte(cpu.Y.readAsByte());

        if (cpu.A.readAsByte() == 0) {
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
        cpu.cycles += cycles;
    }
}

public class TransferYAccumulator extends Instruction {

    public TransferYAccumulator() {
        this.assemblyInstructionName = "TYA";
        this.addOperation(new TransferYAccumulatorImplicit(AddressingMode.Implicit, (byte) 0x98, 1, 2));
    }
}
