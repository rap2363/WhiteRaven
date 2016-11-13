package operations;

import snes.CPU;

abstract class CompareYOperationBase extends Operation {

    CompareYOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Compare the contents of the Y register with a memory held value and set zero, carry,
     * and negative flags appropriately. This sets the flags as if a subtraction had been carried
     * out, and the comparison is made between the bytes treating them as unsigned integers.
     * Example: #$FF > #$23
     *
     * Z,C,N = Y-M
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        byte subtraction = (byte) (cpu.Y.read() - value);
        boolean carryFlag = (cpu.Y.read() >= Utilities.toUnsignedValue(value));
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
        cpu.cycles += cycles;
    }
}

class CompareYImmediate extends CompareYOperationBase {
    public CompareYImmediate(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class CompareYZeroPage extends CompareYOperationBase {
    public CompareYZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}


class CompareYAbsolute extends CompareYOperationBase {
    public CompareYAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

public class CompareY extends Instruction {

    public CompareY() {
        this.assemblyInstructionName = "CPY";
        this.addOperation(new CompareYImmediate(AddressingMode.Immediate, (byte) 0xC0, 2, 2));
        this.addOperation(new CompareYZeroPage(AddressingMode.ZeroPage, (byte) 0xC4, 2, 3));
        this.addOperation(new CompareYAbsolute(AddressingMode.Absolute, (byte) 0xCC, 3, 4));
    }

}
