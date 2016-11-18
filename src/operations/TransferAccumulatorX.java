package operations;

import nes.CPU;

class TransferAccumulatorXImplicit extends Operation {

    TransferAccumulatorXImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Transfers contents of A to X and sets zero and negative flags appropriately
     *
     * X=A
     */
    @Override
    public void execute(CPU cpu) {
        cpu.X.writeByte(cpu.A.readAsByte());

        if (cpu.X.readAsByte() == 0) {
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
        cpu.cycles += cycles;
    }
}

public class TransferAccumulatorX extends Instruction {

    public TransferAccumulatorX() {
        this.assemblyInstructionName = "TAX";
        this.addOperation(new TransferAccumulatorXImplicit(AddressingMode.Implicit, (byte) 0xAA, 1, 2));
    }
}
