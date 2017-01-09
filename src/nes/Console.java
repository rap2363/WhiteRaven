package nes;

import operations.Utilities;

import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Encompasses multiple emulated components of the entire NES (the CPU, PPU, APU, and Memory Mapper for the Cartridge)
 */
public class Console {
    public CPU cpu = new CPU();
    public Cartridge cartridge;

    public Console() {
        cpu = new CPU();
        cartridge = Cartridge.makeFrom(Paths.get("/Users/rparanjpe/WhiteRaven/nestest.nes"));
        cpu.loadCartridge(cartridge);
        cpu.PC.write(0xC000);
    }

    public static void main(String[] args) {
        Console console = new Console();
        System.out.println(console.cpu.state());

        while (true) {
            try {
                console.cpu.fetchAndExecute();
            } catch (UnimplementedOpcode unimplementedOpcode) {
                unimplementedOpcode.printStackTrace();
                return;
            }

            System.out.println(console.cpu.singleLineState());
        }
    }
}
