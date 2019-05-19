package main.java.operations;

import main.java.nes.CPU;

abstract class LoadXOperationBase extends Operation {

    LoadXOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Load a byte of main.java.memory into the X register and set the zero and negative flags appropriately
     *
     * X,Z,N = M
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        cpu.X.writeByte(value);

        // Set the processor status flags
        if (cpu.X.read() == 0) {
            cpu.P.setZeroFlag();
        } else {
            cpu.P.clearZeroFlag();
        }

        if (cpu.X.signBit()) {
            cpu.P.setNegativeFlag();
        } else {
            cpu.P.clearNegativeFlag();
        }

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }
}

class LoadXImmediate extends LoadXOperationBase {
    public LoadXImmediate(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class LoadXZeroPage extends LoadXOperationBase {
    public LoadXZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class LoadXZeroPageY extends LoadXOperationBase {
    public LoadXZeroPageY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class LoadXAbsolute extends LoadXOperationBase {
    public LoadXAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class LoadXAbsoluteY extends LoadXOperationBase {
    public LoadXAbsoluteY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    @Override
    public void execute(CPU cpu) {
        super.execute(cpu);

        byte[] bytes = cpu.readAfterPC(numBytes - 1);
        if (Utilities.getOverflowFlag(bytes[1], cpu.Y.readAsByte(), false)) {
            cpu.cycleCount++;
        }
    }
}

public class LoadX extends Instruction {
    public LoadX() {
        this.assemblyInstructionName = "LDX";
        this.addOperation(new LoadXImmediate(AddressingMode.Immediate, (byte) 0xA2, 2, 2));
        this.addOperation(new LoadXZeroPage(AddressingMode.ZeroPage, (byte) 0xA6, 2, 3));
        this.addOperation(new LoadXZeroPageY(AddressingMode.ZeroPageY, (byte) 0xB6, 2, 4));
        this.addOperation(new LoadXAbsolute(AddressingMode.Absolute, (byte) 0xAE, 3, 4));
        this.addOperation(new LoadXAbsoluteY(AddressingMode.AbsoluteY, (byte) 0xBE, 3, 4));
    }
}
