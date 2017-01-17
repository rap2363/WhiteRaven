package nes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

/**
 * Represents an NES cartridge. Created from the iNES file format (*nes) via makeFrom()
 */
public class Cartridge {
    private final static int HEADER_LENGTH = 16;
    private final static int PRG_ROM_BANK_SIZE = 16 * 1024; // 16 kiB banks
    private final static int CHR_ROM_BANK_SIZE = 8 * 1024; // 8 kiB banks
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
        return prgRomBanks[n];
    }

    public byte read(int address) {
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

    public void write(int address, byte value) {
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

    public static void main(String[] args) {
        Cartridge cartridge = Cartridge.makeFrom(Paths.get("/Users/rparanjpe/WhiteRaven/DonkeyKong.nes"));
        byte[] prgRomBank = cartridge.getPRGRomBank(0);
        int i = 0;
        System.out.println("DFS");
        byte prev = -1;
        for (byte b : prgRomBank) {
            if ((int) b == 0x20) {
                if ((int) prev == 0x00) {
                    System.out.println("PPU_CTRL1");
                }
                if ((int) prev == 0x06) {
                    System.out.println("PPU_ADDR");
                }
            }
            if ((int) b == 0x14 && (int) prev == 0x40) {
                System.out.println("DMA");
            }
            System.out.print(String.format("0x%02x  ", b));
            if (i++ % 16 == 0) {
                System.out.println();
            }
            prev = b;
        }
    }
}
