package nes;

import memory.ConsoleMemory;

import java.io.IOException;
import java.util.Timer;
import java.nio.file.Paths;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import java.io.File;

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
        ppu.setMirroringMode(cartridge.getMirroringMode());
    }

    public static void main(String[] args) {
        Console console = new Console("/Users/rparanjpe/WhiteRaven/DonkeyKong.nes");
        System.out.println(console.cpu.state());

        Timer timer = new Timer();
        int FRAME_TIME = 17;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < (1789773.0 / 60.0); i++) {
                    console.cpu.executeCycle();
                    console.ppu.executeCycles(3);
                    if (console.ppu.triggerVerticalBlank) {
                        console.cpu.triggerInterrupt(Interrupt.NMI);
                        console.ppu.triggerVerticalBlank = false;
                    }
                    if (console.ppu.imageReady) {
                        try {
                            ImageIO.write(console.ppu.getImage(), "png", new File("background.png"));
                        } catch (IOException e) {
                            System.err.println("Background image not saved!");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, 0, FRAME_TIME);
    }
}
