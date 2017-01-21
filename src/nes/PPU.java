package nes;

import memory.ConsoleMemory;

/**
 * Models the PPU architecture
 */
public class PPU {
    private boolean evenFlag;
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

    // Across and going down (add 1, add 32)
    private static int[] tableIncrements = {0x01, 0x20};

    private static final int NUM_VISIBLE_SCANLINES = 240;
    private static final int NUM_TOTAL_SCANLINES = 261;
    private static final int PPU_CYCLES_PER_SCANLINE = 341;

    ConsoleMemory consoleMemory;
    public long cycles;

    public PPU(final ConsoleMemory consoleMemory) {
        this.consoleMemory = consoleMemory;
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

        scrollX = 0;
        scrollY = 0;
    }

    /**
     * The PPU executes cycleCount to render one scanline. scanlineNumber is a number between 0 and 261.
     *
     * @param scanlineNumber
     */
    public int executeScanline(int scanlineNumber) {
        scanlineNumber %= NUM_TOTAL_SCANLINES;
        if (scanlineNumber <= NUM_VISIBLE_SCANLINES) {

        }

        return PPU_CYCLES_PER_SCANLINE;
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
