package snes;

public interface Register {

    /**
     * Read the value as an unsigned integer from the register
     * 
     * @param unsignedValue
     * @return
     */
    public int read();

    /**
     * Write an integer value to this register
     * 
     * @param unsignedValue
     */
    public void write(int unsignedValue);

    /**
     * Increment the register
     */
    public void increment();

    /**
     * Decrement the register
     */
    public void decrement();
}
