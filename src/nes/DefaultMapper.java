package nes;

import memory.MemoryMap;

/**
 * Represents a no-op mapper. This Mapper is instantiated for games with no mapper implementation on the cartridge,
 * it just provides access to the lower and upper banks, CHR-ROM, Expansion ROM, and SRAM.
 */
public class DefaultMapper {
    private final MemoryMap expansionRom;
    private final MemoryMap sram;
    private final MemoryMap prgRomBanks;
    private final MemoryMap chrRom;

    public DefaultMapper() {
        this.expansionRom = new MemoryMap(0x1FE0);
        this.sram = new MemoryMap(0x2000);
        this.prgRomBanks = new MemoryMap(0x8000);
        this.chrRom = new MemoryMap(0x2000);
    }
}
