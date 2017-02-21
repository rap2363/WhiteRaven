package nes;

import memory.CircularBuffer;
import memory.ConsoleMemory;
import memory.Sprite;
import operations.Utilities;

/**
 * Models the PPU architecture
 */
public class PPU extends Processor {
    private boolean evenFlag;
    private int scanlineNumber;
    private int scanlineCycle;
    private MirroringMode mirroringMode;
    private final CircularBuffer<int[]> imageBuffer;
    private static final int NUM_BUFFERED_IMAGES = 2;

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

    public boolean triggerVerticalBlank;

    public static final int SCREEN_WIDTH = 256;
    public static final int SCREEN_HEIGHT = 240;
    private static final int PALETTE_OFFSET = 0x3F00;

    // PPU register addresses
    private static final int PPU_CTRL = 0x2000;
    private static final int PPU_MASK = 0x2001;
    private static final int PPU_STATUS = 0x2002;

    private final CircularBuffer<Byte> nameTableBytes;
    private final CircularBuffer<Byte> attributeTableBytes;

    private final Sprite[] sprites;

    private static final int[] patternTableAddresses = {0x0000, 0x1000};
    private static final int[] nameTableAddresses = {0x2000, 0x2400, 0x2800, 0x2C00};
    private static final int[] attributeTableAddresses = {0x23C0, 0x27C0, 0x2BC0, 0x2FC0};

    // Across and going down (add 1, add 32)
    private static final int[] tableIncrements = {0x01, 0x20};

    private static final int NUM_TOTAL_SCANLINES = 262;
    private static final int PPU_CYCLES_PER_SCANLINE = 341;
    private static final int NUM_VISIBLE_SCANLINES = SCREEN_HEIGHT;

    public static final int[][] systemPaletteColors = {
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
        mirroringMode = MirroringMode.HORIZONTAL;
        scanlineNumber = 241;
        scanlineCycle = 0;
        cycleCount = 0;
        evenFlag = true;
        imageBuffer = new CircularBuffer(NUM_BUFFERED_IMAGES);
        for (int i = 0; i < NUM_BUFFERED_IMAGES; i++) {
            imageBuffer.push(new int[SCREEN_WIDTH * SCREEN_HEIGHT]);
        }

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

        triggerVerticalBlank = false;

        nameTableBytes = new CircularBuffer<>(2);
        attributeTableBytes = new CircularBuffer<>(2);

        sprites = new Sprite[8]; // Eight sprites per line max
    }

    public void setMirroringMode(MirroringMode mirroringMode) {
        this.mirroringMode = mirroringMode;
    }

    /**
     * The PPU has an associated scanline number [0, 261], scanlineCycle number [0, 255], and a cycleCount at any given
     * execute(). Only scanlines 1-240 render pixels. Depending on the scanline number and scanlineCycle number, we take
     * different actions which are handled in preRenderScanline(), renderScanline(), or postRenderScanline().
     * Afterwards we increment the scanlineCycle and scanlineNumber. The PPU uses 341 cycles for each scanline, so one
     * frame takes 341 * 262 = 89,342 cycles.
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
        }

        // Increment the scanlineNumber and scanlineCycle
        scanlineCycle = ++scanlineCycle % PPU_CYCLES_PER_SCANLINE;
        if (scanlineCycle == 0) {
            scanlineNumber = ++scanlineNumber % NUM_TOTAL_SCANLINES;
        }
        if (scanlineNumber == 0 && scanlineCycle == 1) {
            evenFlag = !evenFlag;
        }

        cycleCount++;
    }

    /**
     * This function is called specifically before rendering. All memory accesses made are the same, but no pixels
     * are actually pushed to the screen. The image to write to is cleared on this screen.
     */
    private void preRenderScanline(int scanlineCycle) {
        if (scanlineCycle == 1) {
            this.memory.clearVblank();
            this.memory.clearSpriteZeroHit();
            this.memory.clearSpriteOverflow();
            imageBuffer.push(new int[SCREEN_HEIGHT * SCREEN_WIDTH]);
        }

        renderScanline(-1, scanlineCycle);
        if (!renderingDisabled() && scanlineCycle >= 280 && scanlineCycle <= 304) {
            this.memory.copyVertical();
        }

        // For even frames, we skip a cycle
        if (scanlineCycle == PPU_CYCLES_PER_SCANLINE - 1 && !evenFlag) {
            this.scanlineCycle = 0;
            this.scanlineNumber = 0;
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
        if (renderingDisabled()) {
            return;
        }

        if (scanlineCycle == 0 || scanlineCycle >= 337) {
            renderEightPixels(scanlineCycle, scanlineNumber);
            return;
        }

        if (scanlineCycle < 249 || scanlineCycle > 321) {
            // Fetch background tiles and render
            int tileCycle = scanlineCycle % 8;
            if (tileCycle == 0) {
                renderEightPixels(scanlineCycle, scanlineNumber);
                this.memory.incrementHorizontal();
            } else if (tileCycle == 1) {
                fetchNameTableByte();
            } else if (tileCycle == 3) {
                fetchAttributeTableByte();
            }
        }

        if (scanlineCycle == 256) {
            this.memory.incrementVertical();
        } else if (scanlineCycle == 257) {
            // Comment out the line below for an interesting visual effect
            this.memory.copyHorizontal();
            // Fetch the sprite data for the next scanline
            if (scanlineNumber >= 0) {
                if (this.memory.fetchSprites(sprites, scanlineNumber + 1)) {
                    this.memory.setSpriteOverflow();
                }
            }
        }
    }

    /**
     * Renders eight pixels from (x, y) --> (x + 7, y).
     *
     * @param x
     * @param y
     */
    private void renderEightPixels(int x, int y) {
        if (y < 0 || y >= SCREEN_HEIGHT || x < 0 || x + 7 >= SCREEN_WIDTH) {
            return;
        }

        byte bgPixels = renderEightBackgroundPixels(x, y);
        renderEightSpritePixels(x, y, bgPixels);
    }

    /**
     * Render eight background pixels from (x, y) --> (x + 7, y). We return a byte where 1's are if we have set a
     * pixel to something non-transparent.
     *
     * @param x
     * @param y
     * @return
     */
    private byte renderEightBackgroundPixels(int x, int y) {
        if (!showBG) {
            return 0x0;
        }

        byte attributeTwoBitColor = attributeTableBytes.peek();
        byte renderedPixels = 0x0;

        // Render the eight background pixels
        final byte highBG = fetchHighBGTileByte();
        final byte lowBG = fetchLowBGTileByte();
        for (int i = 0; i < 8; i++) {
            int shiftX = 7 - i;
            final byte bitHigh = (byte) ((highBG >> shiftX) & 0x01);
            final byte bitLow = (byte) ((lowBG >> shiftX) & 0x01);
            // Now we take the attribute tile 2-bits and concatenate them with the bits from the 8x8 tile:
            int bgPalettePixelIndex = ((attributeTwoBitColor << 2) | bitHigh << 1 | bitLow) & 0x0F;
            int bgRGB = getColorFromBackgroundPalette(bgPalettePixelIndex);
            if (!((x + i) < 8 && !leftBG)) {
                setPixelInImage(x+i, y, bgRGB, imageBuffer.peek());
                if (bitHigh == 0x01 || bitLow == 0x01) {
                    renderedPixels |= 0x01 << shiftX;
                }
            }
        }

        return renderedPixels;
    }

    /**
     * Render eight sprite pixels from (x, y) --> (x + 7, y)
     *
     * @param x
     * @param y
     * @param bgPixels
     */
    private void renderEightSpritePixels(final int x, final int y, final byte bgPixels) {
        if (!showSprites) {
            return;
        }

        // Render sprites in order of lowest to highest priority (i.e. backwards)
        for (int j = 7; j >= 0; j--) {
            final Sprite sprite = sprites[j];

            // Only render sprites if they're within our eight bit window
            if (sprite == null
                    || !((Utilities.inRange(sprite.x, x, x + 7) || Utilities.inRange(sprite.x + 7, x, x + 7)))) {
                continue;
            }

            int shiftY = (y - sprite.y) % 0x08;
            if (sprite.flippedVertically()) {
                shiftY = 7 - shiftY;
            }
            byte spriteLow = this.memory.readFromPPU(
                    patternTableAddresses[spriteTableAddressIndex] + sprite.patternTableIndex * 0x10 + shiftY);
            byte spriteHigh = this.memory.readFromPPU(
                    patternTableAddresses[spriteTableAddressIndex] + sprite.patternTableIndex * 0x10 + shiftY + 0x08);

            for (int pixelX = Math.max(x, sprite.x); pixelX <= Math.min(x + 7, sprite.x + 7); pixelX++) {
                int deltaX = sprite.x + 7 - pixelX; // A value between 0 and 7
                int shiftX = sprite.flippedHorizontally() ? (7 - deltaX) : deltaX;
                byte sLow = (byte) ((spriteLow >> shiftX) & 0x01);
                byte sHigh = (byte) ((spriteHigh >> shiftX) & 0x01);

                // Take the 2-bit attribute and concatenate with the sLow and sHigh like for the background
                int spritePaletteIndex = ((sprite.attributes << 2) | (sHigh << 1) | sLow) & 0x0F;
                int spriteRGB = getColorFromSpritePalette(spritePaletteIndex);

                if (!(sHigh == 0x0 && sLow == 0x0) && !(pixelX < 8 && !leftSprites)) {
                    setPixelInImage(pixelX, y, spriteRGB, imageBuffer.peek());
                }

                // Check for a sprite-zero hit
                if (sprite.priority == 0) {
                    if (sLow == 0x01 || sHigh == 0x01) {
                        if (bgPixels >> (pixelX - x) == 0x01) {
                            this.memory.setSpriteZeroHit();
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the RGB color value given a palette index into the background palette
     */
    private int getColorFromBackgroundPalette(int paletteColorIndex) {
        byte systemColorIndex = this.memory.readFromPPU(PALETTE_OFFSET + paletteColorIndex);
        int[] rgb = systemPaletteColors[Utilities.toUnsignedValue(systemColorIndex) & 0x3F];
        int rgbColor = rgb[0] << 16 & 0xFF0000 | rgb[1] << 8 & 0x00FF00 | rgb[2] & 0x0000FF;
        return rgbColor;
    }

    /**
     * Return the RGB color value given a palette index into the sprite palette.
     *
     * @param paletteColorIndex
     * @return
     */
    private int getColorFromSpritePalette(int paletteColorIndex) {
        return getColorFromBackgroundPalette(paletteColorIndex + 0x10);
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
        int tileSquare = ((vramAddress >> 4) & 0x04) + (vramAddress & 0x02); // Returns 0, 2, 4, or 6

        attributeTableBytes.push((byte) (this.memory.readFromPPU(attributeAddress) >> tileSquare));
    }

    /**
     * Fetch the low BG tile byte. We do this by taking the name table byte as an offset from the pattern table
     * (either 0x0000 or 0x1000). Then we use the fineYScroll to get the specific byte in our 8x8 tile.
     *
     * @return
     */
    private byte fetchLowBGTileByte() {
        int patternTable = patternTableAddresses[bgTableIndex];
        int fineYScroll = this.memory.getFineYScroll();
        return this.memory.readFromPPU(
                        patternTable + Utilities.toUnsignedValue(nameTableBytes.peek()) * 0x10 + fineYScroll);
    }

    /**
     * Fetch the high BG tile byte
     *
     * @return
     */
    private byte fetchHighBGTileByte() {
        int patternTable = patternTableAddresses[bgTableIndex];
        int fineYScroll = this.memory.getFineYScroll();
        return this.memory.readFromPPU(
                        patternTable + Utilities.toUnsignedValue(nameTableBytes.peek()) * 0x10 + fineYScroll + 0x08);
    }

    private void postRenderScanline(int scanlineCycle) {
        if (scanlineCycle == 1) {
            this.memory.setVblank();
            triggerVerticalBlank = generateNMI;
        }
    }

    public int[] getImage() {
        return imageBuffer.get();
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
        final byte mask = memory.read(PPU_MASK);

        greyscale = (mask & 0x01) == 0x01;
        leftBG = ((mask >> 1) & 0x01) == 0x01;
        leftSprites = ((mask >> 2) & 0x01) == 0x01;
        showBG = ((mask >> 3) & 0x01) == 0x01;
        showSprites = ((mask >> 4) & 0x01) == 0x01;
        red = ((mask >> 5) & 0x01) == 0x01;
        green = ((mask >> 6) & 0x01) == 0x01;
        blue = ((mask >> 7) & 0x01) == 0x01;
    }

    /**
     * Helper to know whether rendering is currently disabled.
     *
     * @return
     */
    private boolean renderingDisabled() {
        return !showSprites && !showBG;
    }

    private static void setPixelInImage(final int x, final int y, final int value, final int[] image) {
        image[y * SCREEN_WIDTH + x] = value;
    }
}
