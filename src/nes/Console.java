package nes;

import memory.ConsoleMemory;

import java.util.Timer;
import java.nio.file.Paths;
import java.util.TimerTask;

/**
 * Encompasses multiple emulated components of the entire NES (the CPU, PPU, APU, and Memory Mapper for the Cartridge)
 */
public class Console {
    public ConsoleMemory consoleMemory;
    public CPU cpu;
    public PPU ppu;
    public Cartridge cartridge;

    public Console(final String cartridgePath) {
        cartridge = Cartridge.makeFrom(Paths.get(cartridgePath));
        consoleMemory = ConsoleMemory.bootFromCartridge(cartridge);
        cpu = new CPU(consoleMemory);
        ppu = new PPU(consoleMemory);
    }

    public static void main(String[] args) {
        Console console = new Console("/Users/rparanjpe/WhiteRaven/DonkeyKong.nes");
        System.out.println(console.cpu.state());

        Timer timer = new Timer();
        int FRAME_TIME = 17;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                while (console.cpu.cycleCount < (1789773.0 / 60.0)) {
                    console.cpu.executeCycle();
                    System.out.println(console.cpu.singleLineState());
                }
                console.cpu.cycleCount = 0;
            }
        }, 0, FRAME_TIME);
    }
}
