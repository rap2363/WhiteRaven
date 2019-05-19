package main.java.operations;

import main.java.nes.CPU;

abstract class RotateRightOperationBase extends Operation {

    RotateRightOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * This operation shifts all the bits of the accumulator or main.java.memory contents
     * one bit right. Bit 7 is set to the carry flag and bit 0 is placed in the
     * carry flag.
     **/
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        int address = AddressingModeUtilities.getAddress(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        boolean newCarryFlag = Utilities.bitAt(value, 0);
        value = Utilities.bitShift(value, 1);
        if (cpu.P.carryFlag()) {
            value |= (byte) 0x80;
        }
        cpu.memory.write(address, value);

        // Set the processor status flags
        if (newCarryFlag) {
            cpu.P.setCarryFlag();
        } else {
            cpu.P.clearCarryFlag();
        }

        if (value == 0) {
            cpu.P.setZeroFlag();
        } else {
            cpu.P.clearZeroFlag();
        }

        if (value < 0) {
            cpu.P.setNegativeFlag();
        } else {
            cpu.P.clearNegativeFlag();
        }

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }
}

class RotateRightImplicit extends RotateRightOperationBase {
    public RotateRightImplicit(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        boolean newCarryFlag = Utilities.bitAt(cpu.A.readAsByte(), 0);
        cpu.A.shiftRight(1);

        if (cpu.P.carryFlag()) {
            cpu.A.orByte((byte) 0x80);
        }

        // Set the processor status flags
        if (newCarryFlag) {
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
        cpu.cycleCount += cycles;
    }
}

class RotateRightZeroPage extends RotateRightOperationBase {
    public RotateRightZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class RotateRightZeroPageX extends RotateRightOperationBase {
    public RotateRightZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class RotateRightAbsolute extends RotateRightOperationBase {
    public RotateRightAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class RotateRightAbsoluteX extends RotateRightOperationBase {
    public RotateRightAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

public class RotateRight extends Instruction {

    public RotateRight() {
        this.assemblyInstructionName = "ROR";
        this.addOperation(new RotateRightImplicit(AddressingMode.Implicit, (byte) 0x6A, 1, 2));
        this.addOperation(new RotateRightZeroPage(AddressingMode.ZeroPage, (byte) 0x66, 2, 5));
        this.addOperation(new RotateRightZeroPageX(AddressingMode.ZeroPageX, (byte) 0x76, 2, 6));
        this.addOperation(new RotateRightAbsolute(AddressingMode.Absolute, (byte) 0x6E, 3, 6));
        this.addOperation(new RotateRightAbsoluteX(AddressingMode.AbsoluteX, (byte) 0x7E, 3, 7));
    }
}
