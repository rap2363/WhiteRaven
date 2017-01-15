package nes;

/**
 * This is the main Video RAM of the PPU where the sprites and background images are held.
 */
class VRAM extends MemoryMap {
    private static final int SPRITE_TABLE_SIZE = 0x3000;
    private static final int PALLETE_OFFSET = 0x3F00;
    private static final int PHYSICAL_MEMORY_SIZE = 0x4000;

    public VRAM() {
        this.memory = new byte[PHYSICAL_MEMORY_SIZE];
    }

    @Override
    public byte read(int address) {
        if (address < SPRITE_TABLE_SIZE) {
            return this.memory[address];
        } else if (address < PALLETE_OFFSET) {
            return this.memory[address % 0x1000 + 0x2000];
        } else if (address < PHYSICAL_MEMORY_SIZE) {
            return this.memory[address % 0x20 + PALLETE_OFFSET];
        }

        return this.memory[address % PHYSICAL_MEMORY_SIZE];
    }

    @Override
    public void write(int address, byte value) {
        if (address < SPRITE_TABLE_SIZE) {
            this.memory[address] = value;
        } else if (address < PALLETE_OFFSET) {
            this.memory[address % 0x1000 + 0x2000] = value;
        } else if (address < PHYSICAL_MEMORY_SIZE) {
            this.memory[address % 0x20 + PALLETE_OFFSET] = value;
        }

        this.memory[address % PHYSICAL_MEMORY_SIZE] = value;
    }
}

/**
 * The PPU also has a separate location of memory specifically for SPR-RAM which is distinct from main memory.
 * This is written to directly in DMA's.
 */
class SPRAM extends MemoryMap {
    public static final int SPRAM_SIZE = 0x100;
    public int address = 0x0;

    public SPRAM() {
        this.memory = new byte[SPRAM_SIZE];
    }

    @Override
    public byte read(int address) {
        return this.memory[address % SPRAM_SIZE];
    }

    @Override
    public void write(int address, byte value) {
        this.memory[address % SPRAM_SIZE] = value;
    }

    public void dmaWrite(byte[] values) {
        if (values.length != 0x100) {
            System.err.println("SPRAM size is exactly 256 bytes!");
        }
        for (int i = 0; i < SPRAM_SIZE; i++) {
            this.write(i + address, values[i]);
        }
    }
}

/**
 * Models the PPU architecture
 */
public class PPU {
    public MemoryMap vRam;
    public MemoryMap spRam;

    public long cycles;
    public boolean evenFlag;
    public int address;

    // Values from PPUControl
    public boolean generateNMI;
    public boolean normalSpriteSize;
    public int bgTableIndex;
    public int spriteTableAddressIndex;
    public int tableIncrementsIndex;
    public int nameTableAddressIndex;

    // Values from PPUMask
    public boolean green;
    public boolean blue;
    public boolean red;
    public boolean showSprites;
    public boolean showBG;
    public boolean leftSprites;
    public boolean leftBG;
    public boolean greyscale;

    // PPUStatus flags
    public boolean verticalBlank;
    public boolean spriteZeroHit;
    public boolean spriteOverflow;

    private static int[] patternTableAddresses = {0x0000, 0x1000};
    private static int[] nameTableAddresses = {0x2000, 0x2400, 0x2800, 0x2C00};

    // Across and going down (add 1, add 32)
    private static int[] tableIncrements = {0x01, 0x20};

    public PPU() {
        vRam = new VRAM();
        spRam = new SPRAM();

        cycles = 0;
        evenFlag = true;

        generateNMI = true;
        normalSpriteSize = true;
        bgTableIndex = 0;
        spriteTableAddressIndex = 0;
        tableIncrementsIndex = 0;
        nameTableAddressIndex = 0;

        green = true;
        blue = true;
        red = true;
        showSprites = true;
        showBG = true;
        leftSprites = true;
        leftBG = true;
        greyscale = true;

        verticalBlank = false;
        spriteZeroHit = false;
        spriteOverflow = false;
    }
}
