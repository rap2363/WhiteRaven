package main.java.operations;

import main.java.nes.CPU;

abstract class AndOperationBase extends BooleanLogicOperation {

    AndOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    @Override
    protected void operation(CPU cpu, byte value) {
        cpu.A.andByte(value);
    }
}

class AndImmediate extends AndOperationBase {
    public AndImmediate(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class AndZeroPage extends AndOperationBase {
    public AndZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class AndZeroPageX extends AndOperationBase {
    public AndZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class AndAbsolute extends AndOperationBase {
    public AndAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class AndAbsoluteX extends AndOperationBase {
    public AndAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
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

class AndAbsoluteY extends AndOperationBase {
    public AndAbsoluteY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
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

class AndIndirectX extends AndOperationBase {
    public AndIndirectX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class AndIndirectY extends AndOperationBase {
    public AndIndirectY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
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

public class LogicalAND extends Instruction {

    public LogicalAND() {
        this.assemblyInstructionName = "AND";
        this.addOperation(new AndImmediate(AddressingMode.Immediate, (byte) 0x29, 2, 2));
        this.addOperation(new AndZeroPage(AddressingMode.ZeroPage, (byte) 0x25, 2, 3));
        this.addOperation(new AndZeroPageX(AddressingMode.ZeroPageX, (byte) 0x35, 2, 4));
        this.addOperation(new AndAbsolute(AddressingMode.Absolute, (byte) 0x2D, 3, 4));
        this.addOperation(new AndAbsoluteX(AddressingMode.AbsoluteX, (byte) 0x3D, 3, 4));
        this.addOperation(new AndAbsoluteY(AddressingMode.AbsoluteY, (byte) 0x39, 3, 4));
        this.addOperation(new AndIndirectX(AddressingMode.IndirectX, (byte) 0x21, 2, 6));
        this.addOperation(new AndIndirectY(AddressingMode.IndirectY, (byte) 0x31, 2, 5));
    }

}
