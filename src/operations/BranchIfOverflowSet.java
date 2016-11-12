package operations;

import snes.CPU;

class BranchIfOverflowSetRelative extends Branch {
    public BranchIfOverflowSetRelative(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    protected boolean branchCondition(CPU cpu) {
        return cpu.P.overflowFlag();
    }
}

public class BranchIfOverflowSet extends Instruction {
    public BranchIfOverflowSet() {
        this.assemblyInstructionName = "BVS";
        this.addOperation(new BranchIfOverflowSetRelative(AddressingMode.Relative, (byte) 0x70, 2, 2));
    }
}
