package operations;

import snes.CPU;

class BranchIfCarrySetRelative extends Branch {
    public BranchIfCarrySetRelative(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    protected boolean branchCondition(CPU cpu) {
        return cpu.P.carryFlag();
    }
}

public class BranchIfCarrySet extends Instruction {
    public BranchIfCarrySet() {
        this.assemblyInstructionName = "BCS";
        this.addOperation(new BranchIfCarrySetRelative(AddressingMode.Relative, (byte) 0xB0, 2, 2));
    }
}
