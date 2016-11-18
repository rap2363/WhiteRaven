package operations;

import nes.CPU;

class BranchIfPositiveRelative extends Branch {
    public BranchIfPositiveRelative(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    protected boolean branchCondition(CPU cpu) {
        return !cpu.P.negativeFlag();
    }
}

public class BranchIfPositive extends Instruction {
    public BranchIfPositive() {
        this.assemblyInstructionName = "BPL";
        this.addOperation(new BranchIfPositiveRelative(AddressingMode.Relative, (byte) 0x10, 2, 2));
    }
}
