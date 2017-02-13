package nes;

import java.nio.file.Paths;
import memory.ConsoleMemory;

/**
 * Encompasses multiple emulated components of the entire NES (the CPU, PPU, APU, Controllers, and Memory Mapper for the
 * Cartridge)
 */
public class Console {
    public ConsoleMemory consoleMemory;
    public CPU cpu;
    public PPU ppu;
    public Cartridge cartridge;
    public Joypad joypad;

    public Console(final String cartridgePath) {
        cartridge = Cartridge.makeFrom(Paths.get(cartridgePath));
        joypad = new KeyboardController();
        consoleMemory = new ConsoleMemory(cartridge, joypad);
        cpu = new CPU(consoleMemory);
        ppu = new PPU(consoleMemory);
        ppu.setMirroringMode(cartridge.getMirroringMode());
    }
}
