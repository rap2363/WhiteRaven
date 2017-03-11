package memory;

/**
 * This class stores two eight-pixel tile rows to render.
 */
public class ShiftRegister {
    private static final int TILE_LENGTH = 0x08;
    private static final int NUM_TILES = 2;

    private final CircularBuffer<Integer> lowBGTiles;
    private final CircularBuffer<Integer> highBGTiles;
    private final CircularBuffer<Integer> attributeColorTiles;

    public ShiftRegister() {
        lowBGTiles = new CircularBuffer<>(TILE_LENGTH * NUM_TILES);
        highBGTiles = new CircularBuffer<>(TILE_LENGTH * NUM_TILES);
        attributeColorTiles = new CircularBuffer<>(TILE_LENGTH * NUM_TILES);
    }

    /**
     * Get a color for a pixel in the shift register.
     *
     * @return
     */
    public int getPixelIndex() {
        return ((attributeColorTiles.get() << 2) | highBGTiles.get() << 1 | lowBGTiles.get()) & 0x0F;
    }

    /**
     * Load a byte for the lowBG tile.
     */
    public void loadLowBG(final byte lowBG) {
        for (int i = 0; i < 0x08; i++) {
            this.lowBGTiles.push((lowBG >> (7 - i)) & 0x01);
        }
    }

    /**
     * Load a byte for the highBG tile.
     */
    public void loadHighBG(final byte highBG) {
        for (int i = 0; i < 0x08; i++) {
            this.highBGTiles.push((highBG >> (7 - i)) & 0x01);
        }
    }

    /**
     * Load the attribute color tile. We load a bunch of copies for simplicity.
     */
    public void loadAttribute(final byte attributeByte) {
        for (int i = 0; i < 0x08; i++) {
            this.attributeColorTiles.push((int) attributeByte);
        }
    }
}
