package nes;

import operations.Utilities;

import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Encompasses multiple emulated components of the entire NES (the CPU, PPU, APU, and Memory Mapper for the Cartridge)
 */
public class Console {
    public ConsoleMemory consoleMemory;
    public CPU cpu;
    public Cartridge cartridge;

    public Console(final String cartridgePath) {
        cartridge = Cartridge.makeFrom(Paths.get(cartridgePath));
        consoleMemory = ConsoleMemory.bootFromCartridge(cartridge);
        cpu = new CPU(consoleMemory);
    }

    public static void main(String[] args) {
        Console console = new Console("/Users/rparanjpe/WhiteRaven/DonkeyKong.nes");
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
