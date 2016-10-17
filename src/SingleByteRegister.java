
public class SingleByteRegister extends Register {
    public SingleByteRegister() {
        this.registerMemory = new byte[1];
    }

    public void writeByte(byte value) {
        this.registerMemory[0] = value;
    }

    public byte readByte() {
        return this.registerMemory[0];
    }
}
