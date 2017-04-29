package nes;

import java.nio.file.Paths;
import memory.ConsoleMemory;

/**
 * Encompasses multiple emulated components of the entire NES (the CPU, PPU, APU, Controllers, and Memory Mapper for the
 * Cartridge)
 */
public class Console {
    public final ConsoleMemory consoleMemory;
    public final CPU cpu;
    public final PPU ppu;
    public final Cartridge cartridge;
    public Joypad joypadOne;

    /**
     * Allow the option of setting joypadOne later. This lets us add players after a game has started.
     */
    public void setJoypadOne(final Joypad joypadOne) {
        this.joypadOne = joypadOne;
        this.consoleMemory.setJoypadOne(joypadOne);
    }

    private Console(ConsoleMemory consoleMemory, CPU cpu, PPU ppu, Cartridge cartridge, Joypad joypadOne) {
        this.consoleMemory = consoleMemory;
        this.cpu = cpu;
        this.ppu = ppu;
        this.cartridge = cartridge;
        this.joypadOne = joypadOne;
    }

    public static class Builder {
        private ConsoleMemory consoleMemory;
        private CPU cpu;
        private PPU ppu;
        private Cartridge cartridge;
        private Joypad joypadOne;

        public Builder() {

        }

        public Builder setCartridgePath(final String cartridgePath) {
            this.cartridge = Cartridge.makeFrom(Paths.get(cartridgePath));
            return this;
        }

        public Builder setJoypadOne(final Joypad joypadOne) {
            this.joypadOne = joypadOne;
            return this;
        }

        public Console build() {
            if (this.joypadOne == null) {
                this.joypadOne = new NoopController();
            }
            this.consoleMemory = new ConsoleMemory(cartridge, joypadOne);
            this.cpu = new CPU(consoleMemory);
            this.ppu = new PPU(consoleMemory);

            return new Console(consoleMemory, cpu, ppu, cartridge, joypadOne);
        }
    }
}
