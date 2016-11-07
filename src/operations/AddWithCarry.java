package operations;

import snes.CPU;

abstract class AddWithCarryOperationBase extends Operation {

    AddWithCarryOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressingMode, opcode, numBytes, cycles, pageCrossedCycle);
        // TODO Auto-generated constructor stub
    }

    void setProcessorStatus(CPU cpu, boolean carryFlag, boolean overflowFlag, byte lastInstructionInstruction) {
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

        if (overflowFlag) {
            cpu.P.setOverflowFlag();
        } else {
            cpu.P.clearOverflowFlag();
        }

        if (lastInstructionInstruction < 0) {
            cpu.P.setNegativeFlag();
        } else {
            cpu.P.clearNegativeFlag();
        }
    }
}

class AddWithCarryImmediate extends AddWithCarryOperationBase {
    public AddWithCarryImmediate(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    public void execute(CPU cpu) {
        byte[] bytes = cpu.readAtPC(2);
        byte value = bytes[1];

        boolean carryFlag = cpu.A.addByte(value);
        boolean overflowFlag = cpu.getOverflowFlag((byte) cpu.A.read(), value, carryFlag);

        cpu.cycles += 2;

        setProcessorStatus(cpu, carryFlag, overflowFlag, (byte) cpu.A.read());
    }
}

class AddWithCarryZeroPage extends AddWithCarryOperationBase {
    public AddWithCarryZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    public void execute(CPU cpu) {

    }
}

class AddWithCarryZeroPageX extends AddWithCarryOperationBase {
    public AddWithCarryZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    public void execute(CPU cpu) {

    }
}

class AddWithCarryAbsolute extends AddWithCarryOperationBase {
    public AddWithCarryAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    public void execute(CPU cpu) {

    }
}

class AddWithCarryAbsoluteX extends AddWithCarryOperationBase {
    public AddWithCarryAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    public void execute(CPU cpu) {

    }
}

class AddWithCarryAbsoluteY extends AddWithCarryOperationBase {
    public AddWithCarryAbsoluteY(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    public void execute(CPU cpu) {

    }
}

class AddWithCarryIndirectX extends AddWithCarryOperationBase {
    public AddWithCarryIndirectX(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    public void execute(CPU cpu) {

    }
}

class AddWithCarryIndirectY extends AddWithCarryOperationBase {
    public AddWithCarryIndirectY(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    public void execute(CPU cpu) {

    }
}

public class AddWithCarry extends Instruction {
    public AddWithCarry() {
        this.addOperation(new AddWithCarryImmediate(AddressingMode.Immediate, (byte) 0x69, 2, 2, false));
        this.addOperation(new AddWithCarryZeroPage(AddressingMode.ZeroPage, (byte) 0x65, 2, 3, false));
        this.addOperation(new AddWithCarryZeroPageX(AddressingMode.ZeroPageX, (byte) 0x75, 2, 4, false));
        this.addOperation(new AddWithCarryAbsolute(AddressingMode.Absolute, (byte) 0x6D, 3, 4, false));
        this.addOperation(new AddWithCarryAbsoluteX(AddressingMode.AbsoluteX, (byte) 0x7D, 3, 4, true));
        this.addOperation(new AddWithCarryAbsoluteY(AddressingMode.AbsoluteY, (byte) 0x79, 3, 4, true));
        this.addOperation(new AddWithCarryIndirectX(AddressingMode.IndirectX, (byte) 0x61, 2, 6, false));
        this.addOperation(new AddWithCarryIndirectY(AddressingMode.IndirectY, (byte) 0x71, 2, 5, true));
    }
}