package operations;

import snes.CPU;

class IncrementYImplicit extends Operation {

    IncrementYImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Increment the Y register and set the zero and negative flags as appropriate
     *
     * Z,N,Y = Y + 1
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
        cpu.cycles += cycles;
    }
}

public class IncrementY extends Instruction {

    public IncrementY() {
        this.assemblyInstructionName = "INY";
        this.addOperation(new IncrementYImplicit(AddressingMode.Implicit, (byte) 0xC8, 1, 2));
    }
}
