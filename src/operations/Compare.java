package operations;

import nes.CPU;

abstract class CompareOperationBase extends Operation {

    CompareOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Compare the contents of the accumulator with a memory held value and set the zero
     * carry flags appropriately. This sets the flags as if a subtraction had been carried
     * out, and the comparison is made between the bytes treating them as unsigned integers.
     * Example: #$FF > #$23
     *
     * Z,C,N = A-M
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        byte subtraction = (byte) (cpu.A.read() - value);
        boolean carryFlag = (cpu.A.read() >= Utilities.toUnsignedValue(value));
        boolean negativeFlag = subtraction < 0;

        // Set the processor status flags
        if (carryFlag) {
            cpu.P.setCarryFlag();
        } else {
            cpu.P.clearCarryFlag();
        }

        if (subtraction == 0) {
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

class CompareImmediate extends CompareOperationBase {
    public CompareImmediate(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class CompareZeroPage extends CompareOperationBase {
    public CompareZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class CompareZeroPageX extends CompareOperationBase {
    public CompareZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class CompareAbsolute extends CompareOperationBase {
    public CompareAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class CompareAbsoluteX extends CompareOperationBase {
    public CompareAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
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

class CompareAbsoluteY extends CompareOperationBase {
    public CompareAbsoluteY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
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

class CompareIndirectX extends CompareOperationBase {
    public CompareIndirectX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class CompareIndirectY extends CompareOperationBase {
    public CompareIndirectY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
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

public class Compare extends Instruction {

    public Compare() {
        this.assemblyInstructionName = "CMP";
        this.addOperation(new CompareImmediate(AddressingMode.Immediate, (byte) 0xC9, 2, 2));
        this.addOperation(new CompareZeroPage(AddressingMode.ZeroPage, (byte) 0xC5, 2, 3));
        this.addOperation(new CompareZeroPageX(AddressingMode.ZeroPageX, (byte) 0xD5, 2, 4));
        this.addOperation(new CompareAbsolute(AddressingMode.Absolute, (byte) 0xCD, 3, 4));
        this.addOperation(new CompareAbsoluteX(AddressingMode.AbsoluteX, (byte) 0xDD, 3, 4));
        this.addOperation(new CompareAbsoluteY(AddressingMode.AbsoluteY, (byte) 0xD9, 3, 4));
        this.addOperation(new CompareIndirectX(AddressingMode.IndirectX, (byte) 0xC1, 2, 6));
        this.addOperation(new CompareIndirectY(AddressingMode.IndirectY, (byte) 0xD1, 2, 5));
    }

}
