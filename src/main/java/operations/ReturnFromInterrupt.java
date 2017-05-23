package main.java.operations;

import main.java.nes.CPU;

class ReturnFromInterruptImplicit extends Operation {

    ReturnFromInterruptImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Pull a byte from the stack into the processor status register, then two bytes for the PC.
     */
    @Override
    public void execute(CPU cpu) {
        cpu.P.writeByte(cpu.pullFromStack());
        cpu.P.orByte((byte) 0x20);
        cpu.pullPCFromStack();

        cpu.cycleCount += cycles;
    }
}

public class ReturnFromInterrupt extends Instruction {

    public ReturnFromInterrupt() {
        this.assemblyInstructionName = "RTI";
        this.addOperation(new ReturnFromInterruptImplicit(AddressingMode.Implicit, (byte) 0x40, 1, 6));
    }
}
