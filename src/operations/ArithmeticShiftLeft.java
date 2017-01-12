package operations;

import nes.CPU;

abstract class ArithmeticShiftLeftOperationBase extends Operation {

    ArithmeticShiftLeftOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * This operation shifts all the bits of the accumulator or memory contents
     * one bit left. Bit 0 is set to 0 and bit 7 is placed in the carry flag.
     * The effect of this operation is to multiply the memory contents by 2
     * (ignoring 2's complement considerations), setting the carry if the result
     * will not fit in 8 bits.
     *
     * A,Z,C,N = M*2 or M,Z,C,N = M*2
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        int address = AddressingModeUtilities.getAddress(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        byte result = Utilities.bitShift(value, -1);
        cpu.memory.write(address, Utilities.bitShift(value, -1));

        // Set the processor status flags
        if (value < 0) {
            cpu.P.setCarryFlag();
        } else {
            cpu.P.clearCarryFlag();
        }

        if (result == 0) {
            cpu.P.setZeroFlag();
        } else {
            cpu.P.clearZeroFlag();
        }

        if (result < 0) {
            cpu.P.setNegativeFlag();
        } else {
            cpu.P.clearNegativeFlag();
        }

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

class ArithmeticShiftLeftImplicit extends ArithmeticShiftLeftOperationBase {
    public ArithmeticShiftLeftImplicit(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        boolean carryFlag = cpu.A.signBit();
        cpu.A.shiftLeft(1);

        // Set the processor status flags
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
        cpu.cycles += cycles;
    }
}

class ArithmeticShiftLeftZeroPage extends ArithmeticShiftLeftOperationBase {
    public ArithmeticShiftLeftZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class ArithmeticShiftLeftZeroPageX extends ArithmeticShiftLeftOperationBase {
    public ArithmeticShiftLeftZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class ArithmeticShiftLeftAbsolute extends ArithmeticShiftLeftOperationBase {
    public ArithmeticShiftLeftAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class ArithmeticShiftLeftAbsoluteX extends ArithmeticShiftLeftOperationBase {
    public ArithmeticShiftLeftAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

public class ArithmeticShiftLeft extends Instruction {

    public ArithmeticShiftLeft() {
        this.assemblyInstructionName = "ASL";
        this.addOperation(new ArithmeticShiftLeftImplicit(AddressingMode.Implicit, (byte) 0x0A, 1, 2));
        this.addOperation(new ArithmeticShiftLeftZeroPage(AddressingMode.ZeroPage, (byte) 0x06, 2, 5));
        this.addOperation(new ArithmeticShiftLeftZeroPageX(AddressingMode.ZeroPageX, (byte) 0x16, 2, 6));
        this.addOperation(new ArithmeticShiftLeftAbsolute(AddressingMode.Absolute, (byte) 0x0E, 3, 6));
        this.addOperation(new ArithmeticShiftLeftAbsoluteX(AddressingMode.AbsoluteX, (byte) 0x1E, 3, 7));
    }

}
