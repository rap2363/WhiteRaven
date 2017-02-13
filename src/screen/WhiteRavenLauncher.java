package screen;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Starts the Emulator with a path to a game to load in.
 */
public class WhiteRavenLauncher {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Provide a file path to a game!");
            System.exit(1);
        }
        final String pathToGame = args[0];
        final nes.Console console = new nes.Console(pathToGame);
        final MainScreen screen = new MainScreen();
        final Timer timer = new Timer();

        int FRAME_TIME = 17;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < (1789773.0 / 60.0); i++) {
                    console.cpu.executeCycle();
                    console.ppu.executeCycles(3);
                    if (console.ppu.triggerVerticalBlank) {
                        console.cpu.triggerInterrupt(nes.Interrupt.NMI);
                        console.ppu.triggerVerticalBlank = false;
                    }
                    if (console.ppu.imageReady) {
                        screen.redraw(console.ppu.getImage());
                    }
                }
            }
        }, 0, FRAME_TIME);
    }
}
