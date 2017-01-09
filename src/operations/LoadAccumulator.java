package operations;

import nes.CPU;

abstract class LoadAccumulatorOperationBase extends Operation {

    LoadAccumulatorOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Load a byte of memory into the accumulator and set the zero/negative flags appropriately
     *
     * A,Z,N = M
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        cpu.A.writeByte(value);

        // Set the processor status flags
        if (cpu.A.read() == 0) {
            cpu.P.setZeroFlag();
        } else {
            cpu.P.clearZeroFlag();
        }

        if (cpu.A.signBit()) {
            cpu.P.setNegativeFlag();
        } else {
            cpu.P.clearNegativeFlag();
        }

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

class LoadAccumulatorImmediate extends LoadAccumulatorOperationBase {
    public LoadAccumulatorImmediate(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class LoadAccumulatorZeroPage extends LoadAccumulatorOperationBase {
    public LoadAccumulatorZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class LoadAccumulatorZeroPageX extends LoadAccumulatorOperationBase {
    public LoadAccumulatorZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class LoadAccumulatorAbsolute extends LoadAccumulatorOperationBase {
    public LoadAccumulatorAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class LoadAccumulatorAbsoluteX extends LoadAccumulatorOperationBase {
    public LoadAccumulatorAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        super.execute(cpu);

        byte[] bytes = cpu.readAfterPC(numBytes - 1);
        if (Utilities.getOverflowFlag(bytes[1], cpu.X.readAsByte(), false)) {
            cpu.cycles++;
        }
    }
}

class LoadAccumulatorAbsoluteY extends LoadAccumulatorOperationBase {
    public LoadAccumulatorAbsoluteY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        super.execute(cpu);

        byte[] bytes = cpu.readAfterPC(numBytes - 1);
        if (Utilities.getOverflowFlag(bytes[1], cpu.Y.readAsByte(), false)) {
            cpu.cycles++;
        }
    }
}

class LoadAccumulatorIndirectX extends LoadAccumulatorOperationBase {
    public LoadAccumulatorIndirectX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        super.execute(cpu);

        byte[] bytes = cpu.readAfterPC(numBytes - 1);
        int targetAddress = Utilities.toUnsignedValue(bytes[0]);
        byte low = cpu.memory.read(targetAddress);
        if (Utilities.getOverflowFlag(low, cpu.X.readAsByte(), false)) {
            cpu.cycles++;
        }
    }
}

class LoadAccumulatorIndirectY extends LoadAccumulatorOperationBase {
    public LoadAccumulatorIndirectY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        super.execute(cpu);

        byte[] bytes = cpu.readAfterPC(numBytes - 1);
        int targetAddress = Utilities.toUnsignedValue(bytes[0]);
        byte low = cpu.memory.read(targetAddress);
        if (Utilities.getOverflowFlag(low, cpu.Y.readAsByte(), false)) {
            cpu.cycles++;
        }
    }
}

public class LoadAccumulator extends Instruction {
    public LoadAccumulator() {
        this.assemblyInstructionName = "LDA";
        this.addOperation(new LoadAccumulatorImmediate(AddressingMode.Immediate, (byte) 0xA9, 2, 2));
        this.addOperation(new LoadAccumulatorZeroPage(AddressingMode.ZeroPage, (byte) 0xA5, 2, 3));
        this.addOperation(new LoadAccumulatorZeroPageX(AddressingMode.ZeroPageX, (byte) 0xB5, 2, 4));
        this.addOperation(new LoadAccumulatorAbsolute(AddressingMode.Absolute, (byte) 0xAD, 3, 4));
        this.addOperation(new LoadAccumulatorAbsoluteX(AddressingMode.AbsoluteX, (byte) 0xBD, 3, 4));
        this.addOperation(new LoadAccumulatorAbsoluteY(AddressingMode.AbsoluteY, (byte) 0xB9, 3, 4));
        this.addOperation(new LoadAccumulatorIndirectX(AddressingMode.IndirectX, (byte) 0xA1, 2, 6));
        this.addOperation(new LoadAccumulatorIndirectY(AddressingMode.IndirectY, (byte) 0xB1, 2, 5));
    }
}
