package operations;

import nes.CPU;

abstract class LogicalShiftRightOperationBase extends Operation {

    LogicalShiftRightOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * This operation shifts all the bits of the accumulator or memory contents
     * one bit right. Bit 7 is set to 0 and bit 0 is placed in the carry flag.
     *
     * A,Z,C,N = A/2 or M,Z,C,N = M/2
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        int address = AddressingModeUtilities.getAddress(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        cpu.memory.write(address, Utilities.bitShift(value, 1));

        // Set the processor status flags
        if (Utilities.bitAt(value, 0)) {
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

class LogicalShiftRightImplicit extends LogicalShiftRightOperationBase {
    public LogicalShiftRightImplicit(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        cpu.A.shiftLeft(1);

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

class LogicalShiftRightZeroPage extends LogicalShiftRightOperationBase {
    public LogicalShiftRightZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class LogicalShiftRightZeroPageX extends LogicalShiftRightOperationBase {
    public LogicalShiftRightZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class LogicalShiftRightAbsolute extends LogicalShiftRightOperationBase {
    public LogicalShiftRightAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class LogicalShiftRightAbsoluteX extends LogicalShiftRightOperationBase {
    public LogicalShiftRightAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

public class LogicalShiftRight extends Instruction {

    public LogicalShiftRight() {
        this.assemblyInstructionName = "LSR";
        this.addOperation(new LogicalShiftRightImplicit(AddressingMode.Implicit, (byte) 0x4A, 1, 2));
        this.addOperation(new LogicalShiftRightZeroPage(AddressingMode.ZeroPage, (byte) 0x46, 2, 5));
        this.addOperation(new LogicalShiftRightZeroPageX(AddressingMode.ZeroPageX, (byte) 0x56, 2, 6));
        this.addOperation(new LogicalShiftRightAbsolute(AddressingMode.Absolute, (byte) 0x4E, 3, 6));
        this.addOperation(new LogicalShiftRightAbsoluteX(AddressingMode.AbsoluteX, (byte) 0x5E, 3, 7));
    }

}
