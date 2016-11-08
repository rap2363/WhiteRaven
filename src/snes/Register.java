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
     * Increment the register n times
     * 
     * @param n
     */
    default public void incrementBy(int n) {
        for (int i = 0; i < n; i++) {
            increment();
        }
    }

    /**
     * Decrement the register
     */
    public void decrement();

    /**
     * Decrement the register n times
     * 
     * @param n
     */
    default public void decrementBy(int n) {
        for (int i = 0; i < n; i++) {
            decrement();
        }
    }
}
