package memory;


public class IORegisterMemory extends MemoryMap {
    private static final int numPpuRegisters = 0x0008;
    private static final int numSecondSetRegisters = 0x0020;
    private static final int SECOND_SET_IO_REGISTERS = 0x2000;

    // PPU register addresses (in first set of registers)
    private static final int PPU_CTRL = 0x0000;
    private static final int PPU_MASK = 0x0001;
    private static final int PPU_STATUS = 0x0002;
    private static final int SPR_ADDRESS = 0x0003;
    private static final int SPR_DATA = 0x0004;
    private static final int PPU_SCROLL = 0x0005;
    private static final int PPU_ADDRESS = 0x0006;
    private static final int PPU_DATA = 0x0007;

    public int vramAddress; // set via PPU_ADDRESS
    public byte fineXScroll;

    public int tempVramAddress;
    public byte ppuBufferData;
    private boolean firstWrite;

    private ConsoleMemory consoleMemory; // reference to console memory

    public boolean dmaFlag = false;

    // Second set of registers (APU, controllers, DMA, etc.)
    // TODO: implement these registers!
    private static final int SPR_DMA = 0x2014;

    public IORegisterMemory(final ConsoleMemory consoleMemory) {
        super(numPpuRegisters + numSecondSetRegisters);
        vramAddress = 0x0;
        tempVramAddress = 0x0;
        fineXScroll = 0x0;
        firstWrite = true;
        ppuBufferData = 0x0;
        this.consoleMemory = consoleMemory;
    }

    /**
     * Read a byte from memory (implemented with memory mirrors)
     *
     * @param address
     * @return
     */
    @Override
    public byte read(int address) {
        if (address < SECOND_SET_IO_REGISTERS) {
            address %= numPpuRegisters;
            switch (address) {
                case PPU_CTRL:
                    return readFromControl();
                case PPU_MASK:
                    return readFromMask();
                case PPU_STATUS:
                    return readFromStatus();
                case SPR_ADDRESS:
                    return readFromSPRAddress();
                case SPR_DATA:
                    return readFromSPRData();
                case PPU_ADDRESS:
                    return readFromAddress();
                case PPU_SCROLL:
                    return readFromScroll();
                case PPU_DATA:
                    return readFromData();
                default:
                    return 0x0;
            }
        } else {
            return memory[address - SECOND_SET_IO_REGISTERS];
        }
    }

    /**
     * Write a byte into memory (this map references 0x2000 --> 0x4020)
     *
     * @param address
     * @param value
     */
    @Override
    public void write(int address, byte value) {
        if (address < SECOND_SET_IO_REGISTERS) {
            address %= numPpuRegisters;
            switch (address) {
                case PPU_CTRL:
                    writeToControl(value);
                    break;
                case PPU_MASK:
                    writeToMask(value);
                    break;
                case PPU_STATUS:
                    writeToStatus(value);
                    break;
                case SPR_ADDRESS:
                    writeToSPRAddress(value);
                    break;
                case SPR_DATA:
                    writeToSPRData(value);
                    break;
                case PPU_ADDRESS:
                    writeToAddress(value);
                    break;
                case PPU_SCROLL:
                    writeToScroll(value);
                    break;
                case PPU_DATA:
                    writeToData(value);
                    break;
                default:
                    break;
            }
        } else {
            switch (address) {
                case SPR_DMA:
                    dmaFlag = true;
                    break;
            }
            memory[address - SECOND_SET_IO_REGISTERS] = value;
        }
    }

    /**
     * ConsoleMemory uses this to reset the DMA flag here after a DMA write has been triggered once
     */
    public void resetDmaFlag() {
        this.dmaFlag = false;
    }

    /**
     * Write a byte to the PPU_CTRL and update tempVramAddress
     */
    private void writeToControl(byte value) {
        this.memory[PPU_CTRL] = value;
        // t: ...BA.. ........ = d: ......BA
        tempVramAddress = (tempVramAddress & 0xF3FF) + ((value << 10) & 0x0C00);
    }

    /**
     * Read the byte from PPU_CTRL
     */
    private byte readFromControl() {
        return this.memory[PPU_CTRL];
    }

    /**
     * Write a byte to the PPU_MASK and update tempVramAddress
     */
    private void writeToMask(byte value) {
        this.memory[PPU_MASK] = value;
    }

    /**
     * Read the byte from PPU_MASK
     */
    private byte readFromMask() {
        return this.memory[PPU_MASK];
    }

    /**
     * Write a byte to the PPU_STATUS
     */
    private void writeToStatus(byte value) {
        this.memory[PPU_STATUS] = value;
    }

    /**
     * Read the byte from PPU_STATUS and reset the firstWrite and Vblank flag
     */
    private byte readFromStatus() {
        firstWrite = true;
        byte status = this.memory[PPU_STATUS];
        this.writeToStatus((byte) (status & 0x7F));
        return status;
    }

    /**
     * Write a byte to the SPR_ADDRESS
     */
    private void writeToSPRAddress(byte value) {
        this.memory[SPR_ADDRESS] = value;
    }

    /**
     * Read the byte from SPR_ADDRESS
     */
    private byte readFromSPRAddress() {
        return this.memory[SPR_ADDRESS];
    }

    /**
     * Write a byte to the SPR_DATA. Increments the SPRAddress by 4 also.
     */
    private void writeToSPRData(byte value) {
        this.memory[SPR_DATA] = value;
        writeToSPRAddress((byte) (readFromSPRAddress() + 0x04));
    }

    /**
     * Read the byte from SPR_DATA
     */
    private byte readFromSPRData() {
        return this.memory[SPR_DATA];
    }

    /**
     * Write a byte to the PPU_SCROLL and update the tempVramRegister and fineX scroll appropriately.
     */
    private void writeToScroll(byte value) {
        this.memory[PPU_SCROLL] = value;
        if (firstWrite) {
            // t: ....... ...HGFED = d: HGFED...
            // x:              CBA = d: .....CBA
            tempVramAddress = (tempVramAddress & 0xFFE0) + ((value >> 3) & 0x001F);
            fineXScroll = (byte) (value & 0x07);
        } else {
            // t: CBA..HG FED..... = d: HGFEDCBA
            tempVramAddress = (tempVramAddress & 0x8C1F) + ((value << 12) & 0x7000) + ((value << 2) & 0x03E0);
        }
        firstWrite = !firstWrite;
    }

    /**
     * Read the byte from PPU_SCROLL
     */
    private byte readFromScroll() {
        return this.memory[PPU_SCROLL];
    }

    /**
     * Write a byte to the PPU_ADDRESS and update tempVramAddress and vramAddress
     */
    private void writeToAddress(byte value) {
        if (firstWrite) {
            // t: .FEDCBA ........ = d: ..FEDCBA
            // t: X...... ........ = 0
            tempVramAddress = (tempVramAddress & 0x80FF) + ((value << 8) & 0x3F00);
        } else {
            // t: ....... HGFEDCBA = d: HGFEDCBA
            // v                   = t
            tempVramAddress = (tempVramAddress & 0xFF00) + (value & 0x00FF);
            vramAddress = tempVramAddress;
        }
        firstWrite = !firstWrite;
        this.memory[PPU_ADDRESS] = value;
    }

    /**
     * Read the byte from PPU_ADDRESS
     */
    private byte readFromAddress() {
        return this.memory[PPU_ADDRESS];
    }

    /**
     * Write a byte to the PPU_DATA and increment vramAddress by an amount determined by $2000:2.
     * This writes the byte to $2007 in main memory as well as vramAddress in PPU memory.
     */
    private void writeToData(byte value) {
        this.consoleMemory.writeToPPU(vramAddress, value);
        this.memory[PPU_DATA] = value;
        vramAddress += ((readFromControl() >> 2) & 0x01) == 0 ? 1 : 32;
    }

    /**
     * Read the byte from PPU_DATA and increment vramAddress by an amount determined by $2000:2.
     * Reads the data from a buffer, which is updated directly from PPU main memory.
     */
    private byte readFromData() {
        byte ppuData = this.consoleMemory.readFromPPU(vramAddress);

        // If we are in main VRAM, we load the read onto our internal buffer and return the previous buffer contents
        if (vramAddress % 0x4000 < 0x3F00) {
            byte temp = ppuBufferData;
            ppuBufferData = ppuData;
            ppuData = temp;
        } else {
            // Otherwise we mirror down by 0x1000 and put that read on the buffer
            ppuBufferData = this.consoleMemory.readFromPPU(vramAddress - 0x1000);
        }
        vramAddress += ((readFromControl() >> 2) & 0x01) == 0 ? 1 : 32;
        return ppuData;
    }

    /**
     * This is called indirectly from the PPU during the pre render scanline
     */
    public void copyVertical() {
        // v: IHGF.ED CBA..... = t: IHGF.ED CBA.....
        vramAddress = (vramAddress & 0x041F) + (tempVramAddress & 0xFBE0);
    }

    /**
     * This is called indirectly from the PPU at the 257th cycle during rendering
     */
    public void copyHorizontal() {
        // v: ....F.. ...EDCBA = t: ....F.. ...EDCBA
        vramAddress = (vramAddress & 0xFBE0) + (tempVramAddress & 0x041F);
    }

    /**
     * Called indirectly from the PPU to increment the horizontal position of the vramAddress
     */
    public void incrementHorizontal() {
        if ((vramAddress & 0x001F) == 31) {
            // Switch horizontal name table
            vramAddress &= ~0x001F;
            vramAddress ^= 0x0400;
        } else {
            // Increment coarse X
            vramAddress++; // Increment coarse X
        }
    }

    /**
     * Called indirectly from the PPU to increment the Y scroll of the vramAddress.
     */
    public void incrementVertical() {
        if ((vramAddress & 0x7000) != 0x7000) {
            // If fine Y scroll < 7
            vramAddress += 0x1000;
        } else {
            vramAddress &= ~0x7000;
            int y = (vramAddress & 0x03E0) >> 5;
            if (y == 29) {
                y = 0;
                vramAddress ^= 0x800; // Switch vertical nametable
            } else if (y == 31) {
                y = 0;
            } else {
                y++;
            }
            vramAddress = (vramAddress & ~0x03E0) | (y << 5);
        }
    }

    /**
     * Calculate and return the fineY scroll from vramAddress (the upper 3 bits)
     *
     * @return
     */
    public int fineYScroll() {
        return (vramAddress >> 12) & 0x0007;
    }
}
