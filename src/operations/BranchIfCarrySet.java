package operations;

import snes.CPU;

class BranchIfCarrySetRelative extends Operation {
    public BranchIfCarrySetRelative(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    /**
     * Branch by a specific amount if the carry bit is set
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(AddressingMode.Immediate, cpu, cpu.readAfterPC(numBytes - 1));
        boolean carryFlagSet = cpu.P.carryFlag();

        if (Utilities.getOverflowFlag((byte) cpu.PC.read(), value, false)) {
            cpu.cycles++;
        }

        if (carryFlagSet) {
            cpu.PC.addByte(value);
            cpu.cycles++;
        }

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

public class BranchIfCarrySet extends Instruction {

    public BranchIfCarrySet() {
        this.assemblyInstructionName = "BCS";
        this.addOperation(new BranchIfCarrySetRelative(AddressingMode.Relative, (byte) 0xB0, 2, 2));
    }

}
