package memory;

public class IORegisterMemory extends MemoryMap {
    private static final int numPpuRegisters = 0x0008;
    private static final int numSecondSetRegisters = 0x0020;
    private static final int SECOND_SET_IO_REGISTERS = 0x2000;

    // PPU register addresses (in first set of registers)
    public static final int PPU_CTRL = 0x0000;
    public static final int PPU_MASK = 0x0001;
    public static final int PPU_STATUS = 0x0002;
    public static final int SPR_ADDRESS = 0x0003;
    public static final int SPR_DATA = 0x0004;
    public static final int PPU_SCROLL = 0x0005;
    public static final int PPU_ADDRESS = 0x0006;
    public static final int PPU_DATA = 0x0007;

    // These are allocated explicitly because of some two write logic (e.g. the CPU can write to the PPU address by
    // writing twice to PPU_ADDRESS). ConsoleMemory will read these values and load them into the PPU's internal
    // registers at the right time.
    public SixteenBitLatch ppuAddressLatch;
    public SixteenBitLatch ppuDataLatch;

    public boolean dmaFlag = false;

    // Second set of registers (APU, controllers, DMA, etc.)
    // TODO: implement these registers!
    public static final int SPR_DMA = 0x2014;

    public IORegisterMemory() {
        super(numPpuRegisters + numSecondSetRegisters);
        ppuAddressLatch = new SixteenBitLatch();
        ppuDataLatch = new SixteenBitLatch();
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
            switch (address) {
                case PPU_ADDRESS:
                    ppuAddressLatch.write(value);
                case PPU_DATA:
                    ppuDataLatch.write(value);
                case SPR_DATA:
                    this.memory[SPR_ADDRESS]++;
                case SPR_ADDRESS:
                    dmaFlag = true;
                default:
                    memory[address % numPpuRegisters] = value;
            }
        } else {
            memory[address] = value;
        }
    }
}
