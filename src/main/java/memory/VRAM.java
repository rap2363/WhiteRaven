package main.java.memory;

import main.java.nes.MirroringMode;

/**
 * This is the main Video RAM of the PPU where the sprites and background images are held. The first 0x2000 bytes
 * of VRAM are handled by CHR-ROM in the loaded Cartridge, so VRAM only ends up handling the name and attribute tables
 * and sprite and image palettes.
 *
 * The address provided is assumed to be in the range of than 0x2000 -> 0x10000
 */
public class VRAM extends MemoryMap {
    private static final int NAME_TABLE_OFFSET = 0x2000;
    private static final int PALETTE_OFFSET = 0x3F00;

    private NameTableMemory nameTableMemory = new NameTableMemory();
    private PaletteMemory paletteMemory = new PaletteMemory();

    public VRAM() {}

    @Override
    public byte read(int address) {
        if (address < 0x3F00) {
            return nameTableMemory.read(address - NAME_TABLE_OFFSET);
        }
        return paletteMemory.read(address - PALETTE_OFFSET);
    }

    @Override
    public void write(int address, byte value) {
        if (address < 0x3F00) {
            nameTableMemory.write(address - NAME_TABLE_OFFSET, value);
        } else {
            paletteMemory.write(address - PALETTE_OFFSET, value);
        }
    }

    @Override
    public int size() {
        return 0x4000;
    }

    public void setMirroringMode(MirroringMode mirroringMode) {
        this.nameTableMemory.setMirroringMode(mirroringMode);
    }
}

/**
 * This map references the 4 name and attribute tables of the VRAM.
 * The address provided to read/write here references bytes 0x2000 - 0x2EFF.
 */
class NameTableMemory extends MemoryMap {
    private static final int NAME_TABLE_SIZE = 0x1000;
    private MirroringMode mirroringMode = MirroringMode.HORIZONTAL;

    public NameTableMemory() {
        super(NAME_TABLE_SIZE);
    }

    public void setMirroringMode(MirroringMode mirroringMode) {
        this.mirroringMode = mirroringMode;
    }

    @Override
    public byte read(int address) {
        return this.memory[mapAddress(address % size())];
    }

    @Override
    public void write(int address, byte value) {
        this.memory[mapAddress(address % size())] = value;
    }

    private int mapAddress(int address) {
        // Map the $2000 and $2400 to the first physical name table, and $2800 and $2C00 to the second
        if (mirroringMode == MirroringMode.HORIZONTAL) {
            if (address < 0x800) {
                address %= 0x400;
            } else {
                address = address % 0x400 + 0x800;
            }
        }
        // Map the $2000 and $2800 to the first physical name table, and $2400 and $2C00 to the second
        else if (mirroringMode == MirroringMode.VERTICAL) {
            address %= 0x800;
        // Map $2000, $2400, $2800, and $2C00 all to the same name table
        } else if (mirroringMode == MirroringMode.SINGLE) {
            address %= 0x400;
        }

        return address;
    }
}

/**
 * This map references the image and sprite palettes of the VRAM.
 * The address provided to read/write here references bytes 0x3F00 - 0x3FFF.
 * The palette entry at 0x3F00 is the background color and is used for transparency.
 * Mirroring is used so that every four bytes the bg color repeats: $3F04 = $3F08 = $3F0C, etc.
 */
class PaletteMemory extends MemoryMap {
    private static final int PALLETE_TABLE_SIZE = 0x0020;

    public PaletteMemory() {
        super(PALLETE_TABLE_SIZE);
    }

    @Override
    public byte read(int address) {
        address %= size();
        if (address % 4 == 0) {
            address = 0;
        }
        return this.memory[address];
    }

    @Override
    public void write(int address, byte value) {
        address %= size();
        if (address % 4 == 0 && address >= 0x10) {
            address = 0;
        }
        this.memory[address] = value;
    }
}
