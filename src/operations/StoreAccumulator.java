package operations;

import nes.CPU;

abstract class StoreAccumulatorOperationBase extends Operation {

    StoreAccumulatorOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Stores the accumulator byte at a location in memory
     *
     * M=A
     */
    @Override
    public void execute(CPU cpu) {
        int address = AddressingModeUtilities.getAddress(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        cpu.memory.write(address, cpu.A.readAsByte());

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

class StoreAccumulatorZeroPage extends StoreAccumulatorOperationBase {
    public StoreAccumulatorZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class StoreAccumulatorZeroPageX extends StoreAccumulatorOperationBase {
    public StoreAccumulatorZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class StoreAccumulatorAbsolute extends StoreAccumulatorOperationBase {
    public StoreAccumulatorAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class StoreAccumulatorAbsoluteX extends StoreAccumulatorOperationBase {
    public StoreAccumulatorAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class StoreAccumulatorAbsoluteY extends StoreAccumulatorOperationBase {
    public StoreAccumulatorAbsoluteY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class StoreAccumulatorIndirectX extends StoreAccumulatorOperationBase {
    public StoreAccumulatorIndirectX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class StoreAccumulatorIndirectY extends StoreAccumulatorOperationBase {
    public StoreAccumulatorIndirectY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

public class StoreAccumulator extends Instruction {
    public StoreAccumulator() {
        this.assemblyInstructionName = "STA";
        this.addOperation(new StoreAccumulatorZeroPage(AddressingMode.ZeroPage, (byte) 0x85, 2, 3));
        this.addOperation(new StoreAccumulatorZeroPageX(AddressingMode.ZeroPageX, (byte) 0x95, 2, 4));
        this.addOperation(new StoreAccumulatorAbsolute(AddressingMode.Absolute, (byte) 0x8D, 3, 4));
        this.addOperation(new StoreAccumulatorAbsoluteX(AddressingMode.AbsoluteX, (byte) 0x9D, 3, 5));
        this.addOperation(new StoreAccumulatorAbsoluteY(AddressingMode.AbsoluteY, (byte) 0x99, 3, 5));
        this.addOperation(new StoreAccumulatorIndirectX(AddressingMode.IndirectX, (byte) 0x81, 2, 6));
        this.addOperation(new StoreAccumulatorIndirectY(AddressingMode.IndirectY, (byte) 0x91, 2, 6));
    }
}
