package operations;

import snes.CPU;

abstract class AddWithCarryOperationBase extends Operation {

    AddWithCarryOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressingMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    void baseExecute(CPU cpu, byte value) {
        boolean carryFlag = cpu.A.addByte(value, cpu.P.carryFlag());
        boolean overflowFlag = Utilities.getOverflowFlag((byte) cpu.A.read(), value, carryFlag);

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
    }
}

class AddWithCarryImmediate extends AddWithCarryOperationBase {
    public AddWithCarryImmediate(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    public void execute(CPU cpu) {
        final int numBytes = 2;
        byte[] bytes = cpu.readAtPC(numBytes);
        byte value = bytes[1];

        baseExecute(cpu, value);

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += 2;
    }
}

class AddWithCarryZeroPage extends AddWithCarryOperationBase {
    public AddWithCarryZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    public void execute(CPU cpu) {
        final int numBytes = 2;
        byte[] bytes = cpu.readAtPC(numBytes);
        byte value = cpu.memory.read(Utilities.toUnsignedValue(bytes[1]));

        baseExecute(cpu, value);

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += 3;
    }
}

class AddWithCarryZeroPageX extends AddWithCarryOperationBase {
    public AddWithCarryZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    public void execute(CPU cpu) {
        final int numBytes = 2;
        byte[] bytes = cpu.readAtPC(numBytes);
        byte value = cpu.memory.read(Utilities.toUnsignedValue((byte) (bytes[1] + cpu.X.readAsByte())));

        baseExecute(cpu, value);

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += 4;
    }
}

class AddWithCarryAbsolute extends AddWithCarryOperationBase {
    public AddWithCarryAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    public void execute(CPU cpu) {
        final int numBytes = 3;
        byte[] bytes = cpu.readAtPC(numBytes);
        byte value = cpu.memory.read(Utilities.toUnsignedValue(bytes[1], bytes[2]));

        baseExecute(cpu, value);

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += 4;
    }
}

class AddWithCarryAbsoluteX extends AddWithCarryOperationBase {
    public AddWithCarryAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    public void execute(CPU cpu) {
        final int numBytes = 3;
        byte[] bytes = cpu.readAtPC(numBytes);
        byte value = cpu.memory.read(
                Utilities.addByteToUnsignedInt(Utilities.toUnsignedValue(bytes[1], bytes[2]), cpu.X.readAsByte()));

        baseExecute(cpu, value);

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += 4;
        if (Utilities.getOverflowFlag(bytes[2], cpu.X.readAsByte(), false)) {
            cpu.cycles += 1;
        }
    }
}

class AddWithCarryAbsoluteY extends AddWithCarryOperationBase {
    public AddWithCarryAbsoluteY(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    public void execute(CPU cpu) {
        final int numBytes = 3;
        byte[] bytes = cpu.readAtPC(numBytes);
        byte value = cpu.memory.read(
                Utilities.addByteToUnsignedInt(Utilities.toUnsignedValue(bytes[1], bytes[2]), cpu.Y.readAsByte()));

        baseExecute(cpu, value);

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += 4;
        if (Utilities.getOverflowFlag(bytes[2], cpu.Y.readAsByte(), false)) {
            cpu.cycles += 1;
        }
    }
}

class AddWithCarryIndirectX extends AddWithCarryOperationBase {
    public AddWithCarryIndirectX(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    public void execute(CPU cpu) {

    }
}

class AddWithCarryIndirectY extends AddWithCarryOperationBase {
    public AddWithCarryIndirectY(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    public void execute(CPU cpu) {

    }
}

public class AddWithCarry extends Instruction {
    public AddWithCarry() {
        this.addOperation(new AddWithCarryImmediate(AddressingMode.Immediate, (byte) 0x69, 2, 2, false));
        this.addOperation(new AddWithCarryZeroPage(AddressingMode.ZeroPage, (byte) 0x65, 2, 3, false));
        this.addOperation(new AddWithCarryZeroPageX(AddressingMode.ZeroPageX, (byte) 0x75, 2, 4, false));
        this.addOperation(new AddWithCarryAbsolute(AddressingMode.Absolute, (byte) 0x6D, 3, 4, false));
        this.addOperation(new AddWithCarryAbsoluteX(AddressingMode.AbsoluteX, (byte) 0x7D, 3, 4, true));
        this.addOperation(new AddWithCarryAbsoluteY(AddressingMode.AbsoluteY, (byte) 0x79, 3, 4, true));
        this.addOperation(new AddWithCarryIndirectX(AddressingMode.IndirectX, (byte) 0x61, 2, 6, false));
        this.addOperation(new AddWithCarryIndirectY(AddressingMode.IndirectY, (byte) 0x71, 2, 5, true));
    }
}