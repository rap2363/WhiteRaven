package operations;

import snes.CPU;

abstract class RotateLeftOperationBase extends Operation {

    RotateLeftOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * This operation shifts all the bits of the accumulator or memory contents
     * one bit left. Bit 0 is set to the carry flag and bit 0 is placed in the
     * carry flag.
     **/
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        int address = AddressingModeUtilities.getAddress(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        boolean newCarryFlag = Utilities.bitAt(value, 7);
        value = Utilities.bitShift(value, -1);
        if (cpu.P.carryFlag()) {
            value++;
        }
        cpu.memory.write(address, value);

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
        cpu.cycles += cycles;
    }
}

class RotateLeftImplicit extends RotateLeftOperationBase {
    public RotateLeftImplicit(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        cpu.A.shiftLeft(1);

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

class RotateLeftZeroPage extends RotateLeftOperationBase {
    public RotateLeftZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class RotateLeftZeroPageX extends RotateLeftOperationBase {
    public RotateLeftZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class RotateLeftAbsolute extends RotateLeftOperationBase {
    public RotateLeftAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class RotateLeftAbsoluteX extends RotateLeftOperationBase {
    public RotateLeftAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

public class RotateLeft extends Instruction {

    public RotateLeft() {
        this.assemblyInstructionName = "ROL";
        this.addOperation(new RotateLeftImplicit(AddressingMode.Implicit, (byte) 0x2A, 1, 2));
        this.addOperation(new RotateLeftZeroPage(AddressingMode.ZeroPage, (byte) 0x26, 2, 5));
        this.addOperation(new RotateLeftZeroPageX(AddressingMode.ZeroPageX, (byte) 0x36, 2, 6));
        this.addOperation(new RotateLeftAbsolute(AddressingMode.Absolute, (byte) 0x2E, 3, 6));
        this.addOperation(new RotateLeftAbsoluteX(AddressingMode.AbsoluteX, (byte) 0x3E, 3, 7));
    }

}
