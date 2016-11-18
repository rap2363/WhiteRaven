package operations;

import nes.CPU;

abstract class StoreYOperationBase extends Operation {

    StoreYOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Stores the Y register's byte at a location in memory
     *
     * M=Y
     */
    @Override
    public void execute(CPU cpu) {
        int address = AddressingModeUtilities.getAddress(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));
        cpu.memory.write(address, cpu.Y.readAsByte());

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

class StoreYZeroPage extends StoreYOperationBase {
    public StoreYZeroPage(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class StoreYZeroPageX extends StoreYOperationBase {
    public StoreYZeroPageX(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class StoreYAbsolute extends StoreYOperationBase {
    public StoreYAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

public class StoreY extends Instruction {
    public StoreY() {
        this.assemblyInstructionName = "STY";
        this.addOperation(new StoreYZeroPage(AddressingMode.ZeroPage, (byte) 0x84, 2, 3));
        this.addOperation(new StoreYZeroPageX(AddressingMode.ZeroPageY, (byte) 0x94, 2, 4));
        this.addOperation(new StoreYAbsolute(AddressingMode.Absolute, (byte) 0x8C, 3, 4));
    }
}
