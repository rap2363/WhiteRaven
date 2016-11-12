package operations;

import snes.CPU;

class BranchIfEqualRelative extends Operation {
    public BranchIfEqualRelative(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    /**
     * Branch by a specific amount if the zero flag is set
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(AddressingMode.Immediate, cpu, cpu.readAfterPC(numBytes - 1));
        boolean zeroFlagSet = cpu.P.zeroFlag();

        if (Utilities.getOverflowFlag((byte) cpu.PC.read(), value, false)) {
            cpu.cycles++;
        }

        if (zeroFlagSet) {
            cpu.PC.addByte(value);
            cpu.cycles++;
        }

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

public class BranchIfEqual extends Instruction {

    public BranchIfEqual() {
        this.assemblyInstructionName = "BEQ";
        this.addOperation(new BranchIfEqualRelative(AddressingMode.Relative, (byte) 0xF0, 2, 2));
    }

}
