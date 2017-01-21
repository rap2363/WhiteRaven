package nes;

import java.io.*;

public class NESLogComparisonTest {
    private static class State {
        int PC;
        byte A;
        byte X;
        byte Y;
        byte P;
        byte SP;

        public State(int PC, byte A, byte X, byte Y, byte P, byte SP) {
            this.PC = PC;
            this.A = A;
            this.X = X;
            this.Y = Y;
            this.P = P;
            this.SP = SP;
        }

        public State(final CPU cpu) {
            this(cpu.PC.read(), cpu.A.readAsByte(), cpu.X.readAsByte(), cpu.Y.readAsByte(), cpu.P.readAsByte(), cpu.SP.readAsByte());
        }

        public boolean equals(final State other) {
            return this.PC == other.PC
                    && this.A == other.A
                    && this.X == other.X
                    && this.Y == other.Y
                    && this.P == other.P
                    && this.SP == other.SP;
        }
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }

        return true;
    }

    private static State processLogLine(final String line) {
        String[] tokens = line.split("\\s+");
        int PC = Integer.parseInt(tokens[0].toLowerCase(), 16);
        int offset = 1;

        if (isInteger(tokens[tokens.length - offset])) {
            offset++;
        }

        byte A  = (byte) Integer.parseInt(tokens[tokens.length - 5 - offset].toLowerCase().substring(2), 16);
        byte X  = (byte) Integer.parseInt(tokens[tokens.length - 4 - offset].toLowerCase().substring(2), 16);
        byte Y  = (byte) Integer.parseInt(tokens[tokens.length - 3 - offset].toLowerCase().substring(2), 16);
        byte P  = (byte) Integer.parseInt(tokens[tokens.length - 2 - offset].toLowerCase().substring(2), 16);
        byte SP = (byte) Integer.parseInt(tokens[tokens.length - 1 - offset].toLowerCase().substring(3), 16);

        return new State(PC, A, X, Y, P, SP);
    }

    public static void main(String[] args) throws FileNotFoundException {
        Console console = new Console("/Users/rparanjpe/WhiteRaven/nestest.nes");
        console.cpu.PC.write(0xC000);
        console.cpu.setCurrentInterrupt(Interrupt.NONE);

        BufferedReader reader = new BufferedReader(new FileReader(new File("nestest.log")));
        String line;
        int lineCount = 0;
        try {
            while ((line = reader.readLine()) != null) {
                if (!processLogLine(line).equals(new State(console.cpu))) {
                    System.out.println(line);
                    System.out.println(console.cpu.singleLineState());
                    System.out.println("Line #: " + lineCount);
                    return;
                }
                System.out.println(line);
                lineCount++;
                console.cpu.execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
