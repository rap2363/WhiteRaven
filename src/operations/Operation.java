package operations;

import nes.CPU;

public abstract class Operation {
    public AddressingMode addressingMode;
    public byte opcode;
    public int numBytes;
    public int cycles;

    Operation(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        this.addressingMode = addressingMode;
        this.opcode = opcode;
        this.numBytes = numBytes;
        this.cycles = cycles;
    }

    /**
     * Execute the instruction on the CPU
     *
     * @param cpu
     */
    public abstract void execute(CPU cpu);
}
