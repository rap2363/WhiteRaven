package operations;

import snes.CPU;

class BranchIfCarryClearRelative extends Operation {
    public BranchIfCarryClearRelative(AddressingMode addressMode, byte opcode, int numBytes, int cycles) {
        super(addressMode, opcode, numBytes, cycles);
    }

    /**
     * Branch by a specific amount if the carry bit is cleared
     */
    @Override
    public void execute(CPU cpu) {
        byte value = AddressingModeUtilities.getValue(AddressingMode.Immediate, cpu, cpu.readAfterPC(numBytes - 1));
        boolean clearedCarryFlag = !cpu.P.carryFlag();

        if (Utilities.getOverflowFlag((byte) cpu.PC.read(), value, false)) {
            cpu.cycles++;
        }

        if (clearedCarryFlag) {
            cpu.PC.addByte(value);
            cpu.cycles++;
        }

        cpu.PC.incrementBy(numBytes);
        cpu.cycles += cycles;
    }
}

public class BranchIfCarryClear extends Instruction {

    public BranchIfCarryClear() {
        this.assemblyInstructionName = "BCC";
        this.addOperation(new BranchIfCarryClearRelative(AddressingMode.Relative, (byte) 0x90, 2, 2));
    }

}
