package operations;

import nes.CPU;

abstract class XorOperationBase extends BooleanLogicOperation {

    XorOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    @Override
    protected void operation(CPU cpu, byte value) {
        cpu.A.xorByte(value);
    }
}

class XorImmediate extends XorOperationBase {
    public XorImmediate(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class XorZeroPage extends XorOperationBase {
    public XorZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class XorZeroPageX extends XorOperationBase {
    public XorZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class XorAbsolute extends XorOperationBase {
    public XorAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class XorAbsoluteX extends XorOperationBase {
    public XorAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
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

class XorAbsoluteY extends XorOperationBase {
    public XorAbsoluteY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
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

class XorIndirectX extends XorOperationBase {
    public XorIndirectX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class XorIndirectY extends XorOperationBase {
    public XorIndirectY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
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

public class LogicalXOR extends Instruction {

    public LogicalXOR() {
        this.assemblyInstructionName = "EOR";
        this.addOperation(new XorImmediate(AddressingMode.Immediate, (byte) 0x49, 2, 2));
        this.addOperation(new XorZeroPage(AddressingMode.ZeroPage, (byte) 0x45, 2, 3));
        this.addOperation(new XorZeroPageX(AddressingMode.ZeroPageX, (byte) 0x55, 2, 4));
        this.addOperation(new XorAbsolute(AddressingMode.Absolute, (byte) 0x4D, 3, 4));
        this.addOperation(new XorAbsoluteX(AddressingMode.AbsoluteX, (byte) 0x5D, 3, 4));
        this.addOperation(new XorAbsoluteY(AddressingMode.AbsoluteY, (byte) 0x59, 3, 4));
        this.addOperation(new XorIndirectX(AddressingMode.IndirectX, (byte) 0x41, 2, 6));
        this.addOperation(new XorIndirectY(AddressingMode.IndirectY, (byte) 0x51, 2, 5));
    }

}
