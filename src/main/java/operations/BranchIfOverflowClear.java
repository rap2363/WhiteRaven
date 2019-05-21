package operations;

import nes.CPU;

class BranchIfOverflowClearRelative extends Branch {
    public BranchIfOverflowClearRelative(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    protected boolean branchCondition(CPU cpu) {
        return !cpu.P.overflowFlag();
    }
}

public class BranchIfOverflowClear extends Instruction {
    public BranchIfOverflowClear() {
        this.assemblyInstructionName = "BVC";
        this.addOperation(new BranchIfOverflowClearRelative(AddressingMode.Relative, (byte) 0x50, 2, 2));
    }
}
