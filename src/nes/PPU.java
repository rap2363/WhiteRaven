package nes;

import memory.CircularBuffer;
import memory.ConsoleMemory;
import operations.Utilities;

import java.awt.image.BufferedImage;

/**
 * Models the PPU architecture
 */
public class PPU extends Processor {
    private boolean evenFlag;
    private int scanlineNumber;
    private int scanlineCycle;
    private int address;
    private MirroringMode mirroringMode = MirroringMode.HORIZONTAL;
    private BufferedImage backgroundImage;
    public boolean imageReady;

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
    public boolean verticalBlank;
    public boolean triggerVerticalBlank;
    private boolean spriteZeroHit;
    private boolean spriteOverflow;

    private static final int PALLETE_OFFSET = 0x3F00;

    // PPU register addresses
    private static final int PPU_CTRL = 0x2000;
    private static final int PPU_MASK = 0x2001;
    private static final int PPU_STATUS = 0x2002;

    private CircularBuffer<Byte> nameTableBytes;
    private CircularBuffer<Byte> attributeTableBytes;
    private CircularBuffer<Byte> lowBGTileBytes;
    private CircularBuffer<Byte> highBGTileBytes;

    private static int[] patternTableAddresses = {0x0000, 0x1000};
    private static int[] nameTableAddresses = {0x2000, 0x2400, 0x2800, 0x2C00};
    private static int[] attributeTableAddresses = {0x23C0, 0x27C0, 0x2BC0, 0x2FC0};

    // Across and going down (add 1, add 32)
    private static int[] tableIncrements = {0x01, 0x20};

    private static final int NUM_TOTAL_SCANLINES = 261;
    private static final int PPU_CYCLES_PER_SCANLINE = 341;
    private static final int SCREEN_WIDTH = 256;
    private static final int SCREEN_HEIGHT = 240;
    private static final int NUM_VISIBLE_SCANLINES = SCREEN_HEIGHT;

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
    ConsoleMemory memory;

    public PPU(final ConsoleMemory consoleMemory) {
        this.memory = consoleMemory;
        scanlineNumber = 241;
        scanlineCycle = 0;
        cycleCount = 0;
        evenFlag = true;
        backgroundImage = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        imageReady = false;

        generateNMI = true;
        normalSpriteSize = true;
        bgTableIndex = 0;
        spriteTableAddressIndex = 0;
        tableIncrementsIndex = 0;

        green = false;
        blue = false;
        red = false;
        showSprites = false;
        showBG = false;
        leftSprites = false;
        leftBG = false;
        greyscale = false;

        verticalBlank = false;
        triggerVerticalBlank = false;
        spriteZeroHit = false;
        spriteOverflow = false;

        nameTableBytes = new CircularBuffer(2);
        attributeTableBytes = new CircularBuffer(2);
        lowBGTileBytes = new CircularBuffer(2);
        highBGTileBytes = new CircularBuffer(2);
    }

    public void setMirroringMode(MirroringMode mirroringMode) {
        this.mirroringMode = mirroringMode;
    }

    /**
     * The PPU has an associated scanline number [0, 261], scanlineCycle number [0, 255], and a cycleCount at any given
     * execute(). Only scanlines 1-240 render pixels. Depending on the scanline number and scanlineCycle number, we take
     * different actions which are handled in executePixel(). After executePixel() we increment the scanlineCycle and
     * scanlineNumber. The PPU uses 341 cycles for each scanline, so one frame takes 341 * 262 = 89,342 cycles.
     *
     * scanlineNumber:
     *
     * 0-239:   Render 240 256-pixel scanlines to the screen (an image buffer).
     * 240:     Idle
     * 241:     The PPU idles during this scanline
     * 242-260: On the first cycle of scanline 242, set the VBlank flag, which will generate an NMI in the CPU.
     * 261:     Make the same accesses we would on a visible scanline, but don't render any actual pixels.
     *
     * Depending on the scanlineCycle, we take different actions in updating the vramAddress (see renderScanline())
     *
     */
    @Override
    public void execute() {
        readCtrl();
        readMask();

        if (scanlineNumber == NUM_TOTAL_SCANLINES - 1) {
            preRenderScanline(scanlineCycle);
        } else if (scanlineNumber < NUM_VISIBLE_SCANLINES) {
            renderScanline(scanlineNumber, scanlineCycle);
        } else if (scanlineNumber == NUM_VISIBLE_SCANLINES + 1) {
            postRenderScanline(scanlineCycle);
        } // else the PPU idles

        // Increment the scanlineNumber and scanlineCycle
        scanlineCycle = (scanlineCycle + 1) % PPU_CYCLES_PER_SCANLINE;
        if (scanlineCycle == 0) {
            scanlineNumber = (scanlineNumber + 1) % NUM_TOTAL_SCANLINES;
        }

        writeStatus();
    }

    /**
     * This function is called specifically before rendering. All memory accesses made are the same, but no pixels
     * are actually pushed to the screen.
     */
    private void preRenderScanline(int scanlineCycle) {
        if (scanlineCycle == 1) {
            verticalBlank = false;
            spriteZeroHit = false;
            spriteOverflow = false;
        }
        renderScanline(-1, scanlineCycle);
        if (renderingEnabled() && scanlineCycle >= 280 && scanlineCycle <= 304) {
            this.memory.copyVertical();
        }
    }

    /**
     * Render a full scanline. One call of this function will render one pixel on the scanlineNumber. Note that we
     * fetch the tile data for sprites in the next scanline during the 257-320 cycles of the current scanline.
     *
     * scanlineCycle:
     *
     * 0: idle
     * 1-256: Fetch tile data every 8 cycles and render
     * 257-320: Fetch the sprite tile data for the next scanline
     * 321-336: First two BG tiles for the next scanline are fetched
     * 337-340: Two bytes are fetched, but for unknown purposes, so we just idle.
     */
    private void renderScanline(int scanlineNumber, int scanlineCycle) {
        if (!renderingEnabled()) {
            return;
        }

        if (scanlineCycle == 0 || scanlineCycle >= 337) {
            renderEightPixels(scanlineNumber, scanlineCycle);
            return;
        }

        if (scanlineCycle < 257 || scanlineCycle > 321) {
            // Fetch background tiles and render
            int tileCycle = scanlineCycle % 8;
            if (tileCycle == 0) {
                renderEightPixels(scanlineNumber, scanlineCycle);
                this.memory.incrementHorizontal();
            } else if (tileCycle == 1) {
                fetchNameTableByte();
            } else if (tileCycle == 3) {
                fetchAttributeTableByte();
            } else if (tileCycle == 5) {
                fetchLowBGTileByte();
            } else if (tileCycle == 7) {
                fetchHighBGTileByte();
            }
        } else if (renderingEnabled() && scanlineCycle == 257) {
            this.memory.copyHorizontal();
        } else {
            // Fetch the sprite data for the next scanline
        }

        if (renderingEnabled() && scanlineCycle == 256) {
            this.memory.incrementVertical();
        }
    }

    /**
     * Renders eight pixels from (x, y) --> (x + 7, y).
     */
    private void renderEightPixels(int y, int x) {
        if (y < 0 || y >= SCREEN_HEIGHT || x < 0 || x + 7 >= SCREEN_WIDTH) {
            return;
        }

        int attributeSquareX = x % 0x20; // Four 8 x 8 tiles = 32 = 0x20
        int attributeSquareY = y % 0x20;
        int attributeSquareNumber = 0;
        if (attributeSquareX >= 0x10 && attributeSquareY < 0x10) {
            attributeSquareNumber = 1;
        } else if (attributeSquareX < 0x10 && attributeSquareY >= 0x10) {
            attributeSquareNumber = 2;
        } else if (attributeSquareX >= 0x10 && attributeSquareY >= 0x10) {
            attributeSquareNumber = 3;
        }

        byte attributeTwoBitColor = (byte) ((attributeTableBytes.peek() >> (attributeSquareNumber * 2)) & 0x03);

        // Render the eight pixels
        for (int i = 0; i < 8; i++) {
            byte bHigh = (byte) (highBGTileBytes.peek() >> (7 - i) & 0x01);
            byte bLow = (byte) (lowBGTileBytes.peek() >> (7 - i) & 0x01);
            // Now we take the attribute tile 2-bits and concatenate them with the bits from the 8x8 tile:
            byte bgPalletePixelIndex = (byte) ((attributeTwoBitColor << 2) | bHigh << 1 | bLow);
            int bgRGB = getColorFromPalleteIndex(bgPalletePixelIndex);
            backgroundImage.setRGB(x + i, y, bgRGB);
        }
//        System.out.println(x + ", " + y + " - " + attributeSquareX + ", " + attributeSquareY + ": " + nameTableBytes.peek() + ", " + lowBGTileBytes.peek() + ", " + highBGTileBytes.peek());
    }

    /**
     * Returns the RGB color value given a pallete index
     */
    private int getColorFromPalleteIndex(byte palleteColorIndex) {
        byte systemColorIndex = this.memory.readFromPPU(
                Utilities.addUnsignedByteToInt(PALLETE_OFFSET, palleteColorIndex));
        int[] rgb = systemPalleteColors[Utilities.toUnsignedValue(systemColorIndex) & 0x3F];
        int rgbColor = rgb[0] << 16 & 0xFF0000 | rgb[1] << 8 & 0x00FF00 | rgb[2] & 0x0000FF;
        return rgbColor;
    }

    /**
     * Fetch the current name table byte using vramAddress
     *
     * @return
     */
    private void fetchNameTableByte() {
        int tileAddress = 0x2000 | (this.memory.getVramAddress() & 0x0FFF);
        nameTableBytes.push(this.memory.readFromPPU(tileAddress));
    }

    /**
     * Fetch the current attribute table byte using vramAddress
     *
     * @return
     */
    private void fetchAttributeTableByte() {
        int vramAddress = this.memory.getVramAddress();
        int attributeAddress = 0x23C0 | (vramAddress & 0x0C00) | ((vramAddress >> 4) & 0x38) | ((vramAddress >> 2) & 0x07);
        attributeTableBytes.push(this.memory.readFromPPU(attributeAddress));
    }

    /**
     * Fetch the low BG tile byte. We do this by taking the name table byte as an offset from the pattern table
     * (either 0x0000 or 0x1000). Then we use the fineYScroll to get the specific byte in our 8x8 tile.
     *
     * @return
     */
    private void fetchLowBGTileByte() {
        int patternTable = patternTableAddresses[bgTableIndex];
        int fineYScroll = this.memory.getFineYScroll();
        lowBGTileBytes.push(
                this.memory.readFromPPU(
                        patternTable + Utilities.toUnsignedValue(nameTableBytes.peek()) * 0x10 + fineYScroll));
    }

    /**
     * Fetch the high BG tile byte
     *
     * @return
     */
    private void fetchHighBGTileByte() {
        int patternTable = patternTableAddresses[bgTableIndex];
        int fineYScroll = this.memory.getFineYScroll();
        highBGTileBytes.push(
                this.memory.readFromPPU(
                        patternTable + Utilities.toUnsignedValue(nameTableBytes.peek()) * 0x10 + fineYScroll + 0x08));
    }

    private void postRenderScanline(int scanlineCycle) {
        if (scanlineCycle == 1) {
            verticalBlank = true;
            triggerVerticalBlank = generateNMI;
            imageReady = true;
        }
    }

    public BufferedImage getImage() {
        imageReady = false;
        return backgroundImage;
    }

    /**
     * Return the pallete index (4-bits) of a given background pixel at x,y. This method will return a number from
     * from 0 -> F. The x,y specified is a coordinate pair from (0,0) --> (511, 479). This method will use the
     * correct nametable according to the current mirroring mode.
     *
     * @param x
     * @param y
     * @return
     */
    private byte backgroundPixelPalleteIndex(int x, int y) {
        int nameTableAddress = getNameTableAddress(x, y);
        x %= SCREEN_WIDTH;
        y %= SCREEN_HEIGHT;
        int nameTableTile = getNameTableTileNumber(x, y);

        byte b = this.memory.readFromPPU(nameTableAddress + nameTableTile);
        byte bLow = this.memory.readFromPPU(patternTableAddresses[bgTableIndex] + b + y % 8);
        byte bHigh = this.memory.readFromPPU(patternTableAddresses[bgTableIndex] + b + y % 8 + 0x08);

        bLow >>= (7 - x % 8);
        bHigh >>= (7 - x % 8);

        int attributeTableAddress = getAttributeTableAddress(x, y);
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

        byte attributeTwoBitColor = (byte) (this.memory.readFromPPU(attributeTableAddress + attributeTileNumber)
                >> (attributeSquareNumber * 2));

        attributeTwoBitColor &= 0x0003;
        bHigh &= 0x01;
        bLow &= 0x01;

        // Now we take the attribute tile 2-bits and concatenate them with the bits from the 8x8 tile:
        return (byte) ((attributeTwoBitColor << 2) | bHigh << 1 | bLow);
    }

    /**
     * Return the correct name table address
     *
     * @param x
     * @param y
     * @return
     */
    private int getNameTableAddress(int x, int y) {
        int quadrant = 0; // upper left
        if (x >= SCREEN_WIDTH && y < SCREEN_HEIGHT) {
            quadrant = 1; // upper right
        } else if (x < SCREEN_WIDTH && y >= SCREEN_HEIGHT) {
            quadrant = 2; // lower left
        } else if (x >= SCREEN_WIDTH && y >= SCREEN_HEIGHT) {
            quadrant = 3;
        }

        return nameTableAddresses[quadrant];
    }

    /**
     * Return the correct attribute table address
     *
     * @param x
     * @param y
     * @return
     */
    private int getAttributeTableAddress(int x, int y) {
        int quadrant = 0; // upper left
        if (x >= SCREEN_WIDTH && y < SCREEN_HEIGHT) {
            quadrant = 1; // upper right
        } else if (x < SCREEN_WIDTH && y >= SCREEN_HEIGHT) {
            quadrant = 2; // lower left
        } else if (x >= SCREEN_WIDTH && y >= SCREEN_HEIGHT) {
            quadrant = 3;
        }

        return attributeTableAddresses[quadrant];
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
        final byte ctrl = memory.read(PPU_CTRL);

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
        final byte ctrl = memory.read(PPU_MASK);

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
     * Helper to know whether rendering is currently enabled.
     * @return
     */
    private boolean renderingEnabled() {
        return showSprites && showBG;
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

        memory.write(PPU_STATUS, value);
    }
}
