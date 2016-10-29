package operations;

import snes.CPU;

abstract class AddWithCarryOperationBase extends Operation {

    AddWithCarryOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressingMode, opcode, numBytes, cycles, pageCrossedCycle);
        // TODO Auto-generated constructor stub
    }

    void setProcessorStatus(CPU cpu, boolean carryFlag, boolean overflowFlag, byte lastInstruction) {
        if (carryFlag) {
            cpu.P.setCarryFlag();
        }
        if (cpu.A.register == 0) {
            cpu.P.setZeroFlag();
        }
        if (overflowFlag) {
            cpu.P.setOverflowFlag();
        }
        if (lastInstruction < 0) {
            cpu.P.setNegativeFlag();
        }
    }
}

class AddWithCarryImmediate extends AddWithCarryOperationBase {
    public AddWithCarryImmediate(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    void execute(CPU cpu) {
        byte[] bytes = cpu.readPC();

        boolean overflowFlag = (cpu.A.register & bytes[1]) >> 7;
        boolean carryFlag = cpu.A.addByte(bytes[1]);

        cpu.cycles += 2;

        setProcessorStatus(cpu, carryFlag, false, cpu.A.register);
    }
}

class AddWithCarryZeroPage extends AddWithCarryOperationBase {
    public AddWithCarryZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    void execute(CPU cpu) {

    }
}

class AddWithCarryZeroPageX extends AddWithCarryOperationBase {
    public AddWithCarryZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    void execute(CPU cpu) {

    }
}

class AddWithCarryAbsolute extends AddWithCarryOperationBase {
    public AddWithCarryAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    void execute(CPU cpu) {

    }
}

class AddWithCarryAbsoluteX extends AddWithCarryOperationBase {
    public AddWithCarryAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    void execute(CPU cpu) {

    }
}

class AddWithCarryAbsoluteY extends AddWithCarryOperationBase {
    public AddWithCarryAbsoluteY(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    void execute(CPU cpu) {

    }
}

class AddWithCarryIndirectX extends AddWithCarryOperationBase {
    public AddWithCarryIndirectX(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    void execute(CPU cpu) {

    }
}

class AddWithCarryIndirectY extends AddWithCarryOperationBase {
    public AddWithCarryIndirectY(AddressingMode addressMode, byte opcode, int numBytes, int cycles,
            boolean pageCrossedCycle) {
        super(addressMode, opcode, numBytes, cycles, pageCrossedCycle);
    }

    @Override
    void execute(CPU cpu) {

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