package operations;

import snes.CPU;

class TransferStackPointerXImplicit extends Operation {

    TransferStackPointerXImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Transfers contents of SP to X and sets zero and negative flags appropriately
     *
     * X=SP
     */
    @Override
    public void execute(CPU cpu) {
        cpu.X.writeByte(cpu.SP.readAsByte());

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

public class TransferStackPointerX extends Instruction {

    public TransferStackPointerX() {
        this.assemblyInstructionName = "TSX";
        this.addOperation(new TransferStackPointerXImplicit(AddressingMode.Implicit, (byte) 0xBA, 1, 2));
    }
}
