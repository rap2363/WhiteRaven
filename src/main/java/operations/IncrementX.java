package operations;

import nes.CPU;

class IncrementXImplicit extends Operation {

    IncrementXImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Increment the X register and set the zero and negative flags as appropriate
     *
     * Z,N,X = X + 1
     */
    @Override
    public void execute(CPU cpu) {
        cpu.X.addByte((byte) 1, false);
        byte value = cpu.X.readAsByte();

        // Set the processor status flags
        if (value == 0) {
            cpu.P.setZeroFlag();
        } else {
            cpu.P.clearZeroFlag();
        }

        if (value < 0) {
            cpu.P.setNegativeFlag();
        } else {
            cpu.P.clearNegativeFlag();
        }

        cpu.PC.incrementBy(numBytes);
        cpu.cycleCount += cycles;
    }
}

public class IncrementX extends Instruction {

    public IncrementX() {
        this.assemblyInstructionName = "INX";
        this.addOperation(new IncrementXImplicit(AddressingMode.Implicit, (byte) 0xE8, 1, 2));
    }
}
