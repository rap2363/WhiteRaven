package nes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

/**
 * Represents an NES cartridge. Created from the iNES file format (*nes) via makeFrom()
 */
public class Cartridge {
    public final static int PRG_ROM_BANK_SIZE = 0x4000; // 16 kiB banks
    public final static int CHR_ROM_BANK_SIZE = 0x2000; // 8 kiB banks

    private final static int HEADER_LENGTH = 0x10;
    private final byte[][] prgRomBanks;
    private final byte[][] chrRomBanks;
    private final byte[] expansionRom;
    private final byte[] saveRam;

    private final int mapper;
    private final MirroringMode mirroringMode;

    private int lowerBankIndex;
    private int upperBankIndex;
    private int chrRomBankIndex;

    private Cartridge(final byte[][] prgRomBanks, final byte[][] chrRomBanks, int mapper, MirroringMode mirroringMode) {
        this.prgRomBanks = prgRomBanks;
        this.chrRomBanks = chrRomBanks;
        this.mapper = mapper;
        this.mirroringMode = mirroringMode;

        // Temporary while we don't have bank switching or memory management controllers
        lowerBankIndex = 0;
        upperBankIndex = 0;
        chrRomBankIndex = 0;
        this.expansionRom = new byte[0x6000 - 0x4020];
        this.saveRam = new byte[0x2000];
    }

    public static Cartridge makeFrom(final Path nesFile) {
        try {
            byte[] fileData = Files.readAllBytes(nesFile);
            char[] header = new char[HEADER_LENGTH];
            int filePosition = 0;
            for (; filePosition < HEADER_LENGTH; filePosition++) {
                header[filePosition] = (char) fileData[filePosition];
            }
            if (!(validNESString(header) && validFileFormatChar(header))) {
                return null;
            }

            final int numPrgRomBanks = (int) header[4];
            final int numChrRomBanks = (int) header[5];
            final int romControlByteOne = (int) header[6];
            final int romControlByteTwo = (int) header[7];
            final MirroringMode mirroringMode;
            if ((romControlByteOne >> 3 & 0x01) == 0x01) {
                mirroringMode = MirroringMode.FOUR_SCREEN;
            } else if ((romControlByteOne & 0x11) == 0x00) {
                mirroringMode = MirroringMode.HORIZONTAL;
            } else {
                mirroringMode = MirroringMode.VERTICAL;
            }

            final int mapper = romControlByteTwo + (romControlByteOne >> 4);

            // Now attempt to read in and fill up the PRG and CHR Rom banks
            final byte[][] prgRomBanks = new byte[numPrgRomBanks][PRG_ROM_BANK_SIZE];
            final byte[][] chrRomBanks = new byte[numChrRomBanks][CHR_ROM_BANK_SIZE];

            for (int i = 0; i < numPrgRomBanks; i++) {
                for (int j = 0; j < PRG_ROM_BANK_SIZE; j++) {
                    prgRomBanks[i][j] = fileData[filePosition++];
                }
            }
            for (int i = 0; i < numChrRomBanks; i++) {
                for (int j = 0; j < CHR_ROM_BANK_SIZE; j++) {
                    chrRomBanks[i][j] = fileData[filePosition++];
                }
            }

            return new Cartridge(prgRomBanks, chrRomBanks, mapper, mirroringMode);

        } catch (IOException e) {
            System.out.println("File not found: " + nesFile.toString());
            e.printStackTrace();
        }

        return null;
    }

    private static boolean validNESString(final char[] header) {
        return header[0] == 'N' && header[1] == 'E' && header[2] == 'S';
    }

    private static boolean validFileFormatChar(final char[] header) {
        return header[3] == (char) 0x1a;
    }

    public byte[][] getPrgRomBanks() {
        return prgRomBanks;
    }

    public byte[][] getChrRomBanks() {
        return chrRomBanks;
    }

    public byte[] getPRGRomBank(int n) {
        if (n >= this.prgRomBanks.length) {
            return null;
        }
        return prgRomBanks[n];
    }

    public byte[] getCHRRomBank(int n) {
        if (n >= this.chrRomBanks.length) {
            return null;
        }
        return chrRomBanks[n];
    }

    public MirroringMode getMirroringMode() {
        return this.mirroringMode;
    }

    /**
     * Read a byte from the PRG-ROM address space
     *
     * @param address
     * @return
     */
    public byte readPRGROM(int address) {
        if (address < this.expansionRom.length) {
            return this.expansionRom[address];
        } else if (address < (this.expansionRom.length + this.saveRam.length)) {
            return this.saveRam[address - this.expansionRom.length];
        }

        address -= 0x3FE0;
        if (address < PRG_ROM_BANK_SIZE) {
            return getPRGRomBank(lowerBankIndex)[address];
        }
        return getPRGRomBank(upperBankIndex)[address - PRG_ROM_BANK_SIZE];
    }

    /**
     * Write a byte into the PRG-ROM address space
     * Q: Will this be called during normal execution?
     *
     * @param address
     * @param value
     */
    public void writePRGROM(int address, byte value) {
        if (address < this.expansionRom.length) {
            expansionRom[address] = value;
        } else if (address < (this.expansionRom.length + this.saveRam.length)) {
            saveRam[address - this.expansionRom.length] = value;
        }

        address -= 0x3FE0;
        if (address < PRG_ROM_BANK_SIZE) {
            getPRGRomBank(lowerBankIndex)[address] = value;
        } else {
            getPRGRomBank(upperBankIndex)[address - PRG_ROM_BANK_SIZE] = value;
        }
    }

    /**
     * Read a byte from the CHR-ROM address space
     *
     * @param address
     * @return
     */
    public byte readCHRROM(int address) {
        return getCHRRomBank(chrRomBankIndex)[address];
    }


    /**
     * Write a byte into the CHR-ROM address space
     *
     * @param address
     * @param value
     */
    public void writeCHRROM(int address, byte value) {
        getCHRRomBank(chrRomBankIndex)[address] = value;
    }

    public static void main(String[] args) {
        Cartridge cartridge = Cartridge.makeFrom(Paths.get("/Users/rparanjpe/WhiteRaven/DonkeyKong.nes"));
        byte[] patternTable = cartridge.getCHRRomBank(0);
        for (int i = 0; i < 512; i++) {
            int firstTileIndex = i * 16;
            int secondTileIndex = firstTileIndex + 8;
            System.out.println("Tile " + i);
            for (int row = 0; row < 8; row++) {
                byte b1 = patternTable[firstTileIndex + row];
                byte b2 = patternTable[secondTileIndex + row];
                String s = "";
                for (int col = 0; col < 8; col++) {
                    byte s1 = (byte) ((b1 >> (7 - col)) & 0x01);
                    byte s2 = (byte) ((b2 >> (7 - col)) & 0x01);
                    if (s1 == 0 && s2 == 0) {
                        s += "  ";
                    } else if (s1 == 1 && s2 == 0) {
                        s += "* ";
                    } else if (s1 == 0 && s2 == 1) {
                        s += "@ ";
                    } else if (s1 == 1 && s2 == 1){
                        s += "$ ";
                    }
                }
                System.out.println(s);
            }
            System.out.println();
        }
        System.out.println(patternTable.length);
    }
}
