package test;

import io.KeyboardController;

/**
 * Starts the Emulator with a path to a test rom to load in. Reads the status from $6000.
 */
public class NesTestRomLauncher {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Provide a file path to a test rom!");
            System.exit(1);
        }
        final String pathToGame = args[0];
        final nes.Console console = new nes.Console.Builder()
                                        .setCartridgePath(pathToGame)
                                        .setJoypadOne(new KeyboardController())
                                        .build();

        while (true) {
            console.ppu.executeCycles(3);
            console.cpu.executeCycle();
            if (console.ppu.triggerVerticalBlank) {
                console.cpu.triggerInterrupt(nes.Interrupt.NMI);
                console.ppu.triggerVerticalBlank = false;
            }
        }
    }
}
