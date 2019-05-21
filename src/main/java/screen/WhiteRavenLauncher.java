package screen;

import io.KeyboardController;
import java.util.Timer;
import java.util.TimerTask;
import nes.Console;
import nes.Interrupt;

/**
 * Starts the Emulator with a path to a game to load in.
 */
public class WhiteRavenLauncher {
    public static final double CPU_CYCLES_PER_SECOND = 1789773.0; // ~1.79 MHz

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Provide a file path to a game!");
            System.exit(1);
        }
        final String pathToGame = args[0];
        final Console console = new Console.Builder()
            .setCartridgePath(pathToGame)
            .setJoypadOne(new KeyboardController())
            .build();
        final MainScreen screen = new MainScreen();
        final Timer timer = new Timer();
        int FRAME_TIME = 17;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < CPU_CYCLES_PER_SECOND / 60; i++) {
                    console.ppu.executeCycles(3);
                    console.cpu.executeCycle();
                    if (console.ppu.triggerVerticalBlank) {
                        console.cpu.triggerInterrupt(Interrupt.NMI);
                        console.ppu.triggerVerticalBlank = false;
                        int[] image = console.ppu.getImage();
                        screen.push(image);
                        screen.redraw();
                    }
                }
            }
        }, 0, FRAME_TIME);
    }
}
