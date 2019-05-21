package operations;

import nes.CPU;

abstract class IncrementMemoryOperationBase extends Operation {

    IncrementMemoryOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Increment a value in main.java.memory and set the zero and negative flags as appropriate
     *
     * Z,N,M = M + 1
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        int address = AddressingModeUtilities.getAddress(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        value = (byte) (value + (byte) 0x01);
        cpu.memory.write(address, value);
        boolean negativeFlag = value < 0;

        // Set the processor status flags
        if (value == 0) {
            cpu.P.setZeroFlag();
        } else {
            cpu.P.clearZeroFlag();
        }

        if (negativeFlag) {
            cpu.P.setNegativeFlag();
        } else {
            cpu.P.clearNegativeFlag();
        }

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }
}

class IncrementMemoryZeroPage extends IncrementMemoryOperationBase {
    public IncrementMemoryZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class IncrementMemoryZeroPageX extends IncrementMemoryOperationBase {
    public IncrementMemoryZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class IncrementMemoryAbsolute extends IncrementMemoryOperationBase {
    public IncrementMemoryAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class IncrementMemoryAbsoluteX extends IncrementMemoryOperationBase {
    public IncrementMemoryAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

public class IncrementMemory extends Instruction {

    public IncrementMemory() {
        this.assemblyInstructionName = "INC";
        this.addOperation(new IncrementMemoryZeroPage(AddressingMode.ZeroPage, (byte) 0xE6, 2, 5));
        this.addOperation(new IncrementMemoryZeroPageX(AddressingMode.ZeroPageX, (byte) 0xF6, 2, 6));
        this.addOperation(new IncrementMemoryAbsolute(AddressingMode.Absolute, (byte) 0xEE, 3, 6));
        this.addOperation(new IncrementMemoryAbsoluteX(AddressingMode.AbsoluteX, (byte) 0xFE, 3, 7));
    }
}
