package operations;

import snes.CPU;

class BranchIfNotEqualRelative extends Branch {
    public BranchIfNotEqualRelative(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    protected boolean branchCondition(CPU cpu) {
        return !cpu.P.zeroFlag();
    }
}

public class BranchIfNotEqual extends Instruction {
    public BranchIfNotEqual() {
        this.assemblyInstructionName = "BNE";
        this.addOperation(new BranchIfNotEqualRelative(AddressingMode.Relative, (byte) 0xD0, 2, 2));
    }
}
