package operations;

import snes.CPU;

abstract class LoadYOperationBase extends Operation {

    LoadYOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Load a byte of memory into the Y register and set the zero and negative flags appropriately
     *
     * Y,Z,N = M
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        cpu.Y.write(value);

        // Set the processor status flags
        if (cpu.Y.read() == 0) {
            cpu.P.setZeroFlag();
        } else {
            cpu.P.clearZeroFlag();
        }

        if (cpu.Y.signBit()) {
            cpu.P.setNegativeFlag();
        } else {
            cpu.P.clearNegativeFlag();
        }

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

class LoadYImmediate extends LoadYOperationBase {
    public LoadYImmediate(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class LoadYZeroPage extends LoadYOperationBase {
    public LoadYZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class LoadYZeroPageX extends LoadYOperationBase {
    public LoadYZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class LoadYAbsolute extends LoadYOperationBase {
    public LoadYAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class LoadYAbsoluteX extends LoadYOperationBase {
    public LoadYAbsoluteX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        super.execute(cpu);

        byte[] bytes = cpu.readAfterPC(numBytes - 1);
        if (Utilities.getOverflowFlag(bytes[1], cpu.Y.readAsByte(), false)) {
            cpu.cycles++;
        }
    }
}

public class LoadY extends Instruction {
    public LoadY() {
        this.assemblyInstructionName = "LDY";
        this.addOperation(new LoadYImmediate(AddressingMode.Immediate, (byte) 0xA0, 2, 2));
        this.addOperation(new LoadYZeroPage(AddressingMode.ZeroPage, (byte) 0xA4, 2, 3));
        this.addOperation(new LoadYZeroPageX(AddressingMode.ZeroPageX, (byte) 0xB4, 2, 4));
        this.addOperation(new LoadYAbsolute(AddressingMode.Absolute, (byte) 0xAC, 3, 4));
        this.addOperation(new LoadYAbsoluteX(AddressingMode.AbsoluteX, (byte) 0xBC, 3, 4));
    }
}
