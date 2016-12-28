package operations;

import nes.CPU;

class ReturnFromSubroutineImplicit extends Operation {

    ReturnFromSubroutineImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Pull the program counter from the stack.
     *
     */
    @Override
    public void execute(CPU cpu) {
        cpu.pullPCFromStack();

        cpu.cycles += cycles;
    }
}

public class ReturnFromSubroutine extends Instruction {

    public ReturnFromSubroutine() {
        this.assemblyInstructionName = "RTS";
        this.addOperation(new ReturnFromSubroutineImplicit(AddressingMode.Implicit, (byte) 0x60, 1, 6));
    }
}
