package operations;

import snes.CPU;

abstract class JumpOperationBase extends Operation {

    JumpOperationBase(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Set the program counter according to a value in memory. This is not offset based, so
     * we do not increment the PC by numBytes once we make the jump.
     *
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(addressingMode, cpu, cpu.readAfterPC(numBytes - 1));

        cpu.PC.write(value);

        cpu.cycles += cycles;
    }
}

class JumpIndirect extends JumpOperationBase {
    public JumpIndirect(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

class JumpAbsolute extends JumpOperationBase {
    public JumpAbsolute(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }
}

public class Jump extends Instruction {

    public Jump() {
        this.assemblyInstructionName = "JMP";
        this.addOperation(new JumpAbsolute(AddressingMode.Absolute, (byte) 0x4C, 3, 3));
        this.addOperation(new JumpIndirect(AddressingMode.Indirect, (byte) 0x6C, 3, 5));
    }
}
