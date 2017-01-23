package nes;

import memory.ConsoleMemory;

import java.io.IOException;
import java.util.Timer;
import java.nio.file.Paths;
import java.util.TimerTask;
import java.awt.image.*;
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
    }

    public static void main(String[] args) {
        Console console = new Console("/Users/rparanjpe/WhiteRaven/DonkeyKong.nes");
        System.out.println(console.cpu.state());

        int h = 4;
        int w = 16;
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < 64; i ++) {
            int[] rgb = console.ppu.systemPalleteColors[i];
            int x = i % 0x10;
            int y = i / 0x10;
            int rgbColor = rgb[0] << 16 & 0xFF0000 | rgb[1] << 8 & 0x00FF00 | rgb[2] & 0x0000FF;
            bi.setRGB(x, y, rgbColor);
            System.out.println("(x,y,index): " + x + ", " + y + ", " + Integer.toHexString(i) + " --> (" + Integer.toHexString(rgb[0]) + "," + Integer.toHexString(rgb[1]) + "," + Integer.toHexString(rgb[2])  + ")" + " " + rgbColor);
        }
        try {
            ImageIO.write(bi, "png", new File("colors.png"));
        } catch (IOException e) {
            System.err.println("File not saved!");
            e.printStackTrace();
        }

        Timer timer = new Timer();
        int FRAME_TIME = 17;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0;i < (1789773.0 / 60.0); i++) {
                    console.cpu.executeCycle();
                    System.out.println(console.cpu.singleLineState());
                }
            }
        }, 0, FRAME_TIME);
    }
}
