package memory;

/**
 * This is the main Video RAM of the PPU where the sprites and background images are held. The first 0x2000 bytes
 * of VRAM are handled by CHR-ROM in the loaded Cartridge, so VRAM only ends up handling the name and attribute tables
 * and sprite and image palletes.
 *
 * The address provided is assumed to be in the range of than 0x2000 -> 0x10000
 */
public class VRAM extends MemoryMap {
    private static final int NAME_TABLE_OFFSET = 0x2000;
    private static final int PALLETE_OFFSET = 0x3F00;

    private NameTableMemory nameTableMemory = new NameTableMemory();
    private PalleteMemory palleteMemory = new PalleteMemory();

    public VRAM() {}

    @Override
    public byte read(int address) {
        if (address < 0x3F00) {
            return nameTableMemory.read(address - NAME_TABLE_OFFSET);
        }
        return palleteMemory.read(address - PALLETE_OFFSET);
    }

    @Override
    public void write(int address, byte value) {
        if (address < 0x3F00) {
            nameTableMemory.write(address - NAME_TABLE_OFFSET, value);
        } else {
            palleteMemory.write(address - PALLETE_OFFSET, value);
        }
    }

    @Override
    public int size() {
        return 0x10000;
    }
}

/**
 * This map references the 4 name and attribute tables of the VRAM.
 * The address provided to read/write here references bytes 0x2000 - 0x2EFF.
 */
class NameTableMemory extends MemoryMap {
    private static final int NAME_TABLE_SIZE = 0x1000;

    public NameTableMemory() {
        super(NAME_TABLE_SIZE);
    }

    @Override
    public byte read(int address) {
        return this.memory[address % size()];
    }

    @Override
    public void write(int address, byte value) {
        this.memory[address % size()] = value;
    }
}

/**
 * This map references the image and sprite palletes of the VRAM.
 * The address provided to read/write here references bytes 0x3F00 - 0x3FFF.
 * The pallete entry at 0x3F00 is the background color and is used for transparency.
 * Mirroring is used so that every four bytes the bg color repeats: $3F04 = $3F08 = $3F0C, etc.
 */
class PalleteMemory extends MemoryMap {
    private static final int PALLETE_TABLE_SIZE = 0x0020;

    public PalleteMemory() {
        super(PALLETE_TABLE_SIZE);
    }

    @Override
    public byte read(int address) {
        if (address % 4 == 0) {
            address = 0;
        }
        return this.memory[address % size()];
    }

    @Override
    public void write(int address, byte value) {
        if (address % 4 == 0) {
            address = 0;
        }
        this.memory[address % size()] = value;
    }
}
