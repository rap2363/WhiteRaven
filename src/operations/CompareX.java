package operations;

import nes.CPU;

abstract class CompareXOperationBase extends Operation {

    CompareXOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Compare the contents of the X register with a memory held value and set zero, carry,
     * and negative flags appropriately. This sets the flags as if a subtraction had been carried
     * out, and the comparison is made between the bytes treating them as unsigned integers.
     * Example: #$FF > #$23
     *
     * Z,C,N = X-M
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        byte subtraction = (byte) (cpu.X.read() - value);
        boolean carryFlag = (cpu.X.read() >= Utilities.toUnsignedValue(value));
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

class CompareXImmediate extends CompareXOperationBase {
    public CompareXImmediate(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class CompareXZeroPage extends CompareXOperationBase {
    public CompareXZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}


class CompareXAbsolute extends CompareXOperationBase {
    public CompareXAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

public class CompareX extends Instruction {

    public CompareX() {
        this.assemblyInstructionName = "CPX";
        this.addOperation(new CompareXImmediate(AddressingMode.Immediate, (byte) 0xE0, 2, 2));
        this.addOperation(new CompareXZeroPage(AddressingMode.ZeroPage, (byte) 0xE4, 2, 3));
        this.addOperation(new CompareXAbsolute(AddressingMode.Absolute, (byte) 0xEC, 3, 4));
    }

}
