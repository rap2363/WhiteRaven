package nes;

/**
 * This object manages all reads and writes into main memory. It acts similarly to virtual memory and as a layer
 * between the various independent components PPU, CPU, APU, Cartidge ROM/RAM, and I/O (controllers).
 */
public class ConsoleMemory extends MemoryMap {
    MemoryMap cpuram = new CPURAM();
    MemoryMap vram = new VRAM();
    MemoryMap sram = new SPRAM();
    MemoryMap ioRegisterMemory = new IORegisterMemory();
    Cartridge cartridge;

    private static final int addressableMemorySize = 0x10000;

    private ConsoleMemory(Cartridge c) {
        super(0);
        this.cartridge = c;
    }

    @Override
    public byte read(int address) {
        address = address % addressableMemorySize;
        if (address < cpuram.size()) {
            return this.cpuram.read(address);
        } else if (address < 0x4020) {
            return ioRegisterMemory.read(address - 0x2000);
        }
        return this.cartridge.read(address - 0x4020);
    }

    @Override
    public void write(int address, byte value) {
        address = address % addressableMemorySize;
        if (address < cpuram.size()) {
            this.cpuram.write(address, value);
        } else if (address < 0x4020) {
            ioRegisterMemory.write(address - 0x2000, value);
        } else {
            this.cartridge.write(address - 0x4020, value);
        }
    }

    @Override
    public int size() {
        return addressableMemorySize;
    }

    public static ConsoleMemory bootFromCartridge(Cartridge cartridge) {
        return new ConsoleMemory(cartridge);
    }
}
