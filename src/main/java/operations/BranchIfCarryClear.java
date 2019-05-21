package operations;

import nes.CPU;

class BranchIfCarryClearRelative extends Branch {
    public BranchIfCarryClearRelative(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    protected boolean branchCondition(CPU cpu) {
        return !cpu.P.carryFlag();
    }
}

public class BranchIfCarryClear extends Instruction {
    public BranchIfCarryClear() {
        this.assemblyInstructionName = "BCC";
        this.addOperation(new BranchIfCarryClearRelative(AddressingMode.Relative, (byte) 0x90, 2, 2));
    }
}
