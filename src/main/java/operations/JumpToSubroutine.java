package main.java.operations;

import main.java.nes.CPU;

class JumpToSubroutineImplicit extends Operation {

    JumpToSubroutineImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * The JSR instruction loads a new address for the PC (the start of a subroutine), and pushes the PC and P onto
     * the stack. At the end of the subroutine, RTS (return from subroutine) is called, which restores the previously
     * pushed PC. This does NOT push the Processor status onto the stack
     *
     */
    @Override
    public void execute(CPU cpu) {
        int targetAddress = AddressingModeUtilities.getAddress(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        cpu.PC.incrementBy(numBytes - 1);
        cpu.pushPCOntoStack();

        cpu.PC.write(targetAddress);

        cpu.cycleCount += cycles;
    }
}

public class JumpToSubroutine extends Instruction {

    public JumpToSubroutine() {
        this.assemblyInstructionName = "JSR";
        this.addOperation(new JumpToSubroutineImplicit(AddressingMode.Absolute, (byte) 0x20, 3, 6));
    }
}
