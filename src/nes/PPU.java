package nes;

import memory.ConsoleMemory;

/**
 * Models the PPU architecture
 */
public class PPU extends Processor {
    private boolean evenFlag;
    private int scanlineNumber;
    private int column;
    private int address;

    // Values from PPUControl
    private boolean generateNMI;
    private boolean normalSpriteSize;
    private int bgTableIndex;
    private int spriteTableAddressIndex;
    private int tableIncrementsIndex;
    private int nameTableAddressIndex;

    // Values from PPUMask
    private boolean green;
    private boolean blue;
    private boolean red;
    private boolean showSprites;
    private boolean showBG;
    private boolean leftSprites;
    private boolean leftBG;
    private boolean greyscale;

    // PPUStatus flags
    private boolean verticalBlank;
    private boolean spriteZeroHit;
    private boolean spriteOverflow;

    // Scroll registers
    private byte scrollX;
    private byte scrollY;

    // PPU register addresses
    private static final int PPU_CTRL = 0x2000;
    private static final int PPU_MASK = 0x2001;
    private static final int PPU_STATUS = 0x2002;
    private static final int SPR_ADDRESS = 0x2003;
    private static final int SPR_DATA = 0x2004;
    private static final int PPU_SCROLL = 0x2005;
    private static final int PPU_ADDRESS = 0x2006;
    private static final int PPU_DATA = 0x2007;

    private static int[] patternTableAddresses = {0x0000, 0x1000};
    private static int[] nameTableAddresses = {0x2000, 0x2400, 0x2800, 0x2C00};
    private static int[] attributeTableAddresses = {0x23C0, 0x27C0, 0x2BC0, 0x2FC0};
    private static int IMAGE_PALLETE_OFFSET = 0x3F00;
    private static int SPRITE_PALLETE_OFFSET = 0x3F10;

    // Across and going down (add 1, add 32)
    private static int[] tableIncrements = {0x01, 0x20};

    private static final int NUM_VISIBLE_SCANLINES = 240;
    private static final int NUM_TOTAL_SCANLINES = 261;
    private static final int PPU_CYCLES_PER_SCANLINE = 341;

    public static final int[][] systemPalleteColors = {
            {0x75, 0x75, 0x75}, // 0x00
            {0x27, 0x1B, 0x8F}, // 0x01
            {0x00, 0x00, 0xAB}, // 0x02
            {0x47, 0x00, 0x9F}, // 0x03
            {0x8F, 0x00, 0x77}, // 0x04
            {0xAB, 0x00, 0x13}, // 0x05
            {0xA7, 0x00, 0x00}, // 0x06
            {0x7F, 0x0B, 0x00}, // 0x07
            {0x43, 0x2F, 0x00}, // 0x08
            {0x00, 0x47, 0x00}, // 0x09
            {0x00, 0x51, 0x00}, // 0x0A
            {0x00, 0x3F, 0x17}, // 0x0B
            {0x1B, 0x3F, 0x5F}, // 0x0C
            {0x00, 0x00, 0x00}, // 0x0D
            {0x00, 0x00, 0x00}, // 0x0E
            {0x00, 0x00, 0x00}, // 0x0F

            {0xBC, 0xBC, 0xBC}, // 0x10
            {0x00, 0x73, 0xEF}, // 0x11
            {0x23, 0x3B, 0xEF}, // 0x12
            {0x83, 0x00, 0xF3}, // 0x13
            {0xBF, 0x00, 0xBF}, // 0x14
            {0xE7, 0x00, 0x5B}, // 0x15
            {0xDB, 0x2B, 0x00}, // 0x16
            {0xCB, 0x4F, 0x0F}, // 0x17
            {0x8B, 0x73, 0x00}, // 0x18
            {0x00, 0x97, 0x00}, // 0x19
            {0x00, 0xAB, 0x00}, // 0x1A
            {0x00, 0x93, 0x3B}, // 0x1B
            {0x00, 0x83, 0x8B}, // 0x1C
            {0x00, 0x00, 0x00}, // 0x1D
            {0x00, 0x00, 0x00}, // 0x1E
            {0x00, 0x00, 0x00}, // 0x1F

            {0xFF, 0xFF, 0xFF}, // 0x20
            {0x3F, 0xBF, 0xFF}, // 0x21
            {0x5F, 0x97, 0xFF}, // 0x22
            {0xA7, 0x8B, 0xFD}, // 0x23
            {0xF7, 0x7B, 0xFF}, // 0x24
            {0xFF, 0x77, 0xB7}, // 0x25
            {0xFF, 0x77, 0x63}, // 0x26
            {0xFF, 0x9B, 0x3B}, // 0x27
            {0xF3, 0xBF, 0x3F}, // 0x28
            {0x83, 0xD3, 0x13}, // 0x29
            {0x4F, 0xDF, 0x4B}, // 0x2A
            {0x58, 0xF8, 0x98}, // 0x2B
            {0x00, 0xEB, 0xDB}, // 0x2C
            {0x00, 0x00, 0x00}, // 0x2D
            {0x00, 0x00, 0x00}, // 0x2E
            {0x00, 0x00, 0x00}, // 0x2F

            {0xFF, 0xFF, 0xFF}, // 0x30
            {0xAB, 0xE7, 0xFF}, // 0x31
            {0xC7, 0xD7, 0xFF}, // 0x32
            {0xD7, 0xCB, 0xFF}, // 0x33
            {0xFF, 0xC7, 0xFF}, // 0x34
            {0xFF, 0xC7, 0xDB}, // 0x35
            {0xFF, 0xBF, 0xB3}, // 0x36
            {0xFF, 0xDB, 0xAB}, // 0x37
            {0xFF, 0xE7, 0xA3}, // 0x38
            {0xE3, 0xFF, 0xA3}, // 0x39
            {0xAB, 0xF3, 0xBF}, // 0x3A
            {0xB3, 0xFF, 0xCF}, // 0x3B
            {0x9F, 0xFF, 0xF3}, // 0x3C
            {0x00, 0x00, 0x00}, // 0x3D
            {0x00, 0x00, 0x00}, // 0x3E
            {0x00, 0x00, 0x00}  // 0x3F
    };
    ConsoleMemory consoleMemory;

    public PPU(final ConsoleMemory consoleMemory) {
        this.consoleMemory = consoleMemory;
        cycleCount = 0;
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

        scrollX = 0;
        scrollY = 0;
    }

    @Override
    public void execute() {

    }

    /**
     * Return the pallete index (4-bits) of a given background pixel at x,y. This method will return a number
     * from 0 -> F.
     *
     * @param x
     * @param y
     * @return
     */
    private int backgroundPixelPalleteIndex(int x, int y) {
        int nameTableTile = getNameTableTileNumber(x, y);

        byte b = this.consoleMemory.readFromPPU(nameTableAddresses[nameTableAddressIndex] + nameTableTile);
        byte bLow = this.consoleMemory.readFromPPU(patternTableAddresses[bgTableIndex] + b + y % 8);
        byte bHigh = this.consoleMemory.readFromPPU(patternTableAddresses[bgTableIndex] + b + y % 8 + 0x08);

        bLow >>= (7 - x % 8);
        bHigh >>= (7 - x % 8);

        int attributeTileNumber = getAttributeTileNumber(x, y);
        int attributeSquareX = x % 0x20; // Four 8 x 8 tiles = 32 = 0x20
        int attributeSquareY = y % 0x20;
        int attributeSquareNumber = 0;
        if (attributeSquareX > 0x10 && attributeSquareY < 0x10) {
            attributeSquareNumber = 1;
        } else if (attributeSquareX < 0x10 && attributeSquareY > 0x10) {
            attributeSquareNumber = 2;
        } else if (attributeSquareX > 0x10 && attributeSquareY > 0x10) {
            attributeSquareNumber = 3;
        }

        byte attributeTwoBitColor = (byte) (this.consoleMemory.readFromPPU(attributeTableAddresses[attributeTileNumber])
                >> (attributeSquareNumber * 2));

        attributeTwoBitColor &= 0x0003;
        bHigh &= 0x01;
        bLow &= 0x01;

        // Now we take the attribute tile 2-bits and concatenate them with the bits from the 8x8 tile:
        int fullColorIndex = (attributeTwoBitColor << 2) | bHigh << 1 | bLow;

        return fullColorIndex;
    }

    /**
     * Calculate the tile number the current pixel is in.
     *
     * @return
     */
    private int getNameTableTileNumber(int x, int y) {
        return (y / 8) * 32 + (x / 8);
    }

    /**
     * Calculate the attribute table tile the current pixel is in.
     * @return
     */
    private int getAttributeTileNumber(int x, int y) {
        return (y / 32) * 8 + (x / 32);
    }

    /**
     * Read in and load the PPU_CTRL register's values into our local variables.
     * The PPU_CTRL bits hold information regarding:
     * 0-1: Base nametable address
     * 2:   VRAM address increment
     * 3:   Sprite pattern table address for 8x8 sprites
     * 4:   Background pattern table address
     * 5:   Sprite size
     * 6:   PPU master/slave select *Ignored for now*
     * 7:   Generate an NMI at start of the VBLANK
     */
    private void readCtrl() {
        final byte ctrl = consoleMemory.read(PPU_CTRL);

        nameTableAddressIndex = ctrl & 0x03;
        tableIncrementsIndex = (ctrl >> 2) & 0x01;
        spriteTableAddressIndex = (ctrl >> 3) & 0x01;
        bgTableIndex = (ctrl >> 4) & 0x01;
        normalSpriteSize = ((ctrl >> 5) & 0x01) == 0x00;
        generateNMI = ((ctrl >> 7) & 0x01) == 0x01;
    }

    /**
     * Read in and load the PPU_MASK register's values into our local variables.
     * The PPU_MASK bits hold information regarding:
     * 0: Greyscale
     * 1: Show background in left pixels
     * 2: Show sprites in left pixels
     * 3: Show background
     * 4: Show sprites
     * 5: Emphasize red
     * 6: Emphasize green
     * 7: Emphasize blue
     */
    private void readMask() {
        final byte ctrl = consoleMemory.read(PPU_MASK);

        greyscale = (ctrl & 0x01) == 0x01;
        leftBG = (ctrl >> 1 & 0x01) == 0x01;
        leftSprites = (ctrl >> 2 & 0x01) == 0x01;
        showBG = (ctrl >> 3 & 0x01) == 0x01;
        showSprites = (ctrl >> 4 & 0x01) == 0x01;
        red = (ctrl >> 5 & 0x01) == 0x01;
        green = (ctrl >> 6 & 0x01) == 0x01;
        blue = (ctrl >> 7 & 0x01) == 0x01;
    }

    /**
     * Write to the PPU_STATUS register.
     * We specifically write information to bits that represent the following:
     * 0-4: *ignored*
     * 5: Sprite overflow
     * 6: Sprite zero hit
     * 7: Vertical blank has started
     */
    private void writeStatus() {
        byte value = 0x0;
        value |= spriteOverflow ? 0x20 : 0x00;
        value |= spriteZeroHit ? 0x40 : 0x00;
        value |= verticalBlank ? 0x80 : 0x00;

        consoleMemory.write(PPU_STATUS, value);
    }

    /**
     * Read from the X,Y scroll latch to obtain our X and Y values for the scroll position.
     */
    private void readScroll() {
        int xy = consoleMemory.readScrollLatch();
        scrollX = (byte) (xy >> 7);
        scrollY = (byte) xy;
    }

    /**
     * Read the PPU_ADDRESS in from the address latch
     */
    private void readAddress() {
        address = consoleMemory.readAddressLatch();
    }
}
