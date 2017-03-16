package nes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class NESLogComparisonTest {
    private static class State {
        int PC;
        byte A;
        byte X;
        byte Y;
        byte P;
        byte SP;
        long cycleCount;

        public State(int PC, byte A, byte X, byte Y, byte P, byte SP, long cycleCount) {
            this.PC = PC;
            this.A = A;
            this.X = X;
            this.Y = Y;
            this.P = P;
            this.SP = SP;
            this.cycleCount = cycleCount;
        }

        public State(final CPU cpu) {
            this(cpu.PC.read(), cpu.A.readAsByte(), cpu.X.readAsByte(), cpu.Y.readAsByte(), cpu.P.readAsByte(), cpu.SP.readAsByte(), cpu.cycleCount * 3 % 341);
        }

        public boolean equals(final State other) {
            System.out.println(this.cycleCount + ", " + other.cycleCount);
            return this.PC == other.PC
                    && this.A == other.A
                    && this.X == other.X
                    && this.Y == other.Y
                    && this.P == other.P
                    && this.SP == other.SP
                    && this.cycleCount == other.cycleCount;
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
        long cycleCount = Integer.parseInt(line.substring(line.length() - 3, line.length()).replaceAll("\\s+",""));

        return new State(PC, A, X, Y, P, SP, cycleCount);
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
                System.out.println(console.cpu.singleLineState());
                System.out.println(line);
                lineCount++;
                console.cpu.execute();
//                if (lineCount == 4982 || lineCount == 3054 || lineCount == 3065 || lineCount == 3378 || lineCount == 3385 || lineCount == 4369) {
//                    console.cpu.cycleCount++;
//                }
//                if (lineCount == 4964) {
//                    console.cpu.cycleCount--;
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
