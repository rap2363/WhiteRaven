package operations;

import nes.CPU;

abstract class DecrementMemoryOperationBase extends Operation {

    DecrementMemoryOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Decrement a value in memory and set the zero and negative flags as appropriate
     *
     * Z,N,M = M - 1
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        int address = AddressingModeUtilities.getAddress(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        value = (byte) (value - (byte) 0x01);
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

class DecrementMemoryZeroPage extends DecrementMemoryOperationBase {
    public DecrementMemoryZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class DecrementMemoryZeroPageX extends DecrementMemoryOperationBase {
    public DecrementMemoryZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class DecrementMemoryAbsolute extends DecrementMemoryOperationBase {
    public DecrementMemoryAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class DecrementMemoryAbsoluteX extends DecrementMemoryOperationBase {
    public DecrementMemoryAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

public class DecrementMemory extends Instruction {

    public DecrementMemory() {
        this.assemblyInstructionName = "DEC";
        this.addOperation(new DecrementMemoryZeroPage(AddressingMode.ZeroPage, (byte) 0xC6, 2, 5));
        this.addOperation(new DecrementMemoryZeroPageX(AddressingMode.ZeroPageX, (byte) 0xD6, 2, 6));
        this.addOperation(new DecrementMemoryAbsolute(AddressingMode.Absolute, (byte) 0xCE, 3, 6));
        this.addOperation(new DecrementMemoryAbsoluteX(AddressingMode.AbsoluteX, (byte) 0xDE, 3, 7));
    }
}
