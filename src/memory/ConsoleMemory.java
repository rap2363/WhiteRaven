package memory;

import nes.Cartridge;
import operations.Utilities;

/**
 * This object manages all reads and writes into main memory. It acts similarly to virtual memory and as a layer
 * between the various independent components PPU, CPU, APU, Cartidge ROM/RAM, and I/O (controllers).
 *
 * There are three contiguous memory spaces in total:
 * 1. CPU Memory (0x10000 addressable bytes, with RAM, I/O registers, and Cartridge PRG-ROM, SRAM, and WRAM space)
 * 2. PPU Memory (0x10000 addressable bytes with Cartridge CHR-ROM, Name Table, and Sprite/Image pallete space)
 * 3. Sprite Memory (A dedicated 256 bytes for the PPU that contains sprite data).
 *
 * There are also two latches that are written to and read from that are technically separate: the ppuAddressLatch
 * and ppuScrollLatch.
 *
 * This class redirects reads/writes and offsets addresses.
 */
public class ConsoleMemory extends MemoryMap {
    private CPURAM cpuram = new CPURAM();
    private VRAM vram = new VRAM();
    private SPRAM sram = new SPRAM();
    private IORegisterMemory ioRegisterMemory = new IORegisterMemory();
    private Cartridge cartridge;

    private static final int addressableMemorySize = 0x10000;

    private ConsoleMemory(final Cartridge c) {
        super(0);
        this.cartridge = c;
        this.vram.setMirroringMode(c.getMirroringMode());
    }

    public static ConsoleMemory bootFromCartridge(final Cartridge cartridge) {
        return new ConsoleMemory(cartridge);
    }

    /**
     * Read a byte from the CPU Memory Map
     *
     * @param address
     * @return
     */
    @Override
    public byte read(int address) {
        address = address % addressableMemorySize;
        if (address < cpuram.size()) {
            return this.cpuram.read(address);
        } else if (address < 0x4020) {
            return ioRegisterMemory.read(address - 0x2000);
        }
        return this.cartridge.readPRGROM(address - 0x4020);
    }

    /**
     * Write a byte into the CPU Memory map
     *
     * @param address
     * @param value
     */
    @Override
    public void write(int address, byte value) {
        address %= addressableMemorySize;
        if (address < cpuram.size()) {
            this.cpuram.write(address, value);
        } else if (address < 0x4020) {
            ioRegisterMemory.write(address - 0x2000, value);
        } else {
            this.cartridge.writePRGROM(address - 0x4020, value);
        }
    }

    /**
     * Read a byte from the PPU memory space.
     *
     * @param address
     * @return
     */
    public byte readFromPPU(int address) {
        address %= addressableMemorySize;
        if (address < this.cartridge.CHR_ROM_BANK_SIZE) {
            return this.cartridge.readCHRROM(address);
        }
        return this.vram.read(address);
    }

    /**
     * Write a byte into the PPU memory space.
     *
     * @param address
     * @param value
     */
    public void writeIntoPPU(int address, byte value) {
        address %= addressableMemorySize;
        if (address < this.cartridge.CHR_ROM_BANK_SIZE) {
            this.cartridge.writeCHRROM(address, value);
        }
        this.vram.write(address, value);
    }

    public int readScrollLatch() {
        return this.ioRegisterMemory.ppuScrollLatch.read();
    }

    public int readAddressLatch() {
        return this.ioRegisterMemory.ppuAddressLatch.read();
    }

    /**
     * Gets bubbled back up to the CPU
     *
     * @return
     */
    public boolean dma() {
        return this.ioRegisterMemory.dmaFlag;
    }

    /**
     * Execute a DMA. This means we take 512 cycles from the CPU to transfer 256 bytes from the address in 0x4014 x 100
     * directly to sprite RAM.
     */
    public void executeDma() {
        int startingAddress = Utilities.toUnsignedValue(this.read(0x4014)) * 0x100;
        this.sram.dmaWrite(this.read(startingAddress, 256));
    }
}
