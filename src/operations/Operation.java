package operations;

import snes.CPU;

public abstract class Operation {
    AddressingMode addressingMode;
    byte opcode;
    int numBytes;
    int cycles;
    boolean pageCrossedCycle;

    Operation(AddressingMode addressingMode, byte opcode, int numBytes, int cycles, boolean pageCrossedCycle) {
        this.addressingMode = addressingMode;
        this.opcode = opcode;
        this.numBytes = numBytes;
        this.cycles = cycles;
        this.pageCrossedCycle = pageCrossedCycle;
    }

    /**
     * Execute the instruction on the CPU
     * 
     * @param cpu
     */
    public abstract void execute(CPU cpu);
}