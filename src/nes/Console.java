package nes;

import java.io.File;
import java.util.Scanner;

/**
 * Encompasses multiple emulated components of the entire NES (the CPU, PPU, APU, and Memory Mapper for the Cartridge)
 */
public class Console {
    public CPU cpu = new CPU();
    public Cartridge cartridge;

    public Console() {
        cpu = new CPU();
        cartridge = Cartridge.makeFrom(new File("/Users/rparanjpe/WhiteRaven/DonkeyKong.nes"));
        cpu.loadCartridge(cartridge);
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Console console = new Console();
        System.out.println(console.cpu.state());

        while (true) {
            try {
                scan.nextLine();
                console.cpu.fetchAndExecute();
            } catch (UnimplementedOpcode unimplementedOpcode) {
                unimplementedOpcode.printStackTrace();
                return;
            }
            System.out.println(console.cpu.state());

        }
    }
}
