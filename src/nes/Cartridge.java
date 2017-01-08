package nes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Represents an NES cartridge. Created from the iNES file format (*nes) via makeFrom()
 */
public class Cartridge {
    private final static int HEADER_LENGTH = 16;
    private final static int PRG_ROM_BANK_SIZE = 16 * 1024; // 16 kiB banks
    private final static int CHR_ROM_BANK_SIZE = 8 * 1024; // 8 kiB banks
    private final char[][] prgRomBanks;
    private final char[][] chrRomBanks;

    private final int mapper;
    private final MirroringMode mirroringMode;

    private Cartridge(final char[][] prgRomBanks, final char[][] chrRomBanks, int mapper, MirroringMode mirroringMode) {
        this.prgRomBanks = prgRomBanks;
        this.chrRomBanks = chrRomBanks;
        this.mapper = mapper;
        this.mirroringMode = mirroringMode;
    }

    public static Cartridge makeFrom(final File nesFile) {
        FileReader reader;
        try {
            reader = new FileReader(nesFile);
        } catch (FileNotFoundException e) {
            System.err.printf("File: %s not found!\n", nesFile.toString());
            return null;
        }

        char[] header = new char[HEADER_LENGTH];
        try {
            reader.read(header, 0, HEADER_LENGTH);

        } catch (IOException e) {
            System.err.printf("File: %s does not contain header!\n", nesFile.toString());
            return null;
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

        // Now attempt to read in the rest of the file to fill up the PRG and CHR Rom banks
        final char[][] prgRomBanks = new char[numPrgRomBanks][PRG_ROM_BANK_SIZE];
        final char[][] chrRomBanks = new char[numChrRomBanks][CHR_ROM_BANK_SIZE];

        try {
            for (int i = 0; i < numPrgRomBanks; i++) {
                System.out.println(prgRomBanks.length);
                System.out.println(prgRomBanks[i].length);
                System.out.println(HEADER_LENGTH + i * PRG_ROM_BANK_SIZE);
                reader.read(prgRomBanks[i], 0, PRG_ROM_BANK_SIZE);
            }
            for (int i = 0; i < numChrRomBanks; i++) {
                reader.read(chrRomBanks[i], 0, CHR_ROM_BANK_SIZE);
            }
        } catch (IOException e) {
            System.err.printf("File: %s does not contain proper number of bytes to fill memory banks!", nesFile.toString());
            return null;
        }

        return new Cartridge(prgRomBanks, chrRomBanks, mapper, mirroringMode);
    }

    private static boolean validNESString(final char[] header) {
        return header[0] == 'N' && header[1] == 'E' && header[2] == 'S';
    }

    private static boolean validFileFormatChar(final char[] header) {
        return header[3] == (char) 0x1a;
    }

    public char[][] getPrgRomBanks() {
        return prgRomBanks;
    }

    public char[][] getChrRomBanks() {
        return chrRomBanks;
    }

    public char[] getPRGRomBank(int n) {
        if (n >= this.prgRomBanks.length) {
            return null;
        }
        return prgRomBanks[n];
    }

    public char[] getCHRRomBank(int n) {
        if (n >= this.chrRomBanks.length) {
            return null;
        }
        return prgRomBanks[n];
    }

    public static void main(String[] args) {
        Cartridge cartridge = Cartridge.makeFrom(new File("/Users/rparanjpe/WhiteRaven/DonkeyKong.nes"));
        char[] prgRomBank = cartridge.getPRGRomBank(0);
        int i = 0;
        System.out.println("DFS");
        char prev = 'a';
        for (char c : prgRomBank) {
            if ((int) c == 0x20) {
                if ((int) prev == 0x00) {
                    System.out.println("PPU_CTRL1");
                }
                if ((int) prev == 0x06) {
                    System.out.println("PPU_ADDR");
                }
            }
            if ((int) c == 0x14 && (int) prev == 0x40) {
                System.out.println("DMA");
            }
            System.out.print(String.format("0x%02x  ", (byte) c));
            if (i++ % 16 == 0) {
                System.out.println();
            }
            prev = c;
        }
    }
}
