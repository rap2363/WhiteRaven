package operations;

import nes.CPU;

abstract class AddWithCarryOperationBase extends Operation {

    AddWithCarryOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Add with Carry: The instruction adds the contents of a main.java.memory location to
     * the accumulator together with the carry bit. If overflow occurs the carry
     * bit is set, this enables multiple byte addition to be performed.
     *
     * A,Z,C,N = A+M+C
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        byte oldAValue = cpu.A.readAsByte();
        boolean carryFlag = cpu.A.addByte(value, cpu.P.carryFlag());
        boolean overflowFlag = Utilities.getOverflowFlag(oldAValue, value, carryFlag);

        // Set the processor status flags
        if (overflowFlag) {
            cpu.P.setOverflowFlag();
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
        cpu.cycleCount += cycles;
    }
}

class AddWithCarryImmediate extends AddWithCarryOperationBase {
    public AddWithCarryImmediate(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class AddWithCarryZeroPage extends AddWithCarryOperationBase {
    public AddWithCarryZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class AddWithCarryZeroPageX extends AddWithCarryOperationBase {
    public AddWithCarryZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class AddWithCarryAbsolute extends AddWithCarryOperationBase {
    public AddWithCarryAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class AddWithCarryAbsoluteX extends AddWithCarryOperationBase {
    public AddWithCarryAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        super.execute(cpu);

        byte[] bytes = cpu.readAfterPC(numBytes - 1);
        if (Utilities.getOverflowFlag(bytes[1], cpu.X.readAsByte(), false)) {
            cpu.cycleCount++;
        }
    }
}

class AddWithCarryAbsoluteY extends AddWithCarryOperationBase {
    public AddWithCarryAbsoluteY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        super.execute(cpu);

        byte[] bytes = cpu.readAfterPC(numBytes - 1);
        if (Utilities.getOverflowFlag(bytes[1], cpu.Y.readAsByte(), false)) {
            cpu.cycleCount++;
        }
    }
}

class AddWithCarryIndirectX extends AddWithCarryOperationBase {
    public AddWithCarryIndirectX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class AddWithCarryIndirectY extends AddWithCarryOperationBase {
    public AddWithCarryIndirectY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        super.execute(cpu);

        byte[] bytes = cpu.readAfterPC(numBytes - 1);
        int targetAddress = Utilities.toUnsignedValue(bytes[0]);
        byte low = cpu.memory.read(targetAddress);
        if (Utilities.getOverflowFlag(low, cpu.Y.readAsByte(), false)) {
            cpu.cycleCount++;
        }
    }
}

public class AddWithCarry extends Instruction {
    public AddWithCarry() {
        this.assemblyInstructionName = "ADC";
        this.addOperation(new AddWithCarryImmediate(AddressingMode.Immediate, (byte) 0x69, 2, 2));
        this.addOperation(new AddWithCarryZeroPage(AddressingMode.ZeroPage, (byte) 0x65, 2, 3));
        this.addOperation(new AddWithCarryZeroPageX(AddressingMode.ZeroPageX, (byte) 0x75, 2, 4));
        this.addOperation(new AddWithCarryAbsolute(AddressingMode.Absolute, (byte) 0x6D, 3, 4));
        this.addOperation(new AddWithCarryAbsoluteX(AddressingMode.AbsoluteX, (byte) 0x7D, 3, 4));
        this.addOperation(new AddWithCarryAbsoluteY(AddressingMode.AbsoluteY, (byte) 0x79, 3, 4));
        this.addOperation(new AddWithCarryIndirectX(AddressingMode.IndirectX, (byte) 0x61, 2, 6));
        this.addOperation(new AddWithCarryIndirectY(AddressingMode.IndirectY, (byte) 0x71, 2, 5));
    }
}
