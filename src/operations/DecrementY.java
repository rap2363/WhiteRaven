package operations;

import nes.CPU;

class DecrementYImplicit extends Operation {

    DecrementYImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * Decrement the Y register and set the zero and negative flags as appropriate
     *
     * Z,N,Y = Y - 1
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

public class DecrementY extends Instruction {

    public DecrementY() {
        this.assemblyInstructionName = "DEY";
        this.addOperation(new DecrementYImplicit(AddressingMode.Implicit, (byte) 0x88, 1, 2));
    }
}
