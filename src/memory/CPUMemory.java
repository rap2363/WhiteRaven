package memory;

public class CPUMemory extends MemoryMap {
    private static final int MEMORY_SIZE = 0x10000;
    private static final int RAM_SIZE = 0x2000;
    private static final int IO_REGISTER_OFFSET = 0x4000;

    public static final int STACK_OFFSET = 0x0100;
    public static final int PRG_LOWER_BANK = 0x8000;
    public static final int PRG_UPPER_BANK = 0xC000;

    // PPU register addresses and temporary values
    public static final int PPU_CTRL = 0x2000;
    public static final int PPU_MASK = 0x2001;
    public static final int PPU_STATUS = 0x2002;
    public static final int SPR_ADDRESS = 0x2003;
    public static final int SPR_DATA = 0x2004;
    public static final int PPU_SCROLL = 0x2005;
    public static final int PPU_ADDRESS = 0x2006;
    public static final int PPU_DATA = 0x2007;
    public static final int SPR_DMA = 0x4014;

    // Memory-mapped PPU registers that the CPU can write to. These are allocated explicitly because of some two
    // write logic (e.g. the CPU can write to the PPU address by writing twice to PPU_ADDRESS).
    // The Console will read these values and load them into the PPU's internal registers at the right time.
    private boolean isScrollHigh = true;
    public byte ppuScrollHigh;
    public byte ppuScrollLow;

    private boolean isAdressHigh = true;
    public byte ppuAddressHigh;
    public byte ppuAddressLow;

    public boolean dmaFlag = false;

    CPUMemory() {
        super(MEMORY_SIZE);
    }

    /**
     * Read a byte from memory (implemented with memory mirrors)
     *
     * @param address
     * @return
     */
    @Override
    public byte read(int address) {
        // RAM mirrored every 2 kiB
        if (address < RAM_SIZE) {
            return memory[address % 0x0800];
        } else if (address < IO_REGISTER_OFFSET) {
            // I/O registers mirrored (8 I/O registers total)
            return memory[RAM_SIZE + address % 0x0008];
        }

        return memory[address % MEMORY_SIZE];
    }

    /**
     * Write a byte into memory (implemented with memory mirrors)
     *
     * @param address
     * @param value
     */
    @Override
    public void write(int address, byte value) {
        int addressToWriteTo = address;
        if (address < RAM_SIZE) {
            // RAM mirrored every 2 kiB
            addressToWriteTo = address % 0x0800;
        } else if (address < IO_REGISTER_OFFSET) {
            // I/O registers mirrored (8 I/O registers total)
            addressToWriteTo = RAM_SIZE + address % 0x0008;
        } else {
            addressToWriteTo = address % MEMORY_SIZE;
        }

        // 2x write to PPU scroll (X,Y)
        if (addressToWriteTo == PPU_SCROLL) {
            if (isScrollHigh) {
                ppuScrollHigh = value;
            } else {
                ppuScrollLow = value;
            }

            isScrollHigh = !isScrollHigh;
        }

        // 2x write to PPU address (16 bit concatenated address)
        if (addressToWriteTo == PPU_ADDRESS) {
            if (isAdressHigh) {
                ppuAddressHigh = value;
            } else {
                ppuAddressLow = value;
            }

            isAdressHigh = !isAdressHigh;
        }

        // Increment the value at SPR_ADDRESS when SPR_DATA is written to
        if (addressToWriteTo == SPR_DATA) {
            this.memory[SPR_ADDRESS]++;
        }

        // DMA
        if (addressToWriteTo == SPR_DMA) {
            dmaFlag = true;
        }

        memory[addressToWriteTo] = value;
    }
}
