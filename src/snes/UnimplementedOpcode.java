package snes;

public class UnimplementedOpcode extends Exception {
    public UnimplementedOpcode() {
    }

    public UnimplementedOpcode(String message) {
        super(message);
    }

    public UnimplementedOpcode(Throwable reason) {
        super(reason);
    }

    public UnimplementedOpcode(String message, Throwable reason) {
        super(message, reason);
    }
}
