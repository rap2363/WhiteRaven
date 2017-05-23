package main.java.operations;

public class UnimplementedAddressingMode extends Exception {
    public UnimplementedAddressingMode() {
    }

    public UnimplementedAddressingMode(String message) {
        super(message);
    }

    public UnimplementedAddressingMode(Throwable reason) {
        super(reason);
    }

    public UnimplementedAddressingMode(String message, Throwable reason) {
        super(message, reason);
    }
}
