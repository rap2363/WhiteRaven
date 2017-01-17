package nes;

public class IORegisterMemory extends MemoryMap {
    private static final int numPpuRegisters = 0x0008;
    private static final int numSecondSetRegisters = 0x0020;
    private static final int SECOND_SET_IO_REGISTERS = 0x2000;

    public IORegisterMemory() {
        super(numPpuRegisters + numSecondSetRegisters);
    }

    /**
     * Read a byte from memory (implemented with memory mirrors)
     *
     * @param address
     * @return
     */
    @Override
    public byte read(int address) {
        if (address < SECOND_SET_IO_REGISTERS) {
            return memory[address % numPpuRegisters];
        }
        return memory[address];
    }

    /**
     * Write a byte into memory (implemented with memory mirrors)
     *
     * @param address
     * @param value
     */
    @Override
    public void write(int address, byte value) {
        if (address < SECOND_SET_IO_REGISTERS) {
            memory[address % numPpuRegisters] = value;
        }
        memory[address] = value;
    }
}
