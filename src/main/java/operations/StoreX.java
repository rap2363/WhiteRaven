package main.java.operations;

import main.java.nes.CPU;

abstract class StoreXOperationBase extends Operation {

    StoreXOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Stores the X register's byte at a location in main.java.memory
     *
     * M=X
     */
    @Override
    public void execute(CPU cpu) {
        int address = AddressingModeUtilities.getAddress(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        cpu.memory.write(address, cpu.X.readAsByte());

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }
}

class StoreXZeroPage extends StoreXOperationBase {
    public StoreXZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class StoreXZeroPageY extends StoreXOperationBase {
    public StoreXZeroPageY(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class StoreXAbsolute extends StoreXOperationBase {
    public StoreXAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

public class StoreX extends Instruction {
    public StoreX() {
        this.assemblyInstructionName = "STX";
        this.addOperation(new StoreXZeroPage(AddressingMode.ZeroPage, (byte) 0x86, 2, 3));
        this.addOperation(new StoreXZeroPageY(AddressingMode.ZeroPageY, (byte) 0x96, 2, 4));
        this.addOperation(new StoreXAbsolute(AddressingMode.Absolute, (byte) 0x8E, 3, 4));
    }
}
