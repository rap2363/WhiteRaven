package operations;

import snes.CPU;

abstract class OrOperationBase extends BooleanLogicOperation {

    OrOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    @Override
    protected void operation(CPU cpu, byte value) {
        cpu.A.orByte(value);
    }
}

class OrImmediate extends OrOperationBase {
    public OrImmediate(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class OrZeroPage extends OrOperationBase {
    public OrZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class OrZeroPageX extends OrOperationBase {
    public OrZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class OrAbsolute extends OrOperationBase {
    public OrAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class OrAbsoluteX extends OrOperationBase {
    public OrAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
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

class OrAbsoluteY extends OrOperationBase {
    public OrAbsoluteY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
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

class OrIndirectX extends OrOperationBase {
    public OrIndirectX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class OrIndirectY extends OrOperationBase {
    public OrIndirectY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
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

public class LogicalOR extends Instruction {

    public LogicalOR() {
        this.assemblyInstructionName = "ORA";
        this.addOperation(new OrImmediate(AddressingMode.Immediate, (byte) 0x09, 2, 2));
        this.addOperation(new OrZeroPage(AddressingMode.ZeroPage, (byte) 0x05, 2, 3));
        this.addOperation(new OrZeroPageX(AddressingMode.ZeroPageX, (byte) 0x15, 2, 4));
        this.addOperation(new OrAbsolute(AddressingMode.Absolute, (byte) 0x0D, 3, 4));
        this.addOperation(new OrAbsoluteX(AddressingMode.AbsoluteX, (byte) 0x1D, 3, 4));
        this.addOperation(new OrAbsoluteY(AddressingMode.AbsoluteY, (byte) 0x19, 3, 4));
        this.addOperation(new OrIndirectX(AddressingMode.IndirectX, (byte) 0x01, 2, 6));
        this.addOperation(new OrIndirectY(AddressingMode.IndirectY, (byte) 0x11, 2, 5));
    }

}
