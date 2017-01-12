package operations;

import nes.CPU;

abstract class SubtractWithCarryOperationBase extends Operation {

    SubtractWithCarryOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * This instruction subtracts the contents of a memory location to the accumulator
     * together with the not of the carry bit. If overflow occurs the carry bit is clear,
     * this enables multiple byte subtraction to be performed.
     *
     * A,Z,C,N = A-M-(1-C)
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        byte oldA = cpu.A.readAsByte();

        boolean carryFlag = !cpu.A.subtractByte(value, cpu.P.carryFlag());

        // Underflow if we were negative and subtracted a positive number to go positive
        boolean underflowFlag = oldA < 0 && value >= 0 && cpu.A.readAsByte() >= 0;

        // Set the processor status flags
        if (underflowFlag) {
            cpu.P.setOverflowFlag();
            cpu.P.clearCarryFlag();
        } else {
            cpu.P.clearOverflowFlag();
        }

        if (carryFlag) {
            cpu.P.setCarryFlag();
        } else {
            cpu.P.clearCarryFlag();
        }

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

class SubtractWithCarryImmediate extends SubtractWithCarryOperationBase {
    public SubtractWithCarryImmediate(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class SubtractWithCarryZeroPage extends SubtractWithCarryOperationBase {
    public SubtractWithCarryZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class SubtractWithCarryZeroPageX extends SubtractWithCarryOperationBase {
    public SubtractWithCarryZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class SubtractWithCarryAbsolute extends SubtractWithCarryOperationBase {
    public SubtractWithCarryAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class SubtractWithCarryAbsoluteX extends SubtractWithCarryOperationBase {
    public SubtractWithCarryAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
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

class SubtractWithCarryAbsoluteY extends SubtractWithCarryOperationBase {
    public SubtractWithCarryAbsoluteY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
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

class SubtractWithCarryIndirectX extends SubtractWithCarryOperationBase {
    public SubtractWithCarryIndirectX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class SubtractWithCarryIndirectY extends SubtractWithCarryOperationBase {
    public SubtractWithCarryIndirectY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
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

public class SubtractWithCarry extends Instruction {
    public SubtractWithCarry() {
        this.assemblyInstructionName = "SBC";
        this.addOperation(new SubtractWithCarryImmediate(AddressingMode.Immediate, (byte) 0xE9, 2, 2));
        this.addOperation(new SubtractWithCarryZeroPage(AddressingMode.ZeroPage, (byte) 0xE5, 2, 3));
        this.addOperation(new SubtractWithCarryZeroPageX(AddressingMode.ZeroPageX, (byte) 0xF5, 2, 4));
        this.addOperation(new SubtractWithCarryAbsolute(AddressingMode.Absolute, (byte) 0xED, 3, 4));
        this.addOperation(new SubtractWithCarryAbsoluteX(AddressingMode.AbsoluteX, (byte) 0xFD, 3, 4));
        this.addOperation(new SubtractWithCarryAbsoluteY(AddressingMode.AbsoluteY, (byte) 0xF9, 3, 4));
        this.addOperation(new SubtractWithCarryIndirectX(AddressingMode.IndirectX, (byte) 0xE1, 2, 6));
        this.addOperation(new SubtractWithCarryIndirectY(AddressingMode.IndirectY, (byte) 0xF1, 2, 5));
    }
}
