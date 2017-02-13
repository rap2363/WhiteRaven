package nes;

import java.nio.file.Paths;
import memory.ConsoleMemory;

/**
 * Encompasses multiple emulated components of the entire NES (the CPU, PPU, APU, and Memory Mapper for the Cartridge)
 */
public class Console {
    public ConsoleMemory consoleMemory;
    public CPU cpu;
    public PPU ppu;
    public Cartridge cartridge;

    public Console(final String cartridgePath) {
        cartridge = Cartridge.makeFrom(Paths.get(cartridgePath));
        consoleMemory = ConsoleMemory.bootFromCartridge(cartridge);
        cpu = new CPU(consoleMemory);
        ppu = new PPU(consoleMemory);
        ppu.setMirroringMode(cartridge.getMirroringMode());
    }
}
