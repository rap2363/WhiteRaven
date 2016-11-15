package operations;

import snes.CPU;

class DecrementXImplicit extends Operation {

    DecrementXImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Decrement the X register and set the zero and negative flags as appropriate
     *
     * Z,N,X = X - 1
     */
    @Override
    public void execute(CPU cpu) {
        cpu.X.addByte((byte) -1, false);
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

public class DecrementX extends Instruction {

    public DecrementX() {
        this.assemblyInstructionName = "DEX";
        this.addOperation(new DecrementXImplicit(AddressingMode.Implicit, (byte) 0xCA, 1, 2));
    }
}
