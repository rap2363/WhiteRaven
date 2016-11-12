package operations;

import snes.CPU;

abstract class AddWithCarryOperationBase extends Operation {

    AddWithCarryOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    protected void baseExecute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes));
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
    public AddWithCarryImmediate(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        baseExecute(cpu);

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

class AddWithCarryZeroPage extends AddWithCarryOperationBase {
    public AddWithCarryZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        baseExecute(cpu);

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

class AddWithCarryZeroPageX extends AddWithCarryOperationBase {
    public AddWithCarryZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        baseExecute(cpu);

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

class AddWithCarryAbsolute extends AddWithCarryOperationBase {
    public AddWithCarryAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        baseExecute(cpu);

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

class AddWithCarryAbsoluteX extends AddWithCarryOperationBase {
    public AddWithCarryAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        baseExecute(cpu);

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;

        byte[] bytes = cpu.readAfterPC(numBytes);
        if (Utilities.getOverflowFlag(bytes[1], cpu.X.readAsByte(), false)) {
            cpu.cycles++;
        }
    }
}

class AddWithCarryAbsoluteY extends AddWithCarryOperationBase {
    public AddWithCarryAbsoluteY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        baseExecute(cpu);

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;

        byte[] bytes = cpu.readAfterPC(numBytes);
        if (Utilities.getOverflowFlag(bytes[1], cpu.Y.readAsByte(), false)) {
            cpu.cycles++;
        }
    }
}

class AddWithCarryIndirectX extends AddWithCarryOperationBase {
    public AddWithCarryIndirectX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        baseExecute(cpu);

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

class AddWithCarryIndirectY extends AddWithCarryOperationBase {
    public AddWithCarryIndirectY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        baseExecute(cpu);

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;

        byte[] bytes = cpu.readAfterPC(numBytes);
        int targetAddress = Utilities.toUnsignedValue(bytes[0]);
        byte low = cpu.memory.read(targetAddress);
        if (Utilities.getOverflowFlag(low, cpu.Y.readAsByte(), false)) {
            cpu.cycles++;
        }
    }
}

public class AddWithCarry extends Instruction {
    public AddWithCarry() {
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