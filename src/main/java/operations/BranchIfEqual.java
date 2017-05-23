package main.java.operations;

import main.java.nes.CPU;

class BranchIfEqualRelative extends Branch {
    public BranchIfEqualRelative(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    protected boolean branchCondition(CPU cpu) {
        return cpu.P.zeroFlag();
    }
}

public class BranchIfEqual extends Instruction {
    public BranchIfEqual() {
        this.assemblyInstructionName = "BEQ";
        this.addOperation(new BranchIfEqualRelative(AddressingMode.Relative, (byte) 0xF0, 2, 2));
    }
}
