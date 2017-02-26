package memory;

import nes.Cartridge;
import nes.Joypad;
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
    private IORegisterMemory ioRegisterMemory = new IORegisterMemory(this);
    private Cartridge cartridge;
    private Joypad joypadOne;

    private static final int addressableMemorySize = 0x10000;

    public ConsoleMemory(final Cartridge cartridge, final Joypad joypadOne) {
        super(0);
        this.cartridge = cartridge;
        this.joypadOne = joypadOne;
        this.vram.setMirroringMode(cartridge.getMirroringMode());
    }

    /**
     * Read a byte from the CPU Memory Map. The provided address will be modded with 0x10000.
     *
     * @param address
     * @return
     */
    @Override
    public byte read(int address) {
        address = address % addressableMemorySize;
        // address: [0x0 -> 0xFFFF]
        if (address < cpuram.size()) {
            // address: [0x0 -> 0x1FFF]
            return this.cpuram.read(address);
        } else if (address < 0x4020) {
            // address: [0x2000 -> 0x401F]
            return ioRegisterMemory.read(address - 0x2000);
        }
        // address: [0x4020 -> 0xFFFF]
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
        address %= 0x4000;
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
    public void writeToPPU(int address, byte value) {
        address %= 0x4000;
        if (address < this.cartridge.CHR_ROM_BANK_SIZE) {
            this.cartridge.writeCHRROM(address, value);
        } else {
            this.vram.write(address, value);
        }
    }

    /**
     * Write a byte to SPR-RAM
     *
     * @param address
     * @param value
     */
    public void writeToSPRData(int address, byte value) {
        this.sram.write(address, value);
    }

    public byte readFromSPRData(int address) {
        return this.sram.read(address);
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
        this.ioRegisterMemory.resetDmaFlag();
    }

    /**
     * Return the VRAM address maintained by I/O register memory
     *
     * @return
     */
    public int getVramAddress() {
        return this.ioRegisterMemory.vramAddress;
    }

    public int getFineYScroll() {
        return this.ioRegisterMemory.fineYScroll();
    }

    public int getFineXScroll() {
        return this.ioRegisterMemory.fineXScroll();
    }

    public void copyHorizontal() {
        this.ioRegisterMemory.copyHorizontal();
    }

    public void copyVertical() {
        this.ioRegisterMemory.copyVertical();
    }

    public void incrementVertical() {
        this.ioRegisterMemory.incrementVertical();
    }

    public void incrementHorizontal() {
        this.ioRegisterMemory.incrementHorizontal();
    }

    public void setSpriteOverflow() {
        this.ioRegisterMemory.setSpriteOverflow();
    }

    public void setSpriteZeroHit() {
        this.ioRegisterMemory.setSpriteZeroHit();
    }

    public void setVblank() {
        this.ioRegisterMemory.setVblank();
    }

    public void clearSpriteOverflow() {
        this.ioRegisterMemory.clearSpriteOverflow();
    }

    public void clearSpriteZeroHit() {
        this.ioRegisterMemory.clearSpriteZeroHit();
    }

    public void clearVblank() {
        this.ioRegisterMemory.clearVblank();
    }

    /**
     * Fetch all relevant sprites for a line number and fill sprites. We return true if the sprites array is completely
     * filled
     *
     * @param lineNumber
     * @return
     */
    public boolean fetchSprites(final Sprite[] sprites, int lineNumber) {
        return this.sram.fetchSprites(sprites, lineNumber);
    }

    public void writeToJoypadOne(byte value) {
        this.joypadOne.write(value);
    }

    public byte readFromJoypadOne() {
        return this.joypadOne.read();
    }
}
