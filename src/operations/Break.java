package operations;

import nes.CPU;

class BreakImplicit extends Operation {

    BreakImplicit(AddressingMode addressingMode, byte opcode, int numBytes, int cycles) {
        super(addressingMode, opcode, numBytes, cycles);
    }

    /**
     * The BRK instruction forces the generation of an interrupt request. The program counter
     * and processor status are pushed on the stack then the IRQ interrupt vector at $FFFE/F
     * is loaded into the PC and the break flag is set.
     *
     */
    @Override
    public void execute(CPU cpu) {
        cpu.pushOntoStack(cpu.PC.readLSB());
        cpu.pushOntoStack(cpu.PC.readMSB());
        cpu.pushOntoStack(cpu.P.readAsByte());

        cpu.PC.write(cpu.memory.read(0xFFFF), cpu.memory.read(0xFFFE));

        cpu.P.setBreakFlag();

        cpu.cycles += cycles;
    }
}

public class Break extends Instruction {

    public Break() {
        this.assemblyInstructionName = "BRK";
        this.addOperation(new BreakImplicit(AddressingMode.Implicit, (byte) 0x00, 1, 7));
    }
}
