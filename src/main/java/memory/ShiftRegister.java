package memory;

/**
 * This class stores two eight-pixel tile rows to render.
 */
public class ShiftRegister {
    private int lowBGTiles;
    private int highBGTiles;
    private byte loadedAttribute;
    private byte currentAttribute;

    public ShiftRegister() {
        lowBGTiles = 0x0;
        highBGTiles = 0x0;
        loadedAttribute = 0x0;
        currentAttribute = 0x0;
    }

    /**
     * Get a palette color index for a pixel in the shift register.
     *
     * @return
     */
    public int getPixelIndex(final int fineX) {
        final byte bgColorLow = (byte) ((((this.lowBGTiles << fineX) & 0x8000) >> 15) & 0x0001);
        final byte bgColorHigh = (byte) ((((this.highBGTiles << fineX) & 0x8000) >> 15) & 0x0001);

        return ((currentAttribute & 0x03) << 2) | (bgColorHigh << 1) | (bgColorLow);
    }

    /**
     * Shift the background tiles to the left by one. Called by the PPU after a pixel is rendered.
     */
    public void shift() {
        this.highBGTiles <<= 1;
        this.lowBGTiles <<= 1;
    }

    /**
     * Load a byte into the lowBG tile.
     */
    public void loadLowBG(final byte lowBG) {
        this.lowBGTiles &= 0xFF00;
        this.lowBGTiles |= (0x00FF & lowBG);
    }

    /**
     * Load a byte into the highBG tile.
     */
    public void loadHighBG(final byte highBG) {
        this.highBGTiles &= 0xFF00;
        this.highBGTiles |= (0x00FF & highBG);
    }

    /**
     * Load the attribute tile into the unused register (it is shifted in after 8 cycles).
     *
     * @param attribute
     */
    public void loadAttributeTiles(final byte attribute) {
        currentAttribute = loadedAttribute;
        loadedAttribute = attribute;
    }
}
