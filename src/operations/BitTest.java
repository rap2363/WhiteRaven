package operations;

import nes.CPU;

abstract class BitTestOperationBase extends Operation {

    BitTestOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * This instruction is used to test if one or more bits are set in a target
     * memory location. The mask pattern in A is ANDed with the value in memory
     * to set or clear the zero flag, but the result is not kept. Bits 7 and 6
     * of the value from memory are copied into the N and V flags.
     *
     * A&M,N = M7, V = M6
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        boolean zeroFlagSet = (cpu.A.readAsByte() & value) == 0;

        // Set the processor status flags
        if (zeroFlagSet) {
            cpu.P.setZeroFlag();
        } else {
            cpu.P.clearZeroFlag();
        }

        if (Utilities.bitShift(value, 7) == 0x01) {
            cpu.P.setNegativeFlag();
        } else {
            cpu.P.clearNegativeFlag();
        }

        if ((Utilities.bitShift(value, 6) & 0x01) == 0x01) {
            cpu.P.setOverflowFlag();
        } else {
            cpu.P.clearOverflowFlag();
        }

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }
}

class BitTestZeroPage extends BitTestOperationBase {
    public BitTestZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class BitTestAbsolute extends BitTestOperationBase {
    public BitTestAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

public class BitTest extends Instruction {

    public BitTest() {
        this.assemblyInstructionName = "BIT";
        this.addOperation(new BitTestZeroPage(AddressingMode.ZeroPage, (byte) 0x24, 2, 3));
        this.addOperation(new BitTestAbsolute(AddressingMode.Absolute, (byte) 0x2C, 3, 4));
    }
}
