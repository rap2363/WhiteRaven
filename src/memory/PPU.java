package memory;

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
