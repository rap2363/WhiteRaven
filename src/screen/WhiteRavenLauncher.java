package screen;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Starts the Emulator with a path to a game to load in.
 */
public class WhiteRavenLauncher {
    private static final double CPU_CYCLES_PER_SECOND = 1789773.0; // ~1.79 MHz

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Provide a file path to a game!");
            System.exit(1);
        }
        final String pathToGame = args[0];
        final nes.Console console = new nes.Console(pathToGame);
        final MainScreen screen = new MainScreen();
        final Timer timer = new Timer();
        console.ppu.cycleCount = 20000;
        int FRAME_TIME = 17;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < CPU_CYCLES_PER_SECOND / 60; i++) {
                    console.ppu.executeCycles(3);
                    console.cpu.executeCycle();
                    if (console.ppu.triggerVerticalBlank) {
                        console.cpu.triggerInterrupt(nes.Interrupt.NMI);
                        screen.push(console.ppu.getImage());
                        console.ppu.triggerVerticalBlank = false;
                    }
                    screen.redraw();
                }
            }
        }, 0, FRAME_TIME);
    }
}
