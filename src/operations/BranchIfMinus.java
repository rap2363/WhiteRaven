package operations;

import nes.CPU;

class BranchIfMinusRelative extends Branch {
    public BranchIfMinusRelative(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    protected boolean branchCondition(CPU cpu) {
        return cpu.P.negativeFlag();
    }
}

public class BranchIfMinus extends Instruction {
    public BranchIfMinus() {
        this.assemblyInstructionName = "BMI";
        this.addOperation(new BranchIfMinusRelative(AddressingMode.Relative, (byte) 0x30, 2, 2));
    }
}
