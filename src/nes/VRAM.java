package nes;

/**
 * This is the main Video RAM of the PPU where the sprites and background images are held.
 */
class VRAM extends MemoryMap {
    private static final int SPRITE_TABLE_SIZE = 0x3000;
    private static final int PALLETE_OFFSET = 0x3F00;
    private static final int VRAM_SIZE = 0x4000;

    public VRAM() {
        super(VRAM_SIZE);
    }

    @Override
    public byte read(int address) {
        if (address < SPRITE_TABLE_SIZE) {
            return this.memory[address];
        } else if (address < PALLETE_OFFSET) {
            return this.memory[address % 0x1000 + 0x2000];
        } else if (address < size()) {
            return this.memory[address % 0x20 + PALLETE_OFFSET];
        }

        return this.memory[address % size()];
    }

    @Override
    public void write(int address, byte value) {
        if (address < SPRITE_TABLE_SIZE) {
            this.memory[address] = value;
        } else if (address < PALLETE_OFFSET) {
            this.memory[address % 0x1000 + 0x2000] = value;
        } else if (address < size()) {
            this.memory[address % 0x20 + PALLETE_OFFSET] = value;
        }

        this.memory[address % size()] = value;
    }
}
