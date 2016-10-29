package operations;

import java.util.LinkedList;
import java.util.List;

import snes.CPU;

abstract class Operation {
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
    abstract void execute(CPU cpu);
}

public abstract class Instruction {
    List<Operation> operations;

    Instruction() {
        this.operations = new LinkedList<Operation>();
    }

    public void addOperation(Operation operation) {
        this.operations.add(operation);
    }
}
