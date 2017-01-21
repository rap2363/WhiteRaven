package operations;

import nes.CPU;

class TransferXStackPointerImplicit extends Operation {

    TransferXStackPointerImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Transfers contents of X to SP
     *
     * SP=X
     */
    @Override
    public void execute(CPU cpu) {
        cpu.SP.writeByte(cpu.X.readAsByte());

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }
}

public class TransferXStackPointer extends Instruction {

    public TransferXStackPointer() {
        this.assemblyInstructionName = "TXS";
        this.addOperation(new TransferXStackPointerImplicit(AddressingMode.Implicit, (byte) 0x9A, 1, 2));
    }
}
